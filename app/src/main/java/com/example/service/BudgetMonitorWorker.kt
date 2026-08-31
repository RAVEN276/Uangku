package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.data.db.AppDatabase
import com.example.data.model.Budget
import com.example.data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Background Worker yang memantau pengeluaran bulanan secara berkala di latar belakang
 * dan mengirimkan notifikasi peringatan sistem ketika pengeluaran suatu kategori
 * telah mendekati atau melebihi batas (limit) anggaran bulanan yang ditentukan.
 */
class BudgetMonitorWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "BudgetMonitorWorker running background spending check...")

        try {
            val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isMonitoringEnabled = prefs.getBoolean(PREF_BUDGET_MONITORING_ENABLED, true)
            if (!isMonitoringEnabled) {
                Log.d(TAG, "Budget monitoring is disabled in user preferences.")
                return Result.success()
            }

            val database = AppDatabase.getDatabase(appContext)
            val financeDao = database.financeDao()

            // 1. Hitung rentang waktu bulan berjalan (1st of month 00:00:00 s.d sekarang)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonthTimestamp = calendar.timeInMillis
            val endOfCurrentTime = System.currentTimeMillis()

            // Rentang waktu hari ini (untuk budget harian jika ada)
            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDayTimestamp = todayCal.timeInMillis

            // 2. Ambil seluruh transaksi bulan ini & daftar anggaran
            val monthTransactions = financeDao.getTransactionsInTimeRange(startOfMonthTimestamp, endOfCurrentTime)
            val allBudgets = financeDao.getAllBudgetsList()

            if (allBudgets.isEmpty()) {
                Log.d(TAG, "No budget limits configured in database.")
                return Result.success()
            }

            // Filter hanya transaksi pengeluaran (EXPENSE)
            val expenseTransactions = monthTransactions.filter { it.type.equals("EXPENSE", ignoreCase = true) }
            val expensesByCategory = expenseTransactions.groupBy { it.category }
            val totalMonthlyExpense = expenseTransactions.sumOf { it.amount }
            val todayExpense = expenseTransactions.filter { it.timestamp >= startOfDayTimestamp }.sumOf { it.amount }

            // 3. Konfigurasi preferensi mata uang untuk format teks
            val currencyPref = prefs.getString("selected_currency", "IDR") ?: "IDR"
            val isUsd = currencyPref == "USD"
            val monthYearKey = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())

            var alertsSentCount = 0

            // 4. Periksa setiap kategori anggaran
            for (budget in allBudgets) {
                if (budget.limitAmount <= 0) continue

                val spentAmount = when (budget.category) {
                    "ALL", "Semua" -> totalMonthlyExpense
                    "DAILY", "Harian" -> todayExpense
                    else -> expensesByCategory[budget.category]?.sumOf { it.amount } ?: 0.0
                }

                val usagePercent = (spentAmount / budget.limitAmount) * 100.0
                val thresholdPercent = budget.alertThresholdPercent.toDouble().coerceIn(10.0, 100.0)

                // Cek apakah pengeluaran telah melampaui limit (100%+) atau ambang batas peringatan (misal 80%+)
                val isLimitExceeded = spentAmount >= budget.limitAmount
                val isThresholdExceeded = usagePercent >= thresholdPercent

                if (isThresholdExceeded) {
                    val categoryLabel = when (budget.category) {
                        "ALL", "Semua" -> "Semua Kategori (Total)"
                        "DAILY", "Harian" -> "Pengeluaran Harian"
                        else -> budget.category
                    }

                    val formattedSpent = formatCurrency(spentAmount, isUsd)
                    val formattedLimit = formatCurrency(budget.limitAmount, isUsd)
                    val percentRounded = String.format(Locale.US, "%.1f", usagePercent)

                    // Cek duplikasi notifikasi agar tidak spam berkali-kali jika nominal belum bertambah signifikan
                    val alertTier = if (isLimitExceeded) "EXCEEDED" else "WARNING"
                    val notifKey = "notif_sent_${budget.category}_${monthYearKey}_${alertTier}"
                    val lastNotifiedAmountKey = "last_notified_amount_${budget.category}_${monthYearKey}"
                    val lastNotifiedAmount = prefs.getFloat(lastNotifiedAmountKey, 0f)

                    val shouldSend = !prefs.getBoolean(notifKey, false) || (spentAmount - lastNotifiedAmount) >= (budget.limitAmount * 0.05)

                    if (shouldSend) {
                        sendBudgetAlertNotification(
                            category = budget.category,
                            categoryLabel = categoryLabel,
                            spentFormatted = formattedSpent,
                            limitFormatted = formattedLimit,
                            percentageText = percentRounded,
                            isExceeded = isLimitExceeded,
                            thresholdPercent = budget.alertThresholdPercent
                        )

                        // Simpan status notifikasi
                        prefs.edit()
                            .putBoolean(notifKey, true)
                            .putFloat(lastNotifiedAmountKey, spentAmount.toFloat())
                            .putLong("last_budget_check_timestamp", System.currentTimeMillis())
                            .apply()

                        alertsSentCount++
                        Log.i(TAG, "Sent budget alert notification for category ${budget.category} ($percentRounded%)")
                    }
                }
            }

            Log.d(TAG, "Budget check completed successfully. Alerts sent: $alertsSentCount")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error executing BudgetMonitorWorker", e)
            return Result.retry()
        }
    }

    private fun sendBudgetAlertNotification(
        category: String,
        categoryLabel: String,
        spentFormatted: String,
        limitFormatted: String,
        percentageText: String,
        isExceeded: Boolean,
        thresholdPercent: Int
    ) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Notification Channel khusus jika Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pemantauan latar belakang ketika pengeluaran bulanan mendekati atau melebihi limit anggaran"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // PendingIntent untuk membuka halaman anggaran di MainActivity
        val openIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "budget")
            putExtra("EXTRA_ALERT_CATEGORY", category)
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            category.hashCode(),
            openIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val title = if (isExceeded) {
            "🚨 Batas Anggaran Terlampaui: $categoryLabel!"
        } else {
            "⚠️ Peringatan Anggaran: $categoryLabel ($percentageText%)"
        }

        val shortMessage = if (isExceeded) {
            "Pengeluaran $categoryLabel telah mencapai $spentFormatted dari batas limit $limitFormatted ($percentageText%)."
        } else {
            "Pengeluaran $categoryLabel telah mencapai $spentFormatted (melebihi ${thresholdPercent}% dari limit $limitFormatted)."
        }

        val bigTextMessage = if (isExceeded) {
            "🚨 Pengeluaran kategori '$categoryLabel' bulan ini telah melampaui batas anggaran!\n\n" +
                    "• Total Terpakai: $spentFormatted\n" +
                    "• Limit Anggaran: $limitFormatted\n" +
                    "• Rasio Pengeluaran: $percentageText%\n\n" +
                    "Buka aplikasi Uangku untuk meninjau detail mutasi pengeluaran dan menyesuaikan batas anggaran Anda."
        } else {
            "⚠️ Pengeluaran kategori '$categoryLabel' bulan ini mendekati batas anggaran!\n\n" +
                    "• Total Terpakai: $spentFormatted\n" +
                    "• Batas Limit: $limitFormatted\n" +
                    "• Pemakaian: $percentageText% (Ambang batas $thresholdPercent%)\n\n" +
                    "Periksa alokasi pengeluaran Anda agar tidak melampaui batas anggaran bulanan."
        }

        val notificationBuilder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigTextMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationId = NOTIFICATION_BASE_ID + Math.abs(category.hashCode() % 1000)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun formatCurrency(amount: Double, isUsd: Boolean): String {
        return if (isUsd) {
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            format.format(amount)
        } else {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.maximumFractionDigits = 0
            format.format(amount)
        }
    }

    companion object {
        const val TAG = "BudgetMonitorWorker"
        const val CHANNEL_ID = "uangku_budget_alerts"
        const val CHANNEL_NAME = "Peringatan Batas Anggaran"
        const val PREFS_NAME = "uangku_prefs"
        const val PREF_BUDGET_MONITORING_ENABLED = "budget_monitoring_enabled"
        const val NOTIFICATION_BASE_ID = 20000
    }
}

package com.example.service

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Pengatur jadwal (Scheduling Service) untuk pemantauan pengeluaran bulanan dan anggaran.
 * Mendukung eksekusi berkala (Periodic Work) serta pemeriksaan instan (Immediate One-Time Work).
 */
object BudgetMonitoringScheduler {

    private const val TAG = "BudgetScheduler"
    private const val UNIQUE_PERIODIC_WORK_NAME = "budget_monthly_monitoring_periodic"
    private const val UNIQUE_IMMEDIATE_WORK_NAME = "budget_monthly_monitoring_immediate"
    private const val PREFS_NAME = "uangku_prefs"
    private const val PREF_BUDGET_MONITORING_ENABLED = "budget_monitoring_enabled"

    /**
     * Menjadwalkan pemantauan anggaran berkala menggunakan WorkManager.
     * Default interval adalah 1 jam (atau 15 menit jika diperlukan).
     */
    fun schedulePeriodicMonitoring(context: Context, intervalHours: Long = 1, forceUpdate: Boolean = false) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(PREF_BUDGET_MONITORING_ENABLED, true)

        if (!isEnabled) {
            Log.d(TAG, "Periodic budget monitoring is disabled in preferences. Skipping schedule.")
            return
        }

        try {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            // WorkManager periodic work request (minimum 15 menit, standard 1 jam)
            val periodicRequest = PeriodicWorkRequestBuilder<BudgetMonitorWorker>(
                intervalHours.coerceAtLeast(1),
                TimeUnit.HOURS,
                15,
                TimeUnit.MINUTES // Flex interval
            )
                .setConstraints(constraints)
                .addTag("budget_monitoring")
                .build()

            val policy = if (forceUpdate) {
                ExistingPeriodicWorkPolicy.UPDATE
            } else {
                ExistingPeriodicWorkPolicy.KEEP
            }

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_WORK_NAME,
                policy,
                periodicRequest
            )

            Log.i(TAG, "Periodic budget monitoring successfully scheduled (Interval: ${intervalHours}h)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule periodic budget monitoring", e)
        }
    }

    /**
     * Membatalkan pemantauan anggaran berkala.
     */
    fun cancelMonitoring(context: Context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
            Log.i(TAG, "Periodic budget monitoring cancelled.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel periodic budget monitoring", e)
        }
    }

    /**
     * Memicu pemeriksaan anggaran langsung (One-Time Immediate Work) di latar belakang.
     * Dipanggil secara otomatis ketika transaksi pengeluaran baru ditambahkan, struk dipindai,
     * atau saat pengguna menekan tombol periksa di halaman Anggaran/Pengaturan.
     */
    fun triggerImmediateCheck(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean(PREF_BUDGET_MONITORING_ENABLED, true)
            if (!isEnabled) return

            val immediateRequest = OneTimeWorkRequestBuilder<BudgetMonitorWorker>()
                .addTag("budget_monitoring_immediate")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                immediateRequest
            )

            Log.d(TAG, "Immediate budget monitoring worker enqueued.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to trigger immediate budget check", e)
        }
    }

    /**
     * Memeriksa apakah fitur pemantauan latar belakang aktif di preferensi pengguna.
     */
    fun isMonitoringEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_BUDGET_MONITORING_ENABLED, true)
    }

    /**
     * Mengaktifkan atau menonaktifkan pemantauan latar belakang.
     */
    fun setMonitoringEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_BUDGET_MONITORING_ENABLED, enabled).apply()

        if (enabled) {
            schedulePeriodicMonitoring(context, forceUpdate = true)
            triggerImmediateCheck(context)
        } else {
            cancelMonitoring(context)
        }
    }

    /**
     * Mengambil waktu terakhir kali pemeriksaan anggaran dijalankan.
     */
    fun getLastCheckTimestamp(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong("last_budget_check_timestamp", 0L)
    }
}

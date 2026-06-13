package com.example.ui

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BankConnection
import com.example.data.model.Budget
import com.example.data.model.Transaction
import com.example.data.model.SavingGoal
import com.example.data.model.RecurringBill
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import com.example.service.LocalFinanceMLEngine

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    // --- State Management ---
    val allTransactions: StateFlow<List<Transaction>>
    val allBudgets: StateFlow<List<Budget>>
    val allBankConnections: StateFlow<List<BankConnection>>
    val allSavingGoals: StateFlow<List<SavingGoal>>
    val allRecurringBills: StateFlow<List<RecurringBill>>

    private val prefs = application.getSharedPreferences("uangku_prefs", Context.MODE_PRIVATE)

    // UI Interactive States
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "User") ?: "User")
    val userName = _userName.asStateFlow()

    private val _isOnboarded = MutableStateFlow(prefs.getBoolean("is_onboarded", false))
    val isOnboarded = _isOnboarded.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(prefs.getBoolean("is_pin_enabled", false))
    val isPinEnabled = _isPinEnabled.asStateFlow()

    private val _isLocked = MutableStateFlow(
        prefs.getBoolean("is_onboarded", false) && prefs.getBoolean("is_pin_enabled", false)
    )
    val isLocked = _isLocked.asStateFlow()

    private val _biometricsEnabled = MutableStateFlow(prefs.getBoolean("biometrics_enabled", false))
    val biometricsEnabled = _biometricsEnabled.asStateFlow()

    private val _userPin = MutableStateFlow(prefs.getString("user_pin", "1234") ?: "1234")
    val userPin = _userPin.asStateFlow()

    private val _themeDark = MutableStateFlow(prefs.getBoolean("theme_dark", false))
    val themeDark = _themeDark.asStateFlow()

    // Alert message for Budget triggers
    private val _budgetAlert = MutableStateFlow<String?>(null)
    val budgetAlert = _budgetAlert.asStateFlow()

    // Local ML Weekly Summary States (100% Client-side privacy-first Intelligence)
    private val _mlWeeklySummary = MutableStateFlow<String?>(prefs.getString("ml_weekly_summary", null))
    val mlWeeklySummary = _mlWeeklySummary.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary = _isGeneratingSummary.asStateFlow()

    // Dashboard totals derived from State Flows
    val totalBalance: StateFlow<Double>
    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>
    val mlInsights: StateFlow<LocalFinanceMLEngine.MLInsight?>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FinanceRepository(database.financeDao())

        allTransactions = repository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allBudgets = repository.allBudgets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allBankConnections = repository.allBankConnections.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSavingGoals = repository.allSavingGoals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allRecurringBills = repository.allRecurringBills.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Derive financial metric summary
        val baseBalance = 0.0 // Base manual wallet cash (Starting entirely from 0)

        totalIncome = allTransactions.map { txs ->
            txs.filter { it.type == "INCOME" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalExpense = allTransactions.map { txs ->
            txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalBalance = combine(totalIncome, totalExpense, allBankConnections) { inc, exp, banks ->
            val connectedBanksBalance = banks.filter { it.isConnected }.sumOf { it.balance }
            baseBalance + inc - exp + connectedBanksBalance
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), baseBalance)

        mlInsights = combine(allTransactions, userName) { txs, name ->
            if (txs.isEmpty()) null
            else {
                try {
                    LocalFinanceMLEngine.analyzeTransactions(txs, name)
                } catch (e: Exception) {
                    null
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        // Automatically trigger seeding on first startup & establish standard budgets
        viewModelScope.launch {
            seedInitialDataIfNeeded()
            monitorBudgets()
            // Wait a brief moment to ensure DB read is finished, then fetch automatic weekly summary
            delay(1000)
            fetchWeeklySummary(force = false)
        }
    }

    /**
     * Memproses dan membersihkan data seluruh transaksi lokal agar siap digunakan sebagai
     * data latih (training data) untuk model kecerdasan buatan lokal (Machine Learning).
     */
    fun cleanAndPrepareLocalTrainingData(): List<LocalFinanceMLEngine.CleanedMLSample> {
        return LocalFinanceMLEngine.cleanAndPrepareTrainingData(allTransactions.value)
    }

    // --- Biometric Lock Control ---
    fun login(pin: String): Boolean {
        return if (pin == _userPin.value) {
            _isLocked.value = false
            true
        } else {
            false
        }
    }

    fun triggerBiometricUnlock() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(800) // Beautiful scanning simulation
            _isSyncing.value = false
            _isLocked.value = false
        }
    }

    fun unlockWithBiometrics() {
        _isLocked.value = false
    }

    fun lockApp() {
        _isLocked.value = true
    }

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun setOnboardingCompleted(name: String, pinEnabled: Boolean, pin: String, biomEnabled: Boolean) {
        prefs.edit().apply {
            putString("user_name", name)
            putBoolean("is_onboarded", true)
            putBoolean("is_pin_enabled", pinEnabled)
            putString("user_pin", if (pinEnabled) pin else "")
            putBoolean("biometrics_enabled", biomEnabled)
            apply()
        }
        _userName.value = name
        _isOnboarded.value = true
        _isPinEnabled.value = pinEnabled
        _userPin.value = if (pinEnabled) pin else ""
        _biometricsEnabled.value = biomEnabled
        _isLocked.value = false
    }

    fun toggleBiometrics(enabled: Boolean) {
        prefs.edit().putBoolean("biometrics_enabled", enabled).apply()
        _biometricsEnabled.value = enabled
    }

    fun setPin(newPin: String) {
        if (newPin.length == 4 && newPin.all { it.isDigit() }) {
            prefs.edit().putString("user_pin", newPin).apply()
            _userPin.value = newPin
        }
    }

    fun togglePinEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_pin_enabled", enabled).apply()
        _isPinEnabled.value = enabled
        if (!enabled) {
            _userPin.value = ""
            prefs.edit().remove("user_pin").apply()
        }
    }

    fun fetchWeeklySummary(force: Boolean = false) {
        viewModelScope.launch {
            val txs = allTransactions.value
            val expenses = txs.filter { it.type == "EXPENSE" }
            if (expenses.isEmpty()) {
                _mlWeeklySummary.value = "Belum ada data pengeluaran yang tercatat minggu ini untuk dianalisis oleh Asisten Cerdas. Silakan tambahkan transaksi pengeluaran baru Anda."
                return@launch
            }

            val cachedText = prefs.getString("ml_weekly_summary", null)
            val cachedTxCount = prefs.getInt("ml_weekly_sum_tx_count", -1)
            val cachedTimestamp = prefs.getLong("ml_weekly_sum_time", 0)
            val currentTime = System.currentTimeMillis()

            // Auto bypass ML re-calc if:
            // 1. Not forced
            // 2. We have cached text
            // 3. The transaction count is equal
            // 4. Cached summary was created less than 1 hour ago (fast re-evaluation)
            if (!force && cachedText != null && cachedTxCount == expenses.size && (currentTime - cachedTimestamp) < 3600 * 1000) {
                _mlWeeklySummary.value = cachedText
                return@launch
            }

            _isGeneratingSummary.value = true
            try {
                // Add a small delay for delightful UX to show on-device machine learning at work
                delay(800)
                
                val result = LocalFinanceMLEngine.analyzeTransactions(txs, _userName.value)
                val report = result.markdownReport
                
                _mlWeeklySummary.value = report
                
                // Cache local ML intelligence results
                prefs.edit()
                    .putString("ml_weekly_summary", report)
                    .putInt("ml_weekly_sum_tx_count", expenses.size)
                    .putLong("ml_weekly_sum_time", currentTime)
                    .apply()
            } catch (e: Exception) {
                _mlWeeklySummary.value = "Gagal memproses analisis Asisten Cerdas: ${e.localizedMessage}"
            } finally {
                _isGeneratingSummary.value = false
            }
        }
    }

    fun toggleTheme() {
        val nextVal = !_themeDark.value
        prefs.edit().putBoolean("theme_dark", nextVal).apply()
        _themeDark.value = nextVal
    }

    fun setTheme(dark: Boolean) {
        prefs.edit().putBoolean("theme_dark", dark).apply()
        _themeDark.value = dark
    }

    fun updateSystemThemeDefault(systemDark: Boolean) {
        if (!prefs.contains("theme_dark")) {
            _themeDark.value = systemDark
        }
    }

    // --- Transactions Actions ---
    fun addTransaction(title: String, amount: Double, type: String, category: String, bankSource: String? = null) {
        viewModelScope.launch {
            val transaction = Transaction(
                title = title,
                amount = amount,
                type = type,
                category = category,
                bankSource = bankSource
            )
            repository.insertTransaction(transaction)
            triggerAutoSync()
        }
    }

    fun parseAndAddNotification(packageName: String, title: String, text: String): Boolean {
        val parsed = com.example.service.MyNotificationListenerService.parseNotification(packageName, title, text)
        if (parsed != null) {
            viewModelScope.launch {
                repository.insertTransaction(parsed)
                triggerAutoSync()
            }
            return true
        }
        return false
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            triggerAutoSync()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
            triggerAutoSync()
        }
    }

    // --- Budget Management & Warnings ---
    fun setBudget(category: String, amount: Double, alertThreshold: Int = 80) {
        viewModelScope.launch {
            val budget = Budget(category = category, limitAmount = amount, alertThresholdPercent = alertThreshold)
            repository.insertBudget(budget)
            triggerAutoSync()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            triggerAutoSync()
        }
    }

    // --- Saving Goals Actions ---
    fun addSavingGoal(title: String, targetAmount: Double, currentAmount: Double, targetDate: String, category: String) {
        viewModelScope.launch {
            val goal = SavingGoal(
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                targetDate = targetDate,
                category = category
            )
            repository.insertSavingGoal(goal)
            triggerAutoSync()
        }
    }

    fun depositToSavingGoal(goal: SavingGoal, amount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(currentAmount = goal.currentAmount + amount)
            repository.insertSavingGoal(updated)
            
            // Record an expense transaction
            val transaction = Transaction(
                title = "Alokasi Tabungan: ${goal.title}",
                amount = amount,
                type = "EXPENSE",
                category = "Investasi" // we use specific existing category
            )
            repository.insertTransaction(transaction)
            triggerAutoSync()
        }
    }

    fun withdrawFromSavingGoal(goal: SavingGoal, amount: Double) {
        viewModelScope.launch {
            val newAmount = (goal.currentAmount - amount).coerceAtLeast(0.0)
            val updated = goal.copy(currentAmount = newAmount)
            repository.insertSavingGoal(updated)
            
            // Record an income transaction
            val transaction = Transaction(
                title = "Penarikan Tabungan: ${goal.title}",
                amount = amount,
                type = "INCOME",
                category = "Investasi"
            )
            repository.insertTransaction(transaction)
            triggerAutoSync()
        }
    }

    fun deleteSavingGoal(goal: SavingGoal) {
        viewModelScope.launch {
            repository.deleteSavingGoal(goal)
            triggerAutoSync()
        }
    }

    // --- Recurring Bills Actions ---
    fun addRecurringBill(title: String, amount: Double, category: String, billingCycle: String, dueDate: String) {
        viewModelScope.launch {
            val bill = RecurringBill(
                title = title,
                amount = amount,
                category = category,
                billingCycle = billingCycle,
                dueDate = dueDate,
                lastClaimedTimestamp = 0
            )
            repository.insertRecurringBill(bill)
            triggerAutoSync()
        }
    }

    fun deleteRecurringBill(bill: RecurringBill) {
        viewModelScope.launch {
            repository.deleteRecurringBill(bill)
            triggerAutoSync()
        }
    }

    fun payRecurringBill(bill: RecurringBill) {
        viewModelScope.launch {
            val updated = bill.copy(lastClaimedTimestamp = System.currentTimeMillis())
            repository.insertRecurringBill(updated)
            
            // Record an expense transaction
            val transaction = Transaction(
                title = "Bayar Tagihan: ${bill.title}",
                amount = bill.amount,
                type = "EXPENSE",
                category = bill.category
            )
            repository.insertTransaction(transaction)
            triggerAutoSync()
        }
    }

    private fun monitorBudgets() {
        viewModelScope.launch {
            combine(allTransactions, allBudgets) { txs, budgets ->
                // Check overall limit & category limits
                val currentMonthExpenses = txs.filter { tx ->
                    tx.type == "EXPENSE" && run {
                        val cal1 = java.util.Calendar.getInstance()
                        cal1.timeInMillis = tx.timestamp
                        val cal2 = java.util.Calendar.getInstance()
                        cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                        cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH)
                    }
                }

                val expensesByCategory = currentMonthExpenses.groupBy { it.category }

                val totalExpenseSum = currentMonthExpenses.sumOf { it.amount }

                val todayExpenseSum = txs.filter { tx ->
                    tx.type == "EXPENSE" && run {
                        val cal1 = java.util.Calendar.getInstance()
                        cal1.timeInMillis = tx.timestamp
                        val cal2 = java.util.Calendar.getInstance()
                        cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                        cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
                    }
                }.sumOf { it.amount }

                var alertText: String? = null

                for (budget in budgets) {
                    val spent = if (budget.category == "ALL") {
                        totalExpenseSum
                    } else if (budget.category == "DAILY") {
                        todayExpenseSum
                    } else {
                        expensesByCategory[budget.category]?.sumOf { it.amount } ?: 0.0
                    }

                    if (budget.limitAmount > 0) {
                        val usagePercent = (spent / budget.limitAmount) * 100
                        if (usagePercent >= budget.alertThresholdPercent) {
                            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            format.maximumFractionDigits = 0
                            val limitStr = format.format(budget.limitAmount)
                            val spentStr = format.format(spent)
                            alertText = when (budget.category) {
                                "ALL" -> "⚠️ Peringatan Anggaran: Total pengeluaran Anda ($spentStr) telah melampaui ${budget.alertThresholdPercent}% dari limit bulanan ($limitStr)!"
                                "DAILY" -> "⚠️ Peringatan Anggaran: Pengeluaran harian Anda hari ini ($spentStr) telah melampaui ${budget.alertThresholdPercent}% dari limit harian ($limitStr)!"
                                else -> "⚠️ Peringatan Anggaran: Kategori '${budget.category}' ($spentStr) telah melampaui ${budget.alertThresholdPercent}% dari limit ($limitStr)!"
                            }
                            break
                        }
                    }
                }
                alertText
            }.collect { alert ->
                _budgetAlert.value = alert
            }
        }
    }

    fun dismissBudgetAlert() {
        _budgetAlert.value = null
    }

    // --- Remote Auto-Sync Simulation ---
    fun triggerAutoSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // Beautiful network delay animation
            _isSyncing.value = false
        }
    }

    // --- Secure Bank Connection & Sandbox Transaction Integration ---
    fun connectBank(bankId: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1500) // Connect handshake simulation

            val defaultBalance = when (bankId) {
                "bca" -> 15450000.0
                "mandiri" -> 24500000.0
                "bni" -> 8500000.0
                "bri" -> 6200000.0
                else -> 5000000.0
            }

            val bankName = when (bankId) {
                "bca" -> "BCA Sandbox"
                "mandiri" -> "Mandiri Sandbox"
                "bni" -> "BNI Sandbox"
                "bri" -> "BRI Sandbox"
                else -> "Bank Sandbox"
            }

            val accountNumber = when (bankId) {
                "bca" -> "8023-4412-11"
                "mandiri" -> "113-00124-99"
                "bni" -> "0911-3442-21"
                "bri" -> "3201-9023-45"
                else -> "0011-2233-44"
            }

            val connection = BankConnection(
                bankId = bankId,
                bankName = bankName,
                accountNumber = accountNumber,
                balance = defaultBalance,
                isConnected = true,
                lastSyncTime = System.currentTimeMillis()
            )

            repository.insertBankConnection(connection)

            // Inject automatically imported items based on securely linked simulator
            injectBankSandboxTransactions(bankId, bankName)

            _isSyncing.value = false
        }
    }

    fun disconnectBank(bankId: String) {
        viewModelScope.launch {
            val connection = repository.getBankConnectionById(bankId)
            if (connection != null) {
                repository.insertBankConnection(connection.copy(isConnected = false))
                // Clean bank imported transactions if needed
            }
            triggerAutoSync()
        }
    }

    private suspend fun injectBankSandboxTransactions(bankId: String, bankName: String) {
        val format = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val dateStr = format.format(Date())

        val sandboxTransactions = when (bankId) {
            "bca" -> listOf(
                Transaction(title = "Gaji Bulanan PT Telkom", amount = 8500000.0, type = "INCOME", category = "Gaji", bankSource = bankName),
                Transaction(title = "Langganan Netflix Premium", amount = 186000.0, type = "EXPENSE", category = "Hiburan", bankSource = bankName),
                Transaction(title = "Bunga Deposito BCA", amount = 450000.0, type = "INCOME", category = "Investasi", bankSource = bankName)
            )
            "mandiri" -> listOf(
                Transaction(title = "Transfer Masuk (Dana Ayah)", amount = 2500000.0, type = "INCOME", category = "Gaji", bankSource = bankName),
                Transaction(title = "Bayar Token Listrik PLN", amount = 300000.0, type = "EXPENSE", category = "Belanja", bankSource = bankName),
                Transaction(title = "Biaya Admin Mandiri", amount = 12500.0, type = "EXPENSE", category = "Lainnya", bankSource = bankName)
            )
            "bni" -> listOf(
                Transaction(title = "Hasil Jual Reksa Dana", amount = 3200000.0, type = "INCOME", category = "Investasi", bankSource = bankName),
                Transaction(title = "Tagihan Wifi Indihome", amount = 405000.0, type = "EXPENSE", category = "Transportasi", bankSource = bankName)
            )
            "bri" -> listOf(
                Transaction(title = "Penjualan Toko Online", amount = 1450000.0, type = "INCOME", category = "Gaji", bankSource = bankName),
                Transaction(title = "Biaya Parkir & Bensin", amount = 85000.0, type = "EXPENSE", category = "Transportasi", bankSource = bankName)
            )
            else -> listOf(
                Transaction(title = "Koneksi Bank Bonus", amount = 50000.0, type = "INCOME", category = "Lainnya", bankSource = bankName)
            )
        }

        for (tx in sandboxTransactions) {
            repository.insertTransaction(tx)
        }
    }

    // --- Financial Reports & Data Exporting (PDF / Excel Format Report) ---
    private fun saveToDownloadFolder(context: Context, fileName: String, content: String, mimeType: String): String? {
        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            try {
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(content.toByteArray(Charsets.UTF_8))
                    }
                    return "Download/$fileName"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, fileName)
            try {
                FileWriter(file).use { writer ->
                    writer.write(content)
                }
                return file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun saveBytesToDownloadFolder(context: Context, fileName: String, bytes: ByteArray, mimeType: String): String? {
        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            try {
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(bytes)
                    }
                    return "Download/$fileName"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, fileName)
            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(bytes)
                }
                return file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun generateCSVExport(context: Context): String {
        val format = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val fileName = "Laporan_Keuangan_Uangku_${format.format(Date())}.csv"
        
        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Deskripsi,Jumlah,Tipe,Kategori,Sumber,Selesai Tanggal\n")

        val txs = allTransactions.value
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        for (tx in txs) {
            csvBuilder.append("${tx.id},")
            csvBuilder.append("\"${tx.title.replace("\"", "\"\"")}\",")
            csvBuilder.append("${tx.amount},")
            csvBuilder.append("${tx.type},")
            csvBuilder.append("${tx.category},")
            csvBuilder.append(if (tx.bankSource != null) "\"${tx.bankSource}\"," else "Dompet Manual,")
            csvBuilder.append("${sdf.format(Date(tx.timestamp))}\n")
        }

        val content = csvBuilder.toString()
        val downloadPath = saveToDownloadFolder(context, fileName, content, "text/csv")
        
        if (downloadPath != null) {
            return downloadPath
        }

        // Fallback to cache directory
        val file = File(context.cacheDir, fileName)
        try {
            FileWriter(file).use { writer ->
                writer.write(content)
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun generatePDFTextReport(context: Context): String {
        // PDF Simulation Text report for direct visual display + sharing
        val format = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
        val dateStr = format.format(Date())

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        currencyFormat.maximumFractionDigits = 0

        val txs = allTransactions.value
        val txReport = StringBuilder()

        txReport.append("=========================================\n")
        txReport.append("      LAPORAN KEUANGAN BULANAN - UANGKU   \n")
        txReport.append("=========================================\n")
        txReport.append("Nama Pengguna   : ${_userName.value}\n")
        txReport.append("Tanggal Dokumen : $dateStr\n")
        txReport.append("Total Bersih    : ${currencyFormat.format(totalBalance.value)}\n")
        txReport.append("Pemasukan       : ${currencyFormat.format(totalIncome.value)}\n")
        txReport.append("Pengeluaran     : ${currencyFormat.format(totalExpense.value)}\n")
        txReport.append("-----------------------------------------\n\n")

        txReport.append("LIMIT & ANGGARAN:\n")
        val budgets = allBudgets.value
        if (budgets.isEmpty()) {
            txReport.append("- Belum ada anggaran/limit yang disetel.\n")
        } else {
            for (budget in budgets) {
                val label = when (budget.category) {
                    "ALL" -> "Limit Pengeluaran Bulanan"
                    "DAILY" -> "Limit Pengeluaran Harian"
                    else -> "Kategori ${budget.category}"
                }
                txReport.append("- $label: Limit ${currencyFormat.format(budget.limitAmount)}\n")
            }
        }

        txReport.append("\nDETAIL TRANSAKSI TERAKHIR:\n")
        txReport.append(String.format("%-11s | %-12s | %-15s | %s\n", "TGL", "KATEGORI", "JUMLAH", "NAMA TRANSAKSI"))
        txReport.append("-------------------------------------------------------------\n")

        val listFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
        for (tx in txs.take(20)) {
            val amountStr = (if (tx.type == "INCOME") "+" else "-") + currencyFormat.format(tx.amount)
            txReport.append(String.format(
                "%-11s | %-12s | %-15s | %s\n",
                listFormat.format(Date(tx.timestamp)),
                tx.category,
                amountStr,
                tx.title
            ))
        }

        txReport.append("\n=========================================\n")
        txReport.append("           Dibuat secara aman di Uangku   \n")
        txReport.append("=========================================\n")

        val reportStr = txReport.toString()
        val fileFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val fileName = "Laporan_Uangku_PDF_${fileFormat.format(Date())}.pdf"
        
        // Generate REAL high fidelity binary PDF representation
        val pdfBytes = createPdfDocumentBytes(dateStr, txs, budgets)

        // Save real binary pdf to download folder!
        val downloadPath = saveBytesToDownloadFolder(context, fileName, pdfBytes, "application/pdf")
        if (downloadPath != null) {
            Toast.makeText(context, "Laporan PDF disimpan di folder Download: $downloadPath", Toast.LENGTH_LONG).show()
        } else {
            // Fallback to local cache as real binary pdf
            val file = File(context.cacheDir, fileName)
            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(pdfBytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return reportStr
    }

    private fun createPdfDocumentBytes(
        dateStr: String,
        txs: List<Transaction>,
        budgets: List<Budget>
    ): ByteArray {
        val pdfDocument = PdfDocument()
        
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        
        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(41, 128, 185)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        
        val subTitlePaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 10f
            isAntiAlias = true
        }
        
        val headerPaint = Paint().apply {
            color = android.graphics.Color.rgb(44, 62, 80)
            textSize = 13f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 11f
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val greenTextPaint = Paint().apply {
            color = android.graphics.Color.rgb(39, 174, 96)
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val redTextPaint = Paint().apply {
            color = android.graphics.Color.rgb(192, 57, 43)
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val gridPaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        val bgPaint = Paint().apply {
            color = android.graphics.Color.rgb(245, 247, 250)
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        currencyFormat.maximumFractionDigits = 0

        var y = 50f

        canvas.drawText("UANGKU BY USER", 40f, y, titlePaint)
        y += 18f
        canvas.drawText("Aplikasi Manajemen Keuangan Pribadi Aman & Lokal", 40f, y, subTitlePaint)
        y += 24f
        
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 25f

        canvas.drawText("LAPORAN KEUANGAN RESMI", 40f, y, headerPaint)
        y += 20f
        canvas.drawText("Nama Panggilan : ${_userName.value}", 40f, y, textPaint)
        y += 18f
        canvas.drawText("Tanggal Unduh  : $dateStr", 40f, y, textPaint)
        y += 24f

        val rectLeft = 40f
        val rectTop = y
        val rectRight = 555f
        val rectBottom = y + 65f
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, bgPaint)
        
        canvas.drawText("TOTAL SALDO BERSIH", 55f, y + 25f, boldTextPaint)
        canvas.drawText(currencyFormat.format(totalBalance.value), 55f, y + 45f, textPaint)

        canvas.drawText("TOTAL PEMASUKAN", 230f, y + 25f, boldTextPaint)
        canvas.drawText(currencyFormat.format(totalIncome.value), 230f, y + 45f, greenTextPaint)

        canvas.drawText("TOTAL PENGELUARAN", 395f, y + 25f, boldTextPaint)
        canvas.drawText(currencyFormat.format(totalExpense.value), 395f, y + 45f, redTextPaint)
        
        y += 90f

        canvas.drawText("BATAS & ANGGARAN BULANAN", 40f, y, headerPaint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 20f

        if (budgets.isEmpty()) {
            canvas.drawText("- Belum ada batasan anggaran bulanan yang ditetapkan.", 45f, y, textPaint)
            y += 20f
        } else {
            for (b in budgets) {
                canvas.drawText("Kategori ${b.category}:", 45f, y, boldTextPaint)
                canvas.drawText("Limit ${currencyFormat.format(b.limitAmount)} per bulan", 200f, y, textPaint)
                y += 18f
                
                if (y > 780f) {
                    styleFooter(canvas, pageNumber)
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 50f
                }
            }
        }
        y += 15f

        if (y > 580f) {
            styleFooter(canvas, pageNumber)
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 50f
        }

        canvas.drawText("RIWAYAT TRANSAKSI TERBARU", 40f, y, headerPaint)
        y += 15f
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 20f

        canvas.drawRect(40f, y, 555f, y + 20f, bgPaint)
        canvas.drawText("TANGGAL", 45f, y + 14f, boldTextPaint)
        canvas.drawText("KATEGORI", 120f, y + 14f, boldTextPaint)
        canvas.drawText("NAMA TRANSAKSI", 220f, y + 14f, boldTextPaint)
        canvas.drawText("JUMLAH", 450f, y + 14f, boldTextPaint)
        y += 28f

        val listFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        for (tx in txs.take(20)) {
            val dateStrTx = listFormat.format(Date(tx.timestamp))
            val amountStr = (if (tx.type == "INCOME") "+" else "-") + currencyFormat.format(tx.amount)
            val isIncome = tx.type == "INCOME"

            canvas.drawText(dateStrTx, 45f, y, textPaint)
            canvas.drawText(tx.category, 120f, y, textPaint)
            
            var titleShow = tx.title
            if (titleShow.length > 25) {
                titleShow = titleShow.substring(0, 22) + "..."
            }
            canvas.drawText(titleShow, 220f, y, textPaint)
            
            canvas.drawText(amountStr, 450f, y, if (isIncome) greenTextPaint else redTextPaint)
            y += 22f

            canvas.drawLine(40f, y - 6f, 555f, y - 6f, gridPaint)

            if (y > 780f) {
                styleFooter(canvas, pageNumber)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                
                canvas.drawRect(40f, y, 555f, y + 20f, bgPaint)
                canvas.drawText("TANGGAL", 45f, y + 14f, boldTextPaint)
                canvas.drawText("KATEGORI", 120f, y + 14f, boldTextPaint)
                canvas.drawText("NAMA TRANSAKSI", 220f, y + 14f, boldTextPaint)
                canvas.drawText("JUMLAH", 450f, y + 14f, boldTextPaint)
                y += 28f
            }
        }

        y += 10f
        if (y > 760f) {
            styleFooter(canvas, pageNumber)
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 50f
        }
        
        canvas.drawText("Laporan ini diunduh secara instan dari aplikasi Uangku.", 45f, y, subTitlePaint)
        canvas.drawText("Semua data disimpan di penyimpanan terenkripsi perangkat Anda sendiri.", 45f, y + 12f, subTitlePaint)

        styleFooter(canvas, pageNumber)
        pdfDocument.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        try {
            pdfDocument.writeTo(outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        return outputStream.toByteArray()
    }

    private fun styleFooter(canvas: Canvas, pageIndex: Int) {
        val paint = Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 9f
            isAntiAlias = true
        }
        canvas.drawText("Halaman $pageIndex", 500f, 820f, paint)
    }

    // --- Private Initializer & Seeds ---
    private suspend fun seedInitialDataIfNeeded() {
        // Clear previous dummy seed data if present so users have a clean slate starting from 0
        val list = repository.allTransactions.first()
        if (list.any { it.title == "Gaji Pekerjaan Utama" }) {
            repository.clearAllTransactions()
            repository.clearBudgets()
            repository.clearBankConnections()
        }

        // We do not prepopulate dummy target menabung or dummy recurring bills per user request
        val goals = repository.allSavingGoals.first()
        if (goals.any { it.title == "Beli Laptop Macbook Air" || it.title == "Dana Darurat Mandiri" }) {
            repository.clearAllSavingGoals()
        }

        val bills = repository.allRecurringBills.first()
        if (bills.any { it.title == "Langganan Spotify Family" || it.title == "Langganan Netflix UHD" || it.title == "Tagihan Wifi Indihome" }) {
            repository.clearAllRecurringBills()
        }
    }
}

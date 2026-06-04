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
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    // --- State Management ---
    val allTransactions: StateFlow<List<Transaction>>
    val allBudgets: StateFlow<List<Budget>>
    val allBankConnections: StateFlow<List<BankConnection>>

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

    // Dashboard totals derived from State Flows
    val totalBalance: StateFlow<Double>
    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>

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

        // Derive financial metric summary
        val baseBalance = 0.0 // Base manual wallet cash (Starting entirely from 0)

        totalIncome = allTransactions.combine(allBankConnections) { txs, banks ->
            val manualIncome = txs.filter { it.type == "INCOME" && it.bankSource == null }.sumOf { it.amount }
            val bankIncome = txs.filter { it.type == "INCOME" && it.bankSource != null }.sumOf { it.amount }
            manualIncome + bankIncome
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalExpense = allTransactions.combine(allBankConnections) { txs, _ ->
            txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalBalance = combine(totalIncome, totalExpense, allBankConnections) { inc, exp, banks ->
            val connectedBanksBalance = banks.filter { it.isConnected }.sumOf { it.balance }
            baseBalance + inc - exp + connectedBanksBalance
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), baseBalance)

        // Automatically trigger seeding on first startup & establish standard budgets
        viewModelScope.launch {
            seedInitialDataIfNeeded()
            monitorBudgets()
        }
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

    fun toggleTheme() {
        val nextVal = !_themeDark.value
        prefs.edit().putBoolean("theme_dark", nextVal).apply()
        _themeDark.value = nextVal
    }

    fun setTheme(dark: Boolean) {
        prefs.edit().putBoolean("theme_dark", dark).apply()
        _themeDark.value = dark
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

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
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

    private fun monitorBudgets() {
        viewModelScope.launch {
            combine(allTransactions, allBudgets) { txs, budgets ->
                // Check overall limit & category limits
                val expensesByCategory = txs.filter { it.type == "EXPENSE" }
                    .groupBy { it.category }

                val totalExpenseSum = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }

                var alertText: String? = null

                for (budget in budgets) {
                    val spent = if (budget.category == "ALL") {
                        totalExpenseSum
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
                            alertText = if (budget.category == "ALL") {
                                "⚠️ Peringatan Anggaran: Total pengeluaran Anda ($spentStr) telah melampaui ${budget.alertThresholdPercent}% dari limit bulanan ($limitStr)!"
                            } else {
                                "⚠️ Peringatan Anggaran: Kategori '${budget.category}' ($spentStr) telah melampaui ${budget.alertThresholdPercent}% dari limit ($limitStr)!"
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
        txReport.append("Tanggal Dokumen : $dateStr\n")
        txReport.append("Total Bersih    : ${currencyFormat.format(totalBalance.value)}\n")
        txReport.append("Pemasukan       : ${currencyFormat.format(totalIncome.value)}\n")
        txReport.append("Pengeluaran     : ${currencyFormat.format(totalExpense.value)}\n")
        txReport.append("-----------------------------------------\n\n")
        txReport.append("DAFTAR AKSES BANK TERLINKNG:\n")

        val banks = allBankConnections.value
        if (banks.none { it.isConnected }) {
            txReport.append("- Tidak ada bank eksternal terhubung.\n")
        } else {
            for (bank in banks.filter { it.isConnected }) {
                txReport.append("- ${bank.bankName} (${bank.accountNumber}): ${currencyFormat.format(bank.balance)}\n")
            }
        }

        txReport.append("\nLIMIT & ANGGARAN BULANAN:\n")
        val budgets = allBudgets.value
        if (budgets.isEmpty()) {
            txReport.append("- Belum ada anggaran limits yang disetel.\n")
        } else {
            for (budget in budgets) {
                txReport.append("- Kategori ${budget.category}: Limit ${currencyFormat.format(budget.limitAmount)}\n")
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
        val fileName = "Laporan_Uangku_PDF_${fileFormat.format(Date())}.txt"
        
        // Save to Download folder
        val downloadPath = saveToDownloadFolder(context, fileName, reportStr, "text/plain")
        if (downloadPath != null) {
            Toast.makeText(context, "Laporan PDF disimpan di folder Download: $downloadPath", Toast.LENGTH_LONG).show()
        } else {
            // Fallback to local cache
            val file = File(context.cacheDir, fileName)
            try {
                val writer = FileWriter(file)
                writer.write(reportStr)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return reportStr
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
    }
}

package com.example.ui

import com.example.MainActivity

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
import com.example.data.model.getCleanTitle
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
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.util.Calendar
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

    private val _dynamicColor = MutableStateFlow(prefs.getBoolean("dynamic_color_enabled", false))
    val dynamicColor = _dynamicColor.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString("app_language", "id") ?: "id")
    val appLanguage = _appLanguage.asStateFlow()

    private val _bankNotificationEnabled = MutableStateFlow(prefs.getBoolean("bank_notification_enabled", false))
    val bankNotificationEnabled = _bankNotificationEnabled.asStateFlow()

    // Currency Selection (IDR vs USD) with live exchange rate
    private val _selectedCurrency = MutableStateFlow(prefs.getString("selected_currency", "IDR") ?: "IDR")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    // Default USD to IDR exchange rate (e.g. 1 USD = Rp 16.250)
    private val _usdToIdrRate = MutableStateFlow(prefs.getFloat("usd_to_idr_rate", 16250.0f).toDouble())
    val usdToIdrRate = _usdToIdrRate.asStateFlow()

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
    val currentMonthIncome: StateFlow<Double>
    val currentMonthExpense: StateFlow<Double>
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

        currentMonthIncome = allTransactions.map { txs ->
            val cal = Calendar.getInstance()
            val currYear = cal.get(Calendar.YEAR)
            val currMonth = cal.get(Calendar.MONTH)
            txs.filter { tx ->
                if (tx.type != "INCOME") return@filter false
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                txCal.get(Calendar.YEAR) == currYear && txCal.get(Calendar.MONTH) == currMonth
            }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        currentMonthExpense = allTransactions.map { txs ->
            val cal = Calendar.getInstance()
            val currYear = cal.get(Calendar.YEAR)
            val currMonth = cal.get(Calendar.MONTH)
            txs.filter { tx ->
                if (tx.type != "EXPENSE") return@filter false
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                txCal.get(Calendar.YEAR) == currYear && txCal.get(Calendar.MONTH) == currMonth
            }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalBalance = combine(totalIncome, totalExpense, allBankConnections) { inc, exp, banks ->
            val connectedBanksBalance = banks.filter { it.isConnected }.sumOf { it.balance }
            baseBalance + inc - exp + connectedBanksBalance
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), baseBalance)

        mlInsights = combine(allTransactions, userName, selectedCurrency, usdToIdrRate) { txs, name, curr, rate ->
            if (txs.isEmpty()) null
            else {
                try {
                    LocalFinanceMLEngine.analyzeTransactions(
                        transactions = txs,
                        userName = name,
                        currency = curr,
                        exchangeRate = rate
                    )
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
                
                val result = LocalFinanceMLEngine.analyzeTransactions(
                    transactions = txs,
                    userName = _userName.value,
                    currency = _selectedCurrency.value,
                    exchangeRate = _usdToIdrRate.value
                )
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

    // --- Currency Methods ---
    fun setCurrency(currency: String) {
        prefs.edit().putString("selected_currency", currency).apply()
        _selectedCurrency.value = currency
        fetchWeeklySummary(force = true)
    }

    fun setUsdRate(rate: Double) {
        if (rate > 0) {
            prefs.edit().putFloat("usd_to_idr_rate", rate.toFloat()).apply()
            _usdToIdrRate.value = rate
            fetchWeeklySummary(force = true)
        }
    }

    fun formatCurrency(amount: Double): String {
        return if (_selectedCurrency.value == "USD") {
            val inUsd = amount / _usdToIdrRate.value
            val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            }
            formatter.format(inUsd)
        } else {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
                minimumFractionDigits = 0
            }
            formatter.format(amount)
        }
    }

    fun getCurrencySymbol(): String {
        return if (_selectedCurrency.value == "USD") "$" else "Rp"
    }

    fun getCurrencyCode(): String {
        return _selectedCurrency.value
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

    fun setDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean("dynamic_color_enabled", enabled).apply()
        _dynamicColor.value = enabled
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _appLanguage.value = lang
    }

    fun isEnglish(): Boolean = _appLanguage.value.equals("en", ignoreCase = true)

    fun setBankNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("bank_notification_enabled", enabled).apply()
        _bankNotificationEnabled.value = enabled
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

    fun updateSavingGoal(goal: SavingGoal) {
        viewModelScope.launch {
            repository.updateSavingGoal(goal)
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
                            val limitStr = formatCurrency(budget.limitAmount)
                            val spentStr = formatCurrency(spent)
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
                Transaction(title = "Langganan Netflix", amount = 186000.0, type = "EXPENSE", category = "Hiburan", bankSource = bankName),
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
        txReport.append("Total Bersih    : ${formatCurrency(totalBalance.value)}\n")
        txReport.append("Pemasukan       : ${formatCurrency(totalIncome.value)}\n")
        txReport.append("Pengeluaran     : ${formatCurrency(totalExpense.value)}\n")
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
                txReport.append("- $label: Limit ${formatCurrency(budget.limitAmount)}\n")
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

    private fun drawDocumentHeader(
        canvas: Canvas,
        title: String,
        subtitle: String,
        badgeText: String? = null,
        colorPrimary: Int,
        colorDark: Int,
        colorMuted: Int,
        colorOnPrimary: Int
    ) {
        val brandBarPaint = Paint().apply {
            color = colorPrimary
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        // Top accent line at 40f..43.5f
        canvas.drawRoundRect(40f, 40f, 555f, 43.5f, 2f, 2f, brandBarPaint)

        val titlePaint = Paint().apply {
            color = colorDark
            textSize = 17f
            isFakeBoldText = true
            isAntiAlias = true
        }
        // Baseline at 68f -> text height ~15f, top at ~53f, safely 9.5f below 43.5f! No overlap!
        canvas.drawText(title, 40f, 68f, titlePaint)

        if (badgeText != null) {
            val titleWidth = titlePaint.measureText(title)
            val badgeX = 40f + titleWidth + 12f
            val badgePaint = Paint().apply {
                color = colorPrimary
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            val badgeTextPaint = Paint().apply {
                color = colorOnPrimary
                textSize = 7.5f
                isFakeBoldText = true
                isAntiAlias = true
            }
            val badgeTextWidth = badgeTextPaint.measureText(badgeText)
            canvas.drawRoundRect(badgeX, 55f, badgeX + badgeTextWidth + 14f, 70f, 4f, 4f, badgePaint)
            canvas.drawText(badgeText, badgeX + 7f, 65.5f, badgeTextPaint)
        }

        val subTitlePaint = Paint().apply {
            color = colorMuted
            textSize = 8.5f
            isAntiAlias = true
        }
        canvas.drawText(subtitle, 40f, 83f, subTitlePaint)

        val dividerPaint = Paint().apply {
            color = android.graphics.Color.rgb(226, 232, 240)
            strokeWidth = 0.8f
        }
        canvas.drawLine(40f, 95f, 555f, 95f, dividerPaint)
    }

    private fun createPdfDocumentBytes(
        dateStr: String,
        txs: List<Transaction>,
        budgets: List<Budget>
    ): ByteArray {
        val pdfDocument = PdfDocument()
        val isEn = _appLanguage.value == "en"
        
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        
        // --- COLOR PALETTE ---
        val colorPrimary = android.graphics.Color.rgb(79, 70, 229)    // Indigo #4F46E5
        val colorOnPrimary = android.graphics.Color.WHITE
        val colorDark = android.graphics.Color.rgb(15, 23, 42)        // Slate 900 #0F172A
        val colorMuted = android.graphics.Color.rgb(100, 116, 139)    // Slate 500 #64748B
        val colorBg = android.graphics.Color.rgb(248, 250, 252)       // Slate 50 #F8FAFC
        val colorBorder = android.graphics.Color.rgb(226, 232, 240)   // Slate 200 #E2E8F0
        
        val colorSuccess = android.graphics.Color.rgb(16, 185, 129)   // Emerald 500 #10B981
        val colorSuccessBg = android.graphics.Color.rgb(240, 253, 250) // Emerald 50
        val colorSuccessBorder = android.graphics.Color.rgb(209, 250, 229) // Emerald 100
        
        val colorDanger = android.graphics.Color.rgb(239, 68, 68)     // Red 500 #EF4444
        val colorDangerBg = android.graphics.Color.rgb(254, 242, 242)   // Red 50
        val colorDangerBorder = android.graphics.Color.rgb(254, 226, 226) // Red 100

        // --- PAINTS ---
        val brandBarPaint = Paint().apply {
            color = colorPrimary
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = colorMuted
            textSize = 8.5f
            isAntiAlias = true
        }
        
        val headerPaint = Paint().apply {
            color = colorDark
            textSize = 11.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val sectionLabelPaint = Paint().apply {
            color = colorPrimary
            textSize = 8f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = colorDark
            textSize = 9f
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = colorDark
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val greenTextPaint = Paint().apply {
            color = colorSuccess
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val greenValueLargePaint = Paint().apply {
            color = colorSuccess
            textSize = 13.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val redTextPaint = Paint().apply {
            color = colorDanger
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val redValueLargePaint = Paint().apply {
            color = colorDanger
            textSize = 13.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val gridPaint = Paint().apply {
            color = colorBorder
            strokeWidth = 0.8f
            style = Paint.Style.STROKE
        }

        val cardBgPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }

        val currencyFormat = object {
            fun format(amt: Double): String = formatCurrency(amt)
        }

        // ==========================================
        // PAGE 1: OVERVIEW & FINANCIAL PROFILE
        // ==========================================
        drawDocumentHeader(
            canvas = canvas,
            title = "UANGKU",
            subtitle = if (isEn) "Personal Finance Assistant • Secure, Offline & Local" else "Asisten Cerdas Keuangan Pribadi • Aman, Mandiri & Lokal",
            badgeText = if (isEn) "OFFICIAL REPORT" else "LAPORAN RESMI",
            colorPrimary = colorPrimary,
            colorDark = colorDark,
            colorMuted = colorMuted,
            colorOnPrimary = colorOnPrimary
        )

        var y = 110f

        // 1. Metadata Box
        val metaBoxPaint = Paint().apply {
            color = colorBg
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(40f, y, 555f, y + 42f, 8f, 8f, metaBoxPaint)
        canvas.drawRoundRect(40f, y, 555f, y + 42f, 8f, 8f, gridPaint)
        
        canvas.drawText(if (isEn) "USER" else "PENGGUNA", 55f, y + 16f, sectionLabelPaint)
        canvas.drawText(_userName.value, 55f, y + 31f, boldTextPaint)
        
        canvas.drawText(if (isEn) "GENERATION DATE" else "TANGGAL GENERASI", 230f, y + 16f, sectionLabelPaint)
        canvas.drawText(dateStr, 230f, y + 31f, boldTextPaint)
        
        canvas.drawText(if (isEn) "SYSTEM INTEGRATION" else "INTEGRASI SISTEM", 410f, y + 16f, sectionLabelPaint)
        canvas.drawText("Uangku v2.2 (Lokal & Mandiri)", 410f, y + 31f, boldTextPaint)
        
        y += 56f

        // 2. Ringkasan Portfolio (3 Bento Cards)
        canvas.drawText(if (isEn) "PORTFOLIO SUMMARY" else "RINGKASAN PORTFOLIO", 40f, y, headerPaint)
        y += 8f
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 12f

        val cardY = y
        val cardHeight = 62f
        val cardW = 160f
        val cardGap = 17.5f
        
        // Card 1: Balance
        val balanceCardPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        val cardBorderPaint = Paint().apply {
            color = colorBorder
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val c1X = 40f
        canvas.drawRoundRect(c1X, cardY, c1X + cardW, cardY + cardHeight, 8f, 8f, balanceCardPaint)
        canvas.drawRoundRect(c1X, cardY, c1X + cardW, cardY + cardHeight, 8f, 8f, cardBorderPaint)
        
        val indicatorPaint = Paint().apply {
            color = colorPrimary
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(c1X, cardY, c1X + cardW, cardY + 4f, 8f, 8f, indicatorPaint)
        canvas.drawRect(c1X, cardY + 2f, c1X + cardW, cardY + 4f, indicatorPaint)
        
        canvas.drawText(if (isEn) "NET BALANCE" else "SALDO BERSIH", c1X + 14f, cardY + 22f, sectionLabelPaint)
        val balValueLargePaint = Paint().apply {
            color = colorDark
            textSize = 13.5f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(currencyFormat.format(totalBalance.value), c1X + 14f, cardY + 44f, balValueLargePaint)

        // Card 2: Income
        val incCardPaint = Paint().apply {
            color = colorSuccessBg
            style = Paint.Style.FILL
        }
        val incBorderPaint = Paint().apply {
            color = colorSuccessBorder
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val c2X = c1X + cardW + cardGap
        canvas.drawRoundRect(c2X, cardY, c2X + cardW, cardY + cardHeight, 8f, 8f, incCardPaint)
        canvas.drawRoundRect(c2X, cardY, c2X + cardW, cardY + cardHeight, 8f, 8f, incBorderPaint)
        val incIndicatorPaint = Paint().apply {
            color = colorSuccess
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(c2X, cardY, c2X + cardW, cardY + 4f, 8f, 8f, incIndicatorPaint)
        canvas.drawRect(c2X, cardY + 2f, c2X + cardW, cardY + 4f, incIndicatorPaint)
        
        val successLabelPaint = Paint().apply {
            color = colorSuccess
            textSize = 8f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(if (isEn) "TOTAL INCOME" else "TOTAL PEMASUKAN", c2X + 14f, cardY + 22f, successLabelPaint)
        canvas.drawText(currencyFormat.format(totalIncome.value), c2X + 14f, cardY + 44f, greenValueLargePaint)

        // Card 3: Expense
        val expCardPaint = Paint().apply {
            color = colorDangerBg
            style = Paint.Style.FILL
        }
        val expBorderPaint = Paint().apply {
            color = colorDangerBorder
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val c3X = c2X + cardW + cardGap
        canvas.drawRoundRect(c3X, cardY, c3X + cardW, cardY + cardHeight, 8f, 8f, expCardPaint)
        canvas.drawRoundRect(c3X, cardY, c3X + cardW, cardY + cardHeight, 8f, 8f, expBorderPaint)
        val expIndicatorPaint = Paint().apply {
            color = colorDanger
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(c3X, cardY, c3X + cardW, cardY + 4f, 8f, 8f, expIndicatorPaint)
        canvas.drawRect(c3X, cardY + 2f, c3X + cardW, cardY + 4f, expIndicatorPaint)
        
        val dangerLabelPaint = Paint().apply {
            color = colorDanger
            textSize = 8f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(if (isEn) "TOTAL EXPENSE" else "TOTAL PENGELUARAN", c3X + 14f, cardY + 22f, dangerLabelPaint)
        canvas.drawText(currencyFormat.format(totalExpense.value), c3X + 14f, cardY + 44f, redValueLargePaint)

        y += cardHeight + 18f

        // 3. Key Metrics & Financial Health Badges (4 Bento Badges)
        canvas.drawText(if (isEn) "KEY METRICS & CASH FLOW HEALTH" else "STATISTIK & KESEHATAN ARUS KAS", 40f, y, headerPaint)
        y += 8f
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 12f

        val totalIncVal = totalIncome.value
        val totalExpVal = totalExpense.value
        val savingsRate = if (totalIncVal > 0.0) ((totalIncVal - totalExpVal) / totalIncVal * 100).coerceAtLeast(0.0) else 0.0
        val topCatEntry = txs.filter { it.type == "EXPENSE" }.groupBy { it.category }.maxByOrNull { it.value.sumOf { tx -> tx.amount } }
        val topCatName = topCatEntry?.key ?: "-"
        val avgTxAmt = if (txs.isNotEmpty()) txs.map { it.amount }.average() else 0.0

        val metricCardW = 122f
        val metricCardH = 46f
        val metricGap = 9f

        val metricsList = listOf(
            Triple(if (isEn) "SAVINGS RATE" else "RASIO TABUNGAN", String.format(Locale.US, "%.1f%%", savingsRate), colorPrimary),
            Triple(if (isEn) "TOTAL TXS" else "TOTAL TRANSAKSI", "${txs.size} " + if (isEn) "tx" else "rekaman", colorDark),
            Triple(if (isEn) "AVG TX AMOUNT" else "RATA-RATA TRANSAKSI", currencyFormat.format(avgTxAmt), colorDark),
            Triple(if (isEn) "TOP CATEGORY" else "KATEGORI TERBESAR", topCatName, colorDanger)
        )

        metricsList.forEachIndexed { index, m ->
            val mx = 40f + index * (metricCardW + metricGap)
            val metricBgPaint = Paint().apply {
                color = colorBg
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(mx, y, mx + metricCardW, y + metricCardH, 6f, 6f, metricBgPaint)
            canvas.drawRoundRect(mx, y, mx + metricCardW, y + metricCardH, 6f, 6f, gridPaint)
            
            val mLabelPaint = Paint().apply {
                color = colorMuted
                textSize = 7.5f
                isFakeBoldText = true
                isAntiAlias = true
            }
            canvas.drawText(m.first, mx + 10f, y + 16f, mLabelPaint)
            
            val mValPaint = Paint().apply {
                color = m.third
                textSize = 10f
                isFakeBoldText = true
                isAntiAlias = true
            }
            var valShow = m.second
            if (valShow.length > 16) {
                valShow = valShow.substring(0, 14) + ".."
            }
            canvas.drawText(valShow, mx + 10f, y + 33f, mValPaint)
        }

        y += metricCardH + 18f

        // 4. Limits and Budgets section
        canvas.drawText(if (isEn) "MONTHLY BUDGETS & LIMITS" else "LIMIT & ANGGARAN BULANAN", 40f, y, headerPaint)
        y += 8f
        canvas.drawLine(40f, y, 555f, y, gridPaint)
        y += 12f

        if (budgets.isEmpty()) {
            val emptyBoxPaint = Paint().apply {
                color = colorBg
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(40f, y, 555f, y + 38f, 6f, 6f, emptyBoxPaint)
            canvas.drawRoundRect(40f, y, 555f, y + 38f, 6f, 6f, gridPaint)
            canvas.drawText(
                if (isEn) "No monthly budget limits defined yet." else "Belum ada batasan anggaran bulanan yang ditetapkan.",
                60f, y + 23f, subTitlePaint
            )
            y += 50f
        } else {
            val budgetHeaderPaint = Paint().apply {
                color = colorBg
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(40f, y, 555f, y + 22f, 6f, 6f, budgetHeaderPaint)
            canvas.drawRoundRect(40f, y, 555f, y + 22f, 6f, 6f, gridPaint)
            
            canvas.drawText(if (isEn) "CATEGORY" else "KATEGORI", 55f, y + 15f, boldTextPaint)
            canvas.drawText(if (isEn) "MONTHLY BUDGET LIMIT" else "LIMIT ANGGARAN BULANAN", 230f, y + 15f, boldTextPaint)
            canvas.drawText(if (isEn) "STATUS" else "STATUS KEAKTIFAN", 420f, y + 15f, boldTextPaint)
            y += 28f

            for (b in budgets.take(4)) {
                val dotPaint = Paint().apply {
                    color = colorPrimary
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawCircle(50f, y - 3f, 3f, dotPaint)
                canvas.drawText(b.category, 65f, y, boldTextPaint)
                canvas.drawText(currencyFormat.format(b.limitAmount) + if (isEn) " / mo" else " / bulan", 230f, y, textPaint)
                
                val badgeBgPaint = Paint().apply {
                    color = colorSuccessBg
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawRoundRect(420f, y - 9f, 485f, y + 5f, 4f, 4f, badgeBgPaint)
                val badgeTextPaint = Paint().apply {
                    color = colorSuccess
                    textSize = 7.5f
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                canvas.drawText(if (isEn) "ACTIVE" else "AKTIF", 435f, y + 1.5f, badgeTextPaint)
                
                y += 20f
                canvas.drawLine(40f, y - 13f, 555f, y - 13f, gridPaint)
            }
            y += 8f
        }

        // 5. Automated Financial Health Evaluation Card (fills the rest of Page 1 harmoniously)
        val execCardY = y
        val execCardH = 92f
        canvas.drawRoundRect(40f, execCardY, 555f, execCardY + execCardH, 8f, 8f, cardBgPaint)
        canvas.drawRoundRect(40f, execCardY, 555f, execCardY + execCardH, 8f, 8f, gridPaint)
        canvas.drawRoundRect(40f, execCardY, 44f, execCardY + execCardH, 2f, 2f, brandBarPaint)

        canvas.drawText(
            if (isEn) "AUTOMATED FINANCIAL HEALTH EVALUATION" else "EVALUASI KESEHATAN FINANSIAL OTOMATIS",
            55f, execCardY + 20f, boldTextPaint
        )

        val cashflowNote = if (totalIncVal >= totalExpVal) {
            if (isEn) "• Positive cash flow: Total income comfortably covers operational expenses."
            else "• Arus kas tercatat positif: Pemasukan mampu menutup pos pengeluaran secara sehat."
        } else {
            if (isEn) "• Attention needed: Expenses currently exceed income. Monitor discretionary spending."
            else "• Perhatian: Total pengeluaran melebihi pemasukan. Pantau alokasi belanja diskresioner."
        }
        val budgetNote = if (isEn) {
            "• Savings health: ${String.format(Locale.US, "%.1f%%", savingsRate)} of total income is retained for wealth preservation and emergency reserves."
        } else {
            "• Kesehatan tabungan: ${String.format(Locale.US, "%.1f%%", savingsRate)} dari pemasukan berhasil dialokasikan untuk tabungan mandiri."
        }
        val securityNote = if (isEn) {
            "• Zero cloud telemetry: All data processing is strictly executed on device local encrypted storage."
        } else {
            "• Privasi penuh: Seluruh komputasi transaksi dijalankan 100% lokal terenkripsi di perangkat Anda."
        }

        canvas.drawText(cashflowNote, 55f, execCardY + 38f, subTitlePaint)
        canvas.drawText(budgetNote, 55f, execCardY + 54f, subTitlePaint)
        canvas.drawText(securityNote, 55f, execCardY + 70f, subTitlePaint)

        styleFooter(canvas, pageNumber, isEn)
        pdfDocument.finishPage(page)

        // ==========================================
        // PAGE 2: VISUAL CHARTS & TREND ANALYTICS
        // ==========================================
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas

        drawDocumentHeader(
            canvas = canvas,
            title = if (isEn) "FINANCIAL TRENDS & VISUALIZATION" else "VISUALISASI & TREN KEUANGAN",
            subtitle = if (isEn) "Comparative graphical analysis and category spending breakdown" else "Analisis perbandingan grafis dan pola pengeluaran kategori",
            badgeText = null,
            colorPrimary = colorPrimary,
            colorDark = colorDark,
            colorMuted = colorMuted,
            colorOnPrimary = colorOnPrimary
        )

        y = 110f

        // Chart 1: Bar Chart
        canvas.drawText(if (isEn) "1. Monthly Cashflow Trend (Income vs Expense)" else "1. Tren Aliran Kas Bulanan (Pemasukan vs Pengeluaran)", 40f, y, boldTextPaint)
        y += 12f
        
        // Group by month
        val monthFormat = SimpleDateFormat("MMM yy", if (isEn) Locale.US else Locale("id", "ID"))
        val monthlyGroups = txs.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)
        }.entries.sortedBy { it.key }.takeLast(5)

        val chartData = monthlyGroups.map { entry ->
            val label = if (entry.value.isNotEmpty()) {
                monthFormat.format(java.util.Date(entry.value.first().timestamp))
            } else ""
            val income = entry.value.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expense = entry.value.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            Triple(label, income, expense)
        }

        val barCardHeight = 210f
        canvas.drawRoundRect(40f, y, 555f, y + barCardHeight, 10f, 10f, cardBgPaint)
        canvas.drawRoundRect(40f, y, 555f, y + barCardHeight, 10f, 10f, gridPaint)

        if (chartData.isEmpty()) {
            canvas.drawText(if (isEn) "No monthly transaction records available for charting." else "Belum ada data transaksi bulanan untuk grafik.", 60f, y + 105f, subTitlePaint)
        } else {
            val maxVal = chartData.map { maxOf(it.second, it.third) }.maxOrNull() ?: 1.0
            val scaleMax = if (maxVal <= 0.0) 1.0 else maxVal
            
            val baselineY = y + 165f
            val chartHeight = 120f
            
            // Draw grid lines inside chart
            val dashPaint = Paint().apply {
                color = colorBorder
                strokeWidth = 0.8f
            }
            canvas.drawLine(90f, baselineY, 535f, baselineY, dashPaint)
            canvas.drawLine(90f, baselineY - chartHeight * 0.5f, 535f, baselineY - chartHeight * 0.5f, dashPaint)
            canvas.drawLine(90f, baselineY - chartHeight, 535f, baselineY - chartHeight, dashPaint)

            // Draw Y-axis labels
            val axisLabelPaint = Paint().apply {
                color = colorMuted
                textSize = 7.5f
                isAntiAlias = true
            }
            canvas.drawText("0", 48f, baselineY + 3f, axisLabelPaint)
            canvas.drawText(currencyFormat.format(scaleMax * 0.5).replace(",00", ""), 48f, baselineY - chartHeight * 0.5f + 3f, axisLabelPaint)
            canvas.drawText(currencyFormat.format(scaleMax).replace(",00", ""), 48f, baselineY - chartHeight + 3f, axisLabelPaint)

            // Draw Legends
            val legendGreenPaint = Paint().apply { color = colorSuccess; style = Paint.Style.FILL; isAntiAlias = true }
            val legendRedPaint = Paint().apply { color = colorDanger; style = Paint.Style.FILL; isAntiAlias = true }
            
            canvas.drawRoundRect(390f, y + 12f, 400f, y + 22f, 2f, 2f, legendGreenPaint)
            canvas.drawText(if (isEn) "Income" else "Pemasukan", 405f, y + 20f, axisLabelPaint)
            
            canvas.drawRoundRect(470f, y + 12f, 480f, y + 22f, 2f, 2f, legendRedPaint)
            canvas.drawText(if (isEn) "Expense" else "Pengeluaran", 485f, y + 20f, axisLabelPaint)

            val numMonths = chartData.size
            val startX = 110f
            val endX = 530f
            val stepX = (endX - startX) / numMonths.coerceAtLeast(1)

            val barGreenPaint = Paint().apply {
                color = colorSuccess
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            val barRedPaint = Paint().apply {
                color = colorDanger
                style = Paint.Style.FILL
                isAntiAlias = true
            }

            chartData.forEachIndexed { i, data ->
                val cx = startX + i * stepX + stepX / 2f
                
                // Income Bar
                val incHeight = ((data.second / scaleMax) * chartHeight).toFloat()
                if (incHeight > 0f) {
                    canvas.drawRoundRect(cx - 16f, baselineY - incHeight, cx - 3f, baselineY, 3f, 3f, barGreenPaint)
                    if (incHeight > 4f) {
                        canvas.drawRect(cx - 16f, baselineY - 4f, cx - 3f, baselineY, barGreenPaint)
                    }
                }
                
                // Expense Bar
                val expHeight = ((data.third / scaleMax) * chartHeight).toFloat()
                if (expHeight > 0f) {
                    canvas.drawRoundRect(cx + 3f, baselineY - expHeight, cx + 16f, baselineY, 3f, 3f, barRedPaint)
                    if (expHeight > 4f) {
                        canvas.drawRect(cx + 3f, baselineY - 4f, cx + 16f, baselineY, barRedPaint)
                    }
                }
                
                // Month Label
                canvas.drawText(data.first, cx - 16f, baselineY + 18f, boldTextPaint)
            }
        }
        
        y += barCardHeight + 20f

        // Chart 2: Donut Chart
        canvas.drawText(if (isEn) "2. Expense Allocation by Category" else "2. Alokasi Pengeluaran Berdasarkan Kategori", 40f, y, boldTextPaint)
        y += 12f

        val expenseTxs = txs.filter { it.type == "EXPENSE" }
        val totalExpenseAmt = expenseTxs.sumOf { it.amount }
        val catGroups = expenseTxs.groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
            .entries.sortedByDescending { it.value }

        val donutCardHeight = 210f
        canvas.drawRoundRect(40f, y, 555f, y + donutCardHeight, 10f, 10f, cardBgPaint)
        canvas.drawRoundRect(40f, y, 555f, y + donutCardHeight, 10f, 10f, gridPaint)

        if (totalExpenseAmt <= 0.0 || catGroups.isEmpty()) {
            canvas.drawText(if (isEn) "No expense records available for category breakdown." else "Belum ada data pengeluaran untuk diagram kategori.", 60f, y + 105f, subTitlePaint)
        } else {
            val topCats = catGroups.take(4)
            val otherSum = if (catGroups.size > 4) catGroups.drop(4).sumOf { it.value } else 0.0
            val donutData = topCats.map { Pair(it.key, it.value) }.toMutableList()
            if (otherSum > 0.0) {
                donutData.add(Pair(if (isEn) "Others" else "Lainnya", otherSum))
            }

            val cx = 140f
            val cy = y + 105f
            val radius = 66f
            val rectF = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

            var startAngle = -90f
            donutData.forEachIndexed { idx, pair ->
                val pct = (pair.second / totalExpenseAmt).toFloat()
                val sweepAngle = pct * 360f
                
                val slicePaint = Paint().apply {
                    color = when (idx) {
                        0 -> android.graphics.Color.rgb(99, 102, 241)  // Indigo #6366F1
                        1 -> android.graphics.Color.rgb(14, 165, 233)  // Light Blue #0EA5E9
                        2 -> android.graphics.Color.rgb(236, 72, 153)  // Pink #EC4899
                        3 -> android.graphics.Color.rgb(245, 158, 11)   // Amber #F59E0B
                        else -> android.graphics.Color.rgb(148, 163, 184) // Slate 400
                    }
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                
                canvas.drawArc(rectF, startAngle, sweepAngle, true, slicePaint)
                startAngle += sweepAngle
            }

            // Draw White Inner Circle (Donut Hole)
            val whitePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawCircle(cx, cy, 38f, whitePaint)

            // Draw Legends on the Right
            val legendX = 245f
            donutData.forEachIndexed { idx, pair ->
                val legendY = y + 36f + idx * 32f
                
                val markerPaint = Paint().apply {
                    color = when (idx) {
                        0 -> android.graphics.Color.rgb(99, 102, 241)
                        1 -> android.graphics.Color.rgb(14, 165, 233)
                        2 -> android.graphics.Color.rgb(236, 72, 153)
                        3 -> android.graphics.Color.rgb(245, 158, 11)
                        else -> android.graphics.Color.rgb(148, 163, 184)
                    }
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawRoundRect(legendX, legendY - 8f, legendX + 12f, legendY + 4f, 3f, 3f, markerPaint)

                val pctText = String.format(Locale.US, "%.1f%%", (pair.second / totalExpenseAmt) * 100)
                canvas.drawText("${pair.first} ($pctText)", legendX + 20f, legendY, boldTextPaint)
                canvas.drawText(currencyFormat.format(pair.second), legendX + 20f, legendY + 12f, subTitlePaint)
            }
        }
        
        y += donutCardHeight + 20f

        // 3. Spending Trends Takeaways (Fills bottom of Page 2)
        val takeawayH = 75f
        canvas.drawRoundRect(40f, y, 555f, y + takeawayH, 8f, 8f, metaBoxPaint)
        canvas.drawRoundRect(40f, y, 555f, y + takeawayH, 8f, 8f, gridPaint)

        val t1Label = if (isEn) "PEAK SPENDING CATEGORY" else "KATEGORI PENGELUARAN TERBESAR"
        val t1Val = if (catGroups.isNotEmpty()) "${catGroups.first().key} (${currencyFormat.format(catGroups.first().value)})" else "-"
        
        val t2Label = if (isEn) "MONTHS RECORDED" else "BULAN AKTIF TERCATAT"
        val t2Val = "${chartData.size} " + if (isEn) "active period(s)" else "periode aktif"

        val t3Label = if (isEn) "NET MONTHLY FLOW" else "ARUS KAS BULAN BERJALAN"
        val t3Val = currencyFormat.format(currentMonthIncome.value - currentMonthExpense.value)

        canvas.drawText(t1Label, 55f, y + 20f, sectionLabelPaint)
        canvas.drawText(t1Val, 55f, y + 36f, boldTextPaint)

        canvas.drawText(t2Label, 240f, y + 20f, sectionLabelPaint)
        canvas.drawText(t2Val, 240f, y + 36f, boldTextPaint)

        canvas.drawText(t3Label, 410f, y + 20f, sectionLabelPaint)
        canvas.drawText(t3Val, 410f, y + 36f, if (currentMonthIncome.value >= currentMonthExpense.value) greenTextPaint else redTextPaint)

        canvas.drawText(
            if (isEn) "• Systematic tracking helps optimize future budgeting limits and prevents impulsive outlays."
            else "• Pencatatan mutasi berkala mempermudah optimalisasi limit bulanan dan mencegah pengeluaran impulsif.",
            55f, y + 58f, subTitlePaint
        )

        styleFooter(canvas, pageNumber, isEn)
        pdfDocument.finishPage(page)

        // ==========================================
        // PAGE 3: TRANSACTION HISTORY
        // ==========================================
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas

        drawDocumentHeader(
            canvas = canvas,
            title = if (isEn) "RECENT TRANSACTION HISTORY" else "RIWAYAT TRANSAKSI TERBARU",
            subtitle = if (isEn) "Recent financial transactions recorded on your device" else "Daftar mutasi keuangan dan rekaman transaksi terbaru di perangkat Anda",
            badgeText = null,
            colorPrimary = colorPrimary,
            colorDark = colorDark,
            colorMuted = colorMuted,
            colorOnPrimary = colorOnPrimary
        )

        y = 110f

        // Table Header
        val tableHeaderBgPaint = Paint().apply {
            color = colorDark
            style = Paint.Style.FILL
        }
        val tableHeaderLabelPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        canvas.drawRoundRect(40f, y, 555f, y + 24f, 6f, 6f, tableHeaderBgPaint)
        
        canvas.drawText(if (isEn) "DATE" else "TANGGAL", 55f, y + 16f, tableHeaderLabelPaint)
        canvas.drawText(if (isEn) "CATEGORY" else "KATEGORI", 140f, y + 16f, tableHeaderLabelPaint)
        canvas.drawText(if (isEn) "TRANSACTION NAME" else "NAMA TRANSAKSI", 240f, y + 16f, tableHeaderLabelPaint)
        canvas.drawText(if (isEn) "AMOUNT" else "JUMLAH", 465f, y + 16f, tableHeaderLabelPaint)
        y += 24f

        val listFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val rowHeight = 22f
        
        val zebraEvenPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        val zebraOddPaint = Paint().apply {
            color = colorBg
            style = Paint.Style.FILL
        }

        val rowBorderPaint = Paint().apply {
            color = colorBorder
            strokeWidth = 0.6f
        }

        txs.take(22).forEachIndexed { index, tx ->
            val dateStrTx = listFormat.format(java.util.Date(tx.timestamp))
            val amountStr = (if (tx.type == "INCOME") "+" else "-") + currencyFormat.format(tx.amount)
            val isIncome = tx.type == "INCOME"

            val currentBgPaint = if (index % 2 == 0) zebraEvenPaint else zebraOddPaint
            canvas.drawRect(40f, y, 555f, y + rowHeight, currentBgPaint)
            
            canvas.drawText(dateStrTx, 55f, y + 15f, textPaint)
            canvas.drawText(tx.category, 140f, y + 15f, boldTextPaint)
            
            var titleShow = tx.getCleanTitle()
            if (titleShow.length > 30) {
                titleShow = titleShow.substring(0, 27) + "..."
            }
            canvas.drawText(titleShow, 240f, y + 15f, textPaint)
            
            canvas.drawText(amountStr, 465f, y + 15f, if (isIncome) greenTextPaint else redTextPaint)
            y += rowHeight

            canvas.drawLine(40f, y, 555f, y, rowBorderPaint)

            if (y > 720f && index < txs.take(22).size - 1) {
                styleFooter(canvas, pageNumber, isEn)
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas

                drawDocumentHeader(
                    canvas = canvas,
                    title = if (isEn) "RECENT TRANSACTION HISTORY (CONT.)" else "RIWAYAT TRANSAKSI TERBARU (LANJUTAN)",
                    subtitle = if (isEn) "Continued records of financial transactions" else "Lanjutan rekaman mutasi transaksi keuangan Anda",
                    badgeText = null,
                    colorPrimary = colorPrimary,
                    colorDark = colorDark,
                    colorMuted = colorMuted,
                    colorOnPrimary = colorOnPrimary
                )
                y = 110f
                canvas.drawRoundRect(40f, y, 555f, y + 24f, 6f, 6f, tableHeaderBgPaint)
                canvas.drawText(if (isEn) "DATE" else "TANGGAL", 55f, y + 16f, tableHeaderLabelPaint)
                canvas.drawText(if (isEn) "CATEGORY" else "KATEGORI", 140f, y + 16f, tableHeaderLabelPaint)
                canvas.drawText(if (isEn) "TRANSACTION NAME" else "NAMA TRANSAKSI", 240f, y + 16f, tableHeaderLabelPaint)
                canvas.drawText(if (isEn) "AMOUNT" else "JUMLAH", 465f, y + 16f, tableHeaderLabelPaint)
                y += 24f
            }
        }

        y += 14f
        if (y > 710f) {
            styleFooter(canvas, pageNumber, isEn)
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            drawDocumentHeader(
                canvas = canvas,
                title = if (isEn) "DATA SECURITY & VERIFICATION" else "KEAMANAN & VERIFIKASI DATA",
                subtitle = if (isEn) "Local integrity and encryption protocol" else "Protokol enkripsi dan integritas penyimpanan lokal",
                badgeText = null,
                colorPrimary = colorPrimary,
                colorDark = colorDark,
                colorMuted = colorMuted,
                colorOnPrimary = colorOnPrimary
            )
            y = 110f
        }
        
        val closingBgPaint = Paint().apply {
            color = colorBg
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(40f, y, 555f, y + 46f, 8f, 8f, closingBgPaint)
        canvas.drawRoundRect(40f, y, 555f, y + 46f, 8f, 8f, gridPaint)
        
        val disclaimerHeaderPaint = Paint().apply {
            color = colorPrimary
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val disclaimerTextPaint = Paint().apply {
            color = colorMuted
            textSize = 8.5f
            isAntiAlias = true
        }
        
        canvas.drawText(if (isEn) "DATA PRIVACY & LOCAL SECURITY" else "INFORMASI KEAMANAN DATA", 55f, y + 18f, disclaimerHeaderPaint)
        canvas.drawText(
            if (isEn) "This report was generated directly on your device by Uangku. All transactions"
            else "Laporan ini diunduh secara instan dari aplikasi Uangku. Semua data transaksi Anda",
            55f, y + 30f, disclaimerTextPaint
        )
        canvas.drawText(
            if (isEn) "are stored securely on encrypted local device memory and are never uploaded to any cloud."
            else "disimpan di penyimpanan lokal perangkat yang terenkripsi dan tidak pernah diunggah ke cloud.",
            55f, y + 40f, disclaimerTextPaint
        )

        styleFooter(canvas, pageNumber, isEn)
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

    private fun styleFooter(canvas: Canvas, pageIndex: Int, isEn: Boolean = false) {
        val linePaint = Paint().apply {
            color = android.graphics.Color.rgb(226, 232, 240) // Slate 200
            strokeWidth = 0.8f
        }
        canvas.drawLine(40f, 800f, 555f, 800f, linePaint)
        
        val paint = Paint().apply {
            color = android.graphics.Color.rgb(148, 163, 184) // Slate 400
            textSize = 8f
            isAntiAlias = true
        }
        val appTitle = if (isEn) "Uangku - Automated Personal Finance Report" else "Uangku - Laporan Keuangan Otomatis Asisten Cerdas"
        val pageText = if (isEn) "Page $pageIndex" else "Halaman $pageIndex"
        canvas.drawText(appTitle, 40f, 814f, paint)
        canvas.drawText(pageText, 505f, 814f, paint)
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

    // --- Gamifikasi & Motivasi Menabung (Goals Booster) & Local Reminders ---
    private val _savingChallenges = MutableStateFlow<List<com.example.data.model.SavingChallenge>>(emptyList())
    val savingChallenges = _savingChallenges.asStateFlow()

    private val _virtualBadges = MutableStateFlow<List<com.example.data.model.VirtualBadge>>(emptyList())
    val virtualBadges = _virtualBadges.asStateFlow()

    private val _showBadgeUnlockDialog = MutableStateFlow<com.example.data.model.VirtualBadge?>(null)
    val showBadgeUnlockDialog = _showBadgeUnlockDialog.asStateFlow()

    fun loadSavingChallengesAndBadges() {
        val challengesSeed = listOf(
            com.example.data.model.SavingChallenge(
                id = "jumat_berkah",
                title = "Tantangan Jumat Berkah",
                description = "Membiasakan diri menyisihkan uang saku/gaji kecil setiap hari Jumat untuk keberkahan finansial.",
                targetAmount = 100000.0,
                amountPerCheckIn = 10000.0,
                currentProgress = prefs.getInt("challenge_progress_jumat_berkah", 0),
                targetProgress = 10,
                scheduleText = "Setiap Hari Jumat"
            ),
            com.example.data.model.SavingChallenge(
                id = "anti_jajan",
                title = "Anti Jajan Kopi Boba",
                description = "Tantangan disiplin mengurangi jajan harian kopi boba/snack manis di hari kerja.",
                targetAmount = 100000.0,
                amountPerCheckIn = 20000.0,
                currentProgress = prefs.getInt("challenge_progress_anti_jajan", 0),
                targetProgress = 5,
                scheduleText = "Senin - Kamis"
            ),
            com.example.data.model.SavingChallenge(
                id = "akhir_pekan",
                title = "Pemberantas Impulsif",
                description = "Melawan keinginan belanja online atau checkout impulsif saat bersantai di akhir pekan.",
                targetAmount = 200000.0,
                amountPerCheckIn = 50000.0,
                currentProgress = prefs.getInt("challenge_progress_akhir_pekan", 0),
                targetProgress = 4,
                scheduleText = "Sabtu & Minggu"
            ),
            com.example.data.model.SavingChallenge(
                id = "receh_disiplin",
                title = "Tantangan Rp5.000 Receh",
                description = "Melatih konsistensi menabung dari nominal kecil harian secara terus menerus selama 20 hari.",
                targetAmount = 100000.0,
                amountPerCheckIn = 5000.0,
                currentProgress = prefs.getInt("challenge_progress_receh_disiplin", 0),
                targetProgress = 20,
                scheduleText = "Setiap Hari"
            )
        )

        val challenges = challengesSeed.map { ch ->
            if (ch.currentProgress >= ch.targetProgress) {
                ch.copy(status = "COMPLETED")
            } else {
                ch
            }
        }
        _savingChallenges.value = challenges

        val totalChallengeSavings = challenges.sumOf { it.currentProgress * it.amountPerCheckIn }
        val totalCheckIns = challenges.sumOf { it.currentProgress }

        val badgesSeed = listOf(
            com.example.data.model.VirtualBadge(
                id = "badge_first_checkin",
                name = "Prajurit Hemat",
                description = "Selesaikan check-in pertama di tantangan apa saja untuk memulai.",
                icon = "🛡️",
                isUnlocked = totalCheckIns >= 1,
                unlockProgressText = "Selesai $totalCheckIns/1"
            ),
            com.example.data.model.VirtualBadge(
                id = "badge_friday",
                name = "Penakluk Jumat",
                description = "Selesaikan penuh tantangan menabung Jumat Berkah.",
                icon = "🎯",
                isUnlocked = prefs.getInt("challenge_progress_jumat_berkah", 0) >= 10,
                unlockProgressText = "Progress " + prefs.getInt("challenge_progress_jumat_berkah", 0) + "/10"
            ),
            com.example.data.model.VirtualBadge(
                id = "badge_saving_guru",
                name = "Guru Menabung",
                description = "Selesaikan minimal 1 tantangan menabung secara penuh.",
                icon = "🏆",
                isUnlocked = challenges.any { it.status == "COMPLETED" },
                unlockProgressText = if (challenges.any { it.status == "COMPLETED" }) "Selesai!" else "Belum ada tantangan selesai"
            ),
            com.example.data.model.VirtualBadge(
                id = "badge_financial_sultan",
                name = "Sultan Sadar Finansial",
                description = "Miliki akumulasi tabungan tantangan interaktif di atas Rp100.000.",
                icon = "👑",
                isUnlocked = totalChallengeSavings >= 100000.0,
                unlockProgressText = "Tersimpan Rp" + String.format(Locale("id", "ID"), "%,.0f", totalChallengeSavings).replace(",", ".") + "/100.000"
            ),
            com.example.data.model.VirtualBadge(
                id = "badge_consistency",
                name = "Konsistensi Emas",
                description = "Selesaikan total 5 kali check-in pada tantangan menabung apa saja.",
                icon = "🌟",
                isUnlocked = totalCheckIns >= 5,
                unlockProgressText = "Selesai $totalCheckIns/5"
            )
        )

        _virtualBadges.value = badgesSeed
    }

    fun checkInChallenge(challengeId: String, context: Context) {
        val currentVal = prefs.getInt("challenge_progress_$challengeId", 0)
        val ch = _savingChallenges.value.find { it.id == challengeId } ?: return
        
        if (currentVal >= ch.targetProgress) {
            Toast.makeText(context, "Tantangan ini sudah selesai!", Toast.LENGTH_SHORT).show()
            return
        }

        val newVal = currentVal + 1
        prefs.edit().putInt("challenge_progress_$challengeId", newVal).apply()

        viewModelScope.launch {
            val transaction = Transaction(
                title = "Tantangan Menabung: ${ch.title} #${newVal}",
                amount = ch.amountPerCheckIn,
                type = "INCOME",
                category = "Investasi"
            )
            repository.insertTransaction(transaction)
            
            loadSavingChallengesAndBadges()
            
            val updatedBadges = _virtualBadges.value
            for (badge in updatedBadges) {
                val previouslyUnlocked = prefs.getBoolean("badge_unlocked_${badge.id}", false)
                if (badge.isUnlocked && !previouslyUnlocked) {
                    prefs.edit().putBoolean("badge_unlocked_${badge.id}", true).apply()
                    _showBadgeUnlockDialog.value = badge
                    
                    sendLocalNotification(
                        context = context,
                        notificationId = 9999 + badge.hashCode(),
                        title = "🏆 Lencana Baru Terbuka!",
                        message = "Selamat! Anda mendapatkan lencana '${badge.name}' (${badge.icon}) - ${badge.description}"
                    )
                }
            }
            
            Toast.makeText(context, "Berhasil Check-in! Rp" + String.format(Locale("id", "ID"), "%,.0f", ch.amountPerCheckIn).replace(",", ".") + " berhasil ditambahkan.", Toast.LENGTH_SHORT).show()
            triggerAutoSync()
        }
    }

    fun dismissBadgeUnlockDialog() {
        _showBadgeUnlockDialog.value = null
    }

    fun extractDayFromDueDate(dueDate: String): Int? {
        val digits = dueDate.filter { it.isDigit() }
        return digits.toIntOrNull()?.coerceIn(1, 31)
    }

    fun checkAndTriggerBillReminders(context: Context) {
        viewModelScope.launch {
            val bills = repository.allRecurringBills.first()
            val calendar = Calendar.getInstance()
            val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
            
            for (bill in bills) {
                val dueDay = extractDayFromDueDate(bill.dueDate) ?: continue
                val diff = dueDay - todayDay
                
                if (diff == 1) {
                    sendNotificationIfNeeded(context, bill.id * 10 + 1, diff, "Pengingat Tagihan (H-1)", "Tagihan '${bill.title}' sebesar Rp" + String.format(Locale("id", "ID"), "%,.0f", bill.amount).replace(",", ".") + " jatuh tempo BESOK!")
                } else if (diff == 3) {
                    sendNotificationIfNeeded(context, bill.id * 10 + 3, diff, "Pengingat Tagihan (H-3)", "Tagihan '${bill.title}' sebesar Rp" + String.format(Locale("id", "ID"), "%,.0f", bill.amount).replace(",", ".") + " jatuh tempo dalam 3 hari.")
                } else if (diff == 0) {
                    sendNotificationIfNeeded(context, bill.id * 10, diff, "Tagihan Jatuh Tempo HARI INI", "Tagihan '${bill.title}' sebesar Rp" + String.format(Locale("id", "ID"), "%,.0f", bill.amount).replace(",", ".") + " harus dibayar hari ini!")
                }
            }
        }
    }

    private fun sendNotificationIfNeeded(context: Context, notificationId: Int, diff: Int, title: String, message: String) {
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val key = "notified_${notificationId}_${diff}_${todayStr}"
        
        if (!prefs.getBoolean(key, false)) {
            sendLocalNotification(context, notificationId, title, message)
            prefs.edit().putBoolean(key, true).apply()
        }
    }

    fun sendLocalNotification(context: Context, notificationId: Int, title: String, message: String) {
        val channelId = "uangku_bill_reminders"
        val channelName = "Pengingat Uangku"
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, channelName, android.app.NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Saluran untuk pengingat tagihan bulanan dan tantangan menabung"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = android.content.Intent(context, com.example.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT else android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFourMonthsSummary(type: String): List<FourMonthsSummaryItem> {
        val txs = allTransactions.value
        val result = mutableListOf<FourMonthsSummaryItem>()
        val locale = if (isEnglish()) Locale.US else Locale("id", "ID")
        val monthFormat = SimpleDateFormat("MMMM yyyy", locale)
        val shortFormat = SimpleDateFormat("MMM", locale)
        val now = System.currentTimeMillis()

        for (i in 0 until 4) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.MONTH, -i)
            }
            val targetYear = cal.get(Calendar.YEAR)
            val targetMonth = cal.get(Calendar.MONTH)

            val matching = txs.filter { tx ->
                if (tx.type != type) return@filter false
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                txCal.get(Calendar.YEAR) == targetYear && txCal.get(Calendar.MONTH) == targetMonth
            }

            val total = matching.sumOf { it.amount }
            val catMap = matching.groupBy { it.category }
                .mapValues { it.value.sumOf { tx -> tx.amount } }
            val topCat = catMap.maxByOrNull { it.value }

            result.add(
                FourMonthsSummaryItem(
                    monthName = monthFormat.format(cal.time),
                    shortMonthName = shortFormat.format(cal.time),
                    year = targetYear,
                    monthIndex = targetMonth,
                    totalAmount = total,
                    transactionCount = matching.size,
                    transactions = matching.sortedByDescending { it.timestamp },
                    topCategory = topCat?.key,
                    topCategoryAmount = topCat?.value ?: 0.0
                )
            )
        }
        return result
    }
}

data class FourMonthsSummaryItem(
    val monthName: String,
    val shortMonthName: String,
    val year: Int,
    val monthIndex: Int,
    val totalAmount: Double,
    val transactionCount: Int,
    val transactions: List<Transaction>,
    val topCategory: String?,
    val topCategoryAmount: Double
)


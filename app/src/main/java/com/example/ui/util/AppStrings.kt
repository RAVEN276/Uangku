package com.example.ui.util

object AppStrings {
    fun isEn(lang: String): Boolean = lang.lowercase() == "en"

    // Dynamic Greeting based on device local hour
    fun greeting(lang: String): String {
        val hour = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.time.LocalTime.now().hour
        } else {
            java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        }
        val isEnglish = isEn(lang)
        return when (hour) {
            in 5..11 -> if (isEnglish) "Good morning," else "Selamat pagi,"
            in 12..14 -> if (isEnglish) "Good afternoon," else "Selamat siang,"
            in 15..17 -> if (isEnglish) "Good afternoon," else "Selamat sore,"
            else -> if (isEnglish) "Good evening," else "Selamat malam,"
        }
    }

    // Navigation Dock
    fun navDashboard(lang: String): String = if (isEn(lang)) "Home" else "Beranda"
    fun navTransactions(lang: String): String = if (isEn(lang)) "Transactions" else "Transaksi"
    fun navBudgets(lang: String): String = if (isEn(lang)) "Budgets" else "Anggaran"
    fun navSettings(lang: String): String = if (isEn(lang)) "Settings" else "Pengaturan"

    // Dashboard Screen
    fun totalBalance(lang: String): String = if (isEn(lang)) "Total Balance" else "Total Saldo"
    fun quickAdd(lang: String): String = if (isEn(lang)) "Record" else "Catat"
    fun history(lang: String): String = if (isEn(lang)) "History" else "Riwayat"
    fun income(lang: String): String = if (isEn(lang)) "Income" else "Pemasukan"
    fun expense(lang: String): String = if (isEn(lang)) "Expense" else "Pengeluaran"
    fun thisMonth(lang: String): String = if (isEn(lang)) "This Month" else "Bulan Ini"
    fun aiInsights(lang: String): String = if (isEn(lang)) "Financial Analysis" else "Analisis Keuangan"
    fun aiInsightsDesc(lang: String): String = if (isEn(lang)) 
        "Tap to review your monthly spending patterns" 
    else 
        "Ketuk untuk melihat ringkasan pola belanja Anda"
    fun recentActivity(lang: String): String = if (isEn(lang)) "Recent Activity" else "Transaksi Terbaru"
    fun viewAll(lang: String): String = if (isEn(lang)) "View All" else "Lihat Semua"
    fun noTransactions(lang: String): String = if (isEn(lang)) "No transactions recorded yet" else "Belum ada catatan transaksi"
    fun addExpense(lang: String): String = if (isEn(lang)) "Record Expense" else "Catat Pengeluaran"
    fun addIncome(lang: String): String = if (isEn(lang)) "Record Income" else "Catat Pemasukan"
    fun scanReceipt(lang: String): String = if (isEn(lang)) "Scan Receipt" else "Pindai Nota"
    fun syncSuccess(lang: String): String = if (isEn(lang)) "Data synced successfully" else "Data berhasil disinkronkan"

    // Recurring Bills (Tagihan Berkala)
    fun recurringBillsTitle(lang: String): String = if (isEn(lang)) "Recurring Bills" else "Tagihan Berkala"
    fun newRecurringBillTitle(lang: String): String = if (isEn(lang)) "New Recurring Bill" else "Tagihan Berkala Baru"
    fun recurringBillSubtitle(lang: String): String = if (isEn(lang))
        "Fill out details or tap a popular service below to auto-fill."
    else
        "Isi data tagihan atau pilih salah satu layanan populer di bawah."
    fun popularServices(lang: String): String = if (isEn(lang)) "Popular Services:" else "Layanan Populer:"
    fun billNameLabel(lang: String): String = if (isEn(lang)) "Bill Name" else "Nama Tagihan"
    fun billNamePlaceholder(lang: String): String = if (isEn(lang)) "e.g. Home Internet, Music Streaming" else "cth. WiFi Rumah, Langganan Musik"
    fun billAmountLabel(lang: String, currency: String): String = if (isEn(lang))
        "Bill Amount (${if (currency == "USD") "USD $" else "Rupiah Rp"})"
    else
        "Nominal Tagihan (${if (currency == "USD") "USD $" else "Rupiah Rp"})"
    fun billAmountPlaceholder(lang: String, isUsd: Boolean): String = if (isUsd) {
        if (isEn(lang)) "e.g. 15" else "misal: 15"
    } else {
        if (isEn(lang)) "e.g. 150,000" else "misal: 150.000"
    }
    fun billDueDateLabel(lang: String): String = if (isEn(lang)) "Due Date (Day or Date)" else "Jatuh Tempo (Hari / Tanggal)"
    fun billDueDatePlaceholder(lang: String): String = if (isEn(lang)) "e.g. 5th of every month, 1st Monday" else "cth. Tanggal 5 setiap bulan, Senin pertama"
    fun billCycleLabel(lang: String): String = if (isEn(lang)) "Billing Cycle:" else "Siklus Tagihan:"
    fun billCategoryLabel(lang: String): String = if (isEn(lang)) "Bill Category:" else "Kategori Tagihan:"
    fun defaultDueEndMonth(lang: String): String = if (isEn(lang)) "End of each month" else "Setiap akhir bulan"
    fun markPaid(lang: String): String = if (isEn(lang)) "Mark Paid" else "Tandai Lunas"
    fun billPaidToast(lang: String, name: String): String = if (isEn(lang)) "Bill \"$name\" marked as paid" else "Tagihan \"$name\" ditandai lunas"

    // Assistant / Local ML Dialog
    fun assistantTitle(lang: String): String = if (isEn(lang)) "Financial Assistant" else "Asisten Keuangan"
    fun assistantSubtitle(lang: String): String = if (isEn(lang)) "Automated spending insights and forecast" else "Analisis pengeluaran dan proyeksi otomatis"
    fun recalculatingInsights(lang: String): String = if (isEn(lang)) 
        "Analyzing your transactions and spending summary..." 
    else 
        "Menganalisis transaksi dan menghitung pengeluaran Anda..."
    fun recalculateTooltip(lang: String): String = if (isEn(lang)) "Refresh Analysis" else "Hitung Ulang Analisis"

    // 4-Month Summary Modal
    fun summary4MonthsTitle(lang: String, type: String): String = if (isEn(lang)) {
        if (type == "INCOME") "4-Month Income Summary" else "4-Month Expense Summary"
    } else {
        if (type == "INCOME") "Ringkasan Pemasukan 4 Bulan" else "Ringkasan Pengeluaran 4 Bulan"
    }
    fun monthlyAverage(lang: String): String = if (isEn(lang)) "Monthly Average" else "Rata-rata per Bulan"
    fun peakMonth(lang: String): String = if (isEn(lang)) "Highest Month" else "Bulan Tertinggi"
    fun dominantCategory(lang: String): String = if (isEn(lang)) "Top Category" else "Kategori Terbesar"

    // Common Actions
    fun close(lang: String): String = if (isEn(lang)) "Close" else "Tutup"
    fun cancel(lang: String): String = if (isEn(lang)) "Cancel" else "Batal"
    fun save(lang: String): String = if (isEn(lang)) "Save" else "Simpan"
    fun delete(lang: String): String = if (isEn(lang)) "Delete" else "Hapus"
    fun edit(lang: String): String = if (isEn(lang)) "Edit" else "Ubah"
    fun update(lang: String): String = if (isEn(lang)) "Save Changes" else "Simpan Perubahan"
    fun apply(lang: String): String = if (isEn(lang)) "Apply" else "Terapkan"
    fun share(lang: String): String = if (isEn(lang)) "Share" else "Bagikan"
    fun claimAndShare(lang: String): String = if (isEn(lang)) "Done" else "Selesai"
    fun search(lang: String): String = if (isEn(lang)) "Search" else "Cari"

    // Settings Screen
    fun settingsTitle(lang: String): String = if (isEn(lang)) "Settings" else "Pengaturan"
    fun settingsSubtitle(lang: String): String = if (isEn(lang)) 
        "Manage security, currency, display, and export financial statements."
    else 
        "Kelola keamanan, mata uang, tampilan, dan ekspor laporan keuangan."

    fun userProfileTitle(lang: String): String = if (isEn(lang)) "User Profile" else "Profil Pengguna"
    fun nicknameTitle(lang: String): String = if (isEn(lang)) "Display Name" else "Nama Tampilan"
    fun nicknameSubtitle(lang: String, name: String): String = if (isEn(lang)) 
        "Shown on your home dashboard: $name" 
    else 
        "Nama yang dicantumkan pada beranda: $name"
    fun changeNicknameTitle(lang: String): String = if (isEn(lang)) "Change Display Name" else "Ubah Nama Tampilan"
    fun nicknameLabel(lang: String): String = if (isEn(lang)) "Display Name" else "Nama Tampilan"

    fun currencySection(lang: String): String = if (isEn(lang)) "Currency & Exchange Rate" else "Mata Uang & Kurs"
    fun currencySubtitle(lang: String): String = if (isEn(lang)) 
        "Choose your primary currency. All transactions and charts convert automatically." 
    else 
        "Pilih mata uang utama. Semua transaksi dan grafik dikonversi secara otomatis."

    fun benchmarkRate(lang: String): String = if (isEn(lang)) "USD Exchange Rate" else "Kurs Acuan Dollar (USD)"
    fun adjustRate(lang: String): String = if (isEn(lang)) "Adjust Rate" else "Ubah Kurs"
    fun adjustRateTitle(lang: String): String = if (isEn(lang)) "Adjust USD Exchange Rate" else "Sesuaikan Kurs USD ke Rupiah"
    fun adjustRateSubtitle(lang: String): String = if (isEn(lang)) 
        "Enter the current value of 1 US Dollar (USD) in Indonesian Rupiah (IDR)." 
    else 
        "Masukkan nilai kurs 1 Dollar AS (USD) dalam Rupiah (IDR)."
    fun ratePrompt(lang: String): String = if (isEn(lang)) "1 USD Value in Rupiah (Rp)" else "Nilai 1 USD dalam Rupiah (Rp)"
    fun saveRate(lang: String): String = if (isEn(lang)) "Save Rate" else "Simpan Kurs"

    fun appearanceSection(lang: String): String = if (isEn(lang)) "Display & Theme" else "Tampilan & Tema"
    fun darkModeTitle(lang: String): String = if (isEn(lang)) "Dark Theme" else "Tema Gelap"
    fun darkModeSubtitle(lang: String): String = if (isEn(lang)) 
        "Use dark colors for eye comfort and battery efficiency." 
    else 
        "Gunakan warna gelap untuk kenyamanan mata dan hemat baterai."

    fun languageTitle(lang: String): String = if (isEn(lang)) "Language" else "Bahasa"
    fun languageSubtitle(lang: String): String = if (isEn(lang)) 
        "Switch between Indonesian and English." 
    else 
        "Pilih antara Bahasa Indonesia dan English."

    fun dynamicColorTitle(lang: String): String = if (isEn(lang)) "Dynamic Colors (Material You)" else "Warna Dinamis (Material You)"
    fun dynamicColorSubtitle(lang: String): String = if (isEn(lang)) 
        "Match accent colors with your Android wallpaper." 
    else 
        "Sesuaikan warna aksen dengan wallpaper Android Anda."
    fun themePreviewTitle(lang: String): String = if (isEn(lang)) "Active Theme Palette" else "Palet Tema Aktif"
    fun themeModeExpressive(lang: String): String = if (isEn(lang)) "Editorial Slate & Forest" else "Slate & Hijau Editorial"
    fun themeModeDynamic(lang: String): String = if (isEn(lang)) "Dynamic Accent (Matte Slate)" else "Aksen Dinamis (Kartu Matte)"
    fun themePreviewSubtitle(lang: String, isDynamic: Boolean): String = if (isEn(lang)) {
        if (isDynamic) "Wallpaper accent on buttons, clean neutral slate cards" else "Neutral matte slate surfaces with editorial forest green accents"
    } else {
        if (isDynamic) "Aksen wallpaper pada tombol, kartu tetap slate matte bersih" else "Permukaan slate matte netral dengan aksen hijau editorial"
    }

    fun securitySection(lang: String): String = if (isEn(lang)) "Security" else "Keamanan"
    fun securityTitle(lang: String): String = securitySection(lang)
    fun pinLockTitleSettings(lang: String): String = if (isEn(lang)) "PIN Lock" else "Kunci PIN"
    fun pinLockSubtitleSettings(lang: String): String = if (isEn(lang)) 
        "Require 4-digit PIN when opening the app." 
    else 
        "Wajibkan PIN 4 digit setiap membuka aplikasi."
    fun biometricTitleSettings(lang: String): String = if (isEn(lang)) "Biometric Unlock" else "Buka dengan Sidik Jari"
    fun biometricSubtitleSettings(lang: String): String = if (isEn(lang)) 
        "Use device fingerprint sensor for quick and secure access." 
    else 
        "Gunakan sensor sidik jari perangkat untuk akses cepat dan aman."
    fun changePinTitleSettings(lang: String): String = if (isEn(lang)) "Change Security PIN" else "Ubah PIN Keamanan"
    fun changePinSubtitleSettings(lang: String, pin: String): String = if (isEn(lang)) 
        "Update your 4-digit PIN code. Current PIN: $pin" 
    else 
        "Perbarui 4 digit kode PIN Anda. PIN saat ini: $pin"
    fun changeAppPinTitle(lang: String): String = if (isEn(lang)) "Change Security PIN" else "Ganti PIN Keamanan"
    fun newPinPrompt(lang: String): String = if (isEn(lang)) "New 4-Digit PIN" else "PIN Baru (4 Digit Angka)"
    fun savePin(lang: String): String = if (isEn(lang)) "Save PIN" else "Simpan PIN"
    fun lockAppTitleSettings(lang: String): String = if (isEn(lang)) "Lock Session Now" else "Kunci Aplikasi Sekarang"
    fun lockAppSubtitleSettings(lang: String): String = if (isEn(lang)) 
        "Immediately secure your active session." 
    else 
        "Kunci sesi aplikasi saat ini demi menjaga privasi."
    fun lockNowAction(lang: String): String = if (isEn(lang)) "Lock" else "Kunci"

    fun bankDetectorTitle(lang: String): String = if (isEn(lang)) "Bank & E-Wallet Notification Reader" else "Deteksi Notifikasi Bank & E-Wallet"
    fun enableBankDetectorTitle(lang: String): String = if (isEn(lang)) "Enable Automatic Detection" else "Aktifkan Deteksi Otomatis"
    fun enableBankDetectorSubtitle(lang: String): String = if (isEn(lang)) 
        "Automatically record verified transactions from official bank and e-wallet apps only." 
    else 
        "Catat transaksi terverifikasi hanya dari aplikasi bank dan e-wallet resmi."
    fun systemPermissionTitle(lang: String): String = if (isEn(lang)) "Notification Access Permission" else "Izin Akses Notifikasi Android"
    fun systemPermissionSubtitle(lang: String): String = if (isEn(lang)) 
        "Manage Android system permission required to read bank notifications." 
    else 
        "Kelola izin sistem Android agar notifikasi transaksi resmi dapat dibaca."
    fun detectorStatusActive(lang: String): String = if (isEn(lang)) "Active" else "Aktif"
    fun detectorStatusNeedsPermission(lang: String): String = if (isEn(lang)) "Permission Needed" else "Izin Diperlukan"
    fun detectorStatusPaused(lang: String): String = if (isEn(lang)) "Paused" else "Nonaktif"
    fun supportedFinancialAppsLabel(lang: String): String = if (isEn(lang))
        "Official sources: BCA, Mandiri, BNI, BRI, BSI, CIMB, Jago, SeaBank, GoPay, OVO, DANA, ShopeePay, LinkAja"
    else
        "Sumber resmi: BCA, Mandiri, BNI, BRI, BSI, CIMB, Jago, SeaBank, GoPay, OVO, DANA, ShopeePay, LinkAja"

    fun exportReportTitle(lang: String): String = if (isEn(lang)) "Export & Reports" else "Ekspor & Laporan Keuangan"
    fun exportReportDesc(lang: String): String = if (isEn(lang)) 
        "Save your monthly transactions to your Downloads folder as a printable PDF or Excel spreadsheet." 
    else 
        "Simpan rekap transaksi bulanan Anda ke folder Download dalam format PDF atau Excel."
    fun savePdf(lang: String): String = if (isEn(lang)) "Save PDF" else "Simpan PDF"
    fun exportExcel(lang: String): String = if (isEn(lang)) "Export Excel" else "Ekspor Excel"
    fun pdfPreviewTitle(lang: String): String = if (isEn(lang)) "Financial Report Preview" else "Pratinjau Laporan PDF"
    fun sharePdf(lang: String): String = if (isEn(lang)) "Share Document" else "Bagikan Dokumen"

    fun backupSection(lang: String): String = if (isEn(lang)) "Backup & Data Export" else "Cadangkan & Ekspor Data"
    fun jsonBackup(lang: String): String = if (isEn(lang)) "Backup Database (JSON)" else "Cadangkan Data (JSON)"
    fun jsonBackupSubtitle(lang: String): String = if (isEn(lang)) 
        "Save all transactions and budgets into a single backup file." 
    else 
        "Simpan seluruh transaksi dan anggaran ke satu file cadangan."
    fun importBackup(lang: String): String = if (isEn(lang)) "Restore from Backup" else "Pulihkan Data Cadangan"
    fun importBackupSubtitle(lang: String): String = if (isEn(lang)) 
        "Import previous transactions and settings from a JSON file." 
    else 
        "Kembalikan catatan transaksi dan pengaturan dari file JSON."
    fun resetData(lang: String): String = if (isEn(lang)) "Reset All Financial Records" else "Reset Seluruh Data Keuangan"
    fun resetDataSubtitle(lang: String): String = if (isEn(lang)) 
        "Permanently delete all transactions, budgets, and goals." 
    else 
        "Hapus seluruh transaksi, anggaran, dan target secara permanen."

    fun appInfoSection(lang: String): String = if (isEn(lang)) "App Information" else "Informasi Aplikasi"
    fun appInfoSubtitle(lang: String): String = if (isEn(lang)) 
        "Personal Financial Manager" 
    else 
        "Manajemen Keuangan Pribadi"
    fun encryptedDb(lang: String): String = if (isEn(lang)) 
        "Encrypted Local Storage" 
    else 
        "Penyimpanan Lokal Terenkripsi"
    fun updateSafeNote(lang: String): String = if (isEn(lang)) 
        "App updates preserve your existing records safely using local Room migrations." 
    else 
        "Pembaruan aplikasi menjaga data transaksi lama Anda dengan migrasi Room lokal."

    // Transactions Screen
    fun transactionsTitle(lang: String): String = if (isEn(lang)) "Transaction History" else "Riwayat Transaksi"
    fun searchPlaceholder(lang: String): String = if (isEn(lang)) "Search note or category..." else "Cari keterangan atau kategori..."
    fun filterAll(lang: String): String = if (isEn(lang)) "All" else "Semua"
    fun noTransactionsFound(lang: String): String = if (isEn(lang)) "No transactions found" else "Tidak ada transaksi ditemukan"
    fun deleteTxTitle(lang: String): String = if (isEn(lang)) "Delete Transaction?" else "Hapus Transaksi?"
    fun deleteTxDesc(lang: String): String = if (isEn(lang)) 
        "This transaction record will be permanently removed." 
    else 
        "Catatan transaksi ini akan dihapus secara permanen."
    fun txDeletedToast(lang: String, title: String): String = if (isEn(lang)) 
        "Transaction \"$title\" deleted" 
    else 
        "Transaksi \"$title\" berhasil dihapus"
    fun saveTransaction(lang: String): String = if (isEn(lang)) "Save Transaction" else "Simpan Transaksi"
    fun recordIncome(lang: String): String = if (isEn(lang)) "Record Income" else "Catat Pemasukan"
    fun recordExpense(lang: String): String = if (isEn(lang)) "Record Expense" else "Catat Pengeluaran"
    fun editIncome(lang: String): String = if (isEn(lang)) "Edit Income" else "Ubah Pemasukan"
    fun editExpense(lang: String): String = if (isEn(lang)) "Edit Expense" else "Ubah Pengeluaran"
    fun newIncomeDesc(lang: String): String = if (isEn(lang)) "Incoming funds or salary" else "Pemasukan dana atau gaji"
    fun newExpenseDesc(lang: String): String = if (isEn(lang)) "Daily or routine expense" else "Pengeluaran harian atau rutin"
    fun description(lang: String): String = if (isEn(lang)) "Description" else "Keterangan"
    fun descPlaceholderIncome(lang: String): String = if (isEn(lang)) "e.g. Monthly salary, project fee" else "cth. Gaji bulanan, bonus proyek"
    fun descPlaceholderExpense(lang: String): String = if (isEn(lang)) "e.g. Groceries, fuel, lunch" else "cth. Belanja bulanan, bensin, makan siang"
    fun amount(lang: String): String = if (isEn(lang)) "Amount" else "Nominal"
    fun amountPlaceholder(lang: String, isUsd: Boolean): String = if (isUsd) {
        if (isEn(lang)) "e.g. 25" else "misal: 25"
    } else {
        if (isEn(lang)) "e.g. 50,000" else "misal: 50.000"
    }
    fun category(lang: String): String = if (isEn(lang)) "Category" else "Kategori"

    // Budget & Goals Screen
    fun budgetsTitle(lang: String): String = if (isEn(lang)) "Budgets & Goals" else "Anggaran & Target"
    fun budgetTab(lang: String): String = if (isEn(lang)) "Budgets" else "Anggaran"
    fun savingGoalTab(lang: String): String = if (isEn(lang)) "Goals" else "Target Tabungan"
    fun newBudget(lang: String): String = if (isEn(lang)) "Set Budget" else "Atur Batas"
    fun newSavingGoal(lang: String): String = if (isEn(lang)) "New Goal" else "Target Baru"
    fun emptyBudgetTitle(lang: String): String = if (isEn(lang)) "No Spending Limits Set" else "Belum Ada Batas Anggaran"
    fun emptyBudgetDesc(lang: String): String = if (isEn(lang)) 
        "Set monthly or daily spending limits to keep your expenses in check." 
    else 
        "Atur batas belanja bulanan atau harian untuk mengontrol pengeluaran Anda."
    fun monthlyBudgetLimit(lang: String): String = if (isEn(lang)) "Monthly Spending Limit" else "Batas Belanja Bulanan"
    fun dailyBudgetLimit(lang: String): String = if (isEn(lang)) "Daily Spending Limit" else "Batas Belanja Harian"
    fun usedAmount(lang: String, amountStr: String): String = if (isEn(lang)) "Spent: $amountStr" else "Terpakai: $amountStr"
    fun limitAmount(lang: String, amountStr: String): String = if (isEn(lang)) "Limit: $amountStr" else "Batas: $amountStr"
    fun todayAmount(lang: String, amountStr: String): String = if (isEn(lang)) "Today: $amountStr" else "Hari ini: $amountStr"
    fun usagePortion(lang: String, percent: Int): String = if (isEn(lang)) "Used: $percent%" else "Terpakai: $percent%"
    fun alertActiveAt(lang: String, percent: Int): String = if (isEn(lang)) "Alert at $percent%" else "Peringatan pada $percent%"
    fun categoryBudgetTitle(lang: String): String = if (isEn(lang)) "Category Spending Limits" else "Batas Belanja Kategori"
    fun emptyCategoryBudgetDesc(lang: String): String = if (isEn(lang)) 
        "No category limits set yet. Tap Set Budget above to configure limits per category." 
    else 
        "Belum ada batas per kategori. Ketuk tombol Atur Batas di atas untuk menentukan batas per kategori."
    fun remainingAmount(lang: String, amountStr: String): String = if (isEn(lang)) "Remaining: $amountStr" else "Sisa: $amountStr"
    fun percentUsed(lang: String, percent: Int): String = if (isEn(lang)) "$percent% used" else "$percent% terpakai"
    fun emptySavingGoalTitle(lang: String): String = if (isEn(lang)) "No Saving Goals Yet" else "Belum Ada Target Tabungan"
    fun emptySavingGoalDesc(lang: String): String = if (isEn(lang)) 
        "Set aside funds for future goals and track your progress here." 
    else 
        "Buat target tabungan dan pantau progres pencapaiannya secara bertahap."
    fun setBudgetTitle(lang: String): String = if (isEn(lang)) "Set Spending Budget" else "Pasang Batas Anggaran"
    fun selectCategoryTarget(lang: String): String = if (isEn(lang)) "Select Target Category:" else "Pilih Target Kategori:"
    fun totalMonthlySpending(lang: String): String = if (isEn(lang)) "Total Monthly Spending" else "Total Belanja Bulanan"
    fun totalDailySpending(lang: String): String = if (isEn(lang)) "Total Daily Spending" else "Total Belanja Harian"
    fun budgetLimitLabel(lang: String, currency: String): String = if (isEn(lang)) 
        "Spending Limit (${if (currency == "USD") "USD $" else "Rupiah Rp"})" 
    else 
        "Batas Belanja (${if (currency == "USD") "USD $" else "Rupiah Rp"})"
    fun alertTriggerLabel(lang: String): String = if (isEn(lang)) "Alert Notification Threshold (%)" else "Peringatan Ambang Batas (%)"
    fun newSavingGoalTitle(lang: String): String = if (isEn(lang)) "New Saving Goal" else "Target Tabungan Baru"
    fun editSavingGoalTitle(lang: String): String = if (isEn(lang)) "Edit Saving Goal" else "Ubah Target Tabungan"
    fun goalNameLabel(lang: String): String = if (isEn(lang)) "Goal Title" else "Nama Target"
    fun goalNamePlaceholder(lang: String): String = if (isEn(lang)) "e.g. Emergency Fund, Laptop" else "misal: Dana Darurat, Laptop Baru"
    fun targetAmountLabel(lang: String, currency: String): String = if (isEn(lang)) 
        "Target Amount (${if (currency == "USD") "USD $" else "Rupiah Rp"})" 
    else 
        "Target Nominal (${if (currency == "USD") "USD $" else "Rupiah Rp"})"
    fun targetDateLabel(lang: String): String = if (isEn(lang)) "Target Achievement Date" else "Target Tanggal Pencapaian"
    fun targetDatePlaceholder(lang: String): String = if (isEn(lang)) "e.g. December 2026" else "misal: Desember 2026"
    fun targetDateSoon(lang: String): String = if (isEn(lang)) "Soon" else "Segera"
    fun target(lang: String): String = if (isEn(lang)) "Target" else "Target"
    fun budgetAmountPlaceholder(lang: String, isUsd: Boolean): String = if (isUsd) {
        if (isEn(lang)) "e.g. 100" else "misal: 100"
    } else {
        if (isEn(lang)) "e.g. 1,500,000" else "misal: 1.500.000"
    }
    fun savingTargetPlaceholder(lang: String, isUsd: Boolean): String = if (isUsd) {
        if (isEn(lang)) "e.g. 500" else "misal: 500"
    } else {
        if (isEn(lang)) "e.g. 10,000,000" else "misal: 10.000.000"
    }
    fun selectCategory(lang: String): String = if (isEn(lang)) "Select Category:" else "Pilih Kategori:"
    fun createGoal(lang: String): String = if (isEn(lang)) "Create Goal" else "Buat Target"
    fun saveChanges(lang: String): String = if (isEn(lang)) "Save Changes" else "Simpan Perubahan"
    fun congratsTitle(lang: String): String = if (isEn(lang)) "Goal Accomplished" else "Target Tercapai"
    fun badgeUnlocked(lang: String, name: String): String = if (isEn(lang)) "Achievement Unlocked: $name" else "Pencapaian Terbuka: $name"
    fun collectedAmount(lang: String, amountStr: String): String = if (isEn(lang)) "Saved: $amountStr" else "Terkumpul: $amountStr"
    fun targetAmountGoal(lang: String, amountStr: String): String = if (isEn(lang)) "Target: $amountStr" else "Target: $amountStr"
    fun autoDetectBalance(lang: String, percent: Int): String = if (isEn(lang)) "Current balance progress: $percent%" else "Progres saldo saat ini: $percent%"
    fun goalAchieved(lang: String): String = if (isEn(lang)) "Goal Accomplished" else "Target Berhasil Diraih"

    // Speed Dial Menu
    fun speedDialScanReceipt(lang: String): String = if (isEn(lang)) "Scan Receipt" else "Pindai Nota"
    fun speedDialScanReceiptDesc(lang: String): String = if (isEn(lang)) 
        "Capture receipt to fill details automatically" 
    else 
        "Foto nota belanja untuk catat transaksi otomatis"
    fun speedDialIncomeDesc(lang: String): String = if (isEn(lang)) 
        "Record salary, dividends, or client payments" 
    else 
        "Catat gaji, dividen, atau pembayaran masuk"
    fun speedDialExpenseDesc(lang: String): String = if (isEn(lang)) 
        "Record shopping, meals, and daily expenses" 
    else 
        "Catat belanja harian, makanan, dan tagihan"
    fun speedDialOpenContentDesc(lang: String): String = if (isEn(lang)) "Open Transaction Menu" else "Buka Menu Transaksi"
    fun speedDialCloseContentDesc(lang: String): String = if (isEn(lang)) "Close Menu" else "Tutup Menu"
    fun cameraPermissionRequired(lang: String): String = if (isEn(lang)) "Camera permission is required to scan receipt" else "Izin kamera diperlukan untuk memindai nota"

    // Onboarding Strings
    fun onboardingWelcomeTitle(lang: String): String = if (isEn(lang)) "Welcome to Uangku" else "Selamat Datang di Uangku"
    fun onboardingWelcomeDesc(lang: String): String = if (isEn(lang)) 
        "Personal finance management that is private, intelligent, and stored entirely on your device." 
    else 
        "Manajemen keuangan pribadi yang privat, cerdas, dan tersimpan aman di perangkat Anda."
    fun onboardingGetStarted(lang: String): String = if (isEn(lang)) "Get Started" else "Mulai Sekarang"
    fun badgeSecure(lang: String): String = if (isEn(lang)) "Secure" else "Aman"
    fun badgeSmart(lang: String): String = if (isEn(lang)) "Smart" else "Cerdas"
    fun badgeOffline(lang: String): String = if (isEn(lang)) "Private & Offline" else "Privat & Offline"
    fun onboardingNameTitle(lang: String): String = if (isEn(lang)) "What should we call you?" else "Siapa nama panggilan Anda?"
    fun onboardingNameDesc(lang: String): String = if (isEn(lang)) 
        "This name will personalize your dashboard and financial statements." 
    else 
        "Nama ini akan ditampilkan pada beranda dan laporan keuangan Anda."
    fun onboardingNamePlaceholder(lang: String): String = if (isEn(lang)) "e.g. Alex Morgan" else "misal: Budi Santoso"
    fun onboardingContinue(lang: String): String = if (isEn(lang)) "Continue" else "Lanjutkan"
    fun onboardingSecurityTitle(lang: String): String = if (isEn(lang)) "Protect Your Vault" else "Lindungi Data Finansial"
    fun onboardingSecurityDesc(lang: String): String = if (isEn(lang)) 
        "Set up an optional 4-digit PIN for extra privacy. You can configure biometrics later in Settings." 
    else 
        "Atur PIN 4 digit untuk privasi ekstra. Anda juga dapat mengaktifkan sidik jari di Pengaturan."
    fun onboardingEnablePin(lang: String): String = if (isEn(lang)) "Enable PIN Lock" else "Aktifkan Kunci PIN"
    fun onboardingPinOptional(lang: String): String = if (isEn(lang)) "Recommended for shared devices" else "Disarankan untuk menjaga privasi"
    fun onboardingPinLabel(lang: String): String = if (isEn(lang)) "4-Digit PIN" else "PIN 4 Digit"
    fun onboardingPinPlaceholder(lang: String): String = if (isEn(lang)) "e.g. 1234" else "misal: 1234"
    fun onboardingPinValidation(lang: String): String = if (isEn(lang)) "PIN must be exactly 4 digits." else "PIN harus terdiri dari 4 digit angka."
    fun onboardingComplete(lang: String): String = if (isEn(lang)) "Finish Setup" else "Selesaikan Pengaturan"
    fun onboardingNameEmptyToast(lang: String): String = if (isEn(lang)) "Please enter your nickname." else "Silakan masukkan nama panggilan Anda."
    fun onboardingPinIncompleteToast(lang: String): String = if (isEn(lang)) 
        "Please enter a 4-digit PIN or disable PIN lock." 
    else 
        "Masukkan 4 digit PIN atau nonaktifkan kunci PIN."
    fun onboardingWelcomeToast(lang: String, name: String): String = if (isEn(lang)) 
        "Welcome to Uangku, $name!" 
    else 
        "Selamat datang di Uangku, $name!"

    // Auth Screen Strings
    fun authAppTitle(lang: String): String = if (isEn(lang)) "Passcode Verification" else "Verifikasi Keamanan"
    fun authAppSubtitle(lang: String): String = if (isEn(lang)) 
        "Enter your 4-digit PIN to access your finances" 
    else 
        "Masukkan PIN 4 digit untuk membuka akun keuangan Anda"
    fun authPinIncorrect(lang: String): String = if (isEn(lang)) "Incorrect PIN. Please try again." else "PIN salah. Silakan coba lagi."
    fun authEnterPinPrompt(lang: String): String = if (isEn(lang)) "Enter your 4-digit PIN" else "Masukkan PIN 4 digit Anda"
    fun authVerifying(lang: String): String = if (isEn(lang)) "Verifying..." else "Memverifikasi..."
    fun authBiometricPrompt(lang: String): String = if (isEn(lang)) "Unlock with Biometrics" else "Buka dengan Sidik Jari"

    // Financial Health & Assistant Analytics Strings
    fun fhsTitle(lang: String): String = if (isEn(lang)) "Financial Health Score" else "Skor Kesehatan Finansial"
    fun fhsScoreUnit(lang: String): String = if (isEn(lang)) "Score" else "Skor"
    fun fhsRatingOptimal(lang: String): String = if (isEn(lang)) "Optimal Health" else "Sangat Prima"
    fun fhsRatingGood(lang: String): String = if (isEn(lang)) "Healthy" else "Kondisi Sehat"
    fun fhsRatingCaution(lang: String): String = if (isEn(lang)) "Needs Attention" else "Perlu Perhatian"
    fun fhsRatingCritical(lang: String): String = if (isEn(lang)) "High Deficit Risk" else "Defisit Kritis"
    fun forecastTitle(lang: String): String = if (isEn(lang)) "Next Week Spending Forecast" else "Prediksi Tren Pekan Depan"
    fun forecastSavingDesc(lang: String): String = if (isEn(lang)) 
        "Controlled spending detected. Projected expenses are lower than your recent weekly average." 
    else 
        "Pola belanja terkontrol. Proyeksi pengeluaran lebih rendah dibanding rata-rata mingguan Anda."
    fun forecastOverspendDesc(lang: String): String = if (isEn(lang)) 
        "Higher spending velocity detected. Consider trimming non-essential purchases next week." 
    else 
        "Terdeteksi kenaikan belanja. Pertimbangkan untuk membatasi pengeluaran sekunder pekan depan."
    fun metricsSectionTitle(lang: String): String = if (isEn(lang)) "Key Financial Metrics" else "Metrik & Proyeksi Finansial"
    fun dailyBurnRateTitle(lang: String): String = if (isEn(lang)) "Daily Burn Rate" else "Laju Belanja Harian"
    fun dailyBurnRateDesc(lang: String): String = if (isEn(lang)) "Average daily spending" else "Rata-rata per hari"
    fun monthlyProjectionTitle(lang: String): String = if (isEn(lang)) "Month-End Forecast" else "Proyeksi Akhir Bulan"
    fun monthlyProjectionDesc(lang: String): String = if (isEn(lang)) "Estimated remaining need" else "Estimasi kebutuhan sisa hari"
    fun estimatedBalanceTitle(lang: String): String = if (isEn(lang)) "Projected Balance" else "Estimasi Saldo Akhir"
    fun projectedSurplusDesc(lang: String): String = if (isEn(lang)) "Projected surplus" else "Potensi surplus saldo"
    fun projectedDeficitDesc(lang: String): String = if (isEn(lang)) "Risk of cashflow deficit" else "Risiko defisit kas akhir"
    fun topSectorTitle(lang: String): String = if (isEn(lang)) "Top Spending Sector" else "Sektor Pengeluaran Terbesar"
    fun topSectorPortionDesc(lang: String, percent: String): String = if (isEn(lang)) 
        "$percent% of total expenses" 
    else 
        "$percent% dari seluruh belanja"
    fun mlColdTitle(lang: String): String = if (isEn(lang)) "Analysis Engine Ready" else "Model Analisis Siap Digunakan"
    fun mlColdDesc(lang: String): String = if (isEn(lang)) 
        "Your assistant analyzes cash flow directly in your device's private memory. Add transactions to generate your health score and forecast." 
    else 
        "Asisten cerdas menganalisis arus kas di memori privat perangkat Anda. Tambahkan transaksi untuk memunculkan skor kesehatan dan proyeksi."
    fun recordFirstExpense(lang: String): String = if (isEn(lang)) "Record First Expense" else "Catat Pengeluaran Pertama"

    // Helper to translate category names display
    fun translateCategory(cat: String, lang: String): String {
        if (!isEn(lang)) return cat
        return when (cat) {
            "Gaji" -> "Salary"
            "Bonus" -> "Bonus"
            "Investasi" -> "Investment"
            "Penjualan" -> "Sales"
            "Hadiah" -> "Gift"
            "Makanan" -> "Food & Dining"
            "Belanja" -> "Shopping"
            "Transportasi" -> "Transportation"
            "Tagihan" -> "Bills & Utilities"
            "Hiburan" -> "Entertainment"
            "Kesehatan" -> "Health & Medical"
            "Sewa" -> "Housing & Rent"
            "Lainnya" -> "Other"
            "Tabungan" -> "Savings"
            "Gawai" -> "Electronics & Gadgets"
            "Liburan" -> "Travel & Vacation"
            "Dana Darurat" -> "Emergency Fund"
            "Kendaraan" -> "Vehicle"
            "Langganan" -> "Subscriptions"
            "Utilitas" -> "Utilities"
            "Pendidikan" -> "Education"
            else -> cat
        }
    }

    // Helper to translate billing cycles
    fun translateCycle(cycle: String, lang: String): String {
        if (!isEn(lang)) return cycle
        return when (cycle) {
            "Bulanan" -> "Monthly"
            "Mingguan" -> "Weekly"
            "Tahunan" -> "Yearly"
            else -> cycle
        }
    }
}

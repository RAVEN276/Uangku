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
            in 5..11 -> if (isEnglish) "Good Morning," else "Selamat Pagi,"
            in 12..14 -> if (isEnglish) "Good Afternoon," else "Selamat Siang,"
            in 15..17 -> if (isEnglish) "Good Afternoon," else "Selamat Sore,"
            else -> if (isEnglish) "Good Evening," else "Selamat Malam,"
        }
    }

    // Navigation Dock
    fun navDashboard(lang: String): String = if (isEn(lang)) "Dashboard" else "Dasbor"
    fun navTransactions(lang: String): String = if (isEn(lang)) "Transactions" else "Transaksi"
    fun navBudgets(lang: String): String = if (isEn(lang)) "Budgets" else "Anggaran"
    fun navSettings(lang: String): String = if (isEn(lang)) "Settings" else "Pengaturan"

    // Settings Screen
    fun settingsTitle(lang: String): String = if (isEn(lang)) "Settings" else "Pengaturan"
    fun settingsSubtitle(lang: String): String = if (isEn(lang)) 
        "Manage security, appearance, currency, and local data backups."
    else 
        "Atur preferensi keamanan, akses akun, dan ekspor seluruh dokumen keuangan secara lokal."

    fun currencySection(lang: String): String = if (isEn(lang)) "Currency & Exchange Rate" else "Mata Uang & Kurs"
    fun currencySubtitle(lang: String): String = if (isEn(lang)) 
        "Choose primary currency. All amounts, charts, and reports will convert automatically." 
    else 
        "Pilih mata uang utama aplikasi. Seluruh nominal transaksi, grafik, dan laporan akan otomatis dikonversi sesuai kurs pilihan Anda."

    fun benchmarkRate(lang: String): String = if (isEn(lang)) "Dollar Benchmark Rate" else "Kurs Acuan Dollar"
    fun adjustRate(lang: String): String = if (isEn(lang)) "Adjust Rate" else "Sesuaikan Kurs"

    fun appearanceSection(lang: String): String = if (isEn(lang)) "App Appearance" else "Tampilan Aplikasi"
    fun darkModeTitle(lang: String): String = if (isEn(lang)) "Dark Mode" else "Mode Gelap"
    fun darkModeSubtitle(lang: String): String = if (isEn(lang)) 
        "Optimize OLED screen power with an eye-friendly dark aesthetic." 
    else 
        "Optimalkan konsumsi layar OLED dengan mode gelap yang sejuk di mata."

    fun languageTitle(lang: String): String = if (isEn(lang)) "App Language" else "Bahasa Aplikasi"
    fun languageSubtitle(lang: String): String = if (isEn(lang)) 
        "Choose interface display language (Indonesian or English)." 
    else 
        "Pilih bahasa tampilan antarmuka (Bahasa Indonesia atau English)."

    fun dynamicColorTitle(lang: String): String = if (isEn(lang)) "Dynamic Colors (Material You)" else "Warna Dinamis (Material You)"
    fun dynamicColorSubtitle(lang: String): String = if (isEn(lang)) 
        "Harmonize app colors with your Android wallpaper palette." 
    else 
        "Sesuaikan aksen warna aplikasi secara dinamis dengan wallpaper Android."

    fun securitySection(lang: String): String = if (isEn(lang)) "Security & Access" else "Keamanan & Akses"
    fun pinLockTitle(lang: String): String = if (isEn(lang)) "Application PIN Lock" else "Kunci PIN Aplikasi"
    fun pinLockSubtitle(lang: String): String = if (isEn(lang)) 
        "Require 6-digit PIN before entering app" 
    else 
        "Wajibkan verifikasi 6 digit PIN saat membuka aplikasi"

    fun changePin(lang: String): String = if (isEn(lang)) "Change 6-Digit PIN" else "Ganti 6-Digit PIN"
    fun biometricTitle(lang: String): String = if (isEn(lang)) "Biometric Login" else "Masuk Dengan Biometrik"
    fun biometricSubtitle(lang: String): String = if (isEn(lang)) 
        "Fingerprint / Face Unlock" 
    else 
        "Gunakan sensor sidik jari perangkat untuk masuk secara cepat"

    fun lockNow(lang: String): String = if (isEn(lang)) "Lock App Now" else "Kunci Layar Sekarang"
    fun lockNowSubtitle(lang: String): String = if (isEn(lang)) 
        "Immediately lock screen session" 
    else 
        "Kunci sesi aplikasi seketika demi privasi"

    fun bankNotificationTitle(lang: String): String = if (isEn(lang)) "Bank SMS Detection" else "Deteksi Notifikasi Bank"
    fun bankNotificationSubtitle(lang: String): String = if (isEn(lang)) 
        "Auto-read bank transfer SMS notifications" 
    else 
        "Baca otomatis SMS/notifikasi mutasi bank lokal"

    fun backupSection(lang: String): String = if (isEn(lang)) "Backup & Data Export" else "Cadangkan & Ekspor Data"
    fun exportPdf(lang: String): String = if (isEn(lang)) "Export Monthly PDF Report" else "Ekspor Laporan Bulanan (PDF)"
    fun exportPdfSubtitle(lang: String): String = if (isEn(lang)) 
        "Generate printer-friendly PDF financial summary" 
    else 
        "Cetak rekap finansial resmi format PDF"

    fun exportCsv(lang: String): String = if (isEn(lang)) "Export CSV (Excel / Sheets)" else "Ekspor Dokumen CSV (Excel)"
    fun exportCsvSubtitle(lang: String): String = if (isEn(lang)) 
        "Spreadsheet compatible transaction file" 
    else 
        "Berkas tabel yang dapat dibuka di spreadsheet"

    fun jsonBackup(lang: String): String = if (isEn(lang)) "Encrypted JSON Backup" else "Cadangkan Data Enkripsi (JSON)"
    fun jsonBackupSubtitle(lang: String): String = if (isEn(lang)) 
        "Save all database records into a single file" 
    else 
        "Simpan seluruh basis data ke dalam file aman"

    fun importBackup(lang: String): String = if (isEn(lang)) "Restore JSON Backup" else "Pulihkan Data dari Cadangan"
    fun importBackupSubtitle(lang: String): String = if (isEn(lang)) 
        "Restore transactions, budgets, and settings" 
    else 
        "Kembalikan catatan transaksi dan pengaturan"

    fun resetData(lang: String): String = if (isEn(lang)) "Reset All Financial Records" else "Reset Seluruh Riwayat Keuangan"
    fun resetDataSubtitle(lang: String): String = if (isEn(lang)) 
        "Wipe all transactions and budgets permanently" 
    else 
        "Hapus seluruh transaksi dan anggaran secara permanen"

    fun appInfoSection(lang: String): String = if (isEn(lang)) "App Info & Version" else "Informasi & Versi Aplikasi"
    fun appInfoSubtitle(lang: String): String = if (isEn(lang)) 
        "Personal Financial Management" 
    else 
        "Manajemen Keuangan Pribadi"

    fun encryptedDb(lang: String): String = if (isEn(lang)) 
        "Secure & Locally Encrypted Database" 
    else 
        "Basis Data Aman & Terenkripsi Lokal"

    fun updateSafeNote(lang: String): String = if (isEn(lang)) 
        "Version updates are guaranteed safe without deleting past records (Room Migration v2)." 
    else 
        "Pembaruan versi dijamin tidak menghapus data transaksi lama (Room Migration v2)."

    // Dashboard
    fun greetingMorning(lang: String): String = if (isEn(lang)) "Good Morning," else "Selamat Pagi,"
    fun greetingAfternoon(lang: String): String = if (isEn(lang)) "Good Afternoon," else "Selamat Siang,"
    fun greetingEvening(lang: String): String = if (isEn(lang)) "Good Evening," else "Selamat Malam,"
    fun totalBalance(lang: String): String = if (isEn(lang)) "Total Balance" else "Total Saldo"
    fun income(lang: String): String = if (isEn(lang)) "Income" else "Pemasukan"
    fun expense(lang: String): String = if (isEn(lang)) "Expense" else "Pengeluaran"
    fun thisMonth(lang: String): String = if (isEn(lang)) "This Month" else "Bulan Ini"
    fun aiInsights(lang: String): String = if (isEn(lang)) "AI Insights" else "Wawasan AI"
    fun aiInsightsDesc(lang: String): String = if (isEn(lang)) 
        "Tap to view smart financial analysis" 
    else 
        "Ketuk untuk analisis keuangan cerdas"
    fun recentActivity(lang: String): String = if (isEn(lang)) "Recent Activity" else "Transaksi Terbaru"
    fun viewAll(lang: String): String = if (isEn(lang)) "View All" else "Lihat Semua"
    fun noTransactions(lang: String): String = if (isEn(lang)) "No transactions yet" else "Belum ada catatan transaksi"
    fun addExpense(lang: String): String = if (isEn(lang)) "Add Expense" else "Catat Pengeluaran"
    fun addIncome(lang: String): String = if (isEn(lang)) "Add Income" else "Catat Pemasukan"
    fun scanReceipt(lang: String): String = if (isEn(lang)) "Scan Receipt" else "Pindai Nota"

    // 4-Month Summary Modal
    fun summary4MonthsTitle(lang: String, type: String): String = if (isEn(lang)) {
        if (type == "INCOME") "4-Month Income Summary" else "4-Month Expense Summary"
    } else {
        if (type == "INCOME") "Ringkasan Pemasukan 4 Bulan" else "Ringkasan Pengeluaran 4 Bulan"
    }
    fun monthlyAverage(lang: String): String = if (isEn(lang)) "Monthly Average" else "Rata-rata per Bulan"
    fun peakMonth(lang: String): String = if (isEn(lang)) "Peak Month" else "Bulan Tertinggi"
    fun dominantCategory(lang: String): String = if (isEn(lang)) "Top Category" else "Kategori Terbesar"

    // Common Actions
    fun close(lang: String): String = if (isEn(lang)) "Close" else "Tutup"
    fun cancel(lang: String): String = if (isEn(lang)) "Cancel" else "Batal"
    fun save(lang: String): String = if (isEn(lang)) "Save" else "Simpan"
    fun delete(lang: String): String = if (isEn(lang)) "Delete" else "Hapus"
    fun edit(lang: String): String = if (isEn(lang)) "Edit" else "Ubah"
}

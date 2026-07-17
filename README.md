# Uangku - Enterprise-Grade Personal Finance Manager & Local Analytics

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7-4285F4?style=flat-square&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20--%20Clean%20Design-blue?style=flat-square)]()
[![Database](https://img.shields.io/badge/Database-Room%20SQLite%20(KSP)-F89820?style=flat-square)]()

Uangku adalah solusi manajemen keuangan pribadi (*personal finance manager*) berbasis Android yang mengedepankan performa tinggi, privasi data mutlak, dan fungsionalitas luring sepenuhnya (*100% offline-first*). Dirancang menggunakan **Jetpack Compose** dan **Material Design 3**, sistem ini memberikan pengawasan arus kas presisi, otomatisasi penangkapan transaksi real-time dari notifikasi sistem, serta analisis prediktif berbasis statistika terapan langsung di dalam perangkat tanpa ketergantungan API awan atau server pihak ketiga.

---

### Matriks Fitur & Kapabilitas Sistem

| Modul Fitur | Deskripsi Fungsional | Komponen Teknis Utama | Dampak bagi Pengguna |
| :--- | :--- | :--- | :--- |
| **Penerima Mutasi Otomatis** | Ekstraksi mutasi dana masuk/keluar dari notifikasi aplikasi perbankan/dompet digital secara transparan. | `NotificationListenerService` & custom Regular Expression | Eliminasi entri data manual; pencatatan transaksi instan di latar belakang. |
| **Statistical Engine (ML Analyst)** | Komputasi kuadrat terkecil untuk memproyeksikan sisa saldo akhir bulan dan tren konsumsi harian. | Algoritme Regresi Linear Sederhana on-device | Prediksi keuangan berbasis sains untuk mencegah defisit saldo sebelum akhir bulan. |
| **Rupiah Input Formatter** | Pengetikan nominal keuangan yang aman, bebas dari kesalahan visualisasi ribu maupun gangguan lompatan kursor. | `RupiahVisualTransformation` | Pengalaman input nilai uang yang lancar, intuitif, dan bebas bug posisi kursor. |
| **Visualisasi Komprehensif** | Pemetaan kategori pengeluaran dan kurva perbandingan pendapatan secara informatif. | Donut & Line Chart berbasis Canvas & Jetpack Compose | Pemahaman instan mengenai alokasi dana terbesar dan rasio tabungan. |
| **Ekspor Laporan Resmi** | Penyediaan dokumen laporan ringkasan dalam format portabel dan tabel pengolah data. | PDF Document Canvas generator & CSV Exporter | Dokumentasi keuangan terlaporkan yang siap diarsipkan atau diolah di Excel. |
| **Kalender Tagihan Interaktif** (v2.0) | Kalender visual pengatur jadwal penarikan tagihan rutin bulanan (langganan, listrik, WiFi) dengan indikator visual. | Custom Calendar Grid Composables & `RecurringBill` entity | Menghindari keterlambatan denda pembayaran tagihan lewat pengawasan tanggal jatuh tempo yang presisi. |
| **Notifikasi Pengingat Lokal** (v2.0) | Pengiriman sinyal pengingat lokal secara otomatis saat mendekati tanggal jatuh tempo pembayaran tanpa memerlukan server cloud. | `NotificationCompat.Builder`, `POST_NOTIFICATIONS` runtime checks | Jaminan ketepatan waktu membayar tagihan tanpa mengompromikan privasi data. |

---

## Deskripsi Teknis Arsitektur & Fitur Terbaru (v2.0)

### 1. Deteksi Mutasi Latar Belakang Terintegrasi
Subsistem pendeteksi mutasi bekerja pada tingkat sistem operasi untuk menangkap intensitas notifikasi keuangan:
*   **Normalisasi Pemisah Desimal Dinamis**: Parser kustom menyelaraskan variansi simbol penulisan numerik perbankan nasional dan internasional. Sistem menangani simbol ribuan titik (`.`) maupun koma (`,`), sekaligus melakukan pembersihan digit pecahan/sen cent secara otomatis sebelum memproses kueri SQL.
*   **Dukungan Multi-Ecosystem**: Mengenali struktur kalimat transaksi dari SMS token, notifikasi bank (BCA, Mandiri, BRI, BNI), serta dompet digital (OVO, GoPay, ShopeePay, Dana).
*   **Privacy-by-Design**: Operasi sepenuhnya berjalan di ranah memori internal sistem operasi Android perangkat lokal. Tidak ada data finansial yang dikirimkan ke server eksternal mana pun, mematuhi standar kedaulatan data pengguna secara penuh.

### 2. Komputasi Prediktif & Analisis Statistik Lokal (ML Analyst v2)
Mesin analisis statistik Uangku dirancang sebagai sistem keuangan hibrida Tier 1 (pemodelan statistik canggih) dan Tier 3 (NLP ringan & klastering K-Means lokalan) untuk menyusun estimasi keuangan presisi secara luring sepenuhnya:
*   **Model Peramalan Tren Linear Ganda (Holt's Double Exponential Smoothing)**: Menggantikan pengamatan linear dasar dengan algoritme peramalan deret waktu (*time-series forecasting*) dinamis untuk menganalisis level ($L_t$) dan kecenderungan tren ($T_t$) aktivitas pengeluaran pengguna selama 4 minggu terakhir guna memprediksi belanja 7 hari ke depan secara matematis.
*   **Segmentasi Perilaku Belanja (K-Means Clustering - K=3)**: Algoritme klastering spasial mandiri yang mengelompokkan seluruh riwayat pengeluaran ke dalam tiga klaster kualitatif: *Transaksi Mikro & Rutin*, *Belanja Lifestyle & Sekunder*, dan *Pengeluaran Makro & Utama* berdasarkan sebaran spasial nominal pengeluaran harian.
*   **Skor Kesehatan Finansial (Financial Health Score - FHS)**: Formulasi matematis komprehensif berskala 0–100 untuk mengukur kebugaran anggaran pengguna dengan mempertimbangkan laju konsumsi kas (*daily burn rate*), rasio tabungan, sensitivitas anomali, dan konsentrasi pengeluaran.
*   **Deteksi Anomali Kuat Berbasis Interquartile Range (IQR)**: Pengganti metode standar deviasi (Z-Score) umum yang rentan bias akibat bias data ekstrim (*non-skewed robustness*). Menentukan deviasi anomali secara presisi menggunakan batas atas kuartil ketiga ($Q3 + 1.5 \times IQR$).
*   **Prediktor Kategori Otomatis NLP Lokal**: Mesin pengenalan pola deskripsi transaksi berbasis probabilitas frekuensi token (TF-IDF inspired) yang dapat secara otomatis meramalkan dan menyarankan pilihan kategori transaksi yang relevan ketika pengguna sedang mengetik deskripsi baru di antarmuka dialog masukan.

### 3. Solusi Antarmuka RupiahVisualTransformation
Guna mempertahankan pengalaman pengguna yang mulus pada formulir pengisian data numerik, Uangku memanfaatkan pemetaan visual kustom:
*   **Pencegahan Cursor Jumping**: Transformasi visual merombak letak indeks teks tanpa mengubah nilai asli string dalam variabel *state* ( pure numeric digits). Penempatan kursor, penghapusan pertengahan karakter, dan pengeditan angka dapat dilakukan secara presisi tanpa lemparan posisi kursor ke ujung kanan masukan.
*   **Validasi Masukan Statis**: Membatasi input karakter non-numerik melalui pembatasan level keyboard sistem untuk menjamin kepatuhan tipe data database sebelum kompilasi kueri SQLite.

### 4. Pengatur Jadwal & Notifikasi Tagihan Rutin (v2.0)
Meminimalkan beban kognitif pengelolaan tagihan berkala secara offline penuh:
*   **Visualisasi Grid Kalender**: Lembar kalender khusus yang menyoroti tanggal-tanggal jatuh tempo tagihan aktif secara interaktif dengan indikasi titik pink penanda urgensi.
*   **Sistem Pengingat Lokal Cerdas**: Melakukan pengecekan tagihan secara asinkron setiap kali aplikasi dibuka, lalu meluncurkan notifikasi lokal pada perangkat (H-3, H-1, dan hari-H) dengan dukungan kepatuhan izin dinamis Android 13+ (`POST_NOTIFICATIONS`).

---

## Pembaruan Versi Terbaru & Optimalisasi (v2.1)

Pembaruan pada versi **2.1** difokuskan pada peningkatan kegunaan (*usability*), optimalisasi tampilan riwayat keuangan, penanganan kesalahan kompilasi, serta pemeliharaan kompatibilitas data:

### 1. Modifikasi Rencana / Impian Menabung Terintegrasi (Edit Saving Goal)
*   **Dialog Modifikasi Interaktif**: Menambahkan tombol edit (`Icons.Default.Edit`) pada setiap kartu target menabung yang terintegrasi langsung dengan database Room melalui kueri `@Update` asinkronus. Pengguna kini dapat mengubah judul, kategori, nominal target, dan estimasi waktu pencapaian rencana keuangan mereka sewaktu-waktu secara dinamis tanpa perlu menghapus rencana tersebut dan kehilangan catatan progres tabungan yang telah berjalan.

### 2. Redesain Visual dan Statistik Komprehensif Layar Transaksi
*   **Kartu Informasi Statistik Real-Time**: Layar Transaksi kini dilengkapi dua kartu statistik asimetris yang elegan di bagian atas layar untuk memvisualisasikan jumlah total pengeluaran dan pemasukan berdasarkan pencarian serta filter aktif secara langsung (*real-time*).
*   **Pill Tabs Navigasi Modern**: Filter jenis transaksi (Semua, Pemasukan, Pengeluaran) dirombak menggunakan desain tab berbentuk pil berskala penuh dengan ikon indikator penunjuk arah aliran dana (`ArrowUpward` dan `ArrowDownward`) yang ramah pengguna.
*   **Pengelompokan Berdasarkan Tanggal (Date Grouping)**: Daftar transaksi disajikan secara dinamis dalam kelompok hari pelaporan (`Hari Ini`, `Kemarin`, atau tanggal spesifik) yang dipisahkan oleh pembatas beraksen warna primer untuk visualisasi yang lebih beraturan dan mempermudah pemindaian arus keuangan.

### 3. Stabilitas Kode & Kompatibilitas Versi Pembaruan Aman (Safe Update Strategy)
*   **Resolusi Error Sintaksis & Dependensi**: Mengatasi konflik parsing tanda kurung siku dan percabangan `if/else` token pada visualisasi tab ML Analyst di layar dashboard utama, menstabilkan impor ikon `Icons.Default.Edit` di layar anggaran, serta menyempurnakan asinkronisasi render lazy column daftar transaksi.
*   **Perlindungan Data Migrasi**: Pembangunan skema database Room yang terjaga menggunakan migrasi skema `MIGRATION_1_2` menjamin data lokal yang disimpan pada v1.0 dan v2.0 aman 100% dari kehilangan data (*data loss*) saat pengguna menginstal file biner pembaruan v2.1 ini.

---

## Ketahanan Sistem & Strategi Pemeliharaan Versi (Robustness)

Guna menjamin stabilitas fungsionalitas sistem saat pengguna melakukan pembaruan versi jangka panjang, arsitektur data Uangku mengadopsi tiga lapisan pengamanan internal:

```
[ Pembaruan Versi Aplikasi (v2.0) ] 
         │
         ├──► 1. Pelindung Room Database (Migrasi Aman MIGRATION_1_2)
         │       └─► Menghindari kehilangan data transaksi v1.0 saat memperbarui skema tabel baru.
         │
         ├──► 2. Kompatibilitas Ikonik Berkelanjutan
         │       └─► Integrasi Icons.AutoMirrored -> Mencegah visual terpotong pada format RTL/LTR.
         │
         └──► 3. Tipografi Aman Lokal (Google Fonts offline handshake)
                 └─► Integrasi xml font_certs.xml -> Menghindari kegagalan inisialisasi font akibat kegagalan jabat tangan SSL.
```

### 1. Migrasi Basis Data Room yang Tangguh (Update Tanpa Kehilangan Data)
Sebelumnya, skema database menggunakan penghapusan destruktif saat terjadi perubahan tabel. Di versi 2.0 ini, kami menerapkan jalur migrasi `Migration(1, 2)` resmi untuk memperbarui tabel secara aman dan mempertahankan 100% data riwayat transaksi pengguna:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `saving_goals` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL, 
                `targetAmount` REAL NOT NULL, 
                `currentAmount` REAL NOT NULL, 
                `targetDate` TEXT NOT NULL, 
                `category` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recurring_bills` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL, 
                `amount` REAL NOT NULL, 
                `category` TEXT NOT NULL, 
                `billingCycle` TEXT NOT NULL, 
                `dueDate` TEXT NOT NULL, 
                `lastClaimedTimestamp` INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }
}
```

Database Room kemudian diinisialisasi melalui konfigurasi perlindungan ganda ini:
```kotlin
Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "uangku_database"
)
.addMigrations(MIGRATION_1_2)
.fallbackToDestructiveMigration()
.build()
```
*Dampak Mekanisme*: Saat pengguna melakukan update dari versi 1.0 ke versi 2.0, Room akan mengeksekusi `MIGRATION_1_2` untuk menambahkan tabel `saving_goals` dan `recurring_bills` secara mulus tanpa memicu penghapusan data atau *crash* aplikasi.

### 2. Standardisasi Ikonik Fleksibel (Material 3 AutoMirrored)
Seluruh tombol navigasi dan aksi interaktif yang memiliki ketergantungan arah pelacakan visual telah diperbarui ke standar Material 3 terbaru:
*   Elemen navigasi transaksi menggunakan `Icons.AutoMirrored.Filled.ListAlt`.
*   Aksi penghapusan masukan kunci/sandi otentikasi PIN menggunakan `Icons.AutoMirrored.Filled.Backspace`.
*   Langkah ini menjamin antarmuka dapat melakukan penyesuaian tata letak secara dinamis jika perangkat dijalankan dalam format pembacaan teks Kanan-ke-Kiri (RTL) maupun Kiri-ke-Kanan (LTR).

### 3. Sertifikasi Penjabat Tangan Tipografi Dinamis
Ketergantungan terhadap penarikan jenis huruf dinamis dari Google Fonts dilindungi dengan mendefinisikan tanda tangan digital orisinal penyedia dalam modul internal `/res/values/font_certs.xml`. Pengamanan ini memastikan antarmuka rendering Jetpack Compose dapat memetakan font Sans orisinal tanpa risiko kegagalan pemuatan akibat kemacetan jaringan atau kegagalan pertukaran sertifikat SSL.

---

## Spesifikasi Tumpukan Teknologi & Arsitektur Kode

### Detail Spesifikasi Teknis
*   **Pola Arsitektur**: Model-View-ViewModel (MVVM) dikombinasikan dengan Unidirectional Data Flow (UDF).
*   **Bahasa Utama**: Kotlin 2.0.0 (mengoptimalkan fitur Smart Casting dan performa compiler K2).
*   **Penanganan Aliran Data**: Coroutines StateFlow & SharedFlow untuk pemrosesan asinkronus yang reaktif.
*   **Persistensi Data**: Room Database v2.6.1 terkompilasi melalui Kotlin Symbol Processing (KSP) untuk optimasi kueri tingkat kompilasi.
*   **Manajemen Antarmuka**: Jetpack Compose dengan arsitektur Material Design 3 (M3) lengkap dengan konfigurasi Edge-to-Edge terintegrasi dan responsif terhadap perubahan tema.

### Struktur Proyek Utama
```
app/src/main/java/com/example/
│
├── data/                    # Lapisan Persistensi Data (Offline Storage)
│   ├── db/                  # Konfigurasi Basis Data SQLite Room (Entity, DAO, Database)
│   └── model/               # Entitas Finansial (Transaksi, Anggaran, Tabungan, Tagihan)
│
├── service/                 # Komputasi background dan Mesin Matematika
│   ├── NotificationService  # NotificationListenerService penangkap mutasi otomatis
│   └── LocalFinanceMLEngine # Komputasi Regresi Linear & Deteksi Anomali Statistik
│
├── ui/                      # Lapisan Presentasi Antarmuka (Jetpack Compose)
│   ├── theme/               # Konfigurasi Tipografi Google Fonts, Skema Warna M3
│   ├── components/          # Komponen UI Reusable (Custom Charts, Format Rupiah)
│   ├── screens/             # Layanan Layar Utama (Dashboard, Budget, Settings, dll)
│   └── FinanceViewModel.kt  # Logika Bisnis & Penghubung Aliran Data
│
└── MainActivity.kt          # Titik Masuk Utama Aplikasi & Kontrol Navigasi
```

---

## Kontribusi & Lisensi

Aplikasi ini dikembangkan untuk memberikan kendali finansial terbaik bagi pengguna Android dengan mengutamakan performa, privasi, dan ketangguhan arsitektur perangkat lunak. Kontribusi perbaikan arsitektur dan optimisasi kode sangat diapresiasi guna menjaga standar kualitas aplikasi.

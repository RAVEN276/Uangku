# Uangku - Enterprise-Grade Personal Finance Manager & Local Analytics

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7-4285F4?style=flat-square&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20--%20Clean%20Design-blue?style=flat-square)]()
[![Database](https://img.shields.io/badge/Database-Room%20SQLite%20(KSP)-F89820?style=flat-square)]()

Uangku adalah solusi manajemen keuangan pribadi (*personal finance manager*) berbasis Android yang mengedepankan performa tinggi, privasi data mutlak, dan fungsionalitas luring sepenuhnya (*100% offline-first*). Dirancang menggunakan **Jetpack Compose** dan **Material Design 3**, sistem ini memberikan pengawasan arus kas presisi, otomatisasi penangkapan transaksi real-time dari notifikasi sistem, serta analisis prediktif berbasis statistika terapan langsung di dalam perangkat tanpa ketergantungan API awan atau server pihak ketiga.

---

## Matriks Fitur & Kapabilitas Sistem

| Modul Fitur | Deskripsi Fungsional | Komponen Teknis Utama | Dampak bagi Pengguna |
| :--- | :--- | :--- | :--- |
| **Penerima Mutasi Otomatis** | Ekstraksi mutasi dana masuk/keluar dari notifikasi aplikasi perbankan/dompet digital secara transparan. | `NotificationListenerService` & custom Regular Expression | Eliminasi entri data manual; pencatatan transaksi instan di latar belakang. |
| **Statistical Engine (ML Analyst)** | Komputasi kuadrat terkecil untuk memproyeksikan sisa saldo akhir bulan dan tren konsumsi harian. | Algoritme Regresi Linear Sederhana on-device | Prediksi keuangan berbasis sains untuk mencegah defisit saldo sebelum akhir bulan. |
| **Rupiah Input Formatter** | Pengetikan nominal keuangan yang aman, bebas dari kesalahan visualisasi ribu maupun gangguan lompatan kursor. | `RupiahVisualTransformation` | Pengalaman input nilai uang yang lancar, intuitif, dan bebas bug posisi kursor. |
| **Visualisasi Komprehensif** | Pemetaan kategori pengeluaran dan kurva perbandingan pendapatan secara informatif. | Donut & Line Chart berbasis Canvas & Jetpack Compose | Pemahaman instan mengenai alokasi dana terbesar dan rasio tabungan. |
| **Ekspor Laporan Resmi** | Penyediaan dokumen laporan ringkasan dalam format portabel dan tabel pengolah data. | PDF Document Canvas generator & CSV Exporter | Dokumentasi keuangan terlaporkan yang siap diarsipkan atau diolah di Excel. |

---

## Deskripsi Teknis Arsitektur & Fitur

### 1. Deteksi Mutasi Latar Belakang Terintegrasi
Subsistem pendeteksi mutasi bekerja pada tingkat sistem operasi untuk menangkap intensitas notifikasi keuangan:
*   **Normalisasi Pemisah Desimal Dinamis**: Parser kustom menyelaraskan variansi simbol penulisan numerik perbankan nasional dan internasional. Sistem menangani simbol ribuan titik (`.`) maupun koma (`,`), sekaligus melakukan pembersihan digit pecahan/sen cent secara otomatis sebelum memproses kueri SQL.
*   **Dukungan Multi-Ecosystem**: Mengenali struktur kalimat transaksi dari SMS token, notifikasi bank (BCA, Mandiri, BRI, BNI), serta dompet digital (OVO, GoPay, ShopeePay, Dana).
*   **Privacy-by-Design**: Operasi sepenuhnya berjalan di ranah memori internal sistem operasi Android perangkat lokal. Tidak ada data finansial yang dikirimkan ke server eksternal mana pun, mematuhi standar kedaulatan data pengguna secara penuh.

### 2. Komputasi Prediktif & Analisis Statistik Lokal (ML Analyst)
Mesin analisis statistik Uangku menggunakan estimasi matematis objektif untuk menyusun wawasan proyeksi keuangan:
*   **Model Regresi Linier**: Menggunakan koordinat historis pengeluaran harian ($X$: Hari ke-n, $Y$: Akumulasi Pengeluaran) untuk menghitung rumus garis regresi $Y = a + bX$. Pendekatan ini menganalisis tingkat konsumsi harian (*burn rate*) untuk memetakan sisa likuiditas di akhir bulan.
*   **Deteksi Anomali Transaksi**: Mengidentifikasi nilai pengeluaran tunggal yang melampaui rentang batas atas baku deviasi pengeluaran harian rata-rata.
*   **Rekomendasi Kontrol Anggaran**: Menyusun rekomendasi pengetatan dana bagi kategori-kategori yang mengalami laju belanja abnormal (melampaui target alokasi anggaran bulanan).

### 3. Solusi Antarmuka RupiahVisualTransformation
Guna mempertahankan pengalaman pengguna yang mulus pada formulir pengisian data numerik, Uangku memanfaatkan pemetaan visual kustom:
*   **Pencegahan Cursor Jumping**: Transformasi visual merombak letak indeks teks tanpa mengubah nilai asli string dalam variabel *state* ( pure numeric digits). Penempatan kursor, penghapusan pertengahan karakter, dan pengeditan angka dapat dilakukan secara presisi tanpa lemparan posisi kursor ke ujung kanan masukan.
*   **Validasi Masukan Statis**: Membatasi input karakter non-numerik melalui pembatasan level keyboard sistem untuk menjamin kepatuhan tipe data database sebelum kompilasi kueri SQLite.

---

## Ketahanan Sistem & Strategi Pemeliharaan Versi (Robustness)

Guna menjamin stabilitas fungsionalitas sistem saat pengguna melakukan pembaruan versi jangka panjang, arsitektur data Uangku mengadopsi tiga lapisan pengamanan internal:

```
[ Pembaruan Versi Aplikasi ] 
         │
         ├──► 1. Pelindung Room Database (Fallback Mutasi)
         │       └─► .fallbackToDestructiveMigration(dropAllTables = true) -> Aplikasi bebas dari Crash Tabel Lunak.
         │
         ├──► 2. Kompatibilitas Ikonik Berkelanjutan
         │       └─► Integrasi Icons.AutoMirrored -> Mencegah visual terpotong pada format RTL/LTR.
         │
         └──► 3. Tipografi Aman Lokal (Google Fonts offline handshake)
                 └─► Integrasi xml font_certs.xml -> Menghindari kegagalan inisialisasi font akibat kegagalan jabat tangan SSL.
```

### 1. Migrasi Basis Data Room yang Tangguh
Database Room diinisialisasi melalui parameter pengamanan berikut untuk mengantisipasi konflik struktur tabel di masa depan:
```kotlin
Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "uangku_database"
)
.fallbackToDestructiveMigration(dropAllTables = true)
.build()
```
*Dampak Mekanisme*: Apabila terjadi pembaruan skema data (misalnya penambahan tabel anggaran, tujuan tabungan, atau perbaikan atribut) pada update versi aplikasi berikutnya, sistem akan mengoordinasikan migrasi baru secara instan tanpa menghentikan paksa aplikasi (*migration crash avoidance*).

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
│   └── model/               # Entitas Finansial (Transaksi, Anggaran, Tabungan)
│
├── service/                 # Komputasi background dan Mesin Matematika
│   ├── NotificationService  # NotificationListenerService penangkap mutasi
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

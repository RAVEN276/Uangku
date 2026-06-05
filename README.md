# Uangku - Aplikasi Pencatat Keuangan Pintar & Otomatis

**Uangku** adalah aplikasi manajemen keuangan pribadi berbasis Android yang dirancang modern, cepat, dan intuitif menggunakan **Jetpack Compose** dan **Material Design 3**. Aplikasi ini membantu Anda memantau arus kas secara otomatis maupun manual dengan presisi tinggi.

---

## 🚀 Fitur Unggulan

### 1. 🔔 Auto-Detect Mutasi dari Notifikasi (Notif Listener)
Membaca dan merekam transaksi masuk atau keluar secara instan dari notifikasi aplikasi keuangan dan *m-banking* favorit seperti **BCA, Mandiri, BRI, BNI, OVO, GoPay,** dan lain-lain.
*   **Akurasi Tinggi**: Memakai algoritma ekstraksi nominal pintar yang mampu menangani beragam format angka perbankan Indonesia (mencegah salah deteksi nominal karena angka sen `,00` atau `.00`).
*   **Keamanan Terjamin**: Bekerja sepenuhnya secara lokal di perangkat Anda melalui izin *Notification Listener Service*.

### 2. ✏️ Kelola Transaksi dengan Fleksibel (Edit & Hapus)
Semua transaksi (baik yang ditangkap otomatis maupun dicatat manual) dapat dikelola kembali demi kerapihan data:
*   Ketuk transaksi apa pun untuk membuka **Dialog Edit Transaksi**.
*   Ubah keterangan, kategori, jenis transaksi (*Pengeluaran* atau *Pemasukan*), serta nominalnya.
*   Tombol hapus cepat memudahkan Anda merapikan entri yang tidak sengaja tercatat.

### 3. ✍️ Input Anggaran & Nominal dengan Separator Otomatis (Auto-Thousand Separator)
Saat menambahkan atau mengedit transaksi, Anda tidak perlu lagi menebak jumlah nol yang dimasukkan:
*   Format titik ribuan (`.`) otomatis ditambahkan saat Anda mengetik angka di kolom input (misal penulisan `4000` otomatis berubah visual menjadi `4.000` secara dinamis).
*   Membantu meminimalisir kesalahan pelaporan keuangan akibat kurang atau kelebihan menekan angka nol.

### 4. 📊 Grafik Visualisasi Finansial yang Kaya
*   **Grafik Donat Kategori**: Melihat alokasi pengeluaran bulanan berdasarkan kategori (Makanan, Belanja, Transportasi, dsb).
*   **Grafik Tren Mingguan & Bulanan**: Memantau perputaran arus kas secara dinamis.

### 5. 🎯 Rencana Masa Depan (Budget & Saving Goals)
*   **Batas Anggaran (Budgeting)**: Set target reguler per kategori dengan peringatan progres kuota persen terpakai.
*   **Rencana Impian (Saving Goals)**: Tabung uang secara terstruktur untuk mewujudkan impian finansial Anda.

### 6. 📄 Ekspor Laporan Instan
*   Unduh rangkuman keuangan Anda langsung ke format ekspor **PDF** atau **CSV** untuk kebutuhan pengarsipan maupun analisis tingkat lanjut.

---

## 🏗️ Teknologi yang Digunakan
*   **Bahasa Pemrograman**: Kotlin
*   **UI Framework**: Jetpack Compose (Material Design 3)
*   **Database Lokal**: Room Database (SQLite) untuk performa luring (*offline-first*) yang tangguh
*   **Arsitektur**: MVVM (Model-View-ViewModel) dengan StateFlow & Coroutines yang reaktif
*   **Ekspor Data**: Android PDF Canvas Writer & CSV Buffer Streamer

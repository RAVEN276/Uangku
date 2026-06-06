# Uangku - Aplikasi Pencatat Keuangan & Asisten Statistik ML Lokal

**Uangku** adalah aplikasi manajemen keuangan pribadi (*personal finance manager*) berbasis Android yang dirancang modern, cepat, dan sepenuhnya luring (*offline-first*) menggunakan **Jetpack Compose** dan **Material Design 3**. Aplikasi ini memberikan kendali finansial penuh melalui otomatisasi pintar, pemantauan anggaran presisi, dan analisis prediktif berbasis Machine Learning lokal tanpa membutuhkan server eksternal ataupun koneksi internet.

Briefly, aplikasi ini melacak keuangan Anda secara cerdas, baik melalui pencatatan manual maupun otomatisasi deteksi mutasi dari notifikasi perbankan.

---

## 🚀 Fitur Unggulan

### 1. 🔔 Detektor Mutasi Otomatis dari Notifikasi Bank & E-Wallet
Membaca dan merekam transaksi masuk atau keluar secara instan dari notifikasi aplikasi keuangan utama Indonesia secara real-time:
*   **Akurasi Parsing Multi-Format**: Sistem parser reguler cerdas (*regex parser*) mampu menangani perbedaan simbol numerik internasional dan domestik secara dinamis. Mendukung format desimal US/BCA (misalnya `IDR 16,000.00`) maupun domestik (misalnya `Rp 15.000,00`), serta menangani sen cent secara otomatis sehingga mencegah kesalahan nilai nominal.
*   **Pendeteksi Aplikasi Luas**: Mendukung format pemberitahuan transaksi dari **BCA, Mandiri, BRI, BNI, OVO, GoPay,** dan dompet digital terkemuka lainnya.
*   **Privasi Maksimal**: Data dibaca dan diolah 100% secara lokal di dalam perangkat menggunakan izin *Notification Listener Service* tanpa dikirim ke server luar mana pun.

### 2. 🧠 Uangku ML Analyst – Fitur Prediktif & Statistik Lokal
Uangku dilengkapi dengan modul **Statistical ML Engine** lokal yang berjalan sepenuhnya *on-device* untuk menyajikan wawasan analitis tingkat lanjut:
*   **Prediksi Saldo Akhir Bulan**: Menggunakan algoritme **Regresi Linear Sederhana** untuk menghitung tingkat pengeluaran harian (*daily burn rate*) dan memproyeksikan sisa saldo Anda di akhir bulan secara matematis.
*   **Identifikasi Pengeluaran Boros**: Melakukan kategorisasi historis dan merekomendasikan target reduksi anggaran untuk sektor-sektor dengan laju pengeluaran yang tidak wajar.
*   **Deteksi Anomali Finansial**: Menganalisis penyimpangan transaksi yang terlalu tinggi di luar kebiasaan pengeluaran harian untuk menjaga stabilitas arus kas Anda.
*   **Model Training Terbuka**: Proses normalisasi Min-Max, winsorization data ekstrem, dan penghitungan korelasi statistik berjalan transparan bagi pengguna.

### 3. ✍️ Input Nominal Terproteksi dengan RupiahVisualTransformation
Uangku menyelesaikan masalah bug posisi kursor (*cursor jump bug*) yang sering ditemui pada visual format rupiah konvensional:
*   **VisualTransformation Custom**: Memisahkan nilai data asli (*pure digits*) dengan visualisasi ribuan yang memakai pemisah titik (`.`).
*   **Manipulasi Kursor yang Mulus**: Pengguna dapat mengetik secara natural, menghapus di posisi mana pun, atau memindahkan kursor ke tengah tanpa takut kursor terlempar ke ujung kanan input atau berpindah ke tengah secara aneh.
*   **Input Terfokus & Aman**: Validasi keyboard tipe *Number* menjamin hanya angka yang dapat diproses oleh database.

### 4. 📊 Pelacakan Anggaran, Saving Goals & Grafik Interaktif
*   **Grafik Distribusi Kategori**: Diagram lingkaran (*Donut Chart*) beranimasi yang menunjukkan ke mana saja uang Anda mengalir berdasarkan klasifikasi pengeluaran.
*   **Grafik Arus Kas Bulanan**: Grafik tren mingguan & bulanan untuk melihat perbandingan pemasukan dan pengeluaran secara visual.
*   **Sistem Budgeting & Impian**: Tetapkan pagu batas belanja (*budget ceiling*) per kategori dengan persentase real-time dan tabungan impian (*saving goals*) terstruktur.

### 5. 📄 Ekspor Laporan Instan & Manajemen Transaksi Lengkap
*   **Ekspor Fleksibel**: Unduh rangkuman keuangan lengkap dalam format **PDF berdesain profesional** untuk dicetak, atau format **CSV** untuk pengolahan data spreadsheet lanjut.
*   **Kemudahan Sunting Data**: Ketuk transaksi apa pun untuk membuka Dialog Edit yang responsif. Anda dapat menyesuaikan nama, kategori, tipe, waktu, maupun nominal transaksi seketika.

---

## 🏗️ Teknologi & Arsitektur
*   **Bahasa Pemrograman**: Kotlin (Kotlin Coroutines & Flow)
*   **UI Framework**: Jetpack Compose dengan Material Design 3 (Edge-to-Edge Enabled)
*   **Database**: Room Database (SQLite) terintegrasi dengan KSP untuk akses data asinkronus yang cepat
*   **Analisis Lokal**: Statistika matematika terapan (Wawasan Laju / Regresi Matematik di bawah asisten analis AI lokal)
*   **Arsitektur Aplikasi**: MVVM (Model-View-ViewModel) terstandarisasi untuk pemisahan logika bisnis (*Clean Architecture*)

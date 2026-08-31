<div align="center">
  <br />
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/action/account_balance_wallet/materialicons/48dp/2x/baseline_account_balance_wallet_black_48dp.png" width="96" height="96" alt="Uangku Logo" />
  
  # UANGKU — Premium Personal Finance Ecosystem
  ### Kendali Finansial Presisi dalam Balutan Estetika Spatial Bento & On-Device Intelligence

  [![Version](https://img.shields.io/badge/version-2.2.0-emerald.svg?style=for-the-badge)](https://github.com)
  [![Platform](https://img.shields.io/badge/platform-Android_12+-blue.svg?style=for-the-badge)](https://developer.android.com)
  [![Architecture](https://img.shields.io/badge/architecture-MVVM_%2B_Compose-purple.svg?style=for-the-badge)](https://developer.android.com/jetpack/compose)
  [![Security](https://img.shields.io/badge/privacy-100%25_Offline_First-success.svg?style=for-the-badge)](https://developer.android.com)

  <p align="center">
    <b>Uangku</b> mendefinisikan ulang standar aplikasi manajemen keuangan pribadi di platform Android. Menggabungkan paradigma desain <i>Spatial Bento Grid</i> 32.dp berteknologi <i>Jetpack Compose Edge-to-Edge</i>, navigasi <i>Floating Dock</i> yang futuristik, konversi multi-valuta <i>IDR ⇄ USD</i>, serta akselerasi <i>On-Device Machine Learning</i> tanpa internet untuk privasi mutlak.
  </p>
</div>

---

## 🌟 Pilar Keunggulan Uangku v2.2

### 🗄️ 1. Animated Locker Speed-Dial (+) & OCR Receipt Scanner
Interaksi pencatatan kini hadir dengan animasi spring *loker* yang sangat memikat. Sekali ketukan pada tombol aksi utama membuka pintu masuk kilat:
- **Pengeluaran Cepat**: Form pintar dengan kategorisasi instan dan visualisasi nominal otomatis.
- **Pemasukan Cepat**: Catat pendapatan, gaji, dan dividen dalam hitungan detik.
- **Scan Receipt (Pindai Nota)**: Pengenalan optik cerdas berbasis AI lokal untuk mengekstraksi total belanja dan kategori dari struk/nota kasir tanpa perlu mengetik manual.

### 🌐 2. Dual-Currency Engine: IDR (Rupiah) & USD (Dollar)
Mengakomodasi gaya hidup global dan kebutuhan investasi lintas mata uang:
- **Konversi Dinamis Real-Time**: Beralih antara IDR dan USD dengan satu sentuhan di panel Pengaturan.
- **Kurs yang Dapat Disesuaikan**: Atur nilai tukar acuan secara mandiri untuk proyeksi pengeluaran yang akurat.
- **Konsistensi Format Terpadu**: Seluruh metriks saldo, riwayat transaksi, anggaran, analitik bento, dan simulasi ML beradaptasi langsung dengan format mata uang yang dipilih.

### 🛸 3. Floating Glassmorphic Dock Navigation
Tinggalkan bilah navigasi statis yang usang. Uangku v2.2 menghadirkan **Floating Island Navigation**:
- Melayang anggun di atas kanvas aplikasi dengan sudut lengkung 32.dp dan efek bayangan lembut.
- Dilengkapi indikator aktif berbentuk kapsul dinamis dan transisi ikon yang responsif.
- Menghadirkan *screen real estate* maksimal dengan dukungan penuh *Edge-to-Edge* dan *WindowInsets*.

### 🧠 4. Asisten Cerdas Finansial: On-Device ML Engine
Didukung oleh algoritma matematika prediktif tingkat lanjut:
- **Holt’s Double Exponential Smoothing**: Proyeksi tren pengeluaran 7-hari ke depan berbasis momentum historis.
- **Health Score & Diagnostic Index**: Evaluasi kesehatan neraca keuangan secara objektif dalam skala 0–100.
- **Deteksi Anomali & Rekomendasi Alokasi**: Peringatan dini jika pengeluaran harian melampaui ambang batas rasional.
- **100% Offline & Private**: Seluruh komputasi berlangsung di CPU perangkat pengguna tanpa mengirimkan satu byte pun data ke server eksternal.

### 🍱 5. Spatial Bento Visuals & Fluid Layout Transitions
- **32.dp Bento Grid Architecture**: Pengelompokan informasi keuangan yang intuitif, harmonis, dan modern.
- **Fluid Layout Transitions**: Setiap penambahan alokasi anggaran, pembaruan target tabungan, dan perubahan filter bertransformasi dengan animasi interpolasi pegas (*spring-physics*) yang mulus.
- **Dark & Light Dynamic Mode**: Kontras visual tinggi dengan nuansa Slate & Deep Obsidian untuk kenyamanan mata.

---

## 🛡️ Jaminan Migrasi Aman (Safe & Non-Destructive Update)

Integritas data Anda adalah komitmen utama kami. Arsitektur data Uangku v2.2 dibangun di atas fondasi **Room Database Non-Destructive Auto-Migration**:
- **Zero Data Loss Guarantee**: Seluruh data transaksi historis, profil anggaran, tagihan berulang, dan target tabungan dari versi sebelumnya terlindungi secara utuh saat aplikasi diperbarui.
- **Automatic Fallback & Integrity Check**: Sistem secara mandiri memverifikasi konsistensi relasi tabel sebelum dan sesudah inisialisasi skema baru.

---

## 🏗️ Spesifikasi Teknis & Stack

| Komponen | Implementasi Modern |
| :--- | :--- |
| **Language** | Kotlin 2.0+ (Strict Type-Safety & Coroutines) |
| **UI Toolkit** | Jetpack Compose (Material 3 Adaptive, Custom Bento Surfaces) |
| **Architecture** | MVVM + Clean Repository Pattern + StateFlow / Lifecycle Compose |
| **Persistence** | Room 2.6+ with Safe Schema Migrations & SharedPreferences Caching |
| **ML Engine** | On-Device Linear Trend & Holt's Double Exponential Forecasting |
| **Security** | Hardware KeyStore-ready PIN Authentication, 100% Offline Processing |

---

## 🚀 Kompilasi & Build

Pastikan lingkungan Anda menggunakan Android SDK 34+ dan JDK 17+.

```bash
# Verifikasi sintaks dan integritas kode
gradle :app:compileDebugKotlin

# Build APK Debug
gradle :app:assembleDebug
```

---

<div align="center">
  <sub>Uangku &copy; 2026 — Dibuat untuk menghadirkan ketenangan, keanggunan, dan kedaulatan finansial Anda.</sub>
</div>

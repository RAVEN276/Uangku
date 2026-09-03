<div align="center">
  <br />
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/action/account_balance_wallet/materialicons/48dp/2x/baseline_account_balance_wallet_black_48dp.png" width="96" height="96" alt="Uangku Logo" />
  
  # UANGKU — Personal Finance Ecosystem
  ### Precision Financial Control Wrapped in Spatial Bento Aesthetics & On-Device Intelligence

  [![Version](https://img.shields.io/badge/version-2.2.0-emerald.svg?style=for-the-badge)](https://github.com)
  [![Platform](https://img.shields.io/badge/platform-Android_12+-blue.svg?style=for-the-badge)](https://developer.android.com)
  [![Architecture](https://img.shields.io/badge/architecture-MVVM_%2B_Compose-purple.svg?style=for-the-badge)](https://developer.android.com/jetpack/compose)
  [![Security](https://img.shields.io/badge/privacy-100%25_Offline_First-success.svg?style=for-the-badge)](https://developer.android.com)

  <p align="center">
    <b>Uangku</b> is a modern, privacy-first personal finance management application for Android built with <i>Jetpack Compose</i>. It combines the tactile elegance of <i>Spatial Bento Grid (32.dp rounding)</i> design, an ergonomic <i>Floating Island Dock</i>, real-time <i>Dual-Currency (IDR ⇄ USD)</i> conversions, automated <i>Receipt OCR Scanning</i>, and <i>On-Device Machine Learning</i> forecasting—delivering complete financial clarity without ever letting your data leave your device.
  </p>
</div>

---

## 💡 What is Uangku?

**Uangku** is designed to solve a fundamental everyday problem: keeping track of where your money goes without sacrificing privacy, dealing with cluttered spreadsheets, or surrendering financial logs to third-party cloud servers.

Built from the ground up as an **offline-first ecosystem**, Uangku empowers you to:
- **Record & Categorize**: Log daily expenses and incomes with single-tap speed dials or by photographing physical store receipts.
- **Understand Cashflow**: See exactly how much you have spent this current month, compare your financial trajectories across the last 4 months, and pinpoint your peak spending categories.
- **Plan Ahead**: Allocate monthly budgets with progressive threshold warnings, automate recurring subscription reminders, and track savings goals with gamified interactive check-ins.
- **Predict Spending**: Leverage local statistical Holt's Exponential Smoothing algorithms to forecast expenditure trends for the upcoming 7 days and review your Financial Health Score (0–100).
- **Own Your Data**: Export detailed PDF statements, Excel-ready CSV spreadsheets, or full JSON backups at any moment.

---

## 🌟 Key Features

### 📊 1. Dynamic Monthly Tracking & 4-Month Comparative Analysis
- **Automatic Monthly Reset**: Income and expense metrics on the dashboard dynamically recalculate for the active calendar month, automatically rolling over when a new month begins.
- **Interactive 4-Month Summary Modal**: Tapping any income or expense card reveals an in-depth comparative breakdown:
  - Total 4-month accumulated figures and monthly averages.
  - Identification of peak spending/earning months and dominant categories.
  - Interactive chronological bar charts that let you inspect transaction details for any selected month.

### 🛡️ 2. Two-Step Safe Deletion Guard
- Eliminates accidental data loss when reviewing past transactions. Every delete action is safeguarded by an explicit confirmation dialog before records are permanently removed from the local database.

### 🔤 3. Spatial Bento Grid & Refined Typography
- Designed following modern Spatial design principles with generous 32.dp corner radii, subtle surface borders, and balanced negative space.
- Modal dialogues, action buttons (Delete, Update, Save), and category chips feature crisp typography with high-contrast text rendering for both Light and Dark themes.

### 🧾 4. Animated Speed-Dial & On-Device Receipt OCR Scanner
- **Instant Speed-Dial Dock**: An expandable floating action button provides rapid shortcuts for manual transaction logging, category budgeting, and receipt capture.
- **Camera & Gallery OCR**: Powered by Google ML Kit, the scanner reads physical shopping receipts and automatically extracts total amounts and store categories without manual typing.

### 🌐 5. Dual-Currency Engine: IDR (Rupiah) & USD (Dollar)
- **Seamless Currency Switching**: Toggle between Indonesian Rupiah (Rp) and US Dollar ($) anytime from Settings.
- **Configurable Benchmark Exchange Rate**: Set custom exchange rates for precise localized currency conversions.
- **Universal Adaptability**: All dashboard cards, historical transaction lists, budget limits, analytics, and exported files instantly reflect the active currency formatting.

### 🛸 6. Floating Glassmorphic Dock Navigation
- An elevated, pill-shaped navigation dock with rounded geometry floats effortlessly above the content canvas.
- Fully integrated with Android Edge-to-Edge display and system `WindowInsets` to maximize viewable screen area.

### 🧠 7. On-Device ML Financial Intelligence
- **Holt’s Double Exponential Smoothing**: Projects expense trajectories and estimated variance over the next 7 days based on weighted historical velocity.
- **Financial Health Score (0–100)**: Evaluates cash reserve ratios, savings rates, and budget discipline into a tangible health score.
- **Anomaly Detection & Warnings**: Provides proactive local notifications when anomalous daily spikes or lifestyle spending leaks are detected.
- **100% Client-Side Privacy**: All algorithmic evaluations execute on your phone's processor. No external API keys or cloud connections required.

### 🔒 8. Biometric & Passcode Security
- Secure your financial data with an optional 4-digit PIN lock and fingerprint / biometric hardware authentication.

---

## 🛡️ Data Integrity & Upgrade Safety Guarantee

Your financial records are invaluable. Uangku incorporates strict database safeguards:
- **Zero Data Loss Guarantee**: Updating the application to newer versions safely preserves all transactions, custom categories, monthly budgets, savings goals, and account balances.
- **Protected Room Database Migrations**: Configured with non-destructive schema migrations (`fallbackToDestructiveMigrationOnDowngrade`) to prevent unintentional database purges.
- **Full Offline Portability**: Generate encrypted JSON backups or download formatted PDF statements and CSV files directly to your device storage.

---

## 🏗️ Technical Specifications & Architecture

| Component | Modern Architecture Stack |
| :--- | :--- |
| **Language** | Kotlin 2.0+ (Strict Type-Safety, Coroutines & Flow) |
| **UI Framework** | Jetpack Compose (Material Design 3, Spatial Bento 32.dp Layouts) |
| **Architecture** | MVVM + Repository Pattern + StateFlow / Lifecycle-Aware Compose |
| **Local Storage** | Room Database 2.6+ (Protected Migrations) & SharedPreferences |
| **Vision & ML** | Google ML Kit Text Recognition (On-Device OCR) & Custom Holt's Smoother |
| **Security** | AndroidX BiometricPrompt & Local PIN Encryption |
| **Build Optimization** | Trimmed unused libraries for lightweight APK footprint & fast startup |

---

## 🚀 Compilation & Build Setup

Ensure your local development environment meets the following requirements:
- **Android Studio**: Ladybug / Koala or newer
- **JDK**: Version 17+
- **Android SDK**: Compile SDK 36 / Target SDK 36 (Minimum SDK 24 — Android 7.0+)

```bash
# Verify syntax and compile Kotlin sources
gradle :app:compileDebugKotlin

# Assemble Debug APK
gradle :app:assembleDebug
```

---

<div align="center">
  <sub>Uangku &copy; 2026 — Built to bring clarity, poise, and absolute privacy to your everyday financial life.</sub>
</div>

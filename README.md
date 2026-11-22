<div align="center">
  <br />
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/action/account_balance_wallet/materialicons/48dp/2x/baseline_account_balance_wallet_black_48dp.png" width="96" height="96" alt="Uangku Logo" />

  # Uangku: Personal Finance Manager for Android
  ### Offline-First Expense Tracking, Bank Notification Parsing, and On-Device Forecasting

  [![Version](https://img.shields.io/badge/version-2.2.5-156A46.svg?style=for-the-badge)](https://github.com)
  [![Platform](https://img.shields.io/badge/platform-Android_7.0+-blue.svg?style=for-the-badge)](https://developer.android.com)
  [![Architecture](https://img.shields.io/badge/architecture-MVVM_%2B_Compose-slate.svg?style=for-the-badge)](https://developer.android.com/jetpack/compose)
  [![Privacy](https://img.shields.io/badge/privacy-Offline_First-2E7D32.svg?style=for-the-badge)](https://developer.android.com)

  <p align="center">
    <b>Uangku</b> is an offline-first Android personal finance app built with Jetpack Compose and Material 3. All transaction logs, budgets, savings goals, receipt OCR scans, bank notification parsing, and spending forecasts stay on your device without cloud accounts or external tracking.
  </p>
</div>

---

## Overview

Uangku helps you track daily cashflow without manual spreadsheets or third-party cloud servers. Every record is stored locally in Room, and every analytical calculation runs on your phone.

With Uangku, you can:
- Log expenses and income directly from the dashboard or transaction header, or extract totals from paper receipts via Google ML Kit OCR.
- Capture real-time transactions automatically from official Indonesian banking and e-wallet notifications using a strict allowlist and duplicate filter.
- Track active monthly totals, compare 4-month income and spending history, and monitor category and daily budget thresholds.
- Review 7-day expenditure forecasts and a 0–100 Financial Health Score generated locally using Holt's Double Exponential Smoothing.
- Export multi-page PDF financial statements, CSV spreadsheets, and full JSON backups directly to device storage.

---

## Key Features

### 1. Monthly Tracking and 4-Month Comparative Analysis
- Income and expense totals on the dashboard automatically recalculate for the active calendar month and reset cleanly when a new month starts.
- Tapping the income or expense summary card opens a 4-month breakdown showing accumulated totals, monthly averages, peak months, dominant categories, and interactive bar charts.

### 2. Automatic Bank and E-Wallet Notification Reader
- Listens exclusively to an allowlist of official Indonesian bank and e-wallet apps: BCA (myBCA, BCA mobile), Mandiri (Livin'), BNI (Mobile, wondr), BRI (BRImo), BSI, CIMB Niaga, Bank Jago, SeaBank, blu by BCA, Neobank, Jenius, Allo Bank, Permata, Danamon, OCBC, Bank Mega, GoPay, OVO, DANA, ShopeePay, and LinkAja.
- Ignores ongoing/background system notifications, stale notifications, OTP/security alerts, promotional messages, and balance-only notices.
- Requires explicit currency markers and transaction action keywords before recording, with a 120-second semantic fingerprint window to prevent duplicate entries.
- Shows live notification access status in Settings so you can grant or revoke permission at any time.

### 3. On-Device Receipt OCR Scanner
- Powered by Google ML Kit Text Recognition, the scanner reads store receipts from your camera or gallery and extracts total amounts and suggested categories without uploading images.

### 4. Swipe Gestures and Two-Step Delete Guard
- In Transaction History, swipe right on any item to open the edit sheet, or swipe left to open a confirmation dialog before deleting a record from the database.
- Primary actions such as adding a transaction, scanning receipts, setting budgets, and creating savings targets sit in the screen headers and dashboard cards so floating buttons never cover transaction amounts.

### 5. Matte Slate & Editorial Forest Green Theme (Material 3 Expressive)
- Uses a solid matte Neutral Slate and Editorial Forest Green palette (`#156A46` in light mode, `#56C292` in dark mode) with rounded Bento surfaces (`32.dp`) and high-contrast text across light and dark modes.
- Supports Material You Dynamic Color on Android 12+ while keeping cards and surfaces clean matte slate for legibility.
- Adapts navigation between an opaque floating bottom dock on compact phones and a side navigation rail on wide screens and tablets.
- Displays time-of-day greetings (`Pagi/Siang/Sore/Malam` or `Morning/Afternoon/Evening`) from the device clock without requiring location permissions.

### 6. Bilingual Interface (ID & EN) and Dual Currency (IDR / USD)
- Switch between Bahasa Indonesia and English or between Indonesian Rupiah (`Rp`) and US Dollar (`$`) from Settings.
- Configure a custom benchmark exchange rate so dashboard cards, transaction lists, budgets, forecasts, and exported reports update immediately.

### 7. Multi-Page PDF Statements, CSV, and JSON Backup
- Generates formatted multi-page PDF reports containing portfolio balances, savings rate, cashflow trend bar charts, category donut charts, and a paginated transaction ledger.
- Supports full JSON backup export/import and Excel-ready CSV export.

### 8. On-Device ML Financial Intelligence
- Uses Holt's Double Exponential Smoothing to project 7-day spending trajectories from weighted historical patterns.
- Computes a Financial Health Score (0–100) based on cash reserve ratio, savings rate, and budget adherence, alongside local anomaly alerts for unusual spending spikes.

### 9. PIN and Biometric Lock
- Protects app access with a 4-digit PIN and AndroidX Biometric hardware authentication.

---

## Data Integrity and Upgrade Safety

- Room database migrations are configured without destructive wipes on normal upgrades (`fallbackToDestructiveMigrationOnDowngrade`), preserving transactions, custom categories, monthly budgets, and savings goals across updates.
- All financial data remains offline on your device unless you manually export and share a backup file.

---

## Technical Specifications

| Component | Stack |
| :--- | :--- |
| Language | Kotlin 2.2.10 (Coroutines & Flow) |
| UI Framework | Jetpack Compose (BOM 2024.09.00, Material 3) |
| Architecture | MVVM + Repository Pattern + `StateFlow` |
| Local Storage | Room 2.7.0 (with KSP) & SharedPreferences |
| Vision & ML | Google ML Kit Text Recognition (`16.0.1`) & On-Device Holt's Smoother |
| Security | AndroidX Biometric (`1.1.0`) & Local PIN Storage |
| Testing | JUnit, Kotlin Coroutines Test, Robolectric (`4.16.1`), Roborazzi (`1.59.0`) |
| Android SDK | Compile SDK 36.1 / Target SDK 36 / Minimum SDK 24 (Android 7.0+) |

---

## Build and Verification

Requirements:
- Android Studio Ladybug / Koala or newer
- JDK 17+
- Android SDK 36

```bash
# Compile Kotlin sources
gradle :app:compileDebugKotlin

# Run local JVM and Robolectric unit tests
gradle :app:testDebugUnitTest

# Build Debug APK
gradle :app:assembleDebug
```

---

## Changelog

### v2.2.5 (November 2026)

**UI & Theme Overhaul**
- Replaced high-saturation gradients and bright fills with a solid matte Neutral Slate and Editorial Forest Green palette (`#156A46` light primary, `#56C292` dark primary, `#F6F8F7` light canvas, `#0F1315` dark canvas).
- Kept cards, containers, and surface borders clean matte slate even when Material You Dynamic Color is enabled, preventing tinted washout on dashboard, budget, and transaction cards.
- Fixed dark-mode and light-mode typography contrast across Budget cards, modal dialogs, AI insight cards, and transaction lists by enforcing explicit `onSurface` and semantic container colors and wrapping the root layout in `Surface`.

**Navigation & Action Ergonomics**
- Removed floating bottom-right `+` and speed-dial buttons (`LockerSpeedDialMenu`) from Dashboard, Transactions, and Budgets so action buttons no longer cover transaction amounts at the bottom of the screen.
- Moved primary creation actions (`+ Catat`, `Pindai Nota`, `Atur Batas`, `Target Baru`) into the screen headers and dashboard cards.
- Added an Expense/Income `ExpressiveSegmentedSelector` directly inside Quick Add and Add Transaction modals.

**Bug Fixes & Stability**
- Fixed intermittent swipe-to-delete and swipe-to-edit failures in Transaction History (`TransactionsScreen`) by keying swipe containers with `key(tx.id)`, tuning `positionalThreshold` to `0.28f`, keeping callback state current via `rememberUpdatedState`, and snapping swipe rows back to `Settled` after triggering an action.
- Hardened `MyNotificationListenerService` with an official bank and e-wallet package allowlist, ongoing/stale notification filtering, OTP/promo/balance-notice blacklists, strict currency regexes, and 120-second semantic deduplication to prevent phantom expense or income entries.
- Corrected `README.md` feature numbering, updated build and dependency specs, and aligned documentation with the current UI.

### v2.2.0
- Added on-device ML spending forecasts (Holt's Double Exponential Smoothing), 4-month comparative charts, multi-page PDF financial statements, bilingual (`ID`/`EN`) and dual-currency (`IDR`/`USD`) switching, and two-step delete confirmation.

---

<div align="center">
  <sub>Uangku &copy; 2026. Offline-first personal finance tracking for Android.</sub>
</div>

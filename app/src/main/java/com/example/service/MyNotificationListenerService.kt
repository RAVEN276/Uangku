package com.example.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Locale

class MyNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var serviceConnectedTime: Long = System.currentTimeMillis()

    override fun onListenerConnected() {
        super.onListenerConnected()
        serviceConnectedTime = System.currentTimeMillis()
        Log.d(TAG, "NotificationListener connected at $serviceConnectedTime")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName?.trim()?.lowercase(Locale.ROOT) ?: return
        val selfPkg = applicationContext.packageName.trim().lowercase(Locale.ROOT)
        if (packageName == selfPkg) {
            return
        }

        // Strict whitelist gate before reading any notification content
        if (!isOfficialFinancialPackage(packageName)) {
            return
        }

        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(PREF_BANK_NOTIF_ENABLED, false)
        if (!isEnabled) {
            return
        }

        val notification = sbn.notification ?: return
        val flags = notification.flags
        if (sbn.isOngoing ||
            (flags and Notification.FLAG_ONGOING_EVENT) != 0 ||
            (flags and Notification.FLAG_FOREGROUND_SERVICE) != 0 ||
            (flags and Notification.FLAG_GROUP_SUMMARY) != 0 ||
            (flags and Notification.FLAG_LOCAL_ONLY) != 0
        ) {
            return
        }

        val now = System.currentTimeMillis()
        val postTime = sbn.postTime
        if (postTime <= 0L || (now - postTime) > MAX_NOTIFICATION_AGE_MS || postTime < (serviceConnectedTime - CONNECT_GRACE_MS)) {
            Log.d(TAG, "Ignoring stale notification from $packageName (postTime=$postTime, connected=$serviceConnectedTime)")
            return
        }

        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()

        val combinedText = listOf(bigText, text, subText)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .joinToString(" ")

        if (title.isBlank() && combinedText.isBlank()) return

        val sbnKey = sbn.key ?: "$packageName:$postTime"
        if (isDuplicateKeyOrRecentUpdate(sbnKey, postTime, now)) {
            Log.d(TAG, "Ignoring duplicate/updated notification key=$sbnKey")
            return
        }

        val transaction = parseNotification(packageName, title, combinedText) ?: return

        val semanticFingerprint = buildFingerprint(packageName, transaction.type, transaction.amount, title)
        if (isDuplicateFingerprint(prefs, semanticFingerprint, now)) {
            Log.d(TAG, "Ignoring duplicate transaction fingerprint=$semanticFingerprint")
            return
        }

        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.financeDao().insertTransaction(transaction)
                Log.d(TAG, "Recorded bank notification transaction: ${transaction.bankSource} | ${transaction.type} | ${transaction.amount}")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting notification transaction", e)
            }
        }
    }

    companion object {
        private const val TAG = "UangkuNotif"
        private const val PREFS_NAME = "uangku_prefs"
        private const val PREF_BANK_NOTIF_ENABLED = "bank_notification_enabled"
        private const val PREF_RECENT_FINGERPRINTS = "recent_notif_fingerprints_v2"
        private const val MAX_NOTIFICATION_AGE_MS = 3 * 60 * 1000L
        private const val CONNECT_GRACE_MS = 30 * 1000L
        private const val DUPLICATE_WINDOW_MS = 120 * 1000L
        private const val MAX_TRANSACTION_AMOUNT = 500_000_000_000.0

        val OFFICIAL_FINANCIAL_PACKAGES: Map<String, String> = mapOf(
            // Official Indonesian Banks
            "com.bca" to "BCA",
            "com.bca.mybca.omni.android" to "BCA",
            "id.co.bankmandiri.livin" to "Mandiri",
            "com.bankmandiri.mandirionline" to "Mandiri",
            "src.com.bni" to "BNI",
            "id.co.bni.wondr" to "BNI",
            "com.bri.brimo" to "BRI",
            "id.co.bri.brimo" to "BRI",
            "com.bsm.activity2" to "BSI",
            "com.cimbniaga.mobile.android" to "CIMB Niaga",
            "id.co.cimbniaga.mobile.android" to "CIMB Niaga",
            "com.jago.digitalbanking" to "Bank Jago",
            "id.co.seabank.seabankid" to "SeaBank",
            "com.seabank.seabankid" to "SeaBank",
            "com.bcadigital.blu" to "blu by BCA",
            "com.bnc.finance" to "Neobank",
            "com.btpn.dc" to "Jenius",
            "com.allobank.app" to "Allo Bank",
            "net.myinfosys.permatamobilex" to "Permata",
            "com.danamon.dbank" to "Danamon",
            "com.ocbcnisp.onemobileapp" to "OCBC",
            "com.bankmega.msmile" to "Bank Mega",
            // Official Indonesian E-Wallets
            "com.gojek.app" to "GoPay",
            "com.gojek.gopay" to "GoPay",
            "ovo.id" to "OVO",
            "id.dana" to "DANA",
            "com.shopee.id" to "ShopeePay",
            "com.shopeepay.id" to "ShopeePay",
            "com.telkom.mwallet" to "LinkAja"
        )

        private val recentKeyPostTimes = Collections.synchronizedMap(
            object : LinkedHashMap<String, Long>(64, 0.75f, true) {
                override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Long>?): Boolean = size > 128
            }
        )

        private val recentFingerprintsMemory = Collections.synchronizedMap(
            object : LinkedHashMap<String, Long>(64, 0.75f, true) {
                override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Long>?): Boolean = size > 128
            }
        )

        // Strict word-boundary regexes
        private val CURRENCY_PREFIX_REGEX = Regex("\\b(?:rp|idr)\\.?\\s*[0-9]", RegexOption.IGNORE_CASE)
        private val NEGATIVE_CURRENCY_REGEX = Regex("(?:^|[\\s(\\[])-(?:\\s*(?:rp|idr)\\.?)?\\s*[0-9]+|(?:rp|idr)\\.?\\s*-\\s*[0-9]+", RegexOption.IGNORE_CASE)
        private val CURRENCY_AMOUNT_REGEX = Regex("\\b(?:rp|idr)\\.?\\s*([0-9][0-9.,]*)", RegexOption.IGNORE_CASE)
        private val LABELED_AMOUNT_REGEX = Regex("\\b(?:nominal|sebesar|jumlah)\\s*:?\\s*(?:rp|idr)?\\.?\\s*([0-9]{1,3}(?:[.,][0-9]{3})+(?:[.,][0-9]{1,2})?)", RegexOption.IGNORE_CASE)

        private val NON_TRANSACTION_BLACKLIST = listOf(
            "promo", "diskon", "discount", "hadiah", "voucher", "gratis", "hemat",
            "undian", "untung", "bonus", "yuk", "dapatkan", "nikmati", "kupon",
            "spesial", "special", "terbatas", "murah", "buruan", "mulai dari",
            "menangkan", "kesempatan", "klaim", "ajak", "s.d", "s/d", "penawaran",
            "pinjaman", "ajukan", "butuh dana", "otp", "kode otp", "kode verifikasi",
            "pin transaksi", "token", "keamanan", "waspada", "penipu", "hati-hati",
            "verifikasi", "verification", "login", "masuk ke akun", "perangkat baru",
            "call center", "syarat", "ketentuan", "s&k", "jatuh tempo", "reminder",
            "pengingat", "aktifkan", "aktivasi", "upgrade", "minimal", "minimum",
            "sisa saldo", "saldo akhir", "saldo efektif", "saldo tersedia", "cek saldo",
            "saldo anda saat ini", "saldo minimum", "informasi saldo", "info saldo",
            "limit kredit", "limit harian", "belum dibayar", "menunggu pembayaran",
            "selesaikan pembayaran", "bayar sebelum", "kode pembayaran", "tagihan bulan",
            "kadaluarsa", "expired", "pemberitahuan login", "password", "kata sandi",
            "blokir", "buka blokir", "tidak cukup", "saldo kurang", "gagal", "failed",
            "ditolak", "declined", "batal", "cancelled", "canceled", "dibatalkan",
            "bukan transaksi"
        )

        private val TRANSACTION_ACTION_REGEX = Regex(
            "\\b(?:transfer|debet|debit|kredit|masuk|keluar|pemasukan|pengeluaran|pembelian|pembayaran|berhasil|sukses|success|paid|diterima|dibayar|bayar|top\\s?up|isi\\s+saldo|pengisian\\s+saldo|tarik\\s+tunai|qris|belanja|terpotong|autodebet|pencairan|cashback\\s+diterima)\\b",
            RegexOption.IGNORE_CASE
        )

        private val EXPENSE_INTENT_REGEX = Regex(
            "\\b(?:pembayaran|pembelian|bayar|dibayar|belanja|qris|tarik\\s+tunai|debet|debit|uang\\s+keluar|dana\\s+keluar|transfer\\s+ke|transfer\\s+keluar|terpotong|autodebet|pengeluaran|expense|biaya\\s+admin)\\b",
            RegexOption.IGNORE_CASE
        )

        private val INCOME_INTENT_REGEX = Regex(
            "\\b(?:dana\\s+masuk|transfer\\s+masuk|uang\\s+masuk|saldo\\s+masuk|kredit\\s+ke\\s+rekening|kredit|telah\\s+diterima|diterima|menerima\\s+transfer|menerima\\s+dana|pembayaran\\s+diterima|cashback\\s+diterima|pencairan|top\\s?up\\s+(?:berhasil|sukses|masuk)|isi\\s+saldo\\s+(?:berhasil|sukses|masuk)|pengisian\\s+saldo|bunga\\s+rekening|pemasukan|income|masuk\\s+ke\\s+rekening|dikredit)\\b",
            RegexOption.IGNORE_CASE
        )

        fun isOfficialFinancialPackage(packageName: String): Boolean {
            return OFFICIAL_FINANCIAL_PACKAGES.containsKey(packageName.trim().lowercase(Locale.ROOT))
        }

        fun isNotificationServiceEnabled(context: Context): Boolean {
            return try {
                val flat = Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners"
                ) ?: return false
                val componentName = ComponentName(context, MyNotificationListenerService::class.java)
                val shortName = "${componentName.packageName}/${componentName.shortClassName}"
                val fullName = componentName.flattenToString()
                flat.split(":").any { entry ->
                    entry.equals(fullName, ignoreCase = true) || entry.equals(shortName, ignoreCase = true)
                }
            } catch (_: Exception) {
                false
            }
        }

        private fun isDuplicateKeyOrRecentUpdate(key: String, postTime: Long, now: Long): Boolean {
            synchronized(recentKeyPostTimes) {
                val prev = recentKeyPostTimes[key]
                if (prev != null && (prev == postTime || kotlin.math.abs(now - prev) < DUPLICATE_WINDOW_MS)) {
                    return true
                }
                recentKeyPostTimes[key] = now
                return false
            }
        }

        private fun buildFingerprint(packageName: String, type: String, amount: Double, title: String): String {
            val normTitle = title.lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]"), "").take(32)
            return "$packageName|$type|${amount.toLong()}|$normTitle"
        }

        private fun isDuplicateFingerprint(
            prefs: android.content.SharedPreferences,
            fingerprint: String,
            now: Long
        ): Boolean {
            synchronized(recentFingerprintsMemory) {
                val prevMem = recentFingerprintsMemory[fingerprint]
                if (prevMem != null && (now - prevMem) < DUPLICATE_WINDOW_MS) {
                    return true
                }

                val savedRaw = prefs.getString(PREF_RECENT_FINGERPRINTS, "").orEmpty()
                val activeEntries = savedRaw.split(";")
                    .mapNotNull { entry ->
                        val parts = entry.split("@")
                        if (parts.size == 2) {
                            val ts = parts[1].toLongOrNull()
                            if (ts != null && (now - ts) < DUPLICATE_WINDOW_MS) parts[0] to ts else null
                        } else null
                    }
                    .toMutableList()

                if (activeEntries.any { it.first == fingerprint }) {
                    recentFingerprintsMemory[fingerprint] = now
                    return true
                }

                recentFingerprintsMemory[fingerprint] = now
                activeEntries.add(fingerprint to now)
                val trimmed = activeEntries.takeLast(25).joinToString(";") { "${it.first}@${it.second}" }
                prefs.edit().putString(PREF_RECENT_FINGERPRINTS, trimmed).apply()
                return false
            }
        }

        fun parseNotification(packageName: String, title: String, text: String): Transaction? {
            val normalizedPkg = packageName.trim().lowercase(Locale.ROOT)
            val bankSource = OFFICIAL_FINANCIAL_PACKAGES[normalizedPkg] ?: return null

            val rawContent = "$title $text".trim()
            if (rawContent.isEmpty()) return null
            val content = rawContent.lowercase(Locale.ROOT)

            // Reject promotional, security/OTP, informational balance, or failed/unpaid messages
            if (NON_TRANSACTION_BLACKLIST.any { keyword ->
                    if (keyword.contains(" ") || keyword.contains("&") || keyword.contains(".") || keyword.contains("/")) {
                        content.contains(keyword)
                    } else {
                        Regex("\\b${Regex.escape(keyword)}\\b").containsMatchIn(content)
                    }
                }
            ) {
                return null
            }

            // Require explicit currency indicator ("Rp" / "IDR" / formatted "nominal ...")
            val hasCurrencyMarker = CURRENCY_PREFIX_REGEX.containsMatchIn(content) ||
                LABELED_AMOUNT_REGEX.containsMatchIn(content)
            if (!hasCurrencyMarker) return null

            // Reject negative monetary amounts (never record negative expenses/incomes)
            if (NEGATIVE_CURRENCY_REGEX.containsMatchIn(content)) return null

            // Require an explicit financial transaction action keyword
            if (!TRANSACTION_ACTION_REGEX.containsMatchIn(content)) return null

            // Extract and validate positive numerical amount
            val amount = extractAmount(content) ?: return null
            if (amount <= 0.0 || amount > MAX_TRANSACTION_AMOUNT || amount.isNaN() || amount.isInfinite()) {
                return null
            }

            // Remove polite phrases like "terima kasih" before classifying INCOME vs EXPENSE
            val intentContent = content
                .replace(Regex("\\bterima\\s+kasih\\b"), " ")
                .replace(Regex("\\bthank\\s+you\\b"), " ")

            val hasExpenseSignal = EXPENSE_INTENT_REGEX.containsMatchIn(intentContent)
            val hasIncomeSignal = INCOME_INTENT_REGEX.containsMatchIn(intentContent)

            val isIncome = when {
                hasIncomeSignal && !hasExpenseSignal -> true
                hasExpenseSignal && !hasIncomeSignal -> false
                hasIncomeSignal && hasExpenseSignal -> {
                    Regex("\\b(?:dana\\s+masuk|transfer\\s+masuk|uang\\s+masuk|pembayaran\\s+diterima|pencairan|top\\s?up\\s+(?:berhasil|sukses))\\b")
                        .containsMatchIn(intentContent)
                }
                else -> false
            }

            val type = if (isIncome) "INCOME" else "EXPENSE"

            val category = when {
                isIncome && (content.contains("gaji") || content.contains("payroll") || content.contains("salary")) -> "Gaji"
                isIncome && (content.contains("bunga") || content.contains("dividen") || content.contains("deposito")) -> "Investasi"
                content.contains("food") || content.contains("makan") || content.contains("kuliner") || content.contains("warung") || content.contains("resto") -> "Makanan"
                content.contains("grab") || content.contains("gojek") || content.contains("ojek") || content.contains("mrt") || content.contains("krl") || content.contains("tiket") || content.contains("bensin") || content.contains("pertamina") -> "Transportasi"
                content.contains("pln") || content.contains("listrik") || content.contains("wifi") || content.contains("indihome") || content.contains("sewa") || content.contains("kontrakan") || content.contains("pdam") -> "Tagihan"
                !isIncome && (content.contains("belanja") || content.contains("tokopedia") || content.contains("shopee") || content.contains("lazada") || content.contains("pembelian") || content.contains("qris")) -> "Belanja"
                content.contains("reksa") || content.contains("saham") || content.contains("bibit") || content.contains("investasi") -> "Investasi"
                else -> "Lainnya"
            }

            val displayTitle = when {
                content.contains("gofood") -> "GoFood Delivery"
                content.contains("grabfood") -> "GrabFood Order"
                content.contains("tokopedia") -> "Belanja Tokopedia"
                content.contains("shopee") -> if (isIncome) "Top Up ShopeePay" else "Belanja Shopee"
                content.contains("pln") || content.contains("listrik") -> "Tagihan Listrik PLN"
                content.contains("qris") -> "Pembayaran QRIS $bankSource"
                isIncome && (content.contains("gaji") || content.contains("payroll")) -> "Gaji Bulanan"
                isIncome && (content.contains("top up") || content.contains("topup") || content.contains("isi saldo") || content.contains("pengisian saldo")) -> "Top Up $bankSource"
                isIncome -> "Dana Masuk $bankSource"
                else -> "Transaksi $bankSource"
            }

            return Transaction(
                title = displayTitle,
                amount = amount,
                type = type,
                category = category,
                timestamp = System.currentTimeMillis(),
                bankSource = bankSource
            )
        }

        internal fun extractAmount(text: String): Double? {
            return try {
                val clean = text.lowercase(Locale.ROOT).replace(",-", "")

                val currencyMatch = CURRENCY_AMOUNT_REGEX.find(clean)
                if (currencyMatch != null) {
                    val candidate = currencyMatch.groupValues[1]
                    val parsed = parseFormattedNumber(candidate)
                    if (parsed != null && parsed > 0.0) return parsed
                }

                val labeledMatch = LABELED_AMOUNT_REGEX.find(clean)
                if (labeledMatch != null) {
                    val candidate = labeledMatch.groupValues[1]
                    val parsed = parseFormattedNumber(candidate)
                    if (parsed != null && parsed > 0.0) return parsed
                }

                null
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing amount", e)
                null
            }
        }

        private fun parseFormattedNumber(numStr: String): Double? {
            var s = numStr.trim().removeSuffix(",").removeSuffix(".")
            if (s.isEmpty()) return null

            if (s.contains(".") && s.contains(",")) {
                val lastDotIdx = s.lastIndexOf('.')
                val lastCommaIdx = s.lastIndexOf(',')
                if (lastDotIdx > lastCommaIdx) {
                    val centsPart = s.substring(lastDotIdx + 1)
                    val mainPart = s.substring(0, lastDotIdx).replace(",", "")
                    s = if (centsPart == "00") mainPart else "$mainPart.$centsPart"
                } else {
                    val centsPart = s.substring(lastCommaIdx + 1)
                    val mainPart = s.substring(0, lastCommaIdx).replace(".", "")
                    s = if (centsPart == "00") mainPart else "$mainPart.$centsPart"
                }
            } else if (s.contains(",")) {
                val lastCommaIdx = s.lastIndexOf(',')
                val afterComma = s.substring(lastCommaIdx + 1)
                val commaCount = s.count { it == ',' }
                s = if (commaCount > 1 || afterComma.length == 3 || afterComma.length > 2) {
                    s.replace(",", "")
                } else if (afterComma == "00") {
                    s.substring(0, lastCommaIdx)
                } else {
                    s.substring(0, lastCommaIdx) + "." + afterComma
                }
            } else if (s.contains(".")) {
                val lastDotIdx = s.lastIndexOf('.')
                val afterDot = s.substring(lastDotIdx + 1)
                val dotCount = s.count { it == '.' }
                s = if (dotCount > 1 || afterDot.length == 3 || afterDot.length > 2) {
                    s.replace(".", "")
                } else if (afterDot == "00") {
                    s.substring(0, lastDotIdx)
                } else {
                    s.substring(0, lastDotIdx) + "." + afterDot
                }
            }

            val value = s.toDoubleOrNull() ?: return null
            return if (value > 0.0 && value.isFinite()) value else null
        }
    }
}

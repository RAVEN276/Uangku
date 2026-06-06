package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MyNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val packageName = sbn.packageName ?: ""

        Log.d("UangkuNotif", "Package: $packageName | Title: $title | Text: $text")

        val transaction = parseNotification(packageName, title, text)
        if (transaction != null) {
            serviceScope.launch {
                try {
                    val db = AppDatabase.getDatabase(applicationContext)
                    db.financeDao().insertTransaction(transaction)
                    Log.d("UangkuNotif", "Success inserting Transaction: ${transaction.title}")
                } catch (e: Exception) {
                    Log.e("UangkuNotif", "Error inserting transaction", e)
                }
            }
        }
    }

    companion object {
        fun parseNotification(packageName: String, title: String, text: String): Transaction? {
            val content = "$title $text".lowercase()
            
            // Tight blacklist of promo/marketing keywords to prevent promotional notifications
            val isPromo = content.contains("promo") ||
                    content.contains("diskon") ||
                    content.contains("discount") ||
                    content.contains("hadiah") ||
                    content.contains("voucher") ||
                    content.contains("gratis") ||
                    content.contains("free") ||
                    content.contains("hemat") ||
                    content.contains("undian") ||
                    content.contains("untung") ||
                    content.contains("bonus") ||
                    content.contains("yuk") ||
                    content.contains("dapatkan") ||
                    content.contains("nikmati") ||
                    content.contains("kupon") ||
                    content.contains("spesial") ||
                    content.contains("special") ||
                    content.contains("terbatas") ||
                    content.contains("murah") ||
                    content.contains("buruan") ||
                    content.contains("mulai dari") ||
                    content.contains("menangkan") ||
                    content.contains("kesempatan") ||
                    content.contains("klik") ||
                    content.contains("klaim") ||
                    content.contains("ajak") ||
                    content.contains("s.d") ||
                    content.contains("hingga") ||
                    content.contains("selesaikan") ||
                    content.contains("butuh") ||
                    content.contains("penawaran") ||
                    content.contains("pinjaman") ||
                    content.contains("ajukan") ||
                    content.contains("menunggu") ||
                    content.contains("butuh dana")
            
            // Check if it looks like a monetary transaction notification in Indonesia
            val containsRp = content.contains("rp") || content.contains("idr") || content.contains("nominal")
            val isTransaction = containsRp && !isPromo && (
                content.contains("transfer") || 
                content.contains("debet") || 
                content.contains("debit") || 
                content.contains("kredit") || 
                content.contains("masuk") || 
                content.contains("keluar") ||
                content.contains("pemasukan") ||
                content.contains("pengeluaran") ||
                content.contains("pembelian") || 
                content.contains("pembayaran") || 
                content.contains("berhasil") ||
                content.contains("sukses") ||
                content.contains("success") ||
                content.contains("paid") ||
                content.contains("terima") ||
                content.contains("diterima") ||
                content.contains("dibayar") ||
                content.contains("bayar") ||
                content.contains("gopay") ||
                content.contains("ovo") ||
                content.contains("dana") ||
                content.contains("saldo")
            )
            
            if (!isTransaction) return null

            // Detect transaction type (income / expense)
            val isIncome = content.contains("masuk") || 
                           content.contains("kredit") || 
                           content.contains("terima") || 
                           content.contains("pemasukan") ||
                           content.contains("income") ||
                           content.contains("topup") ||
                           content.contains("top up") ||
                           content.contains("bunga")
            val type = if (isIncome) "INCOME" else "EXPENSE"

            // Extract the numerical amount
            val amount = extractAmount(content) ?: return null

            // Guess category
            var category = "Lainnya"
            if (content.contains("food") || content.contains("makan") || content.contains("kuliner") || content.contains("warung") || content.contains("resto")) {
                category = "Makanan"
            } else if (content.contains("grab") || content.contains("gojek") || content.contains("ojek") || content.contains("trans") || content.contains("mrt") || content.contains("krl") || content.contains("tiket") || content.contains("bensin") || content.contains("pertamina")) {
                category = "Transportasi"
            } else if (content.contains("belanja") || content.contains("tokopedia") || content.contains("shopee") || content.contains("lazada") || content.contains("buku") || content.contains("pembelian") || content.contains("qr")) {
                category = "Belanja"
            } else if (content.contains("gaji") || content.contains("payroll") || content.contains("salary") || content.contains("insentif")) {
                category = "Gaji"
            } else if (content.contains("reksa") || content.contains("saham") || content.contains("bibit") || content.contains("investasi") || content.contains("deposito") || content.contains("interest")) {
                category = "Investasi"
            } else if (content.contains("pln") || content.contains("listrik") || content.contains("wifi") || content.contains("indihome") || content.contains("sewa") || content.contains("kontrakan") || content.contains("pbb")) {
                category = "Sewa"
            }

            // Determine Bank/Wallet source from package name or text
            var bankSource = "Bank Notifikasi"
            val pkg = packageName.lowercase()
            if (pkg.contains("bca") || content.contains("bca")) {
                bankSource = "BCA"
            } else if (pkg.contains("mandiri") || content.contains("mandiri")) {
                bankSource = "Mandiri"
            } else if (pkg.contains("bni") || content.contains("bni")) {
                bankSource = "BNI"
            } else if (pkg.contains("bri") || content.contains("bri")) {
                bankSource = "BRI"
            } else if (pkg.contains("ovo") || content.contains("ovo")) {
                bankSource = "OVO"
            } else if (pkg.contains("gojek") || content.contains("gopay")) {
                bankSource = "GoPay"
            }

            // Refine display title based on details
            val displayTitle = when {
                content.contains("gofood") -> "GoFood Delivery ($bankSource)"
                content.contains("grabfood") -> "GrabFood Order ($bankSource)"
                content.contains("tokopedia") -> "Belanja Tokopedia ($bankSource)"
                content.contains("shopee") -> "Belanja Shopee ($bankSource)"
                content.contains("pln") || content.contains("listrik") -> "Tagihan Listrik PLN ($bankSource)"
                content.contains("gaji") -> "Gaji Bulanan ($bankSource)"
                isIncome -> "Transfer Masuk SBN ($bankSource)"
                else -> "Auto Transaksi ($bankSource)"
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

        private fun extractAmount(text: String): Double? {
            try {
                val lowerText = text.lowercase()
                val clean = lowerText.replace(",-", "")

                val pattern = Pattern.compile("(?:rp|idr|nominal|sebesar|jumlah)\\.?\\s*([\\d\\.,]+)")
                val matcher = pattern.matcher(clean)
                if (matcher.find()) {
                    val candidate = matcher.group(1) ?: ""
                    val parsed = parseFormattedNumber(candidate)
                    if (parsed != null) return parsed
                }

                val patternWithDots = Pattern.compile("(\\d{1,3}(?:\\.\\d{3})+(?:,\\d{2})?)")
                val matcherWithDots = patternWithDots.matcher(clean)
                if (matcherWithDots.find()) {
                    val candidate = matcherWithDots.group(1) ?: ""
                    val parsed = parseFormattedNumber(candidate)
                    if (parsed != null) return parsed
                }

                val patternDigits = Pattern.compile("\\b\\d{4,9}\\b")
                val matcherDigits = patternDigits.matcher(clean)
                if (matcherDigits.find()) {
                    return matcherDigits.group().toDoubleOrNull()
                }
            } catch (e: Exception) {
                Log.e("UangkuNotif", "Error parsing amount", e)
            }
            return null
        }

        private fun parseFormattedNumber(numStr: String): Double? {
            var s = numStr.trim()
            if (s.isEmpty()) return null

            // Clean any trailing dots or commas
            s = s.trim().removeSuffix(",").removeSuffix(".")

            if (s.contains(".") && s.contains(",")) {
                val lastDotIdx = s.lastIndexOf('.')
                val lastCommaIdx = s.lastIndexOf(',')
                if (lastDotIdx > lastCommaIdx) {
                    // Dot is the decimal separator (US/BCA style: 16,000.50)
                    val centsPart = s.substring(lastDotIdx + 1)
                    val mainPart = s.substring(0, lastDotIdx).replace(",", "")
                    s = if (centsPart == "00") mainPart else "$mainPart.$centsPart"
                } else {
                    // Comma is the decimal separator (Indonesian style: 16.000,50)
                    val centsPart = s.substring(lastCommaIdx + 1)
                    val mainPart = s.substring(0, lastCommaIdx).replace(".", "")
                    s = if (centsPart == "00") mainPart else "$mainPart.$centsPart"
                }
            } else if (s.contains(",")) {
                val lastCommaIdx = s.lastIndexOf(',')
                val afterComma = s.substring(lastCommaIdx + 1)
                val commaCount = s.count { it == ',' }
                if (commaCount > 1) {
                    s = s.replace(",", "")
                } else {
                    if (afterComma.length == 3 || afterComma.length > 2) {
                        s = s.replace(",", "")
                    } else if (afterComma.length == 2 && afterComma == "00") {
                        s = s.substring(0, lastCommaIdx)
                    } else {
                        s = s.substring(0, lastCommaIdx) + "." + afterComma
                    }
                }
            } else if (s.contains(".")) {
                val lastDotIdx = s.lastIndexOf('.')
                val afterDot = s.substring(lastDotIdx + 1)
                val dotCount = s.count { it == '.' }
                if (dotCount > 1) {
                    s = s.replace(".", "")
                } else {
                    if (afterDot.length == 3 || afterDot.length > 2) {
                        s = s.replace(".", "")
                    } else if (afterDot.length == 2 && afterDot == "00") {
                        s = s.substring(0, lastDotIdx)
                    } else {
                        s = s.substring(0, lastDotIdx) + "." + afterDot
                    }
                }
            }

            return s.toDoubleOrNull()
        }
    }
}

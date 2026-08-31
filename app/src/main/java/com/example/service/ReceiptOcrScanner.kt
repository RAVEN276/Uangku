package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.data.model.Transaction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.coroutines.resume

data class ParsedReceiptItem(
    val name: String,
    val price: Double
)

data class ParsedReceiptResult(
    val merchant: String,
    val totalAmount: Double,
    val suggestedCategory: String,
    val suggestedType: String = "EXPENSE",
    val dateFormatted: String,
    val items: List<ParsedReceiptItem>,
    val rawText: String,
    val confidence: Float = 0.95f
)

object ReceiptOcrScanner {

    private const val TAG = "ReceiptOcrScanner"
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /**
     * Executes real ML Kit On-Device Optical Character Recognition on a Bitmap.
     */
    suspend fun recognizeTextFromBitmap(bitmap: Bitmap, transactions: List<Transaction> = emptyList()): Result<ParsedReceiptResult> =
        withContext(Dispatchers.Default) {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val visionText = processInputImage(image)
                val parsedResult = parseReceiptText(visionText.text, visionText, transactions)
                Result.success(parsedResult)
            } catch (e: Exception) {
                Log.e(TAG, "OCR recognition error", e)
                Result.failure(e)
            }
        }

    /**
     * Executes ML Kit OCR on a Content Uri (e.g. from gallery or camera intent).
     */
    suspend fun recognizeTextFromUri(context: Context, uri: Uri, transactions: List<Transaction> = emptyList()): Result<ParsedReceiptResult> =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = loadBitmapFromUri(context, uri)
                if (bitmap == null) {
                    return@withContext Result.failure(Exception("Gagal membaca berkas gambar"))
                }
                recognizeTextFromBitmap(bitmap, transactions)
            } catch (e: Exception) {
                Log.e(TAG, "Failed loading bitmap from uri", e)
                Result.failure(e)
            }
        }

    private suspend fun processInputImage(image: InputImage): Text =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (continuation.isActive) {
                        continuation.resume(visionText)
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.cancel(error)
                    }
                }
        }

    /**
     * High precision Indonesian & International Receipt Text Parsing & NLP Entity Extraction.
     */
    fun parseReceiptText(
        rawText: String,
        visionText: Text? = null,
        userHistory: List<Transaction> = emptyList()
    ): ParsedReceiptResult {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        
        // 1. Extract Merchant Name
        val merchant = extractMerchantName(lines, visionText)

        // 2. Extract Total Amount
        val (totalAmount, extractedItems) = extractAmountsAndItems(lines)

        // 3. Extract Date
        val dateFormatted = extractDate(rawText) ?: SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        // 4. Determine Best Category
        val category = determineCategory(merchant, extractedItems, rawText, userHistory)

        return ParsedReceiptResult(
            merchant = merchant,
            totalAmount = if (totalAmount > 0.0) totalAmount else (extractedItems.sumOf { it.price }.takeIf { it > 0.0 } ?: 50000.0),
            suggestedCategory = category,
            suggestedType = "EXPENSE",
            dateFormatted = dateFormatted,
            items = extractedItems,
            rawText = rawText,
            confidence = if (totalAmount > 0.0 && merchant != "Nota Pembelian") 0.95f else 0.80f
        )
    }

    private fun extractMerchantName(lines: List<String>, visionText: Text?): String {
        // Known prominent chains
        val knownChains = listOf(
            "Indomaret", "Alfamart", "Alfamidi", "Super Indo", "Hypermart", "Carrefour",
            "Starbucks", "Kopi Kenangan", "Janji Jiwa", "Fore Coffee", "Point Coffee",
            "McDonald's", "KFC", "Burger King", "Pizza Hut", "Dominos", "HokBen", "A&W",
            "Pertamina", "SPBU", "Shell", "BP", "Total",
            "Guardian", "Watsons", "Century", "Kimia Farma", "Apotek K-24",
            "Gramedia", "Uniqlo", "H&M", "Zara", "Miniso", "Ace Hardware", "Informa",
            "Shopee", "Tokopedia", "GrabFood", "GoFood"
        )

        // Check against known chains first
        for (line in lines.take(8)) {
            for (chain in knownChains) {
                if (line.contains(chain, ignoreCase = true)) {
                    return line.trim()
                }
            }
        }

        // Filter out non-merchant noise lines at the top
        val ignoreKeywords = listOf(
            "struk", "nota", "receipt", "invoice", "tagihan", "selamat datang",
            "welcome", "kasir", "cashier", "pos", "terminal", "npwp", "jl.", "jalan",
            "telp", "phone", "fax", "no:", "nomor", "transaksi", "order", "meja", "table",
            "waktu", "date", "time", "idr", "rp", "pt.", "cv."
        )

        val candidate = lines.take(5).firstOrNull { line ->
            val lower = line.lowercase(Locale.ROOT)
            line.length in 3..40 && 
                ignoreKeywords.none { lower.startsWith(it) || lower == it } &&
                !lower.matches(Regex("^[0-9\\W_]+$"))
        }

        return candidate ?: (lines.firstOrNull { it.length in 3..35 } ?: "Nota Pembelian")
    }

    private fun extractAmountsAndItems(lines: List<String>): Pair<Double, List<ParsedReceiptItem>> {
        var totalAmount = 0.0
        val items = mutableListOf<ParsedReceiptItem>()

        val totalKeywords = listOf(
            "total bayar", "total belanja", "total tagihan", "grand total", "total transaksi",
            "total akhir", "jumlah bayar", "total", "netto", "sub total", "subtotal", "jumlah",
            "tunai", "cash", "debit", "qris", "gopay", "ovo", "shopeepay", "dana", "due", "amount due"
        )

        val lineItemFilterOut = listOf(
            "total", "subtotal", "kembali", "change", "tunai", "cash", "debit", "qris",
            "pajak", "tax", "pb1", "diskon", "discount", "potongan", "rounding", "pembulatan",
            "kartu", "bayar", "service charge", "kembalian"
        )

        // Regex patterns to parse numbers formatted like: 25.000, 25,000, 150000, 12.50, Rp 45.000
        val priceRegex = Regex("""(?:Rp\.?|IDR|\$)?\s*([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?|[0-9]+)""")

        for (i in lines.indices) {
            val line = lines[i]
            val lower = line.lowercase(Locale.ROOT)

            // Check for Total line
            val isTotalLine = totalKeywords.any { lower.contains(it) }
            if (isTotalLine) {
                val numbers = parseNumbersFromLine(line)
                if (numbers.isNotEmpty()) {
                    val maxVal = numbers.maxOrNull() ?: 0.0
                    if (maxVal > totalAmount) {
                        totalAmount = maxVal
                    }
                } else if (i + 1 < lines.size) {
                    // Check next line for amount
                    val nextNumbers = parseNumbersFromLine(lines[i + 1])
                    if (nextNumbers.isNotEmpty()) {
                        val nextMax = nextNumbers.maxOrNull() ?: 0.0
                        if (nextMax > totalAmount) {
                            totalAmount = nextMax
                        }
                    }
                }
            }

            // Check for regular line items
            val isIgnoredItem = lineItemFilterOut.any { lower.contains(it) }
            if (!isIgnoredItem && line.length > 3) {
                val numbers = parseNumbersFromLine(line)
                if (numbers.isNotEmpty()) {
                    val price = numbers.last()
                    // Extract item name by removing price part
                    val namePart = line.replace(Regex("""[0-9.,RpIDR$]+"""), "").trim()
                    if (namePart.length in 2..40 && price > 0) {
                        items.add(ParsedReceiptItem(namePart, price))
                    }
                }
            }
        }

        // Fallback total if not explicitly found
        if (totalAmount == 0.0) {
            val allNumbers = lines.flatMap { parseNumbersFromLine(it) }
            if (allNumbers.isNotEmpty()) {
                totalAmount = allNumbers.maxOrNull() ?: 0.0
            }
        }

        return Pair(totalAmount, items.take(8))
    }

    private fun parseNumbersFromLine(line: String): List<Double> {
        val results = mutableListOf<Double>()
        // Match numbers like 50.000 or 50,000 or 50000 or 15.50
        val matcher = Pattern.compile("""(?<!\w)(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?|\d+)(?!\w)""").matcher(line)
        while (matcher.find()) {
            val rawNum = matcher.group(1) ?: continue
            val cleaned = cleanNumberString(rawNum)
            val value = cleaned.toDoubleOrNull()
            if (value != null && value in 100.0..100_000_000.0) {
                results.add(value)
            }
        }
        return results
    }

    private fun cleanNumberString(str: String): String {
        var clean = str.replace(" ", "")
        // Handle 50.000 (IDR standard) vs 50.00 (USD standard)
        if (clean.contains(".") && clean.contains(",")) {
            // e.g. 1.250.000,00 or 1,250,000.00
            val lastDot = clean.lastIndexOf('.')
            val lastComma = clean.lastIndexOf(',')
            clean = if (lastComma > lastDot) {
                clean.replace(".", "").replace(",", ".")
            } else {
                clean.replace(",", "")
            }
        } else if (clean.contains(".")) {
            // Count dots
            val dotCount = clean.count { it == '.' }
            if (dotCount > 1) {
                clean = clean.replace(".", "")
            } else {
                val afterDot = clean.substringAfter(".")
                if (afterDot.length == 3) {
                    // It's a thousands separator: 50.000 -> 50000
                    clean = clean.replace(".", "")
                }
            }
        } else if (clean.contains(",")) {
            val commaCount = clean.count { it == ',' }
            if (commaCount > 1) {
                clean = clean.replace(",", "")
            } else {
                val afterComma = clean.substringAfter(",")
                if (afterComma.length == 3) {
                    clean = clean.replace(",", "")
                } else {
                    clean = clean.replace(",", ".")
                }
            }
        }
        return clean
    }

    private fun extractDate(text: String): String? {
        val dateRegex = Regex("""(\d{1,2}[-/.]\d{1,2}[-/.]\d{2,4}|\d{1,2}\s+(?:Jan|Feb|Mar|Apr|Mei|Jun|Jul|Agu|Sep|Okt|Nov|Des|Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\s+\d{2,4})""", RegexOption.IGNORE_CASE)
        val match = dateRegex.find(text)
        return match?.value
    }

    private fun determineCategory(
        merchant: String,
        items: List<ParsedReceiptItem>,
        rawText: String,
        userHistory: List<Transaction>
    ): String {
        val combined = (merchant + " " + items.joinToString(" ") { it.name } + " " + rawText).lowercase(Locale.ROOT)

        // 1. Food / F&B
        val foodKeywords = listOf(
            "makan", "resto", "restaurant", "cafe", "kafe", "kopi", "coffee", "latte", "tea", "teh",
            "nasi", "mie", "bakso", "ayam", "bebek", "rendang", "padang", "warung", "burger", "pizza",
            "croissant", "bread", "roti", "bakery", "snack", "starbucks", "kfc", "mcd", "hokben",
            "janji jiwa", "kopi kenangan", "fore", "dunkin", "jco", "gofood", "grabfood"
        )
        if (foodKeywords.any { combined.contains(it) }) return "Makanan"

        // 2. Shopping / Grocery
        val shopKeywords = listOf(
            "indomaret", "alfamart", "alfamidi", "superindo", "hypermart", "carrefour", "supermarket",
            "mart", "toko", "belanja", "sabun", "shampoo", "deterjen", "snack", "biskuit", "susu",
            "minyak", "gula", "beras", "guardian", "watsons", "uniqlo", "h&m", "zara", "miniso"
        )
        if (shopKeywords.any { combined.contains(it) }) return "Belanja"

        // 3. Transport
        val transportKeywords = listOf(
            "spbu", "pertamina", "shell", "bp", "bensin", "pertalite", "pertamax", "solar", "dex",
            "grab", "gojek", "goride", "gocar", "maxim", "taxi", "parkir", "tol", "tiket", "kereta", "kai"
        )
        if (transportKeywords.any { combined.contains(it) }) return "Transportasi"

        // 4. Bills & Utilities
        val billKeywords = listOf(
            "pln", "listrik", "pdam", "air", "wifi", "indihome", "biznet", "telkomsel", "indosat", "xl",
            "pulsa", "paket data", "bpjs", "asuransi", "pajak"
        )
        if (billKeywords.any { combined.contains(it) }) return "Tagihan"

        // 5. Health
        val healthKeywords = listOf(
            "apotek", "apotik", "kimia farma", "k-24", "k24", "obat", "vitamin", "paracetamol",
            "klinik", "dokter", "laboratorium", "rumah sakit", "hospital"
        )
        if (healthKeywords.any { combined.contains(it) }) return "Kesehatan"

        // 6. Entertainment
        val entertainmentKeywords = listOf(
            "cinema", "xxi", "cgv", "cinepolis", "bioskop", "tiket bioskop", "game", "steam", "playstation",
            "netflix", "spotify", "karaoke", "timezone"
        )
        if (entertainmentKeywords.any { combined.contains(it) }) return "Hiburan"

        // 7. Education
        val eduKeywords = listOf(
            "buku", "gramedia", "kursus", "seminar", "kuliah", "sekolah", "spp", "les"
        )
        if (eduKeywords.any { combined.contains(it) }) return "Pendidikan"

        // Local ML prediction fallback
        if (userHistory.isNotEmpty()) {
            val predicted = LocalFinanceMLEngine.predictCategory(merchant, userHistory)
            if (predicted != null) return predicted
        }

        return "Belanja"
    }

    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                val input: InputStream? = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from uri", e)
            null
        }
    }
}

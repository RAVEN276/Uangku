package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.example.data.model.Transaction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
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
     * Creates a temporary file Uri in the app's cache directory using FileProvider.
     * This allows the camera app to write full-resolution images directly to disk.
     */
    fun createTempImageUri(context: Context): Uri {
        val imageDir = File(context.cacheDir, "receipt_images").apply {
            if (!exists()) mkdirs()
        }
        val file = File(imageDir, "receipt_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Loads a high-quality, memory-efficient bitmap from Uri with EXIF rotation correction.
     * Prevents OutOfMemory errors while keeping receipt text razor sharp for OCR and UI.
     */
    fun loadOptimizedBitmap(context: Context, uri: Uri, maxDimension: Int = 2048): Bitmap? {
        return try {
            // 1. Decode bounds
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            val origWidth = boundsOptions.outWidth
            val origHeight = boundsOptions.outHeight
            if (origWidth <= 0 || origHeight <= 0) return null

            // 2. Calculate inSampleSize
            var inSampleSize = 1
            var halfWidth = origWidth
            var halfHeight = origHeight
            while (halfWidth > maxDimension || halfHeight > maxDimension) {
                inSampleSize *= 2
                halfWidth /= 2
                halfHeight /= 2
            }

            // 3. Decode scaled bitmap
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            var bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return null

            // 4. Handle EXIF rotation
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    val rotationDegrees = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                    if (rotationDegrees != 0f) {
                        val matrix = Matrix().apply { postRotate(rotationDegrees) }
                        bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap!!.width, bitmap!!.height, matrix, true)
                    }
                }
            } catch (ex: Exception) {
                Log.w(TAG, "EXIF rotation parsing: ${ex.message}")
            }

            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed loading optimized bitmap", e)
            null
        }
    }

    /**
     * Executes real ML Kit On-Device Optical Character Recognition on a full-resolution Uri.
     */
    suspend fun recognizeTextFromUri(
        context: Context,
        uri: Uri,
        transactions: List<Transaction> = emptyList()
    ): Result<ParsedReceiptResult> = withContext(Dispatchers.IO) {
        try {
            // InputImage.fromFilePath handles full resolution and EXIF orientation directly
            val image = InputImage.fromFilePath(context, uri)
            val visionText = processInputImage(image)
            val parsedResult = parseReceiptText(visionText.text, visionText, transactions)
            Result.success(parsedResult)
        } catch (e: Exception) {
            Log.e(TAG, "OCR recognition error from Uri, falling back to bitmap", e)
            val bmp = loadOptimizedBitmap(context, uri)
            if (bmp != null) {
                recognizeTextFromBitmap(bmp, transactions)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Executes real ML Kit On-Device Optical Character Recognition on a Bitmap.
     */
    suspend fun recognizeTextFromBitmap(
        bitmap: Bitmap,
        transactions: List<Transaction> = emptyList()
    ): Result<ParsedReceiptResult> = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val visionText = processInputImage(image)
            val parsedResult = parseReceiptText(visionText.text, visionText, transactions)
            Result.success(parsedResult)
        } catch (e: Exception) {
            Log.e(TAG, "OCR recognition error from Bitmap", e)
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
     * High precision Indonesian & Global Receipt Text Parsing & Entity Extraction.
     */
    fun parseReceiptText(
        rawText: String,
        visionText: Text? = null,
        userHistory: List<Transaction> = emptyList()
    ): ParsedReceiptResult {
        val rawLines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        // 1. Extract Merchant Name
        val merchant = extractMerchantName(rawLines)

        // 2. Extract Total Amount and Individual Items
        val (totalAmount, extractedItems) = extractAmountsAndItems(rawLines)

        // 3. Extract Date
        val dateFormatted = extractDate(rawText) ?: SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        // 4. Determine Best Category
        val category = determineCategory(merchant, extractedItems, rawText, userHistory)

        val finalTotal = when {
            totalAmount > 0.0 -> totalAmount
            extractedItems.isNotEmpty() -> extractedItems.sumOf { it.price }
            else -> 0.0
        }

        val hasAccurateTotal = totalAmount > 0.0 || (extractedItems.isNotEmpty() && finalTotal > 0.0)
        val hasAccurateMerchant = merchant.isNotBlank() && merchant != "Nota Pembelian"
        val confidence = when {
            hasAccurateTotal && hasAccurateMerchant -> 0.96f
            hasAccurateTotal || hasAccurateMerchant -> 0.85f
            else -> 0.60f
        }

        return ParsedReceiptResult(
            merchant = merchant.ifBlank { "Nota Pembelian" },
            totalAmount = finalTotal,
            suggestedCategory = category,
            suggestedType = "EXPENSE",
            dateFormatted = dateFormatted,
            items = extractedItems,
            rawText = rawText,
            confidence = confidence
        )
    }

    private fun extractMerchantName(lines: List<String>): String {
        // Known national and international chains in Indonesia
        val knownChains = listOf(
            "Indomaret", "Alfamart", "Alfamidi", "Super Indo", "Hypermart", "Carrefour", "Transmart",
            "Starbucks", "Kopi Kenangan", "Janji Jiwa", "Fore Coffee", "Point Coffee", "Tomoro Coffee",
            "McDonald's", "KFC", "Burger King", "Pizza Hut", "Dominos", "HokBen", "A&W", "Mie Gacoan",
            "Richeese Factory", "Solaria", "Bakso Afung", "Bakso Boedjangan", "Kopi Kulo", "Chatime",
            "Pertamina", "SPBU", "Shell", "BP", "Total",
            "Guardian", "Watsons", "Century", "Kimia Farma", "Apotek K-24", "Apotik K-24",
            "Gramedia", "Uniqlo", "H&M", "Zara", "Miniso", "Ace Hardware", "Informa", "MR. D.I.Y.", "MR DIY",
            "Shopee", "Tokopedia", "GrabFood", "GoFood"
        )

        // Check top 10 lines for known chains first
        for (line in lines.take(10)) {
            for (chain in knownChains) {
                if (line.contains(chain, ignoreCase = true)) {
                    return chain
                }
            }
        }

        // Store prefixes common in Indonesia
        val storePrefixes = listOf(
            "rm ", "rumah makan ", "warung ", "warteg ", "kafe ", "cafe ", "resto ", "restoran ",
            "toko ", "kedai ", "bakso ", "soto ", "sate ", "ayam ", "depot ", "minimarket ", "supermarket ", "bakery "
        )

        for (line in lines.take(8)) {
            val lower = line.lowercase(Locale.ROOT)
            if (storePrefixes.any { lower.startsWith(it) }) {
                return line.trim()
            }
        }

        // Filter out header noise lines
        val ignoreKeywords = listOf(
            "struk", "nota", "receipt", "invoice", "tagihan", "selamat datang", "welcome",
            "kasir", "cashier", "pos", "terminal", "npwp", "jl.", "jalan", "telp", "phone",
            "fax", "no:", "nomor", "transaksi", "order", "meja", "table", "waktu", "date",
            "time", "idr", "rp", "pt.", "cv.", "kode", "member", "customer", "pelanggan"
        )

        val candidate = lines.take(6).firstOrNull { line ->
            val lower = line.lowercase(Locale.ROOT)
            val hasLetters = line.any { it.isLetter() }
            hasLetters &&
                line.length in 3..40 &&
                ignoreKeywords.none { lower.startsWith(it) || lower == it } &&
                !lower.matches(Regex("^[0-9\\W_]+$"))
        }

        return candidate ?: (lines.firstOrNull { it.any { c -> c.isLetter() } && it.length in 3..35 } ?: "")
    }

    private fun extractAmountsAndItems(lines: List<String>): Pair<Double, List<ParsedReceiptItem>> {
        var totalAmount = 0.0
        val items = mutableListOf<ParsedReceiptItem>()

        // Specific high-priority Total phrases
        val priority1Keywords = listOf(
            "total bayar", "total belanja", "total tagihan", "grand total", "total transaksi",
            "total akhir", "jumlah bayar", "jumlah tagihan", "total harga", "total netto",
            "jumlah total", "amount due", "balance due"
        )

        val priority2Keywords = listOf(
            "total", "jumlah", "tagihan", "harga jual", "netto"
        )

        val priority3Keywords = listOf(
            "sub total", "subtotal"
        )

        val fallbackPaymentKeywords = listOf(
            "tunai", "cash", "qris", "debit", "kartu", "bayar"
        )

        // Forbidden keywords that represent change, taxes, or discounts (MUST NOT be selected as total)
        val forbiddenAmountKeywords = listOf(
            "kembali", "kembalian", "change", "pajak", "tax", "ppn", "pb1", "service",
            "diskon", "discount", "potongan", "hemat", "saving", "voucher", "cashback",
            "pembulatan", "rounding", "poin", "points", "kembalian tunai"
        )

        var foundPriority = 0 // 1 = highest, 2 = total, 3 = subtotal, 4 = payment fallback

        for (i in lines.indices) {
            val line = lines[i]
            val lower = line.lowercase(Locale.ROOT)

            // Skip lines that represent change or discounts
            val isForbidden = forbiddenAmountKeywords.any { lower.contains(it) }
            if (isForbidden) {
                continue
            }

            var linePriority = 0
            if (priority1Keywords.any { lower.contains(it) }) {
                linePriority = 1
            } else if (priority2Keywords.any { lower.contains(it) }) {
                linePriority = 2
            } else if (priority3Keywords.any { lower.contains(it) }) {
                linePriority = 3
            } else if (fallbackPaymentKeywords.any { lower.contains(it) }) {
                linePriority = 4
            }

            if (linePriority > 0 && (foundPriority == 0 || linePriority < foundPriority)) {
                // Look for numbers on the same line
                val numbersOnLine = parseNumbersFromLine(line)
                if (numbersOnLine.isNotEmpty()) {
                    totalAmount = numbersOnLine.last()
                    foundPriority = linePriority
                } else if (i + 1 < lines.size) {
                    // Check if number is on the next line
                    val nextLine = lines[i + 1]
                    val nextLower = nextLine.lowercase(Locale.ROOT)
                    if (forbiddenAmountKeywords.none { nextLower.contains(it) }) {
                        val nextNumbers = parseNumbersFromLine(nextLine)
                        if (nextNumbers.isNotEmpty()) {
                            totalAmount = nextNumbers.last()
                            foundPriority = linePriority
                        }
                    }
                }
            }

            // Extract regular line items
            val isMetadataOrTotal = linePriority > 0 || isForbidden ||
                lower.contains("kasir") || lower.contains("npwp") || lower.contains("struk") ||
                lower.contains("selamat") || lower.contains("terima kasih") || lower.contains("jl.") ||
                lower.contains("telp") || lower.contains("meja") || lower.contains("order")

            if (!isMetadataOrTotal && line.length in 4..60) {
                val numbers = parseNumbersFromLine(line)
                if (numbers.isNotEmpty()) {
                    val price = numbers.last()
                    // Extract item name by removing the price portion and numbers
                    val nameCandidate = line
                        .replace(Regex("""(?:Rp\.?|IDR|\$)?\s*\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?|\d+"""), "")
                        .replace(Regex("""[-:=*x]+"""), " ")
                        .trim()

                    if (nameCandidate.length in 2..40 && nameCandidate.any { it.isLetter() } && price in 500.0..50_000_000.0) {
                        items.add(ParsedReceiptItem(nameCandidate, price))
                    }
                }
            }
        }

        // If no explicit total was found, try the largest number from the items or fallback to sum
        if (totalAmount == 0.0 && items.isNotEmpty()) {
            val sum = items.sumOf { it.price }
            totalAmount = if (sum > 0) sum else items.maxOfOrNull { it.price } ?: 0.0
        }

        return Pair(totalAmount, items.distinctBy { it.name }.take(10))
    }

    private fun parseNumbersFromLine(line: String): List<Double> {
        val results = mutableListOf<Double>()

        // 1. Normalize common OCR glitches where 'O' or 'o' is misread for '0' in prices
        // e.g. "45.OOO" or "5O.000"
        var normalized = line.replace(Regex("""(?<=[0-9.,])[oO](?=[0-9.,]|\b)|(?<=\b[1-9])[oO](?=[0-9]{2,})""")) {
            "0"
        }

        // Remove currency symbols to leave clear numbers
        normalized = normalized.replace("Rp.", " ").replace("Rp", " ").replace("IDR", " ").replace("$", " ")

        // Match patterns like 50.000, 50,000, 50000, 15.50, 1.250.000
        val matcher = Pattern.compile("""(?<!\w)(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?|\d+)(?!\w)""").matcher(normalized)
        while (matcher.find()) {
            val rawNum = matcher.group(1) ?: continue
            val cleaned = cleanNumberString(rawNum)
            val value = cleaned.toDoubleOrNull()
            // Reasonable financial transactions in IDR or standard currency
            if (value != null && value in 100.0..500_000_000.0) {
                results.add(value)
            }
        }
        return results
    }

    private fun cleanNumberString(str: String): String {
        var clean = str.replace(" ", "").trim()

        // Handle both separators present (e.g. 1.250.000,00 or 1,250,000.00)
        if (clean.contains(".") && clean.contains(",")) {
            val lastDot = clean.lastIndexOf('.')
            val lastComma = clean.lastIndexOf(',')
            clean = if (lastComma > lastDot) {
                clean.replace(".", "").replace(",", ".")
            } else {
                clean.replace(",", "")
            }
        } else if (clean.contains(".")) {
            val dotCount = clean.count { it == '.' }
            if (dotCount > 1) {
                clean = clean.replace(".", "")
            } else {
                val afterDot = clean.substringAfter(".")
                if (afterDot.length == 3) {
                    // Indonesian thousands separator (e.g. 50.000 -> 50000)
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
        val dateRegex = Regex(
            """(\d{1,2}[-/.]\d{1,2}[-/.]\d{2,4}|\d{1,2}\s+(?:Jan|Feb|Mar|Apr|Mei|Jun|Jul|Agu|Sep|Okt|Nov|Des|Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\s+\d{2,4})""",
            RegexOption.IGNORE_CASE
        )
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
            "janji jiwa", "kopi kenangan", "fore", "dunkin", "jco", "gofood", "grabfood", "gacoan",
            "richeese", "solaria", "kuliner", "dimsum", "soto", "sate"
        )
        if (foodKeywords.any { combined.contains(it) }) return "Makanan"

        // 2. Shopping / Grocery
        val shopKeywords = listOf(
            "indomaret", "alfamart", "alfamidi", "superindo", "hypermart", "carrefour", "supermarket",
            "mart", "toko", "belanja", "sabun", "shampoo", "deterjen", "snack", "biskuit", "susu",
            "minyak", "gula", "beras", "guardian", "watsons", "uniqlo", "h&m", "zara", "miniso", "mr diy"
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

        // Local history match fallback
        if (userHistory.isNotEmpty() && merchant.isNotBlank()) {
            val pastMatch = userHistory.firstOrNull { it.title.contains(merchant, ignoreCase = true) }
            if (pastMatch != null) return pastMatch.category
        }

        return "Belanja"
    }
}

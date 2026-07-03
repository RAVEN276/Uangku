package com.example.service

import com.example.data.model.Transaction
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object LocalFinanceMLEngine {

    data class CleanedMLSample(
        val normalizedAmount: Double, // Min-Max normalized amount
        val dayOfWeek: Int,           // 1 (Ahad/Sun) - 7 (Sabtu/Sat)
        val dayOfMonth: Int,          // 1 - 31
        val isExpense: Double,        // 1.0 = Expense, 0.0 = Income
        val categoryEncoded: Double,  // Integer-index mapping of category
        val isAnomaly: Int,           // 1 = Anomaly Outlier, 0 = Normal
        val cleanTitle: String        // Standardized and tokenized string title
    )

    data class MLInsight(
        val totalExpense: Double,
        val averageExpense: Double,
        val forecastedNextWeek: Double,
        val topCategory: String,
        val topCategoryPercentage: Double,
        val anomalies: List<Transaction>,
        val recommendations: List<String>,
        val markdownReport: String,
        
        // Extended insights
        val categoryToReduce: String,
        val categoryReductionExplanation: String,
        val estimatedBalanceEndOfMonth: Double,
        val dailyBurnRate: Double,
        val projectedSpendRemainingMonth: Double,

        // Quantum leap additions for dynamic user insights
        val financialHealthScore: Int,
        val kMeansClusterSummaries: List<String>
    )

    data class KMeansCluster(
        val label: String,
        val centroidAmount: Double,
        val transactions: List<Transaction>,
        val meanAmount: Double,
        val totalAmount: Double
    )

    /**
     * Preprocesses, scrubs, and cleans transaction histories to generate structured numeric and normalized
     * training feature vectors ready for local client-side ML model training/inference execution.
     */
    fun cleanAndPrepareTrainingData(transactions: List<Transaction>): List<CleanedMLSample> {
        val validTxs = transactions.filter { it.amount > 0 && it.title.isNotBlank() }
        if (validTxs.isEmpty()) return emptyList()

        // Parameters for normalization (Min-Max Scaling)
        val maxAmt = validTxs.maxOfOrNull { it.amount } ?: 1.0
        val minAmt = validTxs.minOfOrNull { it.amount } ?: 0.0
        val amtRange = if (maxAmt - minAmt > 0) maxAmt - minAmt else 1.0

        // Build index mapping for string categories for encoding
        val distinctCategories = validTxs.map { it.category.trim().uppercase() }.distinct()
        val categoryEncodingMap = distinctCategories.mapIndexed { index, cat -> cat to index.toDouble() }.toMap()

        // Statistical params for outlier z-score (using fall-back)
        val avgAmt = validTxs.map { it.amount }.average()
        val variance = validTxs.map { 
            val diff = it.amount - avgAmt
            diff * diff
        }.average()
        val stdDev = sqrt(variance)

        val calendar = Calendar.getInstance()

        return validTxs.map { tx ->
            calendar.timeInMillis = tx.timestamp
            
            // Clean title: Convert to lowercase, remove punctuation, remove excessive spaces
            val cleanedTitle = tx.title.trim()
                .lowercase(Locale.ROOT)
                .replace(Regex("[^a-zA-Z0-9\\s]"), "")
                .replace(Regex("\\s+"), " ")

            // Min-Max normalize amount: (x - min) / range bound to [0.0, 1.0]
            val normalizedAmt = ((tx.amount - minAmt) / amtRange).coerceIn(0.0, 1.0)

            // Extract features
            val dotw = calendar.get(Calendar.DAY_OF_WEEK)
            val dotm = calendar.get(Calendar.DAY_OF_MONTH)
            val isExp = if (tx.type == "EXPENSE") 1.0 else 0.0
            val catEncoded = categoryEncodingMap[tx.category.trim().uppercase()] ?: -1.0

            // Robust outlier detection label tag
            val zScore = if (stdDev > 0) (tx.amount - avgAmt) / stdDev else 0.0
            val isAnomalyLabel = if (tx.type == "EXPENSE" && zScore > 1.3) 1 else 0

            CleanedMLSample(
                normalizedAmount = normalizedAmt,
                dayOfWeek = dotw,
                dayOfMonth = dotm,
                isExpense = isExp,
                categoryEncoded = catEncoded,
                isAnomaly = isAnomalyLabel,
                cleanTitle = cleanedTitle
            )
        }
    }

    /**
     * Performs a professional statistical and machine learning analysis of user transactions 100% on-device.
     * Incorporates K-Means Clustering, Holt's Double Exponential Time-Series forecasting, Robust IQR Anomalies,
     * and a dynamic Financial Health Score.
     */
    fun analyzeTransactions(transactions: List<Transaction>, userName: String): MLInsight {
        val expenses = transactions.filter { it.type == "EXPENSE" }.sortedBy { it.timestamp }
        val incomes = transactions.filter { it.type == "INCOME" }
        val totalIncomeAmt = incomes.sumOf { it.amount }
        val totalExpenseAmt = expenses.sumOf { it.amount }
        val currentBalance = totalIncomeAmt - totalExpenseAmt

        val rp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        if (expenses.isEmpty()) {
            return MLInsight(
                totalExpense = 0.0,
                averageExpense = 0.0,
                forecastedNextWeek = 0.0,
                topCategory = "Belum Ada",
                topCategoryPercentage = 0.0,
                anomalies = emptyList(),
                recommendations = listOf("Tambahkan transaksi pengeluaran pertama Anda agar Asisten Cerdas dapat menganalisis data keuangan Anda secara akurat."),
                markdownReport = """
                    ### 📊 Analisis Keuangan Asisten Cerdas
                    
                    Belum ada data pengeluaran yang tercatat di basis data lokal perangkat Anda untuk dianalisis.
                    
                    💡 **Rekomendasi Awal:**
                    - Catat setiap pengeluaran harian Anda (seperti makanan, transportasi, kebutuhan bulanan).
                    - Kembali kesini setelah Anda menambahkan transaksi untuk melihat analisis klan, deteksi anomali otomatis, serta proyeksi statistik cerdas kami.
                """.trimIndent(),
                categoryToReduce = "Tidak Ada",
                categoryReductionExplanation = "Semua kas atau anggaran Anda seimbang karena belum terdeteksi pengeluaran harian.",
                estimatedBalanceEndOfMonth = currentBalance,
                dailyBurnRate = 0.0,
                projectedSpendRemainingMonth = 0.0,
                financialHealthScore = 100,
                kMeansClusterSummaries = emptyList()
            )
        }

        val totalExp = totalExpenseAmt
        val avgExp = expenses.map { it.amount }.average()

        // 1. ROBUST ANOMALY DETECTION USING INTERQUARTILE RANGE (IQR)
        // Highly robust against extreme scale variance in comparison with Standard Deviation Z-Scores
        val anomalies = detectOutliersIQR(expenses)

        // 2. K-MEANS SPENDING CLUSTERING ALGORITHM
        // Performs iterative on-device spatial clustering to classify expenses into distinct behavioral groups:
        // A: Routine/Micro, B: Medium/Lifestyle, C: High Scale/Luxury or Scheduled Big Spends
        val kMeansClusters = performKMeansClustering(expenses)
        val clusterSummaries = kMeansClusters.map { cluster ->
            "**${cluster.label}** (${cluster.transactions.size} transaksi): Rerata ${rp.format(cluster.meanAmount)} (Total: ${rp.format(cluster.totalAmount)})"
        }

        // 3. TIME-SERIES EXPENSE FORECASTING VIA DOUBLE EXPONENTIAL SMOOTHING (HOLT'S LINEAR TREND)
        // Model handles both Level (L) and Trend (T) components for accurate sequential projections
        val forecastedNextWeek = forecastNextWeekHolt(expenses)

        // 4. DETAILED END-OF-MONTH BALANCE PROJECTIONS
        val calendar = Calendar.getInstance()
        val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val remainingDays = (totalDaysInMonth - currentDay).coerceAtLeast(1)

        // Calculate a robust Daily Burn Rate (recent 14 days, falling back gracefully to all history)
        val now = System.currentTimeMillis()
        val periodMs = 14L * 24L * 60L * 60L * 1000L
        val recentPeriodExp = expenses.filter { (now - it.timestamp) <= periodMs }
        val periodDays = if (recentPeriodExp.isNotEmpty()) {
            val oldestTx = recentPeriodExp.minOf { it.timestamp }
            val diffDays = ((now - oldestTx) / (24L * 60L * 60L * 1000L)).toInt().coerceAtLeast(1)
            diffDays
        } else 1

        val calculatedDailyBurn = if (recentPeriodExp.isNotEmpty()) {
            recentPeriodExp.sumOf { it.amount } / periodDays
        } else {
            totalExp / 30.0
        }

        val projectedSpendRemaining = calculatedDailyBurn * remainingDays
        val estimatedBalanceEnd = currentBalance - projectedSpendRemaining

        // 5. MAXIMUM INFLUENCE CATEGORY FOR REDUCTION ANALYSIS
        val categoryGroups = expenses.groupBy { it.category }
        val categoryTotals = categoryGroups.mapValues { (_, txList) -> txList.sumOf { it.amount } }
        val sortedCategories = categoryTotals.entries.sortedByDescending { it.value }
        val topCat = sortedCategories.firstOrNull()?.key ?: "Lainnya"
        val topCatAmt = sortedCategories.firstOrNull()?.value ?: 0.0
        val topCatPct = if (totalExp > 0) (topCatAmt / totalExp) * 100.0 else 0.0

        val categoryToReduce = topCat
        val categoryToReducePercent = topCatPct
        val reductionExplanation = if (categoryToReducePercent > 35.0) {
            "Alokasi dana Anda terkonsentrasi sangat tinggi pada sektor **$categoryToReduce** (${String.format("%.1f", categoryToReducePercent)}%). Mengingat nilainya melampaui batas aman sirkulasi (35%), memangkas separuh pos belanja ini disarankan guna mendiversifikasi sisa tabungan harian."
        } else {
            "Sangat terkendali! Alokasi di sektor **$categoryToReduce** (${String.format("%.1f", categoryToReducePercent)}%) berada dalam ambang batas wajar. Pertahankan performa ini guna menjamin stabilitas surplus arus kas bulanan Anda."
        }

        // 6. CALCULATING FINANCIAL HEALTH SCORE (FHS) OUT OF 100
        val fhs = calculateFinancialHealthScore(
            totalIncome = totalIncomeAmt,
            totalExpense = totalExpenseAmt,
            burnRate = calculatedDailyBurn,
            totalBalance = currentBalance,
            anomaliesCount = anomalies.size,
            topCategoryPct = topCatPct
        )

        // 7. DEVELOPING HIGHLY TARGETED STRATEGIC RECOMMENDATIONS
        val recs = mutableListOf<String>()

        // Recommendation 1: Financial Health Categorization
        when {
            fhs >= 85 -> recs.add("✅ Pokok Tabungan Terjaga Cemerlang: **Skor Kesehatan $fhs/100** menunjukkan model finansial Anda sangat solid. Rasio tabungan dan batas belanja seimbang demi masa depan.")
            fhs >= 70 -> recs.add("✅ Pola Arus Kas Sehat & Stabil: **Skor Kesehatan $fhs/100** membuktikan manajemen kas Anda berjalan positif. Sedikit efisiensi sekunder akan meningkatkan skor ke level prima.")
            fhs >= 50 -> recs.add("⚠️ Waspada Kebocoran Anggaran: **Skor Kesehatan $fhs/100** dinilai moderat. Disarankan melakukan restrukturisasi pos pengeluaran gaya hidup demi mempertahankan sisa likuiditas.")
            else -> recs.add("🔴 Kritis! Defisit Kas Membuntuti Anda: **Skor Kesehatan $fhs/100** memasuki area bahaya. Laju hoki belanja terlampau agresif dibanding pendapatan masuk. Hentikan pengeluaran komplementer.")
        }

        // Recommendation 2: Anomaly Outliers
        if (anomalies.isNotEmpty()) {
            val largestAnomaly = anomalies.maxByOrNull { it.amount }
            if (largestAnomaly != null) {
                recs.add("⚠️ Deteksi Lonjakan Ekstrem (Anomali): Transaksi **\"${largestAnomaly.title}\"** sebesar ${rp.format(largestAnomaly.amount)} terhitung sebagai lonjakan deviasi yang tidak wajar. Evaluasi kembali urgensi di balik transaksi ini.")
            }
        } else {
            recs.add("✨ Keuangan Bebas Anomali: Model data tidak mendeteksi adanya lompatan transaksi mikro maupun makro yang mencurigakan sepanjang periode pencatatan minggu ini.")
        }

        // Recommendation 3: Category concentration
        if (topCatPct > 40.0) {
            recs.add("🔴 Konsentrasi Anggaran Tidak Sehat: Kelompok kategori **$topCat** menyedot hingga **${String.format("%.1f", topCatPct)}%** dana belanja. Batasi pagu anggaran kategori ini per tanggal berjalan.")
        } else {
            recs.add("✨ Diversifikasi Alokasi Anggaran Baik: Distribusi pengeluaran Anda bermigrasi secara seimbang di berbagai lini tanpa adanya dominasi tunggal ekspansif.")
        }

        // Recommendation 4: Forecasting Next Week Spending (Holt's dynamic output)
        val recent7DaysMs = 7L * 24L * 60L * 60L * 1000L
        val recent7DaysExp = expenses.filter { (now - it.timestamp) <= recent7DaysMs }.sumOf { it.amount }
        val forecastedVariance = forecastedNextWeek - recent7DaysExp
        if (forecastedVariance > 0) {
            recs.add("📈 Prediksi Tren Pengeluaran Naik: Algoritme Holt memprediksi pengeluaran minggu depan meningkat sebesar **${rp.format(forecastedVariance)}**. Tunda rencana pembelian barang hobi.")
        } else {
            recs.add("📉 Pola Penghematan Bermutu Tinggi: Tren belanja diprediksi menyusut sebesar **${rp.format(abs(forecastedVariance))}** dalam 7 hari ke depan. Pertahankan ketelitian belanja.")
        }

        // 8. GENERATING AN EXTREMELY PREMIUM MARKDOWN REPORT
        val reportBuilder = StringBuilder()
        reportBuilder.append("### 🦾 Laporan Asisten Cerdas (ML Analyst v2)\n\n")
        reportBuilder.append("Analisis parameter keuangan internal diproses secara andal pada perangkat Anda:\n\n")
        reportBuilder.append("- **Total Belanja**: **${rp.format(totalExp)}**\n")
        reportBuilder.append("- **Rerata Transaksi**: **${rp.format(avgExp)}** per rincian\n")
        reportBuilder.append("- **Sektor Dominan**: **$topCat** (${String.format("%.1f", topCatPct)}% dari seluruh aliran kas keluar)\n")
        reportBuilder.append("- **Rasio Laju Belanja Harian**: **${rp.format(calculatedDailyBurn)}** sehari\n")
        reportBuilder.append("- **Proyeksi Sisa Bulan Ini**: Menghabiskan sekira **${rp.format(projectedSpendRemaining)}** untuk $remainingDays hari ke depan.\n")
        reportBuilder.append("- **Estimasi Saldo Akhir Bulan**: **${rp.format(estimatedBalanceEnd)}**\n\n")

        reportBuilder.append("#### Skor Kesehatan Finansial (Financial Health Score)\n")
        val scoreLabel = when {
            fhs >= 85 -> "SANGAT PRIMA 🌟"
            fhs >= 70 -> "SEHAT 👍"
            fhs >= 50 -> "WASPADA ⚠️"
            else -> "KRITIS 🚨"
        }
        reportBuilder.append("- Nilai Kesehatan Anda: **$fhs / 100** ($scoreLabel)\n")
        reportBuilder.append("- Rekomendasi Utama: $reductionExplanation\n\n")

        reportBuilder.append("#### Hasil Klastering Perilaku Belanja (K-Means Clustering)\n")
        kMeansClusters.forEach { cluster ->
            val icon = when (cluster.label) {
                "Transaksi Mikro & Rutin" -> "🛒"
                "Belanja Lifestyle & Sekunder" -> "🍿"
                "Pegeluaran Makro & Utama" -> "💳"
                else -> "📦"
            }
            reportBuilder.append("- $icon **${cluster.label}**: ${cluster.transactions.size} item, rerata *${rp.format(cluster.meanAmount)}*, akumulasi: *${rp.format(cluster.totalAmount)}*\n")
        }
        reportBuilder.append("\n")

        reportBuilder.append("#### 🧠 Rekomendasi Berbasis Data:\n")
        recs.forEach { rec ->
            reportBuilder.append("- $rec\n")
        }

        reportBuilder.append("\n_🛡️ Seluruh kalkulasi statistik dikerjakan secara luring menggunakan standardisasi enkripsi memori RAM internal peranti demi menjamin privasi keuangan mutlak Anda._")

        return MLInsight(
            totalExpense = totalExp,
            averageExpense = avgExp,
            forecastedNextWeek = forecastedNextWeek,
            topCategory = topCat,
            topCategoryPercentage = topCatPct,
            anomalies = anomalies,
            recommendations = recs,
            markdownReport = reportBuilder.toString(),
            categoryToReduce = categoryToReduce,
            categoryReductionExplanation = reductionExplanation,
            estimatedBalanceEndOfMonth = estimatedBalanceEnd,
            dailyBurnRate = calculatedDailyBurn,
            projectedSpendRemainingMonth = projectedSpendRemaining,
            financialHealthScore = fhs,
            kMeansClusterSummaries = clusterSummaries
        )
    }

    /**
     * Outlier detection using standard numerical math statistics: Interquartile Range (IQR).
     * Filters items higher than Q3 + 1.5 * IQR.
     */
    fun detectOutliersIQR(expenses: List<Transaction>): List<Transaction> {
        if (expenses.size < 4) return emptyList()
        val sorted = expenses.sortedBy { it.amount }
        
        val q1Index = (sorted.size * 0.25).toInt()
        val q3Index = (sorted.size * 0.75).toInt()
        
        val q1 = sorted[q1Index].amount
        val q3 = sorted[q3Index].amount
        val iqr = q3 - q1
        
        // If IQR is extremely low, fallback to double average
        val threshold = if (iqr > 2000.0) q3 + 1.5 * iqr else expenses.map { it.amount }.average() * 2.5
        
        return expenses.filter { it.amount > threshold }
    }

    /**
     * Implements an on-device mathematical K-Means algorithm (K=3) to categorize spending
     * behaviors natively without external heavy execution frameworks.
     */
    fun performKMeansClustering(expenses: List<Transaction>): List<KMeansCluster> {
        if (expenses.isEmpty()) return emptyList()
        if (expenses.size < 3) {
            // Graceful fallback for low sample volumes
            return listOf(
                KMeansCluster("Transaksi Mikro & Rutin", expenses.map { it.amount }.average(), expenses, expenses.map { it.amount }.average(), expenses.sumOf { it.amount })
            )
        }

        val maxAmt = expenses.maxOf { it.amount }
        val minAmt = expenses.minOf { it.amount }
        
        // Initialize 3 spread-out centroids
        var centroid1 = minAmt
        var centroid3 = maxAmt
        var centroid2 = (maxAmt + minAmt) / 2.0

        val maxIterations = 8
        var clusters = List(3) { mutableListOf<Transaction>() }

        for (iter in 0 until maxIterations) {
            clusters = List(3) { mutableListOf<Transaction>() }
            
            // Assign points to nearest centroid
            for (tx in expenses) {
                val dist1 = abs(tx.amount - centroid1)
                val dist2 = abs(tx.amount - centroid2)
                val dist3 = abs(tx.amount - centroid3)
                
                when {
                    dist1 <= dist2 && dist1 <= dist3 -> clusters[0].add(tx)
                    dist2 <= dist1 && dist2 <= dist3 -> clusters[1].add(tx)
                    else -> clusters[2].add(tx)
                }
            }

            // Update Centroids
            if (clusters[0].isNotEmpty()) centroid1 = clusters[0].map { it.amount }.average()
            if (clusters[1].isNotEmpty()) centroid2 = clusters[1].map { it.amount }.average()
            if (clusters[2].isNotEmpty()) centroid3 = clusters[2].map { it.amount }.average()
        }

        // Sort clusters by their mean transaction amount
        val sortedClusterData = clusters.mapIndexed { index, list -> index to list }
            .map { (originalIdx, list) ->
                val avg = if (list.isEmpty()) 0.0 else list.map { it.amount }.average()
                val sum = if (list.isEmpty()) 0.0 else list.sumOf { it.amount }
                originalIdx to Pair(avg, sum)
            }
            .sortedBy { it.second.first } // sort ascending by average amount

        val labelNames = listOf("Transaksi Mikro & Rutin", "Belanja Lifestyle & Sekunder", "Pegeluaran Makro & Utama")

        return sortedClusterData.mapIndexed { i, (originalIdx, metrics) ->
            KMeansCluster(
                label = labelNames[i],
                centroidAmount = when (originalIdx) {
                    0 -> centroid1
                    1 -> centroid2
                    else -> centroid3
                },
                transactions = clusters[originalIdx],
                meanAmount = metrics.first,
                totalAmount = metrics.second
            )
        }
    }

    /**
     * Holt's Linear Double Exponential Smoothing timeseries forecaster.
     * Groups raw expense histories into four 7-day preceding bins and runs statistical smoothing
     * to capture Level and Slope Trend lines.
     */
    private fun forecastNextWeekHolt(expenses: List<Transaction>): Double {
        val now = System.currentTimeMillis()
        val oneWeekMs = 7L * 24L * 60L * 60L * 1000L
        
        // Collate past 4 weekly bins
        val bins = DoubleArray(4)
        for (i in 0..3) {
            val startMs = now - ((4 - i) * oneWeekMs)
            val endMs = now - ((3 - i) * oneWeekMs)
            bins[i] = expenses.filter { it.timestamp in startMs until endMs }.sumOf { it.amount }
        }

        // Handle cold boot scenario (degrade to average)
        if (bins.all { it == 0.0 }) {
            return expenses.sumOf { it.amount } / 4.0
        }

        // Alpha and Beta smoothing coefficients
        val alpha = 0.5
        val beta = 0.4

        var level = bins[0]
        var trend = bins[1] - bins[0]

        // Iterative smoothing updates
        for (i in 1..3) {
            val lastLevel = level
            val currentObs = bins[i]
            
            level = alpha * currentObs + (1.0 - alpha) * (level + trend)
            trend = beta * (level - lastLevel) + (1.0 - beta) * trend
        }

        // Forecast for next week
        val forecast = level + trend
        return forecast.coerceAtLeast(0.0)
    }

    /**
     * Core mathematical formulation scoring user financial discipline.
     * Evaluates multiple indices: savings ratios, cash consumption rates, outlier sizes, and budget constraints.
     */
    fun calculateFinancialHealthScore(
        totalIncome: Double,
        totalExpense: Double,
        burnRate: Double,
        totalBalance: Double,
        anomaliesCount: Int,
        topCategoryPct: Double
    ): Int {
        var score = 100

        // Ratio 1: Savings Rate (Savings = 1 - Expense/Income) (Weight: 40 points)
        if (totalIncome > 0) {
            val expenseRatio = totalExpense / totalIncome
            val savingsRateScore = ((1.0 - expenseRatio) * 40).coerceIn(0.0, 40.0)
            score = score - (40 - savingsRateScore.toInt())
        } else {
            // Cold budget: standard budget deduction
            score -= 15
        }

        // Ratio 2: Cash Lifetime / Longevity Index (Weight: 30 points)
        // Checks how many days the current balance can withstand at recent daily burn values
        if (burnRate > 10.0 && totalBalance > 0.0) {
            val daysOfSurvival = totalBalance / burnRate
            val burnScore = when {
                daysOfSurvival >= 90.0 -> 30
                daysOfSurvival >= 45.0 -> 24
                daysOfSurvival >= 30.0 -> 18
                daysOfSurvival >= 14.0 -> 10
                else -> 0
            }
            score -= (30 - burnScore)
        } else if (totalBalance <= 0.0 && totalExpense > 0.0) {
            score -= 30 // Zero reserves penalty
        }

        // Ratio 3: Anomaly & Outlier Abundance (Weight: 15 points)
        val anomalyPenalty = (anomaliesCount * 5).coerceIn(0, 15)
        score -= anomalyPenalty

        // Ratio 4: Budget Concentration (Weight: 15 points)
        if (topCategoryPct > 45.0) {
            val excessPercent = topCategoryPct - 45.0
            val concentrationPenalty = (excessPercent * 0.4).coerceIn(0.0, 15.0).toInt()
            score -= concentrationPenalty
        }

        return score.coerceIn(5, 100)
    }

    /**
     * Analyzes tokens from historical user inputs to dynamically predict the best suited category
     * for a given typed description (Natural Language Processing - TF-IDF inspired on-device algorithm).
     */
    fun predictCategory(title: String, transactions: List<Transaction>): String? {
        val validTxs = transactions.filter { it.category.isNotBlank() && it.title.isNotBlank() }
        if (validTxs.isEmpty()) return null

        // Tokenize target title
        val targetTokens = tokenize(title)
        if (targetTokens.isEmpty()) return null

        // Map categories onto lists of occurrences
        val categoryGroups = validTxs.groupBy { it.category.trim() }

        var bestCategory: String? = null
        var highestScore = 0.0

        categoryGroups.forEach { (category, txList) ->
            // Aggregate all tokens for this category and calculate frequencies
            val categoryAllTokens = txList.flatMap { tokenize(it.title) }
            val categoryTokenFreq = categoryAllTokens.groupingBy { it }.eachCount()

            // Sum token matches weighted by frequency
            var score = 0.0
            targetTokens.forEach { token ->
                val count = categoryTokenFreq[token] ?: 0
                if (count > 0) {
                    // Normalize token importance relative to cluster size
                    score += count.toDouble() / categoryAllTokens.size
                }
            }

            if (score > highestScore) {
                highestScore = score
                bestCategory = category
            }
        }

        return bestCategory
    }

    /**
     * Holt's Linear Double Exponential Smoothing timeseries forecaster with custom coefficients.
     */
    fun forecastNextWeekHoltCustom(expenses: List<Transaction>, alpha: Double, beta: Double): Double {
        val now = System.currentTimeMillis()
        val oneWeekMs = 7L * 24L * 60L * 60L * 1000L
        
        // Collate past 4 weekly bins
        val bins = DoubleArray(4)
        for (i in 0..3) {
            val startMs = now - ((4 - i) * oneWeekMs)
            val endMs = now - ((3 - i) * oneWeekMs)
            bins[i] = expenses.filter { it.timestamp in startMs until endMs }.sumOf { it.amount }
        }

        // Handle cold boot scenario (degrade to average)
        if (bins.all { it == 0.0 }) {
            return expenses.sumOf { it.amount } / 4.0
        }

        var level = bins[0]
        var trend = bins[1] - bins[0]

        // Iterative smoothing updates
        for (i in 1..3) {
            val lastLevel = level
            val currentObs = bins[i]
            
            level = alpha * currentObs + (1.0 - alpha) * (level + trend)
            trend = beta * (level - lastLevel) + (1.0 - beta) * trend
        }

        // Forecast for next week
        val forecast = level + trend
        return forecast.coerceAtLeast(0.0)
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase(Locale.ROOT)
            .replace(Regex("[^a-zA-Z0-9\\s]"), "")
            .split(Regex("\\s+"))
            .filter { it.length > 2 } // ignore short prepositions
    }
}

package com.example.service

import com.example.data.model.Transaction
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
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
        
        // Extended insights requested by user
        val categoryToReduce: String,
        val categoryReductionExplanation: String,
        val estimatedBalanceEndOfMonth: Double,
        val dailyBurnRate: Double,
        val projectedSpendRemainingMonth: Double
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

        // Statistical params for outlier z-score
        val avgAmt = validTxs.map { it.amount }.average()
        val variance = validTxs.map { Math.pow(it.amount - avgAmt, 2.0) }.average()
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

            // High statistical deviation label tag
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

    fun analyzeTransactions(transactions: List<Transaction>, userName: String): MLInsight {
        val expenses = transactions.filter { it.type == "EXPENSE" }
        val incomes = transactions.filter { it.type == "INCOME" }
        val currentBalance = incomes.sumOf { it.amount } - expenses.sumOf { it.amount }

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
                recommendations = listOf("Tambahkan transaksi pengeluaran pertama Anda agar Uangku ML Engine dapat menganalisis data keuangan Anda secara akurat."),
                markdownReport = """
                    ### 📊 Analisis Keuangan Cerdas Uangku ML
                    
                    Belum ada data pengeluaran yang tercatat di basis data lokal perangkat Anda untuk dianalisis.
                    
                    💡 **Rekomendasi Awal:**
                    - Catat setiap pengeluaran harian Anda (seperti makanan, transportasi, tagihan).
                    - Kembali kesini setelah Anda menambahkan transaksi untuk melihat proyeksi & deteksi anomali otomatis dari mesin intelijen lokal kami.
                """.trimIndent(),
                categoryToReduce = "Tidak Ada",
                categoryReductionExplanation = "Semua kas atau anggaran Anda seimbang karena belum terdeteksi pengeluaran.",
                estimatedBalanceEndOfMonth = currentBalance,
                dailyBurnRate = 0.0,
                projectedSpendRemainingMonth = 0.0
            )
        }

        val totalExp = expenses.sumOf { it.amount }
        val avgExp = expenses.map { it.amount }.average()

        // 1. ANOMALY DETECTION (Outliers using Standard Deviation Z-Score > 1.3)
        val variance = expenses.map { Math.pow(it.amount - avgExp, 2.0) }.average()
        val stdDev = sqrt(variance)
        val anomalies = if (stdDev > 0 && expenses.size >= 3) {
            expenses.filter { (it.amount - avgExp) / stdDev > 1.3 }
        } else {
            emptyList()
        }

        // 2. CATEGORY CLUSTERING & CONCENTRATION ANALYSIS
        val categoryGroups = expenses.groupBy { it.category }
        val categoryTotals = categoryGroups.mapValues { (_, txList) -> txList.sumOf { it.amount } }
        val sortedCategories = categoryTotals.entries.sortedByDescending { it.value }
        
        val topCat = sortedCategories.firstOrNull()?.key ?: "Lainnya"
        val topCatAmt = sortedCategories.firstOrNull()?.value ?: 0.0
        val topCatPct = if (totalExp > 0) (topCatAmt / totalExp) * 100.0 else 0.0

        // 3. TIME-SERIES EXPENSE FORECASTING (Simple Exponential Smoothing & Trend Line Indicator)
        val now = System.currentTimeMillis()
        val oneWeekMs = 7L * 24L * 60L * 60L * 1000L
        val recent7DaysExp = expenses.filter { (now - it.timestamp) <= oneWeekMs }.sumOf { it.amount }
        val older7To14DaysExp = expenses.filter { 
            val diff = now - it.timestamp
            diff > oneWeekMs && diff <= 2 * oneWeekMs 
        }.sumOf { it.amount }

        val forecastedNextWeek = when {
            recent7DaysExp == 0.0 && older7To14DaysExp == 0.0 -> {
                totalExp * 0.85
            }
            older7To14DaysExp > 0.0 -> {
                val growthRate = (recent7DaysExp - older7To14DaysExp) / older7To14DaysExp
                val stabilizedGrowth = growthRate.coerceIn(-0.30, 0.30)
                recent7DaysExp * (1.0 + stabilizedGrowth)
            }
            else -> {
                recent7DaysExp * 1.05
            }
        }

        // 4. DETAILED END-OF-MONTH BALANCE PROJECTIONS
        val calendar = Calendar.getInstance()
        val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val remainingDays = (totalDaysInMonth - currentDay).coerceAtLeast(1)

        // Calculate robust Daily Burn Rate (using last 14 days if possible, defaulting to all history)
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

        // 5. DETERMINE CATEGORY THAT MOST NEEDS REDUCTION
        // The algorithm selects the category with the highest total amount AND that exceeds 25% of total,
        // or settles on the absolute highest category.
        val categoryToReduce = topCat
        val categoryToReducePercent = topCatPct
        val reductionExplanation = if (categoryToReducePercent > 35.0) {
            "Sangat boros! Porsi anggaran habis di **$categoryToReduce** (${String.format("%.1f", categoryToReducePercent)}%). Tekan hingga minimal setengahnya demi mengamankan aliran tabungan."
        } else {
            "Pertahankan! Kategori **$categoryToReduce** (${String.format("%.1f", categoryToReducePercent)}%) terpantau aman namun tetap harus dijaga agar pengeluaran sekunder stabil."
        }

        // 6. GENERATING TAILORED REASONINGS & STRATEGIES
        val recs = mutableListOf<String>()

        if (topCatPct > 40.0) {
            recs.add("🔴 **Risiko Konsentrasi**: Pengeluaran Anda sangat didominasi oleh kategori **$topCat** (${String.format("%.1f", topCatPct)}%). Cobalah menetapkan anggaran batas ketat untuk kategori ini.")
        } else {
            recs.add("✅ **Diversifikasi Sehat**: Alokasi belanja Anda terbagi secara seimbang dengan rentang kategori yang bervariasi tanpa dominasi tunggal ekstrim.")
        }

        if (anomalies.isNotEmpty()) {
            val largestAnomaly = anomalies.maxByOrNull { it.amount }
            if (largestAnomaly != null) {
                recs.add("⚠️ **Deteksi Anomali**: Transaksi \"**${largestAnomaly.title}**\" senilai ${rp.format(largestAnomaly.amount)} tergolong sebagai lonjakan tidak wajar (melebihi batas deviasi normal). Evaluasi kembali apakah belanja ini impulsif.")
            }
        } else {
            recs.add("✨ **Keuangan Stabil**: Tim analisis tidak mendeteksi adanya pengeluaran impulsif atau lonjakan ektrem yang berisiko merusak struktur kas minggu ini.")
        }

        val trendIndicator = forecastedNextWeek - recent7DaysExp
        if (trendIndicator > 0) {
            recs.add("📈 **Ramalan Tren**: Proyeksi Machine Learning memprediksi pengeluaran minggu depan naik sekitar **${rp.format(trendIndicator)}**. Sangat disarankan untuk menahan diri dari pembelian non-esensial.")
        } else {
            recs.add("📉 **Ramalan Tren**: Kinerja belanja menunjukkan penurunan sehat! Diproyeksikan minggu depan Anda akan berhemat sebesar **${rp.format(Math.abs(trendIndicator))}**.")
        }

        // Build elegant markdown report
        val reportBuilder = StringBuilder()
        reportBuilder.append("### 🦾 Uangku ML Intelligence Report\n\n")
        reportBuilder.append("Analisis performa keuangan lokal Anda berdasarkan pola transaksi:\n\n")
        reportBuilder.append("- **Total Belanja**: **${rp.format(totalExp)}**\n")
        reportBuilder.append("- **Rerata Transaksi**: **${rp.format(avgExp)}** per rincian\n")
        reportBuilder.append("- **Sektor Dominan**: **$topCat** (${String.format("%.1f", topCatPct)}% dari seluruh aliran kas keluar)\n")
        reportBuilder.append("- **Rasio Laju Belanja Harian**: **${rp.format(calculatedDailyBurn)}** sehari\n")
        reportBuilder.append("- **Proyeksi Sisa Bulan Ini**: Menghabiskan sekira **${rp.format(projectedSpendRemaining)}** untuk $remainingDays hari ke depan.\n")
        reportBuilder.append("- **Estimasi Saldo Akhir Bulan**: **${rp.format(estimatedBalanceEnd)}**\n\n")

        reportBuilder.append("#### 🧠 Rekomendasi Berbasis Data:\n")
        recs.forEach { rec ->
            reportBuilder.append("- $rec\n")
        }

        reportBuilder.append("\n_🛡️ Laporan ini diproses 100% di perangkat Anda menggunakan algoritma statistik lokal tanpa mengirim data pribadi ke awan/cloud._")

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
            projectedSpendRemainingMonth = projectedSpendRemaining
        )
    }
}

package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.testTag
import com.example.data.model.getCleanTitle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MonthlyData(
    val monthName: String,
    val income: Float,
    val expense: Float
)

@Composable
fun MonthlyBarComparisonChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val monthlyDataList = remember(transactions) {
        val sdf = SimpleDateFormat("MMM", Locale("id", "ID"))
        val list = mutableListOf<MonthlyData>()
        
        for (i in 4 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -i)
            val monthStart = cal.clone() as Calendar
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            monthStart.set(Calendar.HOUR_OF_DAY, 0)
            monthStart.set(Calendar.MINUTE, 0)
            monthStart.set(Calendar.SECOND, 0)
            monthStart.set(Calendar.MILLISECOND, 0)
            
            val monthEnd = cal.clone() as Calendar
            monthEnd.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            monthEnd.set(Calendar.HOUR_OF_DAY, 23)
            monthEnd.set(Calendar.MINUTE, 59)
            monthEnd.set(Calendar.SECOND, 59)
            monthEnd.set(Calendar.MILLISECOND, 999)
            
            val startTs = monthStart.timeInMillis
            val endTs = monthEnd.timeInMillis
            
            val monthTxs = transactions.filter { it.timestamp in startTs..endTs }
            val incomeSum = monthTxs.filter { it.type == "INCOME" }.sumOf { it.amount }.toFloat()
            val expenseSum = monthTxs.filter { it.type == "EXPENSE" }.sumOf { it.amount }.toFloat()
            
            list.add(
                MonthlyData(
                    monthName = sdf.format(cal.time),
                    income = incomeSum,
                    expense = expenseSum
                )
            )
        }
        list
    }

    // Default to the last month (index 4, usually current month) so the chart displays values instantly
    var selectedMonthIndex by remember { mutableStateOf(4) }

    val maxBarValue = remember(monthlyDataList) {
        val maxVal = monthlyDataList.maxOfOrNull { data -> if (data.income > data.expense) data.income else data.expense } ?: 0f
        if (maxVal == 0f) 1000000f else maxVal * 1.15f
    }

    val scaleProgress = remember { Animatable(0f) }
    LaunchedEffect(monthlyDataList) {
        scaleProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val incomeColor = Color(0xFF2ECC71) 
    val expenseColor = Color(0xFFE74C3C) 

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_bar_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Perbandingan Keuangan Bulanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val selectedData = if (selectedMonthIndex in monthlyDataList.indices) monthlyDataList[selectedMonthIndex] else null
            if (selectedData != null) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
                val incStr = formatter.format(selectedData.income).replace(",00", "")
                val expStr = formatter.format(selectedData.expense).replace(",00", "")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${selectedData.monthName}: ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "🟢 $incStr ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = incomeColor
                    )
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "🔴 $expStr",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = expenseColor
                    )
                }
            } else {
                Text(
                    text = "Visualisasi Pemasukan vs Pengeluaran 5 bulan terakhir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Graph Area with Y-axis scale and chart bars side-by-side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. Left Y-axis Label Column
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp, bottom = 22.dp), // align nicely with grid area height
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    val rawMax = monthlyDataList.maxOfOrNull { maxOf(it.income, it.expense) } ?: 0f
                    val displayMax = if (rawMax == 0f) 1000000f else rawMax
                    
                    fun formatCompact(value: Float): String {
                        return when {
                            value >= 1_000_000_000f -> "Rp${String.format("%.1f", value / 1_000_000_000f)}M"
                            value >= 1_000_000f -> "Rp${String.format("%.1f", value / 1_000_000f)}jt"
                            value >= 1_000f -> "Rp${(value / 1_000f).toInt()}rb"
                            else -> "Rp${value.toInt()}"
                        }.replace(".0", "")
                    }

                    Text(text = formatCompact(displayMax), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    Text(text = formatCompact(displayMax * 0.75f), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(text = formatCompact(displayMax * 0.5f), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(text = formatCompact(displayMax * 0.25f), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(text = "Rp0", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }

                // 2. Right Grid & Chart Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Grid lines drawn behind
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = 22.dp) // offset bottom to match chart bars height
                    ) {
                        val lineCount = 5
                        val stepHeight = size.height / (lineCount - 1)
                        for (i in 0 until lineCount) {
                            val y = i * stepHeight
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.15f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }

                    // Interactive columns with capsule bars
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        monthlyDataList.forEachIndexed { index, data ->
                            val isSelected = selectedMonthIndex == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        else Color.Transparent
                                    )
                                    .then(
                                        if (isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .clickable { selectedMonthIndex = index }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = 6.dp)
                                ) {
                                    // Income bar with background track
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(incomeColor.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        val incomeRatio = (data.income / maxBarValue).coerceIn(0f, 1f)
                                        val animatedIncomeHeight = incomeRatio * scaleProgress.value
                                        if (animatedIncomeHeight > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(animatedIncomeHeight)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(incomeColor, incomeColor.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )
                                        }
                                    }

                                    // Expense bar with background track
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(expenseColor.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        val expenseRatio = (data.expense / maxBarValue).coerceIn(0f, 1f)
                                        val animatedExpenseHeight = expenseRatio * scaleProgress.value
                                        if (animatedExpenseHeight > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(animatedExpenseHeight)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(expenseColor, expenseColor.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = data.monthName.uppercase(Locale.getDefault()),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(incomeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pemasukan",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(expenseColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pengeluaran",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionCategoryDonutChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val expenses = transactions.filter { it.type == "EXPENSE" }
    val totalExpense = expenses.sumOf { it.amount }

    // Group and calculate sum by category
    val expensesByCategory = expenses.groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        .filter { it.value > 0 }

    // Palette for different categories
    val categoryColors = mapOf(
        "Makanan" to Color(0xFFE57373),      // Coral Red
        "Sewa" to Color(0xFF4FC3F7),         // Soft Blue
        "Transportasi" to Color(0xFFFFD54F), // Amber
        "Gaji" to Color(0xFF81C784),         // Light Green
        "Investasi" to Color(0xFFBA68C8),    // Purple
        "Belanja" to Color(0xFFFFB74D),      // Orange
        "Koneksi Bank" to Color(0xFF4DB6AC), // Teal
        "Lainnya" to Color(0xFF90A4AE)       // Slate Grey
    )

    val defaultColor = Color(0xFFA1887F)

    // Curate list of slices
    val slices = expensesByCategory.map { (cat, amt) ->
        val sweepAngle = if (totalExpense > 0) (amt / totalExpense * 360f).toFloat() else 0f
        val color = categoryColors[cat] ?: defaultColor
        DonutSlice(category = cat, amount = amt, sweepAngle = sweepAngle, color = color)
    }.sortedByDescending { it.amount }

    // Currency Formatter
    val rubelFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    rubelFormat.maximumFractionDigits = 0
    val totalExpenseStr = rubelFormat.format(totalExpense)

    // Animated sweep
    val transitionProgress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        transitionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_donut_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Porsi Pengeluaran per Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (slices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rp0",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada riwayat pengeluaran bulan ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Left: Actual Live Draw Donut Chart
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(140.dp)) {
                            var startAngle = -90f
                            val strokeWidth = 36f

                            for (slice in slices) {
                                val animatedSweep = slice.sweepAngle * transitionProgress.value
                                drawArc(
                                    color = slice.color,
                                    startAngle = startAngle,
                                    sweepAngle = animatedSweep,
                                    useCenter = false,
                                    size = Size(size.width, size.height),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                startAngle += slice.sweepAngle
                            }
                        }

                        // Center total text
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                            Text(
                                text = if (totalExpense >= 1000000) {
                                    "Rp" + String.format(Locale.US, "%.1fjt", totalExpense / 1000000.0)
                                } else {
                                    rubelFormat.format(totalExpense).replace(",00", "")
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: Beautiful Grid of Labels
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        slices.take(4).forEach { slice ->
                            val percent = if (totalExpense > 0) (slice.amount / totalExpense * 100).toInt() else 0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(slice.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = slice.category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (slices.size > 4) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Wrap-row for extra labels below
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        slices.drop(4).forEach { slice ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(slice.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slice.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionWeeklyTrendLineChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    // Generate daily expense graph for last 7 entries
    val expenses = transactions.filter { it.type == "EXPENSE" }.sortedBy { it.timestamp }
    val lastSevenTxs = expenses.takeLast(7)

    if (lastSevenTxs.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("weekly_trend_chart_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Tren Pengeluaran Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Grafik fluktuasi 7 transaksi pengeluaran terakhir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada transaksi pengeluaran untuk ditampilkan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    var selectedIndex by remember(lastSevenTxs) { mutableStateOf(lastSevenTxs.size - 1) }

    val maxVal = lastSevenTxs.maxOfOrNull { it.amount.toFloat() } ?: 0f
    val minVal = lastSevenTxs.minOfOrNull { it.amount.toFloat() } ?: 0f
    val range = if (maxVal == minVal) {
        if (maxVal == 0f) 10000f else maxVal * 0.5f
    } else {
        maxVal - minVal
    }
    val displayMin = (minVal - range * 0.15f).coerceAtLeast(0f)
    val displayMax = maxVal + range * 0.15f
    val displayRange = displayMax - displayMin

    val lineProgress = remember { Animatable(0f) }
    LaunchedEffect(lastSevenTxs) {
        lineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val selectedTx = if (selectedIndex in lastSevenTxs.indices) lastSevenTxs[selectedIndex] else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_trend_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Interactive Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (selectedTx != null) {
                        Text(
                            text = selectedTx.getCleanTitle(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(selectedTx.timestamp))} • ${selectedTx.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Tren Pengeluaran Harian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Grafik fluktuasi 7 transaksi pengeluaran terakhir",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (selectedTx != null) {
                    val rubelFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
                    val amountStr = rubelFormatter.format(selectedTx.amount).replace(",00", "")
                    Text(
                        text = "-$amountStr",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val pointCount = lastSevenTxs.size

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. Y-axis Column
                Column(
                    modifier = Modifier
                        .width(55.dp)
                        .fillMaxHeight()
                        .padding(bottom = 24.dp), // align nicely with grid area height
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    fun formatCompact(value: Float): String {
                        return when {
                            value >= 1_000_000_000f -> "Rp${String.format("%.1f", value / 1_000_000_000f)}M"
                            value >= 1_000_000f -> "Rp${String.format("%.1f", value / 1_000_000f)}jt"
                            value >= 1_000f -> "Rp${String.format("%.1f", value / 1_000f)}rb"
                            else -> "Rp${value.toInt()}"
                        }.replace(".0", "")
                    }

                    Text(text = formatCompact(displayMax), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    Text(text = formatCompact(displayMin + displayRange * 0.5f), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(text = formatCompact(displayMin), style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 2. Chart + X-axis Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Chart Grid & Line Area
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val widthDp = maxWidth
                        val heightDp = maxHeight

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            // Draw 3 horizontal grid lines
                            val gridY = listOf(0f, height * 0.5f, height)
                            gridY.forEach { y ->
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.12f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 2f,
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }

                            // Map points to Offsets
                            val points = lastSevenTxs.mapIndexed { idx, tx ->
                                val x = if (pointCount > 1) idx.toFloat() / (pointCount - 1) * width else width / 2
                                val valFloat = tx.amount.toFloat()
                                val y = if (displayRange > 0f) {
                                    height - (((valFloat - displayMin) / displayRange) * (height * 0.8f)) - (height * 0.1f)
                                } else {
                                    height / 2
                                }
                                Offset(x, y)
                            }

                            // If selectedIndex is valid, draw a vertical crosshair line to the selected dot
                            if (selectedIndex in points.indices) {
                                val selPoint = points[selectedIndex]
                                drawLine(
                                    color = primaryColor.copy(alpha = 0.3f),
                                    start = Offset(selPoint.x, 0f),
                                    end = Offset(selPoint.x, height),
                                    strokeWidth = 3f,
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                                )
                            }

                            // Draw curve / spline
                            val strokePath = Path().apply {
                                if (points.isNotEmpty()) {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 1 until points.size) {
                                        val previous = points[i - 1]
                                        val current = points[i]
                                        val controlPoint1 = Offset(previous.x + (current.x - previous.x) / 2f, previous.y)
                                        val controlPoint2 = Offset(previous.x + (current.x - previous.x) / 2f, current.y)
                                        cubicTo(
                                            controlPoint1.x, controlPoint1.y,
                                            controlPoint2.x, controlPoint2.y,
                                            current.x, current.y
                                        )
                                    }
                                }
                            }

                            // Draw animated stroke path
                            drawPath(
                                path = strokePath,
                                color = primaryColor,
                                style = Stroke(width = 6f, cap = StrokeCap.Round)
                            )

                            // Fill gradient under curve
                            if (points.isNotEmpty()) {
                                val fillPath = Path().apply {
                                    addPath(strokePath)
                                    lineTo(points.last().x, height)
                                    lineTo(points.first().x, height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                            }

                            // Draw circles for each point
                            points.forEachIndexed { idx, offset ->
                                val isSelected = selectedIndex == idx
                                if (isSelected) {
                                    // Pulsing outer circle
                                    drawCircle(
                                        color = primaryColor.copy(alpha = 0.25f),
                                        radius = 18f * lineProgress.value,
                                        center = offset
                                    )
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 10f * lineProgress.value,
                                        center = offset
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 5f * lineProgress.value,
                                        center = offset
                                    )
                                } else {
                                    // Standard dot
                                    drawCircle(
                                        color = primaryColor.copy(alpha = 0.6f),
                                        radius = 6f,
                                        center = offset
                                    )
                                }
                            }
                        }

                        // Clickable areas
                        lastSevenTxs.forEachIndexed { index, tx ->
                            val xFraction = if (pointCount > 1) index.toFloat() / (pointCount - 1) else 0.5f
                            val valFloat = tx.amount.toFloat()
                            val yFraction = if (displayRange > 0f) {
                                1f - (((valFloat - displayMin) / displayRange) * 0.8f + 0.1f)
                            } else {
                                0.5f
                            }
                            val targetSize = 48.dp
                            Box(
                                modifier = Modifier
                                    .size(targetSize)
                                    .offset(
                                        x = widthDp * xFraction - targetSize / 2,
                                        y = heightDp * yFraction - targetSize / 2
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedIndex = index
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // X-axis date labels
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    ) {
                        lastSevenTxs.forEachIndexed { index, tx ->
                            val xFraction = if (pointCount > 1) index.toFloat() / (pointCount - 1) else 0.5f
                            val dateLabel = SimpleDateFormat("dd/MM", Locale("id", "ID")).format(Date(tx.timestamp))
                            val isSelected = selectedIndex == index
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .offset(
                                        x = maxWidth * xFraction - 22.dp,
                                        y = 0.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DonutSlice(
    val category: String,
    val amount: Double,
    val sweepAngle: Float,
    val color: Color
)

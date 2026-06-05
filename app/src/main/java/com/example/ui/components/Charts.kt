package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
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

    val maxBarValue = remember(monthlyDataList) {
        val maxVal = monthlyDataList.maxOfOrNull { maxOf(it.income, it.expense) } ?: 0f
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
            Text(
                text = "Visualisasi Pemasukan vs Pengeluaran 5 bulan terakhir",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineCount = 5
                    val stepHeight = size.height / lineCount
                    for (i in 0 until lineCount) {
                        val y = i * stepHeight
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.2f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthlyDataList.forEach { data ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 8.dp)
                            ) {
                                val incomeRatio = (data.income / maxBarValue).coerceIn(0f, 1f)
                                val animatedIncomeHeight = incomeRatio * scaleProgress.value
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .fillMaxHeight(animatedIncomeHeight.coerceAtLeast(0.01f))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(incomeColor, incomeColor.copy(alpha = 0.7f))
                                            )
                                        )
                                )

                                val expenseRatio = (data.expense / maxBarValue).coerceIn(0f, 1f)
                                val animatedExpenseHeight = expenseRatio * scaleProgress.value
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .fillMaxHeight(animatedExpenseHeight.coerceAtLeast(0.01f))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(expenseColor, expenseColor.copy(alpha = 0.7f))
                                            )
                                        )
                                )
                            }

                            Text(
                                text = data.monthName.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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

    // Use actual transactions; if empty or 1 point, start from 0f gracefully
    val dataPoints = if (lastSevenTxs.isNotEmpty()) {
        if (lastSevenTxs.size == 1) {
            listOf(0f, lastSevenTxs.first().amount.toFloat())
        } else {
            lastSevenTxs.map { it.amount.toFloat() }
        }
    } else {
        listOf(0f, 0f)
    }

    val maxVal = dataPoints.maxOrNull() ?: 0f
    val minVal = dataPoints.minOrNull() ?: 0f
    val range = maxVal - minVal

    val lineProgress = remember { Animatable(0f) }
    LaunchedEffect(dataPoints) {
        lineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

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

            // Canvas Line Chart with dynamic Spline & Gradient Glow Area
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val width = size.width
                val height = size.height
                val pointCount = dataPoints.size
                val spaceBetween = width / (pointCount - 1)

                val points = dataPoints.mapIndexed { idx, value ->
                    val x = idx * spaceBetween
                    val y = if (range > 0) {
                        height - (((value - minVal) / range) * (height * 0.8f)) - (height * 0.1f)
                    } else {
                        height / 2
                    }
                    Offset(x, y)
                }

                // Create Spline Path
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

                // Drawing Animated Line
                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )

                // Fill glowing gradient below card trend
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

                // Draw pulsing point on the final coordinate
                if (points.isNotEmpty()) {
                    val lastPoint = points.last()
                    drawCircle(
                        color = primaryColor,
                        radius = 12f * lineProgress.value,
                        center = lastPoint
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f * lineProgress.value,
                        center = lastPoint
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Labels under Trend Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val rubelFormatAbbr = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                rubelFormatAbbr.maximumFractionDigits = 0

                Text(
                    text = "Terendah: " + rubelFormatAbbr.format(minVal).replace(",00", ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tertinggi: " + rubelFormatAbbr.format(maxVal).replace(",00", ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
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

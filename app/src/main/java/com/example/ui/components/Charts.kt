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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import java.text.NumberFormat
import java.util.Locale

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

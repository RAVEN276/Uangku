package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.RupiahVisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.BorderStroke
import android.content.Intent
import android.widget.Toast
import com.example.data.model.Transaction
import com.example.ui.FinanceViewModel
import com.example.ui.components.MonthlyBarComparisonChart
import com.example.ui.components.TransactionCategoryDonutChart
import com.example.ui.components.TransactionWeeklyTrendLineChart
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Storage

fun parseMarkdownLine(line: String, defaultColor: Color, boldColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var tempCursor = 0
        val l = line.length
        while (tempCursor < l) {
            val nextBoldStart = line.indexOf("**", tempCursor)
            if (nextBoldStart != -1) {
                append(line.substring(tempCursor, nextBoldStart))
                val nextBoldEnd = line.indexOf("**", nextBoldStart + 2)
                if (nextBoldEnd != -1) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                        append(line.substring(nextBoldStart + 2, nextBoldEnd))
                    }
                    tempCursor = nextBoldEnd + 2
                } else {
                    append("**")
                    tempCursor = nextBoldStart + 2
                }
            } else {
                append(line.substring(tempCursor, l))
                tempCursor = l
            }
        }
    }
}

@Composable
fun BeautifulMarkdownView(displayText: String, themeDark: Boolean) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    val lines = remember(displayText) { displayText.split("\n") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                // Spacer for empty paragraphs
                Spacer(modifier = Modifier.height(4.dp))
            } else if (trimmed.startsWith("### ")) {
                val titleText = trimmed.removePrefix("### ").trim()
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = if (themeDark) Color(0xFFC084FC) else Color(0xFF5B21B6),
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
            } else if (trimmed.startsWith("#### ")) {
                val subtitleText = trimmed.removePrefix("#### ").trim()
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.25.sp
                    ),
                    color = if (themeDark) Color(0xFFE9D5FF) else Color(0xFF4C229E),
                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                )
            } else if (trimmed.startsWith("- ")) {
                val isRecommendation = trimmed.contains("🔴") || trimmed.contains("⚠️") || trimmed.contains("✅") || trimmed.contains("✨") || trimmed.contains("📈") || trimmed.contains("📉")
                
                if (isRecommendation) {
                    val emoji = when {
                        trimmed.contains("🔴") -> "🔴"
                        trimmed.contains("⚠️") -> "⚠️"
                        trimmed.contains("✅") -> "✅"
                        trimmed.contains("✨") -> "✨"
                        trimmed.contains("📈") -> "📈"
                        trimmed.contains("📉") -> "📉"
                        else -> ""
                    }
                    var cleanTextLine = trimmed.removePrefix("- ").trim()
                    if (emoji.isNotEmpty()) {
                        cleanTextLine = cleanTextLine.replace(emoji, "").trim()
                    }
                    
                    val (bgColor, borderColor, textColor) = when (emoji) {
                        "🔴" -> listOf(
                            if (themeDark) Color(0xFF2D0613) else Color(0xFFFFF1F2),
                            if (themeDark) Color(0xFF881337) else Color(0xFFFECDD3),
                            if (themeDark) Color(0xFFFDA4AF) else Color(0xFF9F1239)
                        )
                        "⚠️" -> listOf(
                            if (themeDark) Color(0xFF2E1B05) else Color(0xFFFFFBEB),
                            if (themeDark) Color(0xFF78350F) else Color(0xFFFDE68A),
                            if (themeDark) Color(0xFFFCD34D) else Color(0xFF92400E)
                        )
                        "✅", "✨" -> listOf(
                            if (themeDark) Color(0xFF022C22) else Color(0xFFF0FDF4),
                            if (themeDark) Color(0xFF065F46) else Color(0xFFBBF7D0),
                            if (themeDark) Color(0xFF34D399) else Color(0xFF15803D)
                        )
                        "📈" -> listOf(
                            if (themeDark) Color(0xFF1E152F) else Color(0xFFF5F3FF),
                            if (themeDark) Color(0xFF4A1D96) else Color(0xFFDDD6FE),
                            if (themeDark) Color(0xFFA78BFA) else Color(0xFF6D28D9)
                        )
                        "📉" -> listOf(
                            if (themeDark) Color(0xFF0F172A) else Color(0xFFEFF6FF),
                            if (themeDark) Color(0xFF1E3A8A) else Color(0xFFBFDBFE),
                            if (themeDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
                        )
                        else -> listOf(
                            if (themeDark) Color(0xFF1F2937) else Color(0xFFF9FAFB),
                            if (themeDark) Color(0xFF374151) else Color(0xFFF3F4F6),
                            if (themeDark) Color(0xFFD1D5DB) else Color(0xFF374151)
                        )
                    }
                    
                    val annotatedCardText = parseMarkdownLine(
                        cleanTextLine,
                        defaultColor = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                        boldColor = textColor
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(bgColor, RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = emoji, fontSize = 16.sp, modifier = Modifier.padding(top = 1.dp))
                            Text(
                                text = annotatedCardText,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                            )
                        }
                    }
                } else {
                    val contentLine = trimmed.removePrefix("- ").trim()
                    val icon = when {
                        contentLine.startsWith("**Total Belanja**") || contentLine.startsWith("Total Belanja") -> "🛍️"
                        contentLine.startsWith("**Rerata Transaksi**") || contentLine.startsWith("Rerata Transaksi") -> "📊"
                        contentLine.startsWith("**Sektor Dominan**") || contentLine.startsWith("Sektor Dominan") -> "🏷️"
                        contentLine.startsWith("**Rasio Laju Belanja Harian**") || contentLine.contains("Laju Belanja") -> "⚡"
                        contentLine.startsWith("**Proyeksi Sisa Bulan Ini**") || contentLine.contains("Proyeksi") -> "📅"
                        contentLine.startsWith("**Estimasi Saldo Akhir Bulan**") || contentLine.contains("Estimasi Saldo") -> "💰"
                        else -> "•"
                    }
                    
                    val annotatedBulletText = parseMarkdownLine(
                        contentLine,
                        defaultColor = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                        boldColor = if (themeDark) Color.White else Color(0xFF111827)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = icon,
                            fontSize = if (icon == "•") 16.sp else 14.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Text(
                            text = annotatedBulletText,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                        )
                    }
                }
            } else if (trimmed.startsWith("_") && trimmed.endsWith("_")) {
                val cleanItalic = trimmed.removeSurrounding("_", "_").trim()
                Text(
                    text = cleanItalic,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 16.sp
                    ),
                    color = if (themeDark) Color(0xFFA78BFA).copy(alpha = 0.8f) else Color(0xFF6D28D9).copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
            } else {
                val annotatedText = parseMarkdownLine(
                    trimmed,
                    defaultColor = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                    boldColor = if (themeDark) Color.White else Color(0xFF111827)
                )
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
    onNavigateToTransactions: () -> Unit = {}
) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val budgetAlert by viewModel.budgetAlert.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val themeDark by viewModel.themeDark.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val recurringBills by viewModel.allRecurringBills.collectAsState()
    val recentTxs = remember(transactions) { transactions.take(5) }

    var showQuickAddDialog by remember { mutableStateOf(false) }
    var showAddBillDialog by remember { mutableStateOf(false) }
    var showMlReportDialog by remember { mutableStateOf(false) }

    val rubelFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    rubelFormat.maximumFractionDigits = 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // BUTTON FOR PRIVATE LOCAL ML ENGINE REPORT
                FloatingActionButton(
                    onClick = { showMlReportDialog = true },
                    containerColor = if (themeDark) Color(0xFF5B21B6) else Color(0xFFDDD6FE),
                    contentColor = if (themeDark) Color(0xFFF3E8FF) else Color(0xFF5B21B6),
                    modifier = Modifier
                        .testTag("ml_report_fab")
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome, 
                        contentDescription = "Buka Analisis ML",
                        modifier = Modifier.size(20.dp)
                    )
                }

                FloatingActionButton(
                    onClick = { showQuickAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .testTag("quick_add_transaction_fab")
                        .padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Quick Add Expense")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Card + Connection Sync Indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Halo, $userName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Analisis real-time keuangan Anda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Interactive Warning Alert System (Budget Alerts)
            item {
                AnimatedVisibility(
                    visible = budgetAlert != null,
                    enter = fadeIn(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    budgetAlert?.let { alertMsg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_budget_alert_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Alert icon",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = alertMsg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Medium
                                )
                                IconButton(
                                    onClick = { viewModel.dismissBudgetAlert() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Elegant Metallic Wallet Balance Card (Material M3 Expressive Card with visual gradients)
            item {
                val premiumGradient = Brush.linearGradient(
                    colors = if (themeDark) {
                        listOf(Color(0xFF2E1065), Color(0xFF6D28D9), Color(0xFFBD287C))
                    } else {
                        listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6), Color(0xFFEC4899))
                    }
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wallet_balance_gradient_card"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        width = 1.6.dp,
                        color = if (themeDark) Color(0xFF4A2B8F) else Color(0xFFDDD6FE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(premiumGradient)
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet Icon",
                                        tint = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Total Saldo Gabungan",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Keamanan Aktif",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = rubelFormat.format(totalBalance),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 34.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Glassmorphism-inspired Info Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowUpward,
                                                contentDescription = "Inflow",
                                                tint = Color(0xFF6EE7B7),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pemasukan",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = rubelFormat.format(totalIncome),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDownward,
                                                contentDescription = "Outflow",
                                                tint = Color(0xFFFCA5A5),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pengeluaran",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = rubelFormat.format(totalExpense),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // DETEKSI NOTIFIKASI BANK CARD & SIMULATOR
            item {
                var isNotifAccessGranted by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    isNotifAccessGranted = isNotificationServiceEnabled(context)
                }

                val cardBg = if (themeDark) Color(0xFF1A1625) else Color(0xFFFAF5FF)
                val cardBorder = if (themeDark) Color(0xFF3A2B5E) else Color(0xFFE9E2FC)

                val textTitleColor = if (themeDark) Color.White else Color(0xFF311062)
                val textDescColor = if (themeDark) Color(0xFFD1CDDB) else Color(0xFF5A5266)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_notif_sync_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.2.dp, cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (isNotifAccessGranted) {
                                                if (themeDark) Color(0xFF0F2D24) else Color(0xFFE6F4EA)
                                            } else {
                                                if (themeDark) Color(0xFF3B2E1E) else Color(0xFFFFF7E6)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isNotifAccessGranted) Icons.Default.Check else Icons.Default.NotificationsActive,
                                        contentDescription = "Sync Icon",
                                        tint = if (isNotifAccessGranted) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Detektor Notifikasi Bank",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = textTitleColor
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isNotifAccessGranted) "Sistem Pendeteksi Aktif" else "Menunggu Akses Izin Notifikasi",
                                        fontSize = 11.sp,
                                        color = if (isNotifAccessGranted) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            if (!isNotifAccessGranted) {
                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Gagal membuka menu Pengaturan", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (themeDark) Color(0xFF8B5CF6) else Color(0xFF6D28D9)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Aktifkan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Text(
                            text = if (isNotifAccessGranted) 
                                "Membaca & merekam otomatis mutasi masuk/keluar dari notifikasi m-banking (BCA, Mandiri, BRI, BNI, OVO, GoPay) secara real-time."
                                else "Aktifkan izin membaca notifikasi agar pengeluaran/pemasukan di m-banking & e-wallet otomatis tercatat tanpa perlu input manual.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = textDescColor
                        )
                    }
                }
            }

            // Monthly Income vs Expense comparison bar chart (Recharts style)
            item {
                MonthlyBarComparisonChart(transactions = transactions)
            }

            // Weekly Trend line chart
            item {
                TransactionWeeklyTrendLineChart(transactions = transactions)
            }

            // Donut category split
            item {
                TransactionCategoryDonutChart(transactions = transactions)
            }

            // Tagihan Berkala (Recurring Bills) Panel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tagihan Berkala (Jatuh Tempo)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { showAddBillDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah")
                    }
                }
            }

            if (recurringBills.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Tidak Ada Tagihan Aktif", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("Tambahkan langganan Spotify, Netflix, PLN atau tagihan WIFI Anda di sini.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                items(recurringBills, key = { it.id }) { bill ->
                    RecurringBillItemCard(
                        bill = bill,
                        rubelFormat = rubelFormat,
                        themeDark = themeDark,
                        onPay = { viewModel.payRecurringBill(bill) },
                        onDelete = { viewModel.deleteRecurringBill(bill) }
                    )
                }
            }

            // Recent Transactions header list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Transaksi Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToTransactions) {
                        Text("Lihat Semua")
                    }
                }
            }

            if (recentTxs.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada transaksi masukan/luaran.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(recentTxs, key = { it.id }) { tx ->
                    TransactionItemRow(
                        tx = tx,
                        onDelete = { viewModel.deleteTransaction(it) },
                        onUpdate = { viewModel.updateTransaction(it) }
                    )
                }
            }

            // Extra space
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Interactive Quick Add Expense/Income dialog screen
    if (showQuickAddDialog) {
        var txTitle by remember { mutableStateOf("") }
        var txAmount by remember { mutableStateOf("") }
        var txType by remember { mutableStateOf("EXPENSE") } // or INCOME
        var txCategory by remember { mutableStateOf("Makanan") }

        val categories = listOf("Makanan", "Belanja", "Transportasi", "Gaji", "Investasi", "Sewa", "Lainnya")

        Dialog(onDismissRequest = { showQuickAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_add_dialog_surface")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Transaksi Cepat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Inflow / Outflow Segmented Control capsule toggler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    color = if (txType == "EXPENSE") MaterialTheme.colorScheme.errorContainer else Color.Transparent
                                )
                                .clickable { txType = "EXPENSE" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pengeluaran",
                                color = if (txType == "EXPENSE") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    color = if (txType == "INCOME") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                                .clickable { txType = "INCOME" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pemasukan",
                                color = if (txType == "INCOME") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    OutlinedTextField(
                        value = txTitle,
                        onValueChange = { txTitle = it },
                        label = { Text("Deskripsi Transaksi") },
                        placeholder = { Text("misal: Sate Ayam, Beli Bensin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = txAmount,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                txAmount = clean
                            }
                        },
                        label = { Text("Jumlah (IDR)") },
                        placeholder = { Text("misal: 45.000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category dropdown text label options
                    Text(
                        text = "Pilih Kategori:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val rowCats = if (txType == "INCOME") listOf("Gaji", "Investasi", "Lainnya") else listOf("Makanan", "Belanja", "Transportasi", "Sewa", "Lainnya")
                        rowCats.forEach { cat ->
                            val isSelected = txCategory == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { txCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showQuickAddDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val cleanAmt = txAmount.filter { it.isDigit() }
                                val amtDouble = cleanAmt.toDoubleOrNull() ?: 0.0
                                if (txTitle.isNotBlank() && amtDouble > 0) {
                                    viewModel.addTransaction(
                                        title = txTitle,
                                        amount = amtDouble,
                                        type = txType,
                                        category = if (txType == "INCOME" && txCategory == "Makanan") "Gaji" else txCategory
                                    )
                                    showQuickAddDialog = false
                                }
                            },
                            enabled = txTitle.isNotBlank() && txAmount.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }

    if (showAddBillDialog) {
        var billTitle by remember { mutableStateOf("") }
        var billAmountInput by remember { mutableStateOf("") }
        var billDueDate by remember { mutableStateOf("") }
        var billCategory by remember { mutableStateOf("Langganan") }
        var billCycle by remember { mutableStateOf("Bulanan") }

        val catsOptions = listOf("Langganan", "Utilitas", "Pendidikan", "Sewa", "Lainnya")
        val cycleOptions = listOf("Bulanan", "Mingguan", "Tahunan")

        Dialog(onDismissRequest = { showAddBillDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tambah Tagihan Berkala Baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = billTitle,
                        onValueChange = { billTitle = it },
                        label = { Text("Nama Tagihan") },
                        placeholder = { Text("misal: Spotify Family") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = billAmountInput,
                        onValueChange = { billAmountInput = it },
                        label = { Text("Nominal Tagihan (IDR)") },
                        placeholder = { Text("misal: 125000") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = billDueDate,
                        onValueChange = { billDueDate = it },
                        label = { Text("Hari / Tanggal Jatuh Tempo") },
                        placeholder = { Text("misal: Setiap Tanggal 15") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Siklus Tagihan:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cycleOptions.forEach { cy ->
                            val isSelected = billCycle == cy
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) Color(0xFFF06292) else if (themeDark) Color(0xFF2C1423) else Color(0xFFF3E5F5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { billCycle = cy }
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else if (themeDark) Color(0xFFEF9A9A) else Color(0xFF880E4F)
                                )
                            }
                        }
                    }

                    Text("Kategori Tagihan:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        catsOptions.forEach { cat ->
                            val isSelected = billCategory == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) Color(0xFFF06292) else if (themeDark) Color(0xFF2C1423) else Color(0xFFF3E5F5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { billCategory = cat }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else if (themeDark) Color(0xFFEF9A9A) else Color(0xFF880E4F)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddBillDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amtValue = billAmountInput.toDoubleOrNull() ?: 0.0
                                if (billTitle.isNotBlank() && amtValue > 0) {
                                    viewModel.addRecurringBill(
                                        title = billTitle,
                                        amount = amtValue,
                                        category = billCategory,
                                        billingCycle = billCycle,
                                        dueDate = billDueDate.ifEmpty { "Setiap akhir bulan" }
                                    )
                                    showAddBillDialog = false
                                }
                            },
                            enabled = billTitle.isNotBlank() && billAmountInput.toDoubleOrNull() != null
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }

    if (showMlReportDialog) {
        val summary by viewModel.mlWeeklySummary.collectAsState()
        val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()

        val infiniteTransition = rememberInfiniteTransition(label = "rotation_dlg")
        val rotationAngle by if (isGeneratingSummary) {
            infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotate_dlg"
            )
        } else {
            remember { mutableStateOf(0f) }
        }

        Dialog(onDismissRequest = { showMlReportDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .testTag("ml_report_dialog_container"),
                shape = RoundedCornerShape(24.dp),
                color = if (themeDark) Color(0xFF13111A) else Color(0xFFFAFAFE),
                border = BorderStroke(
                    width = 1.6.dp,
                    color = if (themeDark) Color(0xFF4C229E) else Color(0xFFE2D9FF)
                ),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (themeDark) Color(0xFF2E1C4E) else Color(0xFFF1E9FF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "On-Device ML Engine",
                                    tint = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Asisten Cerdas",
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (themeDark) Color.White else Color(0xFF311062)
                                )
                                Text(
                                    text = "Saran & Analisis Keuangan Otomatis",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.fetchWeeklySummary(force = true) },
                                enabled = !isGeneratingSummary,
                                modifier = Modifier.rotate(rotationAngle)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Hitung Ulang ML",
                                    tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9)
                                )
                            }
                            IconButton(onClick = { showMlReportDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = if (themeDark) Color.White.copy(alpha = 0.7f) else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Scrollable report text body
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(
                                if (themeDark) Color(0xFF0C0A10) else Color(0xFFF6F3FB),
                                RoundedCornerShape(18.dp)
                            )
                            .border(
                                1.dp,
                                if (themeDark) Color(0xFF2A1C44) else Color(0xFFEDE5F8),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(18.dp)
                    ) {
                        if (isGeneratingSummary) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (themeDark) Color(0xFFEC4899) else Color(0xFF6D28D9),
                                    trackColor = if (themeDark) Color(0xFF221A30) else Color(0xFFE9E4F5)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Mengalkulasi algoritme matematika lokal & melatih model regresi linear...",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = if (themeDark) Color.LightGray else Color(0xFF4A4A4A),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val displayText = summary ?: "Belum ada laporan statistik asisten cerdas yang tersimpan. Tekan tombol putar di kanan atas untuk menghitung laporan baru secara real-time!"
                            BeautifulMarkdownView(displayText, themeDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showMlReportDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ml_report_dialog_close_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (themeDark) Color(0xFF6D28D9) else Color(0xFF5B21B6)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Tutup Laporan", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    tx: Transaction,
    onDelete: (Transaction) -> Unit,
    onUpdate: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    currencyFormat.maximumFractionDigits = 0

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
    val cleanDate = dateFormat.format(Date(tx.timestamp))

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        var title by remember { mutableStateOf(tx.title) }
        
        // Initial amount format
        val initialAmountStr = if (tx.amount > 0) {
            tx.amount.toLong().toString()
        } else ""
        
        var amount by remember { mutableStateOf(initialAmountStr) }
        var type by remember { mutableStateOf(tx.type) }
        var category by remember { mutableStateOf(tx.category) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Transaksi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Segmented capsule toggler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    color = if (type == "EXPENSE") MaterialTheme.colorScheme.errorContainer else Color.Transparent
                                )
                                .clickable { type = "EXPENSE" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pengeluaran",
                                color = if (type == "EXPENSE") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    color = if (type == "INCOME") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                                .clickable { type = "INCOME" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pemasukan",
                                color = if (type == "INCOME") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                amount = clean
                            }
                        },
                        label = { Text("Jumlah (IDR)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Kategori:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val catRows = listOf("Makanan", "Belanja", "Transportasi", "Gaji", "Investasi", "Sewa", "Lainnya")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        catRows.forEach { cat ->
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Danger Delete Button
                        TextButton(
                            onClick = {
                                onDelete(tx)
                                showEditDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hapus")
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { showEditDialog = false }) {
                                Text("Batal")
                            }
                            Button(
                                onClick = {
                                    val cleanAmt = amount.filter { it.isDigit() }
                                    val dAmount = cleanAmt.toDoubleOrNull() ?: 0.0
                                    if (title.isNotBlank() && dAmount > 0) {
                                        val updatedTx = tx.copy(
                                            title = title,
                                            amount = dAmount,
                                            type = type,
                                            category = if (type == "INCOME" && category == "Makanan") "Gaji" else category
                                        )
                                        onUpdate(updatedTx)
                                        showEditDialog = false
                                    }
                                },
                                enabled = title.isNotBlank() && amount.filter { it.isDigit() }.isNotEmpty()
                            ) {
                                Text("Simpan")
                            }
                        }
                    }
                }
            }
        }
    }

    var animatedAlpha by remember { mutableStateOf(0f) }
    var animatedOffsetY by remember { mutableStateOf(40.dp) }

    val alphaAnim by animateFloatAsState(
        targetValue = animatedAlpha,
        animationSpec = tween(durationMillis = 350),
        label = "alpha"
    )
    val offsetYAnim by animateDpAsState(
        targetValue = animatedOffsetY,
        animationSpec = spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "offset"
    )

    LaunchedEffect(Unit) {
        animatedAlpha = 1f
        animatedOffsetY = 0.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = alphaAnim
                translationY = offsetYAnim.toPx()
            }
            .clickable { showEditDialog = true }
            .testTag("transaction_item_${tx.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Colored status indicator icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (tx.type == "INCOME") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = tx.type,
                        tint = if (tx.type == "INCOME") Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tx.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $cleanDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                val valueStr = (if (tx.type == "INCOME") "+" else "-") + currencyFormat.format(tx.amount).replace(",00", "")
                Text(
                    text = valueStr,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = if (tx.type == "INCOME") Color(0xFF00875A) else MaterialTheme.colorScheme.error
                )

                if (tx.bankSource != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tx.bankSource,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecurringBillItemCard(
    bill: com.example.data.model.RecurringBill,
    rubelFormat: NumberFormat,
    themeDark: Boolean,
    onPay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (themeDark) Color(0xFF1C0A15) else Color(0xFFFCE4EC)
    val cardBorderColor = if (themeDark) Color(0xFF38142C) else Color(0xFFF8BBD0)

    val iconBg = if (themeDark) Color(0xFF500F31) else Color(0xFFF48FB1)
    val iconTint = if (themeDark) Color(0xFFF06292) else Color(0xFF880E4F)

    val categoryColor = if (themeDark) Color(0xFFF06292) else Color(0xFFC2185B)
    val amountColor = if (themeDark) Color.White else Color(0xFF212121)
    val subtextColor = if (themeDark) Color.LightGray else Color.DarkGray

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recurring_bill_item_${bill.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Bill",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = bill.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bill.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = " • Tempo: ${bill.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = subtextColor
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = rubelFormat.format(bill.amount).replace(",00", ""),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Black,
                        color = amountColor
                    )
                    Text(
                        text = "Siklus: ${bill.billingCycle}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }

                IconButton(
                    onClick = onPay,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF06292), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Bayar Tagihan",
                        tint = Color(0xFF1C0A15),
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = if (themeDark) Color(0xFFEF9A9A) else Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun isNotificationServiceEnabled(context: android.content.Context): Boolean {
    val pkgName = context.packageName
    val flat = android.provider.Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    if (!flat.isNullOrEmpty()) {
        val names = flat.split(":")
        for (name in names) {
            val cn = android.content.ComponentName.unflattenFromString(name)
            if (cn != null && cn.packageName == pkgName) {
                return true
            }
        }
    }
    return false
}

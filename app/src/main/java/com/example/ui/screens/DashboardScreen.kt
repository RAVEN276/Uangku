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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.data.model.getCleanTitle
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
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val budgetAlert by viewModel.budgetAlert.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()
    val bankNotificationEnabled by viewModel.bankNotificationEnabled.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val recurringBills by viewModel.allRecurringBills.collectAsStateWithLifecycle()
    val recentTxs = remember(transactions) { transactions.take(5) }

    var showQuickAddDialog by remember { mutableStateOf(false) }
    var showAddBillDialog by remember { mutableStateOf(false) }
    var showMlReportDialog by remember { mutableStateOf(false) }
    var showBillCalendar by remember { mutableStateOf(false) }
    var selectedCalendarDay by remember { mutableStateOf<Int?>(null) }

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
            if (!bankNotificationEnabled) {
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
                                            viewModel.setBankNotificationEnabled(true)
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
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Toggle Calendar / List
                    IconButton(
                        onClick = { showBillCalendar = !showBillCalendar },
                        modifier = Modifier.size(36.dp).testTag("bill_toggle_calendar_view_btn")
                    ) {
                        Icon(
                            imageVector = if (showBillCalendar) Icons.Default.ListAlt else Icons.Default.CalendarToday,
                            contentDescription = if (showBillCalendar) "Tampilan Daftar" else "Tampilan Kalender",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(onClick = { showAddBillDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah")
                    }
                }
            }

            if (showBillCalendar) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Month Header
                        val monthName = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date())
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Weekdays Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        val calendar = java.util.Calendar.getInstance()
                        val todayDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        val maxDays = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                        
                        val firstDayCal = java.util.Calendar.getInstance().apply {
                            set(java.util.Calendar.DAY_OF_MONTH, 1)
                        }
                        val firstDayOfWeek = (firstDayCal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                        
                        val totalSlots = firstDayOfWeek + maxDays
                        val rows = (totalSlots + 6) / 7
                        
                        var currentDayToDraw = 1
                        for (r in 0 until rows) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                for (c in 0..6) {
                                    val slotIndex = r * 7 + c
                                    if (slotIndex < firstDayOfWeek || currentDayToDraw > maxDays) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    } else {
                                        val dayNum = currentDayToDraw
                                        val isToday = dayNum == todayDay
                                        
                                        val billsDueThisDay = recurringBills.filter { bill ->
                                            viewModel.extractDayFromDueDate(bill.dueDate) == dayNum
                                        }
                                        val hasBill = billsDueThisDay.isNotEmpty()
                                        
                                        val isSelected = selectedCalendarDay == dayNum
                                        
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else if (isToday) {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    } else {
                                                        Color.Transparent
                                                    }
                                                )
                                                .clickable {
                                                    selectedCalendarDay = if (isSelected) null else dayNum
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else if (isToday) {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                if (hasBill) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(RoundedCornerShape(2.5.dp))
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.onPrimary else Color(0xFFF06292)
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                        currentDayToDraw++
                                    }
                                }
                            }
                        }
                    }
                }

                if (selectedCalendarDay != null) {
                    val dayNum = selectedCalendarDay!!
                    val billsForDay = recurringBills.filter { bill ->
                        viewModel.extractDayFromDueDate(bill.dueDate) == dayNum
                    }
                    
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tagihan Tanggal $dayNum:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = { selectedCalendarDay = null }) {
                                    Text("Tutup", fontSize = 11.sp)
                                }
                            }
                            
                            if (billsForDay.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Tidak ada tagihan jatuh tempo pada tanggal ini.",
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(12.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                billsForDay.forEach { bill ->
                                    RecurringBillItemCard(
                                        bill = bill,
                                        rubelFormat = rubelFormat,
                                        themeDark = themeDark,
                                        onPay = { viewModel.payRecurringBill(bill) },
                                        onDelete = { viewModel.deleteRecurringBill(bill) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "💡 Pilih tanggal dengan indikator titik pink untuk melihat detail tagihan.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
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
                    items(recurringBills, key = { "bill_${it.id}" }) { bill ->
                        RecurringBillItemCard(
                            bill = bill,
                            rubelFormat = rubelFormat,
                            themeDark = themeDark,
                            onPay = { viewModel.payRecurringBill(bill) },
                            onDelete = { viewModel.deleteRecurringBill(bill) }
                        )
                    }
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
                items(recentTxs, key = { "tx_${it.id}" }) { tx ->
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
                        onValueChange = { input ->
                            txTitle = input
                            val hist = viewModel.allTransactions.value
                            val predicted = com.example.service.LocalFinanceMLEngine.predictCategory(input, hist)
                            if (predicted != null) {
                                txCategory = predicted
                            }
                        },
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

        // Pre-defined template items for rapid filling
        val popularTemplates = listOf(
            Triple("Spotify", 86000, "Langganan"),
            Triple("Netflix", 186000, "Langganan"),
            Triple("YouTube", 59000, "Langganan"),
            Triple("ISP WiFi", 350000, "Utilitas"),
            Triple("Token Listrik", 200000, "Utilitas"),
            Triple("Sewa Kos", 1500000, "Sewa")
        )

        Dialog(onDismissRequest = { showAddBillDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header with Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (themeDark) Color(0xFF500F31) else Color(0xFFFCE4EC),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (themeDark) Color(0xFFF06292) else Color(0xFF880E4F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Tagihan Berkala Baru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Isi form atau pilih salah satu pintasan layanan populer di bawah untuk mengisi otomatis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Popular Subscriptions Shortcut Row
                    Text(
                        text = "Rekomendasi Layanan Populer:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (themeDark) Color(0xFFF06292) else Color(0xFF880E4F)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularTemplates.forEach { (name, price, cat) ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (themeDark) Color(0xFF2C1423) else Color(0xFFFCE4EC).copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (themeDark) Color(0xFF500F31) else Color(0xFFF8BBD0),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        billTitle = name
                                        billAmountInput = price.toString()
                                        billCategory = cat
                                        billCycle = "Bulanan"
                                        billDueDate = "Setiap tanggal 5"
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = when (name) {
                                            "Spotify" -> "🎵"
                                            "Netflix" -> "🍿"
                                            "YouTube" -> "📺"
                                            "ISP WiFi" -> "🌐"
                                            "Token Listrik" -> "⚡"
                                            else -> "🏠"
                                        },
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (themeDark) Color(0xFFEF9A9A) else Color(0xFF880E4F)
                                    )
                                }
                            }
                        }
                    }

                    // Fields
                    OutlinedTextField(
                        value = billTitle,
                        onValueChange = { billTitle = it },
                        label = { Text("Nama Tagihan") },
                        placeholder = { Text("misal: Langganan Spotify Family") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = billAmountInput,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() }
                                if (clean.length <= 15) {
                                    billAmountInput = clean
                                }
                            },
                            label = { Text("Nominal Tagihan (IDR)") },
                            placeholder = { Text("misal: 125.000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = RupiahVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick Amount Adders
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(20000, 50000, 100000, 200000, 500000).forEach { amt ->
                                val priceFormatted = if (amt >= 1000) "${amt / 1000}rb" else amt.toString()
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (themeDark) Color(0xFF1E1A2B) else Color(0xFFECEFF1),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            val current = billAmountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
                                            billAmountInput = (current + amt).toString()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+Rp$priceFormatted",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (themeDark) Color.LightGray else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = billDueDate,
                        onValueChange = { billDueDate = it },
                        label = { Text("Jatuh Tempo (Hari / Tanggal)") },
                        placeholder = { Text("misal: Tanggal 15, Senin pertama") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Siklus Tagihan Berkala:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                val cleanAmt = billAmountInput.filter { it.isDigit() }
                                val amtValue = cleanAmt.toDoubleOrNull() ?: 0.0
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
                            enabled = billTitle.isNotBlank() && billAmountInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }

    if (showMlReportDialog) {
        val summary by viewModel.mlWeeklySummary.collectAsStateWithLifecycle()
        val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsStateWithLifecycle()
        val mlInsights by viewModel.mlInsights.collectAsStateWithLifecycle()
        var activeMlTab by remember { mutableStateOf(0) }

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

        Dialog(
            onDismissRequest = { showMlReportDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
                    .testTag("ml_report_dialog_container"),
                shape = RoundedCornerShape(26.dp),
                color = if (themeDark) Color(0xFF110D1C) else Color(0xFFFAFAFE),
                border = BorderStroke(
                    width = 1.6.dp,
                    color = if (themeDark) Color(0xFF4C229E) else Color(0xFFDCD3FD)
                ),
                tonalElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
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
                                    .size(40.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (themeDark) Color(0xFF1E142B) else Color(0xFFF1E9FF),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp)
                    ) {
                        val activeColor = if (themeDark) Color(0xFF6D28D9) else Color(0xFFE9D5FF)
                        val activeTextColor = if (themeDark) Color.White else Color(0xFF4C229E)
                        val inactiveTextColor = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeMlTab == 0) activeColor else Color.Transparent)
                                .clickable { activeMlTab = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Ringkasan Analisis",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeMlTab == 0) activeTextColor else inactiveTextColor
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeMlTab == 1) activeColor else Color.Transparent)
                                .clickable { activeMlTab = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (activeMlTab == 1) activeTextColor else inactiveTextColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Lab Interaktif 2.0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeMlTab == 1) activeTextColor else inactiveTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Content Scroll Container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
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
                                    color = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9),
                                    trackColor = if (themeDark) Color(0xFF221A30) else Color(0xFFE9E4F5)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Mengalkulasi parameter matematika, klastering spasial, dan peramalan tren...",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = if (themeDark) Color.LightGray else Color(0xFF4A4A4A),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val isColdState = mlInsights == null || mlInsights!!.totalExpense == 0.0 || (summary != null && summary!!.startsWith("Belum ada data pengeluaran"))

                            if (isColdState) {
                                // Cold Onboarding layout for Untrained local Model
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (themeDark) Color(0xFF1A1428) else Color(0xFFF6F3FB)
                                        ),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (themeDark) Color(0xFF332057) else Color(0xFFEDE3FB)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .background(
                                                        if (themeDark) Color(0xFF2E1C4E) else Color(0xFFF1E9FF),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9),
                                                    modifier = Modifier.size(34.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = "Model Analisis Belum Terlatih",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (themeDark) Color.White else Color(0xFF311062),
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Asisten Cerdas memproses data keuangan hibrida di memori perlindungan lokal perangkat Anda. Tambahkan transaksi pengeluaran terlebih dahulu agar model lokal kami dapat melacak laju belanja harian.",
                                                fontSize = 11.sp,
                                                color = if (themeDark) Color.LightGray.copy(alpha = 0.7f) else Color.DarkGray,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 16.sp
                                            )

                                            Spacer(modifier = Modifier.height(18.dp))

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (themeDark) Color(0xFF0F0A18) else Color(0xFFFAF7FE),
                                                        RoundedCornerShape(14.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (themeDark) Color(0xFF26183F) else Color(0xFFF1EAFD),
                                                        RoundedCornerShape(14.dp)
                                                    )
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                listOf(
                                                    Pair("🎯 Skor Keseimbangan FHS", "Kombinasi rasio tabungan, konsentrasi belanja, & burn rate."),
                                                    Pair("🛒 Segmentasi K-Means", "Klastering cerdas perilaku belanja rutin harian vs makro."),
                                                    Pair("📈 Peramalan Holt Linear", "Peramalan time-series tingkat sirkulasi kas pekan depan."),
                                                    Pair("🛡️ Deteksi Anomali IQR", "Pemetaan bias pengeluaran ekstrem di luar kewajaran belanja.")
                                                ).forEach { (title, desc) ->
                                                    Row(verticalAlignment = Alignment.Top) {
                                                        Text(
                                                            text = "⚡",
                                                            fontSize = 11.sp,
                                                            modifier = Modifier.padding(top = 1.dp, end = 6.dp),
                                                            color = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9)
                                                        )
                                                        Column {
                                                            Text(
                                                                text = title,
                                                                fontSize = 10.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (themeDark) Color.White else Color(0xFF311062)
                                                            )
                                                            Text(
                                                                text = desc,
                                                                fontSize = 9.sp,
                                                                color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray,
                                                                lineHeight = 12.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(18.dp))

                                            Button(
                                                onClick = {
                                                    showMlReportDialog = false
                                                    showQuickAddDialog = true
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (themeDark) Color(0xFF6D28D9) else Color(0xFF5B21B6)
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Tambah Pengeluaran Pertama", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Full visual dashboard reporting layout
                                val insights = mlInsights!!
                                val rp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                                    maximumFractionDigits = 0
                                }

                                if (activeMlTab == 0) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                    // 1. Health Score Meter (Animated Arc Dial)
                                    val scoreColor = when {
                                        insights.financialHealthScore >= 85 -> if (themeDark) Color(0xFFC084FC) else Color(0xFF7C3AED)
                                        insights.financialHealthScore >= 70 -> if (themeDark) Color(0xFF34D399) else Color(0xFF059669)
                                        insights.financialHealthScore >= 50 -> if (themeDark) Color(0xFFFBBF24) else Color(0xFFD97706)
                                        else -> if (themeDark) Color(0xFFF87171) else Color(0xFFDC2626)
                                    }
                                    
                                    val scoreLabel = when {
                                        insights.financialHealthScore >= 85 -> "RATING: SANGAT PRIMA ✨"
                                        insights.financialHealthScore >= 70 -> "RATING: KONDISI SEHAT 👍"
                                        insights.financialHealthScore >= 50 -> "RATING: PERLU WASPADA ⚠️"
                                        else -> "RATING: DEFISIT KRITIS 🚨"
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (themeDark) Color(0xFF1B1527) else Color(0xFFF5F2FB)
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (themeDark) Color(0xFF332057) else Color(0xFFE5DEFF)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "SKOR KESEHATAN FINANSIAL",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.Gray,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.size(110.dp)
                                            ) {
                                                // Dial outline
                                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                    drawArc(
                                                        color = scoreColor.copy(alpha = if (themeDark) 0.15f else 0.1f),
                                                        startAngle = 140f,
                                                        sweepAngle = 260f,
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                            width = 9.dp.toPx(),
                                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                                        )
                                                    )
                                                    drawArc(
                                                        color = scoreColor,
                                                        startAngle = 140f,
                                                        sweepAngle = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                            width = 9.dp.toPx(),
                                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                                        )
                                                    )
                                                }
                                                // Inner score text
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "${insights.financialHealthScore}",
                                                        fontSize = 30.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                    )
                                                    Text(
                                                        text = "skor FHS",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(scoreColor.copy(alpha = 0.15f))
                                                    .border(1.dp, scoreColor.copy(alpha = 0.4f), RoundedCornerShape(50))
                                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    text = scoreLabel,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = scoreColor
                                                )
                                            }
                                        }
                                    }

                                    // 2. Holt's Spending Forecast Card
                                    val averageWeeklySpent = insights.totalExpense / 4.0
                                    val isSaving = insights.forecastedNextWeek < averageWeeklySpent
                                    val trendColor = if (isSaving) {
                                        if (themeDark) Color(0xFF34D399) else Color(0xFF0F9D58)
                                    } else {
                                        if (themeDark) Color(0xFFF87171) else Color(0xFFDB4437)
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (themeDark) Color(0xFF1B1527) else Color(0xFFF5F2FB)
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (themeDark) Color(0xFF332057) else Color(0xFFE5DEFF)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (isSaving) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                        contentDescription = null,
                                                        tint = trendColor,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "PREDIKSI TREN PEKAN DEPAN",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.Gray,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                                
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (themeDark) Color(0xFF2E1C4E) else Color(0xFFEDE5F8))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Holt's Model",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9)
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            Text(
                                                text = rp.format(insights.forecastedNextWeek),
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                            )
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Text(
                                                text = if (isSaving) {
                                                    "✨ Pola penghematan bermutu tinggi. Model memprediksi tingkat sirkulasi belanja menurun dibanding pekan sebelumnya."
                                                } else {
                                                    "🔴 Terdeteksi kenaikan belanja. Algoritme memproyeksikan peningkatan sisa. Batasi belanja sekunder Anda."
                                                },
                                                fontSize = 11.sp,
                                                color = if (themeDark) Color.LightGray.copy(alpha = 0.7f) else Color.DarkGray,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }

                                    // 3. Stats Metric Grid (2x2 Grid using Rows)
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = "METRIK LAJU & PROYEKSI",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.Gray,
                                            letterSpacing = 0.5.sp
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(92.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)
                                                    ),
                                                    border = BorderStroke(1.dp, if (themeDark) Color(0xFF2A2045) else Color(0xFFECE6F5))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text("DAILY BURN RATE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray)
                                                            Icon(Icons.Default.TrendingDown, null, tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9), modifier = Modifier.size(13.dp))
                                                        }
                                                        Text(rp.format(insights.dailyBurnRate), fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (themeDark) Color.White else Color(0xFF1E1B4B))
                                                        Text("Laju belanja per hari", fontSize = 8.5.sp, color = if (themeDark) Color.LightGray.copy(alpha = 0.5f) else Color.Gray)
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(92.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)
                                                    ),
                                                    border = BorderStroke(1.dp, if (themeDark) Color(0xFF2A2045) else Color(0xFFECE6F5))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text("PROYEKSI BULAN", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray)
                                                            Icon(Icons.Default.CalendarToday, null, tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9), modifier = Modifier.size(13.dp))
                                                        }
                                                        Text(rp.format(insights.projectedSpendRemainingMonth), fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (themeDark) Color.White else Color(0xFF1E1B4B))
                                                        Text("Est. kebutuhan sisa hari", fontSize = 8.5.sp, color = if (themeDark) Color.LightGray.copy(alpha = 0.5f) else Color.Gray)
                                                    }
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                val remainingBalance = insights.estimatedBalanceEndOfMonth
                                                val isPositive = remainingBalance >= 0
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(92.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)
                                                    ),
                                                    border = BorderStroke(1.dp, if (themeDark) Color(0xFF2A2045) else Color(0xFFECE6F5))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text("ESTIMASI SALDO", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray)
                                                            Icon(Icons.Default.AccountBalanceWallet, null, tint = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171), modifier = Modifier.size(13.dp))
                                                        }
                                                        Text(rp.format(remainingBalance), fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (themeDark) Color.White else Color(0xFF1E1B4B))
                                                        Text(if (isPositive) "Potensi surplus saldo" else "Resiko defisit kas akhir", fontSize = 8.5.sp, color = if (themeDark) Color.LightGray.copy(alpha = 0.5f) else Color.Gray)
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(92.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)
                                                    ),
                                                    border = BorderStroke(1.dp, if (themeDark) Color(0xFF2A2045) else Color(0xFFECE6F5))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text("SEKTOR UTAMA", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray)
                                                            Icon(Icons.Default.CreditCard, null, tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9), modifier = Modifier.size(13.dp))
                                                        }
                                                        Text(insights.topCategory, fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (themeDark) Color.White else Color(0xFF1E1B4B), maxLines = 1)
                                                        Text("${String.format("%.1f", insights.topCategoryPercentage)}% dari seluruh belanja", fontSize = 8.5.sp, color = if (themeDark) Color.LightGray.copy(alpha = 0.5f) else Color.Gray, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 4. K-Means Clusters summary
                                    if (insights.kMeansClusterSummaries.isNotEmpty()) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (themeDark) Color(0xFF1B1527) else Color(0xFFF5F2FB)
                                            ),
                                            border = BorderStroke(
                                                1.dp,
                                                if (themeDark) Color(0xFF332057) else Color(0xFFE5DEFF)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.ListAlt,
                                                        contentDescription = null,
                                                        tint = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "SEGMENTASI PERILAKU (K-Means)",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.Gray,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                                
                                                Spacer(modifier = Modifier.height(12.dp))
                                                
                                                insights.kMeansClusterSummaries.forEach { summaryStr ->
                                                    var parsedSuccessfully = false
                                                    var cleanLabel = ""
                                                    var countPart = ""
                                                    var detailsPart = ""
                                                    try {
                                                        cleanLabel = summaryStr.substringBefore("** (").replace("**", "").trim()
                                                        countPart = summaryStr.substringAfter("** (").substringBefore("):").trim()
                                                        detailsPart = summaryStr.substringAfter("):").trim()
                                                        parsedSuccessfully = true
                                                    } catch (e: Exception) {
                                                        parsedSuccessfully = false
                                                    }

                                                    if (parsedSuccessfully) {
                                                        val iconEmoji = when {
                                                            cleanLabel.contains("Mikro") -> "🛒"
                                                            cleanLabel.contains("Lifestyle") -> "🍿"
                                                            else -> "💳"
                                                        }

                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                                                .background(
                                                                    if (themeDark) Color(0xFF130F1F) else Color(0xFFFAF8FD),
                                                                    RoundedCornerShape(12.dp)
                                                                )
                                                                .border(
                                                                    1.dp,
                                                                    if (themeDark) Color(0xFF281C44) else Color(0xFFEDE4F8),
                                                                    RoundedCornerShape(12.dp)
                                                                )
                                                                .padding(10.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .background(if (themeDark) Color(0xFF23143E) else Color(0xFFF1E8FD), CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(iconEmoji, fontSize = 14.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = cleanLabel,
                                                                    fontSize = 11.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                                )
                                                                Text(
                                                                    text = detailsPart,
                                                                    fontSize = 9.sp,
                                                                    color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray,
                                                                    lineHeight = 11.sp
                                                                )
                                                            }
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(6.dp))
                                                                    .background(if (themeDark) Color(0xFFC084FC).copy(alpha = 0.15f) else Color(0xFF6D28D9).copy(alpha = 0.1f))
                                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                                            ) {
                                                                Text(
                                                                    text = countPart,
                                                                    fontSize = 8.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9)
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Text(
                                                            text = summaryStr,
                                                            fontSize = 10.sp,
                                                            color = if (themeDark) Color.LightGray else Color.DarkGray,
                                                            modifier = Modifier.padding(vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 5. Smart Recommendations List
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "STRATEGI & REKOMENDASI INTERAKTIF",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.Gray,
                                            letterSpacing = 0.5.sp
                                        )
                                        
                                        insights.recommendations.forEach { recommendation ->
                                            val recEmoji = when {
                                                recommendation.contains("🔴") -> "🔴"
                                                recommendation.contains("⚠️") -> "⚠️"
                                                recommendation.contains("✅") -> "✅"
                                                recommendation.contains("✨") -> "✨"
                                                recommendation.contains("📈") -> "📈"
                                                recommendation.contains("📉") -> "📉"
                                                else -> "💡"
                                            }
                                            
                                            var cleanRec = recommendation.trim()
                                            if (recEmoji != "💡") {
                                                cleanRec = cleanRec.substringAfter(recEmoji).trim()
                                            }
                                            
                                            val (bgColor, borderColor, textCol) = when (recEmoji) {
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
                                                    if (themeDark) Color(0xFF151324) else Color(0xFFFAF9FD),
                                                    if (themeDark) Color(0xFF2D234A) else Color(0xFFEDE4F9),
                                                    if (themeDark) Color(0xFFC084FC) else Color(0xFF6D28D9)
                                                )
                                            }

                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp),
                                                colors = CardDefaults.cardColors(containerColor = bgColor),
                                                border = BorderStroke(1.dp, borderColor)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.Top,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(text = recEmoji, fontSize = 14.sp)
                                                    Text(
                                                        text = parseMarkdownLine(
                                                            cleanRec,
                                                            defaultColor = if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                                            boldColor = textCol
                                                        ),
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Laporan Tekstual Lengkap v2 removed per user request
                                }
                                } else {
                                    val expenses = remember(transactions) { transactions.filter { it.type == "EXPENSE" }.sortedBy { it.timestamp } }
                                    val totalIncomeAmt = remember(transactions) { transactions.filter { it.type == "INCOME" }.sumOf { it.amount } }
                                    val totalExpenseAmt = remember(transactions) { transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount } }
                                    val calculatedDailyBurn = insights.dailyBurnRate
                                    val topCatPct = insights.topCategoryPercentage

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF1E142B) else Color(0xFFF1E9FF))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Text(
                                                     "Eksplorasi Algoritme ML Lokal",
                                                     fontWeight = FontWeight.Bold,
                                                     fontSize = 12.sp,
                                                     color = if (themeDark) Color.White else Color(0xFF311062)
                                                 )
                                                 Spacer(modifier = Modifier.height(4.dp))
                                                 Text(
                                                     "Uji dan latih ulang model matematika cerdas Uangku secara interaktif di bawah ini. Semua kalkulasi diproses 100% luring.",
                                                     fontSize = 10.sp,
                                                     color = if (themeDark) Color.LightGray.copy(alpha = 0.8f) else Color.DarkGray,
                                                     lineHeight = 14.sp
                                                 )
                                             }
                                         }

                                         var testTitle by remember { mutableStateOf("") }
                                         val testPredicted = remember(testTitle, transactions) {
                                             if (testTitle.isBlank()) null else com.example.service.LocalFinanceMLEngine.predictCategory(testTitle, transactions)
                                         }

                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)),
                                             border = BorderStroke(1.dp, if (themeDark) Color(0xFF2E2045) else Color(0xFFECE6F5))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                                     Box(
                                                         modifier = Modifier
                                                             .size(24.dp)
                                                             .background(if (themeDark) Color(0xFF331F4D) else Color(0xFFE9D5FF), CircleShape),
                                                         contentAlignment = Alignment.Center
                                                     ) {
                                                         Text("6", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9))
                                                     }
                                                     Spacer(modifier = Modifier.width(8.dp))
                                                     Text(
                                                         "Prediktor Kategori Otomatis (NLP)",
                                                         fontWeight = FontWeight.Bold,
                                                         fontSize = 12.sp,
                                                         color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                     )
                                                 }
                                                 
                                                 Spacer(modifier = Modifier.height(10.dp))
                                                 
                                                 OutlinedTextField(
                                                     value = testTitle,
                                                     onValueChange = { testTitle = it },
                                                     label = { Text("Ketik Deskripsi Pengujian", fontSize = 10.sp) },
                                                     placeholder = { Text("misal: kopi susu siang, bensin pertalite", fontSize = 10.sp) },
                                                     singleLine = true,
                                                     modifier = Modifier.fillMaxWidth(),
                                                     textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                                 )
                                                 
                                                 Spacer(modifier = Modifier.height(8.dp))
                                                 
                                                 if (testTitle.isNotBlank()) {
                                                     Row(
                                                         verticalAlignment = Alignment.CenterVertically,
                                                         modifier = Modifier
                                                             .fillMaxWidth()
                                                             .background(if (themeDark) Color(0xFF221A30) else Color(0xFFF5F3FF), RoundedCornerShape(8.dp))
                                                             .padding(8.dp)
                                                     ) {
                                                         Text("Hasil Prediksi Kategori:", fontSize = 10.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                         Spacer(modifier = Modifier.width(8.dp))
                                                         Box(
                                                             modifier = Modifier
                                                                 .clip(RoundedCornerShape(6.dp))
                                                                 .background(if (themeDark) Color(0xFF6D28D9) else Color(0xFFDDD6FE))
                                                                 .padding(horizontal = 8.dp, vertical = 3.dp)
                                                         ) {
                                                             Text(
                                                                 text = testPredicted ?: "Lainnya (Default)",
                                                                 fontSize = 10.sp,
                                                                 fontWeight = FontWeight.Bold,
                                                                 color = if (themeDark) Color.White else Color(0xFF4C229E)
                                                             )
                                                         }
                                                     }
                                                 } else {
                                                     Text(
                                                         "Ketik deskripsi transaksi di atas untuk menguji model klasifikasi NLP lokal.",
                                                         fontSize = 9.sp,
                                                         color = if (themeDark) Color.Gray else Color.LightGray
                                                     )
                                                 }
                                             }
                                         }

                                         var simIncome by remember { mutableStateOf(totalIncomeAmt.coerceIn(500000.0, 50000000.0)) }
                                         var simExpense by remember { mutableStateOf(totalExpenseAmt.coerceIn(0.0, 50000000.0)) }
                                         var simBurnRate by remember { mutableStateOf(calculatedDailyBurn.coerceIn(0.0, 2500000.0)) }
                                         var simAnomalies by remember { mutableStateOf(insights.anomalies.size.toFloat().coerceIn(0f, 10f)) }

                                         val simScore = remember(simIncome, simExpense, simBurnRate, simAnomalies, topCatPct) {
                                             com.example.service.LocalFinanceMLEngine.calculateFinancialHealthScore(
                                                 totalIncome = simIncome,
                                                 totalExpense = simExpense,
                                                 burnRate = simBurnRate,
                                                 totalBalance = (simIncome - simExpense).coerceAtLeast(0.0),
                                                 anomaliesCount = simAnomalies.toInt(),
                                                 topCategoryPct = topCatPct
                                             )
                                         }

                                         val simColor = when {
                                             simScore >= 85 -> if (themeDark) Color(0xFFC084FC) else Color(0xFF7C3AED)
                                             simScore >= 70 -> if (themeDark) Color(0xFF34D399) else Color(0xFF059669)
                                             simScore >= 50 -> if (themeDark) Color(0xFFFBBF24) else Color(0xFFD97706)
                                             else -> if (themeDark) Color(0xFFF87171) else Color(0xFFDC2626)
                                         }

                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)),
                                             border = BorderStroke(1.dp, if (themeDark) Color(0xFF2E2045) else Color(0xFFECE6F5))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                                     Box(
                                                         modifier = Modifier
                                                             .size(24.dp)
                                                             .background(if (themeDark) Color(0xFF331F4D) else Color(0xFFE9D5FF), CircleShape),
                                                         contentAlignment = Alignment.Center
                                                     ) {
                                                         Text("4", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9))
                                                     }
                                                     Spacer(modifier = Modifier.width(8.dp))
                                                     Text(
                                                         "Simulator Skor Kesehatan (FHS)",
                                                         fontWeight = FontWeight.Bold,
                                                         fontSize = 12.sp,
                                                         color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                     )
                                                 }
                                                 
                                                 Spacer(modifier = Modifier.height(14.dp))
                                                 
                                                 Row(
                                                     modifier = Modifier.fillMaxWidth(),
                                                     horizontalArrangement = Arrangement.SpaceBetween,
                                                     verticalAlignment = Alignment.CenterVertically
                                                 ) {
                                                     Column(modifier = Modifier.weight(1f)) {
                                                         Text("Simulasi Pendapatan: ${rp.format(simIncome)}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                         androidx.compose.material3.Slider(
                                                             value = simIncome.toFloat(),
                                                             onValueChange = { simIncome = it.toDouble() },
                                                             valueRange = 500000f..50000000f,
                                                             modifier = Modifier.fillMaxWidth().height(24.dp)
                                                         )
                                                         
                                                         Spacer(modifier = Modifier.height(6.dp))
                                                         
                                                         Text("Simulasi Pengeluaran: ${rp.format(simExpense)}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                         androidx.compose.material3.Slider(
                                                             value = simExpense.toFloat(),
                                                             onValueChange = { simExpense = it.toDouble() },
                                                             valueRange = 0f..50000000f,
                                                             modifier = Modifier.fillMaxWidth().height(24.dp)
                                                         )

                                                         Spacer(modifier = Modifier.height(6.dp))
                                                         
                                                         Text("Simulasi Laju Harian: ${rp.format(simBurnRate)}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                         androidx.compose.material3.Slider(
                                                             value = simBurnRate.toFloat(),
                                                             onValueChange = { simBurnRate = it.toDouble() },
                                                             valueRange = 0f..2500000f,
                                                             modifier = Modifier.fillMaxWidth().height(24.dp)
                                                         )

                                                         Spacer(modifier = Modifier.height(6.dp))
                                                         
                                                         Text("Simulasi Jumlah Anomali: ${simAnomalies.toInt()}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                         androidx.compose.material3.Slider(
                                                             value = simAnomalies,
                                                             onValueChange = { simAnomalies = it },
                                                             valueRange = 0f..10f,
                                                             steps = 9,
                                                             modifier = Modifier.fillMaxWidth().height(24.dp)
                                                         )
                                                     }
                                                     
                                                     Spacer(modifier = Modifier.width(16.dp))
                                                     
                                                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                         Box(contentAlignment = Alignment.Center, modifier = Modifier.size(68.dp)) {
                                                             androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                                 drawArc(
                                                                     color = simColor.copy(alpha = 0.15f),
                                                                     startAngle = 140f,
                                                                     sweepAngle = 260f,
                                                                     useCenter = false,
                                                                     style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                                 )
                                                                 drawArc(
                                                                     color = simColor,
                                                                     startAngle = 140f,
                                                                     sweepAngle = 260f * (simScore / 100f),
                                                                     useCenter = false,
                                                                     style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                                 )
                                                             }
                                                             Text(
                                                                 text = "$simScore",
                                                                 fontSize = 18.sp,
                                                                 fontWeight = FontWeight.Black,
                                                                 color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                             )
                                                         }
                                                         Spacer(modifier = Modifier.height(4.dp))
                                                         Text(
                                                             text = if (simScore >= 85) "Prima" else if (simScore >= 70) "Sehat" else if (simScore >= 50) "Waspada" else "Kritis",
                                                             fontSize = 9.sp,
                                                             fontWeight = FontWeight.Bold,
                                                             color = simColor
                                                         )
                                                     }
                                                 }
                                             }
                                         }

                                         var simAlpha by remember { mutableStateOf(0.5f) }
                                         var simBeta by remember { mutableStateOf(0.4f) }

                                         val simForecast = remember(simAlpha, simBeta, expenses) {
                                             com.example.service.LocalFinanceMLEngine.forecastNextWeekHoltCustom(expenses, simAlpha.toDouble(), simBeta.toDouble())
                                         }

                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)),
                                             border = BorderStroke(1.dp, if (themeDark) Color(0xFF2E2045) else Color(0xFFECE6F5))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                                     Box(
                                                         modifier = Modifier
                                                             .size(24.dp)
                                                             .background(if (themeDark) Color(0xFF331F4D) else Color(0xFFE9D5FF), CircleShape),
                                                         contentAlignment = Alignment.Center
                                                     ) {
                                                         Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9))
                                                     }
                                                     Spacer(modifier = Modifier.width(8.dp))
                                                     Text(
                                                         "Simulator Peramalan Holt Linear",
                                                         fontWeight = FontWeight.Bold,
                                                         fontSize = 12.sp,
                                                         color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                     )
                                                 }
                                                 
                                                 Spacer(modifier = Modifier.height(12.dp))
                                                 
                                                 Text("Koefisien Alpha (Smoothing Level): ${String.format("%.2f", simAlpha)}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                 androidx.compose.material3.Slider(
                                                     value = simAlpha,
                                                     onValueChange = { simAlpha = it },
                                                     valueRange = 0.05f..0.95f,
                                                     modifier = Modifier.fillMaxWidth().height(24.dp)
                                                 )
                                                 
                                                 Spacer(modifier = Modifier.height(6.dp))
                                                 
                                                 Text("Koefisien Beta (Smoothing Trend): ${String.format("%.2f", simBeta)}", fontSize = 9.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                 androidx.compose.material3.Slider(
                                                     value = simBeta,
                                                     onValueChange = { simBeta = it },
                                                     valueRange = 0.05f..0.95f,
                                                     modifier = Modifier.fillMaxWidth().height(24.dp)
                                                 )
                                                 
                                                 Spacer(modifier = Modifier.height(10.dp))
                                                 
                                                 Row(
                                                     modifier = Modifier
                                                         .fillMaxWidth()
                                                         .background(if (themeDark) Color(0xFF221A30) else Color(0xFFF5F3FF), RoundedCornerShape(10.dp))
                                                         .padding(10.dp),
                                                     verticalAlignment = Alignment.CenterVertically,
                                                     horizontalArrangement = Arrangement.SpaceBetween
                                                 ) {
                                                     Text("Proyeksi Belanja Pekan Depan:", fontSize = 9.5.sp, color = if (themeDark) Color.LightGray else Color.DarkGray)
                                                     Text(
                                                         text = rp.format(simForecast),
                                                         fontSize = 13.sp,
                                                         fontWeight = FontWeight.Black,
                                                         color = if (themeDark) Color.White else Color(0xFF6D28D9)
                                                     )
                                                 }
                                             }
                                         }

                                         val clusters = remember(expenses) {
                                             com.example.service.LocalFinanceMLEngine.performKMeansClustering(expenses)
                                         }
                                         var expandedClusterIdx by remember { mutableStateOf(-1) }

                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)),
                                             border = BorderStroke(1.dp, if (themeDark) Color(0xFF2E2045) else Color(0xFFECE6F5))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                                     Box(
                                                         modifier = Modifier
                                                             .size(24.dp)
                                                             .background(if (themeDark) Color(0xFF331F4D) else Color(0xFFE9D5FF), CircleShape),
                                                         contentAlignment = Alignment.Center
                                                     ) {
                                                         Text("3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9))
                                                     }
                                                     Spacer(modifier = Modifier.width(8.dp))
                                                     Text(
                                                         "Segmentasi Perilaku Belanja (K-Means)",
                                                         fontWeight = FontWeight.Bold,
                                                         fontSize = 12.sp,
                                                         color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                     )
                                                 }
                                                 
                                                 Spacer(modifier = Modifier.height(10.dp))
                                                 
                                                 clusters.forEachIndexed { index, cluster ->
                                                     val iconEmoji = when {
                                                         cluster.label.contains("Mikro") -> "🛒"
                                                         cluster.label.contains("Lifestyle") -> "🍿"
                                                         else -> "💳"
                                                     }
                                                     val isExpanded = expandedClusterIdx == index
                                                     
                                                     Column(
                                                         modifier = Modifier
                                                             .fillMaxWidth()
                                                             .padding(vertical = 4.dp)
                                                             .border(
                                                                 1.dp,
                                                                 if (isExpanded) {
                                                                     if (themeDark) Color(0xFF6D28D9) else Color(0xFFDCD3FD)
                                                                 } else {
                                                                     if (themeDark) Color(0xFF281C44) else Color(0xFFEDE4F8)
                                                                 },
                                                                 RoundedCornerShape(10.dp)
                                                             )
                                                             .clip(RoundedCornerShape(10.dp))
                                                             .clickable { expandedClusterIdx = if (isExpanded) -1 else index }
                                                             .background(
                                                                 if (isExpanded) {
                                                                     if (themeDark) Color(0xFF1B142D) else Color(0xFFF7F4FD)
                                                                 } else {
                                                                     if (themeDark) Color(0xFF130F1F) else Color(0xFFFAF8FD)
                                                                 }
                                                             )
                                                             .padding(10.dp)
                                                     ) {
                                                         Row(
                                                             modifier = Modifier.fillMaxWidth(),
                                                             verticalAlignment = Alignment.CenterVertically
                                                         ) {
                                                             Box(
                                                                 modifier = Modifier
                                                                     .size(26.dp)
                                                                     .background(if (themeDark) Color(0xFF23143E) else Color(0xFFF1E8FD), CircleShape),
                                                                 contentAlignment = Alignment.Center
                                                             ) {
                                                                 Text(iconEmoji, fontSize = 11.sp)
                                                             }
                                                             Spacer(modifier = Modifier.width(8.dp))
                                                             Column(modifier = Modifier.weight(1f)) {
                                                                 Text(
                                                                     text = cluster.label,
                                                                     fontSize = 10.5.sp,
                                                                     fontWeight = FontWeight.Bold,
                                                                     color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                                 )
                                                                 Text(
                                                                     text = "Rerata: ${rp.format(cluster.meanAmount)} • Total: ${rp.format(cluster.totalAmount)}",
                                                                     fontSize = 8.5.sp,
                                                                     color = if (themeDark) Color.LightGray.copy(alpha = 0.6f) else Color.Gray
                                                                 )
                                                             }
                                                             
                                                             Box(
                                                                 modifier = Modifier
                                                                     .clip(RoundedCornerShape(6.dp))
                                                                     .background(if (themeDark) Color(0xFFC084FC).copy(alpha = 0.15f) else Color(0xFF6D28D9).copy(alpha = 0.1f))
                                                                     .padding(horizontal = 6.dp, vertical = 2.dp)
                                                             ) {
                                                                 Text(
                                                                     text = "${cluster.transactions.size} tx",
                                                                     fontSize = 8.sp,
                                                                     fontWeight = FontWeight.Bold,
                                                                     color = if (themeDark) Color.White else Color(0xFF6D28D9)
                                                                 )
                                                             }
                                                         }
                                                         
                                                         if (isExpanded) {
                                                             Spacer(modifier = Modifier.height(8.dp))
                                                             if (cluster.transactions.isEmpty()) {
                                                                 Text("Tidak ada transaksi dalam klaster ini.", fontSize = 9.sp, color = Color.Gray)
                                                             } else {
                                                                 Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                     cluster.transactions.take(8).forEach { tx ->
                                                                         Row(
                                                                             modifier = Modifier
                                                                                 .fillMaxWidth()
                                                                                 .background(if (themeDark) Color(0xFF110B1C) else Color(0xFFFDFDFD), RoundedCornerShape(6.dp))
                                                                                 .padding(6.dp),
                                                                             horizontalArrangement = Arrangement.SpaceBetween,
                                                                             verticalAlignment = Alignment.CenterVertically
                                                                         ) {
                                                                             Column {
                                                                                 Text(tx.title, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = if (themeDark) Color.White else Color.Black)
                                                                                 Text(tx.category, fontSize = 7.5.sp, color = Color.Gray)
                                                                             }
                                                                             Text(rp.format(tx.amount), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color.White else Color(0xFF6D28D9))
                                                                         }
                                                                     }
                                                                     if (cluster.transactions.size > 8) {
                                                                         Text("...dan ${cluster.transactions.size - 8} transaksi lainnya", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                                                                     }
                                                                 }
                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                         }

                                         val iqrAnomalies = remember(expenses) {
                                             com.example.service.LocalFinanceMLEngine.detectOutliersIQR(expenses)
                                         }

                                         Card(
                                             modifier = Modifier.fillMaxWidth(),
                                             shape = RoundedCornerShape(16.dp),
                                             colors = CardDefaults.cardColors(containerColor = if (themeDark) Color(0xFF141120) else Color(0xFFFAFAFE)),
                                             border = BorderStroke(1.dp, if (themeDark) Color(0xFF2E2045) else Color(0xFFECE6F5))
                                         ) {
                                             Column(modifier = Modifier.padding(14.dp)) {
                                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                                     Box(
                                                         modifier = Modifier
                                                             .size(24.dp)
                                                             .background(if (themeDark) Color(0xFF331F4D) else Color(0xFFE9D5FF), CircleShape),
                                                         contentAlignment = Alignment.Center
                                                     ) {
                                                         Text("5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFD8B4FE) else Color(0xFF6D28D9))
                                                     }
                                                     Spacer(modifier = Modifier.width(8.dp))
                                                     Text(
                                                         "Deteksi Anomali Kuat (IQR)",
                                                         fontWeight = FontWeight.Bold,
                                                         fontSize = 12.sp,
                                                         color = if (themeDark) Color.White else Color(0xFF1E1B4B)
                                                     )
                                                 }
                                                 
                                                 Spacer(modifier = Modifier.height(10.dp))
                                                 
                                                 if (iqrAnomalies.isEmpty()) {
                                                     Row(
                                                         modifier = Modifier
                                                             .fillMaxWidth()
                                                             .background(if (themeDark) Color(0xFF092C1A) else Color(0xFFF0FDF4), RoundedCornerShape(10.dp))
                                                         .padding(10.dp),
                                                         verticalAlignment = Alignment.CenterVertically
                                                     ) {
                                                         Text("✨", fontSize = 12.sp)
                                                         Spacer(modifier = Modifier.width(8.dp))
                                                         Text(
                                                             "Hebat! Tidak terdeteksi adanya anomali pengeluaran ekstrem dalam riwayat belanja Anda.",
                                                             fontSize = 9.5.sp,
                                                             color = if (themeDark) Color(0xFF34D399) else Color(0xFF15803D),
                                                             lineHeight = 13.sp
                                                         )
                                                     }
                                                 } else {
                                                     Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                         Text(
                                                             "Daftar pengeluaran yang terdeteksi melompat tinggi di luar kebiasaan normal Anda (Batas Atas IQR):",
                                                             fontSize = 9.5.sp,
                                                             color = if (themeDark) Color.LightGray else Color.DarkGray,
                                                             lineHeight = 13.sp
                                                         )
                                                         
                                                         iqrAnomalies.forEach { tx ->
                                                             Row(
                                                                 modifier = Modifier
                                                                     .fillMaxWidth()
                                                                     .background(if (themeDark) Color(0xFF2A0812) else Color(0xFFFFF1F2), RoundedCornerShape(10.dp))
                                                                     .border(1.dp, if (themeDark) Color(0xFF881337) else Color(0xFFFECDD3), RoundedCornerShape(10.dp))
                                                                     .padding(10.dp),
                                                                 verticalAlignment = Alignment.CenterVertically,
                                                                 horizontalArrangement = Arrangement.SpaceBetween
                                                             ) {
                                                                 Column {
                                                                     Text(tx.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFFDA4AF) else Color(0xFF9F1239))
                                                                     val dateStr = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(tx.timestamp))
                                                                     Text("Tanggal: $dateStr • Sektor: ${tx.category}", fontSize = 8.sp, color = Color.Gray)
                                                                 }
                                                                 Text(
                                                                     text = rp.format(tx.amount),
                                                                     fontSize = 11.sp,
                                                                     fontWeight = FontWeight.ExtraBold,
                                                                     color = if (themeDark) Color(0xFFFDA4AF) else Color(0xFF9F1239)
                                                                 )
                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                         }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showMlReportDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("ml_report_dialog_close_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (themeDark) Color(0xFF6D28D9) else Color(0xFF5B21B6)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Tutup Laporan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
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
        var title by remember { mutableStateOf(tx.getCleanTitle()) }
        
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
                        text = tx.getCleanTitle(),
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TOP ROW: Service Icon + Title + Category Badge & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val emoji = when {
                            bill.title.contains("Spotify", ignoreCase = true) -> "🎵"
                            bill.title.contains("Netflix", ignoreCase = true) -> "🍿"
                            bill.title.contains("YouTube", ignoreCase = true) -> "📺"
                            bill.title.contains("WiFi", ignoreCase = true) || bill.title.contains("Indihome", ignoreCase = true) -> "🌐"
                            bill.title.contains("Listrik", ignoreCase = true) || bill.title.contains("Token", ignoreCase = true) -> "⚡"
                            bill.title.contains("Sewa", ignoreCase = true) || bill.title.contains("Kos", ignoreCase = true) -> "🏠"
                            else -> "💳"
                        }
                        if (emoji == "💳") {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Bill",
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = bill.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = amountColor,
                            maxLines = 1
                        )
                        Text(
                            text = bill.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Amount Column on the Right
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = rubelFormat.format(bill.amount).replace(",00", ""),
                        style = MaterialTheme.typography.titleMedium,
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
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(cardBorderColor.copy(alpha = 0.5f))
            )

            // BOTTOM ROW: Tempo Info & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tempo / Due Date with Calendar Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Due Date",
                        tint = subtextColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Tempo: ${bill.dueDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtextColor,
                        maxLines = 1
                    )
                }

                // Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Delete Button
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

                    // Pay Button (as a compact button)
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (themeDark) Color(0xFFE26090) else Color(0xFFC2185B),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "Bayar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
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

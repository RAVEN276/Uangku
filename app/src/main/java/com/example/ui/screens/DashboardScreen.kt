package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
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
import com.example.ui.components.LockerSpeedDialMenu
import com.example.ui.components.ReceiptScannerDialog
import androidx.compose.animation.animateContentSize
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
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import com.example.service.ReceiptOcrScanner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.Intent
import android.widget.Toast
import com.example.data.model.Transaction
import com.example.data.model.getCleanTitle
import com.example.ui.FinanceViewModel
import com.example.ui.FourMonthsSummaryItem
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
    val currentMonthIncome by viewModel.currentMonthIncome.collectAsStateWithLifecycle()
    val currentMonthExpense by viewModel.currentMonthExpense.collectAsStateWithLifecycle()
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
    var isBalanceHidden by remember { mutableStateOf(false) }
    var showSummaryType by remember { mutableStateOf<String?>(null) }

    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val rubelFormat = remember(selectedCurrency) {
        object {
            fun format(amt: Double): String = viewModel.formatCurrency(amt)
            fun format(amt: Long): String = viewModel.formatCurrency(amt.toDouble())
            fun format(amt: Number): String = viewModel.formatCurrency(amt.toDouble())
        }
    }

    var isSpeedDialExpanded by remember { mutableStateOf(false) }
    var initialDialogType by remember { mutableStateOf("EXPENSE") }
    var capturedReceiptBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            val uri = pendingCameraUri!!
            capturedReceiptUri = uri
            capturedReceiptBitmap = ReceiptOcrScanner.loadOptimizedBitmap(context, uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ReceiptOcrScanner.createTempImageUri(context)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk memindai nota", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchDirectCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = ReceiptOcrScanner.createTempImageUri(context)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            LockerSpeedDialMenu(
                isExpanded = isSpeedDialExpanded,
                onToggleExpand = { isSpeedDialExpanded = !isSpeedDialExpanded },
                onSelectExpense = {
                    initialDialogType = "EXPENSE"
                    showQuickAddDialog = true
                },
                onSelectIncome = {
                    initialDialogType = "INCOME"
                    showQuickAddDialog = true
                },
                onSelectScanReceipt = {
                    launchDirectCamera()
                },
                modifier = Modifier
                    .padding(bottom = 72.dp, end = 4.dp)
                    .testTag("dashboard_speed_dial_menu")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // BENTO: Welcome & Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .clickable { viewModel.triggerAutoSync() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // BENTO: Main Balance Card (Glass/Mesh style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (themeDark) listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                     else listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                        )
                    )
                    .border(
                        1.dp,
                        if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                        RoundedCornerShape(32.dp)
                    )
            ) {
                // Decorative mesh/blur shapes could go here
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Total Balance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (themeDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBalanceHidden) "Rp ••••••••" else rubelFormat.format(totalBalance),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (themeDark) Color.White else Color.Black,
                                    letterSpacing = (-1).sp
                                )
                            }
                            IconButton(
                                onClick = { isBalanceHidden = !isBalanceHidden },
                                modifier = Modifier
                                    .background(
                                        if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle",
                                    tint = if (themeDark) Color.White else Color.Black
                                )
                            }
                        }

                        // Bottom Actions inside card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showQuickAddDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeDark) Color.White else Color.Black,
                                    contentColor = if (themeDark) Color.Black else Color.White
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onNavigateToTransactions() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                    contentColor = if (themeDark) Color.White else Color.Black
                                ),
                                elevation = null
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("History", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // BENTO: 2 Column Grid (Income / Expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Bento Box (Bulanan & Ringkasan 4 Bulan)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(136.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))
                            )
                        )
                        .clickable { showSummaryType = "INCOME" }
                        .padding(18.dp)
                        .testTag("dashboard_income_bento_box")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF059669).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(22.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF059669).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Bulan Ini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF065F46).copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isBalanceHidden) "•••" else rubelFormat.format(currentMonthIncome),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF064E3B),
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Expense Bento Box (Bulanan & Ringkasan 4 Bulan)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(136.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFE4E6), Color(0xFFFECDD3))
                            )
                        )
                        .clickable { showSummaryType = "EXPENSE" }
                        .padding(18.dp)
                        .testTag("dashboard_expense_bento_box")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFE11D48).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(22.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE11D48).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Bulan Ini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF9F1239).copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isBalanceHidden) "•••" else rubelFormat.format(currentMonthExpense),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF881337),
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // BENTO: Analytics & AI Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (themeDark) listOf(Color(0xFF4C1D95), Color(0xFF312E81))
                                     else listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
                        )
                    )
                    .clickable { showMlReportDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (themeDark) Color(0xFFC4B5FD) else Color(0xFF7C3AED),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Insights",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (themeDark) Color(0xFFC4B5FD) else Color(0xFF7C3AED),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to view smart financial analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (themeDark) Color.White else Color(0xFF4C1D95)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (themeDark) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = if (themeDark) Color.White else Color(0xFF4C1D95)
                        )
                    }
                }
            }
            
            // BENTO: Recent Transactions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (recentTxs.isNotEmpty()) {
                    TextButton(
                        onClick = onNavigateToTransactions,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lihat Semua",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (recentTxs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No transactions yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recentTxs.forEach { tx ->
                        // BENTO: Compact Transaction Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onNavigateToTransactions() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (tx.type == "INCOME") Color(0xFF10B981).copy(alpha = 0.1f)
                                        else Color(0xFFEF4444).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = tx.category,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = (if (tx.type == "INCOME") "+" else "-") + rubelFormat.format(tx.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Black,
                                color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding().height(150.dp))
        }
    }

    // 4 Months Summary Dialog triggered by tapping Income or Expense Bento Cards
    if (showSummaryType != null) {
        FourMonthsSummaryDialog(
            type = showSummaryType!!,
            viewModel = viewModel,
            themeDark = themeDark,
            onDismiss = { showSummaryType = null }
        )
    }

    // Interactive Quick Add Expense/Income dialog screen
    if (showQuickAddDialog) {
        var txTitle by remember { mutableStateOf("") }
        var txAmount by remember { mutableStateOf("") }
        val txType = initialDialogType
        var txCategory by remember(initialDialogType) { mutableStateOf(if (initialDialogType == "INCOME") "Gaji" else "Makanan") }

        Dialog(onDismissRequest = { showQuickAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(32.dp),
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
                    // Header indicating transaction type directly without extra switches
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (txType == "INCOME") Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (txType == "INCOME") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (txType == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (txType == "INCOME") "Catat Pemasukan" else "Catat Pengeluaran",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (txType == "INCOME") "Pemasukan Dana Baru" else "Pengeluaran Biaya Baru",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (txType == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                fontWeight = FontWeight.SemiBold
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
                        placeholder = { Text(if (txType == "INCOME") "misal: Gaji Bulanan, Bonus Proyek" else "misal: Sate Ayam, Beli Bensin") },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
                    )

                    OutlinedTextField(
                        value = txAmount,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                txAmount = clean
                            }
                        },
                        label = { Text("Jumlah (${if (selectedCurrency == "USD") "USD $" else "Rupiah Rp"})") },
                        placeholder = { Text(if (selectedCurrency == "USD") "misal: 25" else "misal: 45.000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
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
                        val rowCats = if (txType == "INCOME") listOf("Gaji", "Bonus", "Investasi", "Penjualan", "Hadiah", "Lainnya") else listOf("Makanan", "Belanja", "Transportasi", "Tagihan", "Hiburan", "Kesehatan", "Sewa", "Lainnya")
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

    if (capturedReceiptBitmap != null || capturedReceiptUri != null) {
        ReceiptScannerDialog(
            viewModel = viewModel,
            bitmap = capturedReceiptBitmap,
            imageUri = capturedReceiptUri,
            onDismiss = {
                capturedReceiptBitmap = null
                capturedReceiptUri = null
            },
            onRelaunchCamera = {
                capturedReceiptBitmap = null
                capturedReceiptUri = null
                launchDirectCamera()
            },
            onConfirmAdd = { scannedTitle, scannedAmount, scannedType, scannedCategory ->
                viewModel.addTransaction(
                    title = scannedTitle,
                    amount = scannedAmount,
                    type = scannedType,
                    category = scannedCategory
                )
                capturedReceiptBitmap = null
                capturedReceiptUri = null
            }
        )
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
                shape = RoundedCornerShape(32.dp),
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
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
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
                            label = { Text("Nominal Tagihan (${if (selectedCurrency == "USD") "USD $" else "Rupiah Rp"})") },
                            placeholder = { Text(if (selectedCurrency == "USD") "misal: 10" else "misal: 125.000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
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
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
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
                shape = RoundedCornerShape(32.dp),
                color = if (themeDark) Color(0xFF110D1C) else Color.White,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (themeDark) Color(0xFF4C229E) else Color(0xFFE2E8F0)
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
                                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                                        shape = RoundedCornerShape(24.dp),
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
                                                val animatedSweepAngle by androidx.compose.animation.core.animateFloatAsState(
                                                    targetValue = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                                    label = "scoreSweepAnim"
                                                )
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
                                                        sweepAngle = animatedSweepAngle,
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
                                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                                        shape = RoundedCornerShape(24.dp),
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
                                if (false) {
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
    modifier: Modifier = Modifier,
    isUsd: Boolean = false,
    formatAmount: ((Double) -> String)? = null
) {
    val themeDark = androidx.compose.foundation.isSystemInDarkTheme()
    val currencyFormat = remember(isUsd) {
        if (isUsd) {
            NumberFormat.getCurrencyInstance(Locale.US).apply {
                maximumFractionDigits = 2
            }
        } else {
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }
        }
    }

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
    val cleanDate = dateFormat.format(Date(tx.timestamp))

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            modifier = Modifier.testTag("dashboard_delete_confirmation_dialog"),
            shape = RoundedCornerShape(32.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Hapus Transaksi?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus transaksi \"${tx.getCleanTitle()}\"? Data yang dihapus tidak dapat dikembalikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(tx)
                        showDeleteConfirmDialog = false
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

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
                shape = RoundedCornerShape(32.dp),
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                amount = clean
                            }
                        },
                        label = { Text("Jumlah (${if (isUsd) "USD $" else "Rupiah Rp"})") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = isUsd),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )
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
                                showDeleteConfirmDialog = true
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
                val isDarkTheme = MaterialTheme.colorScheme.surface.let { it.red * 0.299 + it.green * 0.587 + it.blue * 0.114 < 0.5f }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (tx.type == "INCOME") {
                                if (isDarkTheme) Color(0xFF0F2D1E) else Color(0xFFD1FAE5)
                            } else {
                                if (isDarkTheme) Color(0xFF3E121E) else Color(0xFFFFE4E6)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = tx.type,
                        tint = if (tx.type == "INCOME") {
                            if (isDarkTheme) Color(0xFF34D399) else Color(0xFF059669)
                        } else {
                            if (isDarkTheme) Color(0xFFFB7185) else Color(0xFFE11D48)
                        },
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
                val formattedAmount = formatAmount?.invoke(tx.amount) ?: currencyFormat.format(tx.amount).replace(",00", "")
                val valueStr = (if (tx.type == "INCOME") "+" else "-") + formattedAmount
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

@Composable
fun FourMonthsSummaryDialog(
    type: String,
    viewModel: FinanceViewModel,
    themeDark: Boolean,
    onDismiss: () -> Unit
) {
    val isIncome = type == "INCOME"
    val accentColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
    val accentBg = if (isIncome) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    val summaries = remember(transactions, type) {
        viewModel.getFourMonthsSummary(type)
    }

    val chronologicalSummaries = remember(summaries) { summaries.reversed() }

    var selectedMonthIndex by remember { mutableStateOf(0) }
    val currentSelectedMonth = summaries.getOrNull(selectedMonthIndex) ?: summaries.firstOrNull()

    val total4Months = remember(summaries) { summaries.sumOf { it.totalAmount } }
    val avg4Months = remember(total4Months) { total4Months / 4.0 }
    val maxMonth = remember(summaries) { summaries.maxByOrNull { it.totalAmount } }
    val maxAmountIn4Months = remember(summaries) { summaries.maxOfOrNull { it.totalAmount }?.takeIf { it > 0 } ?: 1.0 }

    val topCatOverall = remember(summaries) {
        summaries.flatMap { it.transactions }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
            .maxByOrNull { it.value }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = if (themeDark) Color(0xFF1E293B) else Color.White,
            contentColor = if (themeDark) Color.White else Color(0xFF0F172A),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("four_months_summary_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(accentBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isIncome) "Ringkasan Pemasukan" else "Ringkasan Pengeluaran",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (themeDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "4 Bulan Terakhir • Reset Tiap Awal Bulan",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_summary_dialog_button")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = if (themeDark) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 2x2 Bento Metric Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total 4 Bulan Bento Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    1.dp,
                                    if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Total 4 Bulan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                                Text(
                                    text = viewModel.formatCurrency(total4Months),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor,
                                    maxLines = 1
                                )
                            }
                        }

                        // Rata-rata Bulanan Bento Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    1.dp,
                                    if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Rata-rata / Bulan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                                Text(
                                    text = viewModel.formatCurrency(avg4Months),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (themeDark) Color.White else Color(0xFF0F172A),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bulan Tertinggi Bento Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    1.dp,
                                    if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Puncak Tertinggi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                                Text(
                                    text = maxMonth?.shortMonthName ?: "-",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (themeDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = viewModel.formatCurrency(maxMonth?.totalAmount ?: 0.0),
                                    fontSize = 11.sp,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    maxLines = 1
                                )
                            }
                        }

                        // Kategori Utama Bento Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    1.dp,
                                    if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isIncome) "Sumber Terbesar" else "Kategori Terbesar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                                Text(
                                    text = topCatOverall?.key ?: "Belum ada",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (themeDark) Color.White else Color(0xFF0F172A),
                                    maxLines = 1
                                )
                                Text(
                                    text = if (topCatOverall != null) viewModel.formatCurrency(topCatOverall.value) else "-",
                                    fontSize = 11.sp,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Interactive 4 Months Bar Comparison Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                            )
                            .border(
                                1.dp,
                                if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grafik Perbandingan 4 Bulan",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (themeDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Ketuk untuk pilih bulan",
                                    fontSize = 11.sp,
                                    color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }

                            // 4 Bars Container
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                chronologicalSummaries.forEach { monthItem ->
                                    val originalIndex = summaries.indexOf(monthItem)
                                    val isSelected = selectedMonthIndex == originalIndex
                                    val heightFraction = if (maxAmountIn4Months > 0) {
                                        (monthItem.totalAmount / maxAmountIn4Months).toFloat().coerceIn(0.08f, 1.0f)
                                    } else 0.08f

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedMonthIndex = originalIndex }
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = if (monthItem.totalAmount > 0) {
                                                if (monthItem.totalAmount >= 1_000_000) {
                                                    String.format(Locale.US, "%.1f jt", monthItem.totalAmount / 1_000_000)
                                                } else if (monthItem.totalAmount >= 1_000) {
                                                    String.format(Locale.US, "%.0f rb", monthItem.totalAmount / 1_000)
                                                } else {
                                                    monthItem.totalAmount.toLong().toString()
                                                }
                                            } else "0",
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) accentColor else if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.55f)
                                                .height((100 * heightFraction).dp)
                                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
                                                .background(
                                                    if (isSelected) {
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                accentColor,
                                                                accentColor.copy(alpha = 0.65f)
                                                            )
                                                        )
                                                    } else {
                                                        Brush.verticalGradient(
                                                            colors = if (themeDark) listOf(Color(0xFF334155), Color(0xFF1E293B))
                                                            else listOf(Color(0xFFCBD5E1), Color(0xFFE2E8F0))
                                                        )
                                                    }
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) accentBg else Color.Transparent
                                        ) {
                                            Text(
                                                text = monthItem.shortMonthName,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                                color = if (isSelected) accentColor else if (themeDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Selected Month Details & Transactions List
                    if (currentSelectedMonth != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = currentSelectedMonth.monthName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (themeDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${currentSelectedMonth.transactionCount} transaksi • Total: ${viewModel.formatCurrency(currentSelectedMonth.totalAmount)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                }

                                if (selectedMonthIndex == 0) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = accentBg
                                    ) {
                                        Text(
                                            text = "Bulan Ini",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (currentSelectedMonth.transactions.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                        )
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = if (themeDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Belum ada transaksi ${if (isIncome) "pemasukan" else "pengeluaran"} di bulan ini",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                val txDateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")) }
                                currentSelectedMonth.transactions.forEach { tx ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                                            )
                                            .border(
                                                1.dp,
                                                if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .background(accentBg, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                        contentDescription = null,
                                                        tint = accentColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = tx.getCleanTitle(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (themeDark) Color.White else Color(0xFF0F172A),
                                                        maxLines = 1
                                                    )
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(
                                                            text = tx.category,
                                                            fontSize = 11.sp,
                                                            color = accentColor,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        Text(
                                                            text = "•",
                                                            fontSize = 11.sp,
                                                            color = if (themeDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                                                        )
                                                        Text(
                                                            text = txDateFormat.format(Date(tx.timestamp)),
                                                            fontSize = 11.sp,
                                                            color = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = "${if (isIncome) "+" else "-"}${viewModel.formatCurrency(tx.amount)}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = accentColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Close Button at bottom
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

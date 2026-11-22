package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.MotionTokens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.ExpressiveMorphChip
import com.example.ui.components.ExpressiveSegmentOption
import com.example.ui.components.ExpressiveSegmentedSelector
import com.example.ui.components.RupiahVisualTransformation
import com.example.ui.components.ReceiptScannerDialog
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.tabular
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
    val uangkuColors = LocalUangkuColors.current
    val bodyTextColor = MaterialTheme.colorScheme.onSurface
    val headingColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
            } else if (trimmed.startsWith("### ")) {
                val titleText = trimmed.removePrefix("### ").trim()
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = uangkuColors.aiAccent,
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
                    color = uangkuColors.onAiContainer,
                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                )
            } else if (trimmed.startsWith("- ")) {
                val isRecommendation = trimmed.contains("🔴") || trimmed.contains("⚠️") || trimmed.contains("✅") || trimmed.contains("✨") || trimmed.contains("📈") || trimmed.contains("📉")

                if (isRecommendation) {
                    val recMarker = when {
                        trimmed.contains("🔴") -> "🔴"
                        trimmed.contains("⚠️") -> "⚠️"
                        trimmed.contains("✅") -> "✅"
                        trimmed.contains("✨") -> "✨"
                        trimmed.contains("📈") -> "📈"
                        trimmed.contains("📉") -> "📉"
                        else -> ""
                    }
                    var cleanTextLine = trimmed.removePrefix("- ").trim()
                    if (recMarker.isNotEmpty()) {
                        cleanTextLine = cleanTextLine.replace(recMarker, "").trim()
                    }

                    val (bgColor, borderColor, accentTextColor, onBgColor) = when (recMarker) {
                        "🔴" -> listOf(
                            uangkuColors.expenseContainer,
                            uangkuColors.expenseText.copy(alpha = 0.4f),
                            uangkuColors.expenseText,
                            uangkuColors.onExpenseContainer
                        )
                        "⚠️" -> listOf(
                            uangkuColors.warningContainer,
                            uangkuColors.warningText.copy(alpha = 0.4f),
                            uangkuColors.warningText,
                            uangkuColors.onWarningContainer
                        )
                        "✅", "✨" -> listOf(
                            uangkuColors.incomeContainer,
                            uangkuColors.incomeText.copy(alpha = 0.4f),
                            uangkuColors.incomeText,
                            uangkuColors.onIncomeContainer
                        )
                        "📈" -> listOf(
                            uangkuColors.aiContainer,
                            uangkuColors.aiBorder,
                            uangkuColors.aiAccent,
                            uangkuColors.onAiContainer
                        )
                        "📉" -> listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        else -> listOf(
                            uangkuColors.elevatedSubSurface,
                            uangkuColors.cardBorder,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onSurface
                        )
                    }

                    val annotatedCardText = parseMarkdownLine(
                        cleanTextLine,
                        defaultColor = onBgColor,
                        boldColor = onBgColor
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
                            val iconVec = when (recMarker) {
                                "🔴" -> Icons.Default.Close
                                "⚠️" -> Icons.Default.NotificationsActive
                                "✅", "✨" -> Icons.Default.Check
                                "📈" -> Icons.AutoMirrored.Filled.TrendingUp
                                "📉" -> Icons.AutoMirrored.Filled.TrendingDown
                                else -> Icons.Default.Check
                            }
                            Icon(
                                imageVector = iconVec,
                                contentDescription = null,
                                tint = accentTextColor,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Text(
                                text = annotatedCardText,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = onBgColor
                            )
                        }
                    }
                } else {
                    val contentLine = trimmed.removePrefix("- ").trim()
                    val bulletIcon = when {
                        contentLine.startsWith("**Total Belanja**") || contentLine.startsWith("Total Belanja") -> Icons.Default.ShoppingBag
                        contentLine.startsWith("**Rerata Transaksi**") || contentLine.startsWith("Rerata Transaksi") -> Icons.Default.Analytics
                        contentLine.startsWith("**Sektor Dominan**") || contentLine.startsWith("Sektor Dominan") -> Icons.Default.LocalOffer
                        contentLine.startsWith("**Rasio Laju Belanja Harian**") || contentLine.contains("Laju Belanja") -> Icons.Default.Bolt
                        contentLine.startsWith("**Proyeksi Sisa Bulan Ini**") || contentLine.contains("Proyeksi") -> Icons.Default.CalendarToday
                        contentLine.startsWith("**Estimasi Saldo Akhir Bulan**") || contentLine.contains("Estimasi Saldo") -> Icons.Default.AccountBalanceWallet
                        else -> Icons.Default.Info
                    }

                    val annotatedBulletText = parseMarkdownLine(
                        contentLine,
                        defaultColor = bodyTextColor,
                        boldColor = headingColor
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = bulletIcon,
                            contentDescription = null,
                            tint = uangkuColors.aiAccent,
                            modifier = Modifier.padding(top = 2.dp).size(16.dp)
                        )
                        Text(
                            text = annotatedBulletText,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = bodyTextColor
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
            } else {
                val annotatedText = parseMarkdownLine(
                    trimmed,
                    defaultColor = bodyTextColor,
                    boldColor = headingColor
                )
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = bodyTextColor,
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
    val uangkuColors = LocalUangkuColors.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompactWidth = maxWidth < 600.dp
        val isWideDashboard = maxWidth >= 840.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1040.dp)
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
                                text = com.example.ui.util.AppStrings.greeting(appLanguage),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
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
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    val heroCardContent: @Composable (Modifier) -> Unit = { cardModifier ->
                        Box(
                            modifier = cardModifier
                                .defaultMinSize(minHeight = 204.dp)
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.HeroCorner))
                                .background(uangkuColors.heroStart)
                                .border(
                                    1.dp,
                                    uangkuColors.cardBorder,
                                    RoundedCornerShape(ExpressiveShapeTokens.HeroCorner)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 204.dp)
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = com.example.ui.util.AppStrings.totalBalance(appLanguage),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = uangkuColors.onHeroVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (isBalanceHidden) "Rp ••••••••" else rubelFormat.format(totalBalance),
                                            style = MaterialTheme.typography.displaySmall.tabular(),
                                            fontWeight = FontWeight.Black,
                                            color = uangkuColors.onHero,
                                            letterSpacing = (-1).sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { isBalanceHidden = !isBalanceHidden },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle saldo",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Bottom Actions inside Hero card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            initialDialogType = "EXPENSE"
                                            showQuickAddDialog = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .testTag("dashboard_quick_add_button"),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = com.example.ui.util.AppStrings.quickAdd(appLanguage),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { onNavigateToTransactions() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = com.example.ui.util.AppStrings.history(appLanguage),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val incomeShape = RoundedCornerShape(
                        topStart = ExpressiveShapeTokens.BentoCorner,
                        bottomStart = ExpressiveShapeTokens.BentoCorner,
                        topEnd = ExpressiveShapeTokens.BentoInnerCorner,
                        bottomEnd = ExpressiveShapeTokens.BentoInnerCorner
                    )
                    val expenseShape = RoundedCornerShape(
                        topStart = ExpressiveShapeTokens.BentoInnerCorner,
                        bottomStart = ExpressiveShapeTokens.BentoInnerCorner,
                        topEnd = ExpressiveShapeTokens.BentoCorner,
                        bottomEnd = ExpressiveShapeTokens.BentoCorner
                    )
                    val analysisShape = incomeShape
                    val scanShape = expenseShape

                    val cashflowBentoRow: @Composable () -> Unit = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Income Bento Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 138.dp)
                                    .clip(incomeShape)
                                    .background(uangkuColors.incomeContainer)
                                    .border(1.dp, uangkuColors.incomeBorder, incomeShape)
                                    .clickable { showSummaryType = "INCOME" }
                                    .padding(18.dp)
                                    .testTag("dashboard_income_bento_box")
                            ) {
                                Column(
                                    modifier = Modifier.defaultMinSize(minHeight = 102.dp).fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(uangkuColors.cardSurface, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = uangkuColors.incomeText, modifier = Modifier.size(20.dp))
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = uangkuColors.cardSurface
                                        ) {
                                            Text(
                                                text = com.example.ui.util.AppStrings.thisMonth(appLanguage),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uangkuColors.onIncomeContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column {
                                        Text(
                                            text = com.example.ui.util.AppStrings.income(appLanguage),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = uangkuColors.onIncomeContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (isBalanceHidden) "•••" else rubelFormat.format(currentMonthIncome),
                                            style = MaterialTheme.typography.titleLarge.tabular(),
                                            fontWeight = FontWeight.Black,
                                            color = uangkuColors.onIncomeContainer,
                                            fontSize = 18.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Expense Bento Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 138.dp)
                                    .clip(expenseShape)
                                    .background(uangkuColors.expenseContainer)
                                    .border(1.dp, uangkuColors.expenseBorder, expenseShape)
                                    .clickable { showSummaryType = "EXPENSE" }
                                    .padding(18.dp)
                                    .testTag("dashboard_expense_bento_box")
                            ) {
                                Column(
                                    modifier = Modifier.defaultMinSize(minHeight = 102.dp).fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(uangkuColors.cardSurface, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = uangkuColors.expenseText, modifier = Modifier.size(20.dp))
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = uangkuColors.cardSurface
                                        ) {
                                            Text(
                                                text = com.example.ui.util.AppStrings.thisMonth(appLanguage),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = uangkuColors.onExpenseContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column {
                                        Text(
                                            text = com.example.ui.util.AppStrings.expense(appLanguage),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = uangkuColors.onExpenseContainer,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (isBalanceHidden) "•••" else rubelFormat.format(currentMonthExpense),
                                            style = MaterialTheme.typography.titleLarge.tabular(),
                                            fontWeight = FontWeight.Black,
                                            color = uangkuColors.onExpenseContainer,
                                            fontSize = 18.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val featureBentoRow: @Composable () -> Unit = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Wide Analysis Bento Card (1.42f)
                            Box(
                                modifier = Modifier
                                    .weight(1.42f)
                                    .fillMaxHeight()
                                    .defaultMinSize(minHeight = 114.dp)
                                    .clip(analysisShape)
                                    .background(uangkuColors.aiSurface)
                                    .border(1.dp, uangkuColors.aiBorder, analysisShape)
                                    .clickable { showMlReportDialog = true }
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Analytics,
                                                contentDescription = null,
                                                tint = uangkuColors.aiAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = com.example.ui.util.AppStrings.aiInsights(appLanguage),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = uangkuColors.aiAccent,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = com.example.ui.util.AppStrings.aiInsightsDesc(appLanguage),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = uangkuColors.onAiContainer
                                        )
                                    }
                                }
                            }

                            // Compact Scan Receipt Bento Action (0.58f)
                            Box(
                                modifier = Modifier
                                    .weight(0.58f)
                                    .fillMaxHeight()
                                    .defaultMinSize(minHeight = 114.dp)
                                    .clip(scanShape)
                                    .background(uangkuColors.cardSurface)
                                    .border(1.dp, uangkuColors.cardBorder, scanShape)
                                    .clickable { launchDirectCamera() }
                                    .padding(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = com.example.ui.util.AppStrings.scanReceipt(appLanguage),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = com.example.ui.util.AppStrings.scanReceipt(appLanguage),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }

                    if (isWideDashboard) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            heroCardContent(Modifier.weight(1.12f).fillMaxHeight())
                            Column(
                                modifier = Modifier.weight(0.88f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                cashflowBentoRow()
                                featureBentoRow()
                            }
                        }
                    } else {
                        heroCardContent(Modifier.fillMaxWidth())
                        cashflowBentoRow()
                        featureBentoRow()
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
                            text = com.example.ui.util.AppStrings.recentActivity(appLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (recentTxs.isNotEmpty()) {
                            TextButton(
                                onClick = onNavigateToTransactions,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = com.example.ui.util.AppStrings.viewAll(appLanguage),
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
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .padding(vertical = 32.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                com.example.ui.util.AppStrings.noTransactions(appLanguage),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            recentTxs.forEach { tx ->
                                // BENTO: Compact Transaction Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(ExpressiveShapeTokens.RowCardCorner))
                                        .background(uangkuColors.cardSurface)
                                        .clickable { onNavigateToTransactions() }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                if (tx.type == "INCOME") uangkuColors.incomeContainer
                                                else uangkuColors.expenseContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tx.getCleanTitle(),
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
                                        style = MaterialTheme.typography.bodyLarge.tabular(),
                                        fontWeight = FontWeight.Black,
                                        color = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.navigationBarsPadding().height(if (isCompactWidth) 108.dp else 32.dp))
                }
            }
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
        var txType by remember(initialDialogType) { mutableStateOf(initialDialogType) }
        var txCategory by remember(initialDialogType) { mutableStateOf(if (initialDialogType == "INCOME") "Gaji" else "Makanan") }

        Dialog(onDismissRequest = { showQuickAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, uangkuColors.cardBorder),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_add_dialog_surface")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header indicating transaction type
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (txType == "INCOME") uangkuColors.incomeContainer else uangkuColors.expenseContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (txType == "INCOME") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (txType == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (txType == "INCOME") com.example.ui.util.AppStrings.recordIncome(appLanguage) else com.example.ui.util.AppStrings.recordExpense(appLanguage),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (txType == "INCOME") com.example.ui.util.AppStrings.newIncomeDesc(appLanguage) else com.example.ui.util.AppStrings.newExpenseDesc(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (txType == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    ExpressiveSegmentedSelector(
                        options = listOf(
                            ExpressiveSegmentOption(key = "EXPENSE", label = com.example.ui.util.AppStrings.expense(appLanguage)),
                            ExpressiveSegmentOption(key = "INCOME", label = com.example.ui.util.AppStrings.income(appLanguage))
                        ),
                        selectedKey = txType,
                        onSelect = { newType ->
                            txType = newType
                            txCategory = if (newType == "INCOME") "Gaji" else "Makanan"
                        }
                    )

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
                        label = { Text(com.example.ui.util.AppStrings.description(appLanguage)) },
                        placeholder = { Text(if (txType == "INCOME") com.example.ui.util.AppStrings.descPlaceholderIncome(appLanguage) else com.example.ui.util.AppStrings.descPlaceholderExpense(appLanguage)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
                        label = { Text("${com.example.ui.util.AppStrings.amount(appLanguage)} (${if (selectedCurrency == "USD") "USD $" else "Rupiah Rp"})") },
                        placeholder = { Text(com.example.ui.util.AppStrings.amountPlaceholder(appLanguage, selectedCurrency == "USD")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Category dropdown text label options
                    Text(
                        text = com.example.ui.util.AppStrings.selectCategory(appLanguage),
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
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { txCategory = cat }
                            ) {
                                Text(
                                    text = com.example.ui.util.AppStrings.translateCategory(cat, appLanguage),
                                    fontSize = 12.sp,
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
                            Text(com.example.ui.util.AppStrings.cancel(appLanguage))
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
                            Text(com.example.ui.util.AppStrings.save(appLanguage))
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
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = com.example.ui.util.AppStrings.newRecurringBillTitle(appLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = com.example.ui.util.AppStrings.recurringBillSubtitle(appLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Popular Subscriptions Shortcut Row
                    Text(
                        text = com.example.ui.util.AppStrings.popularServices(appLanguage),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularTemplates.forEach { (name, price, cat) ->
                            val templateIcon = when (name) {
                                "Spotify" -> Icons.Default.MusicNote
                                "Netflix" -> Icons.Default.Movie
                                "YouTube" -> Icons.Default.PlayCircle
                                "ISP WiFi" -> Icons.Default.Wifi
                                "Token Listrik" -> Icons.Default.Bolt
                                else -> Icons.Default.Home
                            }
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        uangkuColors.cardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        billTitle = name
                                        billAmountInput = price.toString()
                                        billCategory = cat
                                        billCycle = "Bulanan"
                                        billDueDate = "Setiap tanggal 5"
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = templateIcon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Fields
                    OutlinedTextField(
                        value = billTitle,
                        onValueChange = { billTitle = it },
                        label = { Text(com.example.ui.util.AppStrings.billNameLabel(appLanguage)) },
                        placeholder = { Text(com.example.ui.util.AppStrings.billNamePlaceholder(appLanguage)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
                            label = { Text(com.example.ui.util.AppStrings.billAmountLabel(appLanguage, selectedCurrency)) },
                            placeholder = { Text(com.example.ui.util.AppStrings.billAmountPlaceholder(appLanguage, selectedCurrency == "USD")) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
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
                                        .heightIn(min = 48.dp)
                                        .background(
                                            color = uangkuColors.elevatedSubSurface,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            val current = billAmountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
                                            billAmountInput = (current + amt).toString()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+Rp$priceFormatted",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = billDueDate,
                        onValueChange = { billDueDate = it },
                        label = { Text(com.example.ui.util.AppStrings.billDueDateLabel(appLanguage)) },
                        placeholder = { Text(com.example.ui.util.AppStrings.billDueDatePlaceholder(appLanguage)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(com.example.ui.util.AppStrings.billCycleLabel(appLanguage), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cycleOptions.forEach { cy ->
                            val isSelected = billCycle == cy
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { billCycle = cy }
                            ) {
                                Text(
                                    text = com.example.ui.util.AppStrings.translateCycle(cy, appLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Text(com.example.ui.util.AppStrings.billCategoryLabel(appLanguage), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        catsOptions.forEach { cat ->
                            val isSelected = billCategory == cat
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { billCategory = cat }
                            ) {
                                Text(
                                    text = com.example.ui.util.AppStrings.translateCategory(cat, appLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddBillDialog = false }) {
                            Text(com.example.ui.util.AppStrings.cancel(appLanguage))
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
                                        dueDate = billDueDate.ifEmpty { com.example.ui.util.AppStrings.defaultDueEndMonth(appLanguage) }
                                    )
                                    showAddBillDialog = false
                                }
                            },
                            enabled = billTitle.isNotBlank() && billAmountInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text(com.example.ui.util.AppStrings.save(appLanguage))
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
                color = uangkuColors.cardSurface,
                border = BorderStroke(1.dp, uangkuColors.aiBorder),
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
                                    .background(uangkuColors.aiContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "On-Device ML Engine",
                                    tint = uangkuColors.onAiContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = com.example.ui.util.AppStrings.assistantTitle(appLanguage),
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = com.example.ui.util.AppStrings.assistantSubtitle(appLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = uangkuColors.aiAccent,
                                    fontSize = 11.sp,
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
                                    contentDescription = com.example.ui.util.AppStrings.recalculateTooltip(appLanguage),
                                    tint = uangkuColors.aiAccent
                                )
                            }
                            IconButton(onClick = { showMlReportDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = com.example.ui.util.AppStrings.close(appLanguage),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    color = uangkuColors.aiAccent,
                                    trackColor = uangkuColors.elevatedSubSurface
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = com.example.ui.util.AppStrings.recalculatingInsights(appLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val isColdState = mlInsights == null || mlInsights!!.totalExpense == 0.0 || (summary != null && summary!!.startsWith("Belum ada data pengeluaran"))

                            if (isColdState) {
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
                                        colors = CardDefaults.cardColors(containerColor = uangkuColors.aiSurface),
                                        border = BorderStroke(1.5.dp, uangkuColors.aiBorder)
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
                                                    .background(uangkuColors.aiContainer, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Analytics,
                                                    contentDescription = null,
                                                    tint = uangkuColors.onAiContainer,
                                                    modifier = Modifier.size(34.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = com.example.ui.util.AppStrings.mlColdTitle(appLanguage),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = com.example.ui.util.AppStrings.mlColdDesc(appLanguage),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 18.sp
                                            )

                                            Spacer(modifier = Modifier.height(18.dp))

                                            Button(
                                                onClick = {
                                                    showMlReportDialog = false
                                                    showQuickAddDialog = true
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(com.example.ui.util.AppStrings.recordFirstExpense(appLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            } else {
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
                                    // 1. Health Score Meter
                                    val scoreColor = when {
                                        insights.financialHealthScore >= 85 -> uangkuColors.aiAccent
                                        insights.financialHealthScore >= 70 -> uangkuColors.incomeText
                                        insights.financialHealthScore >= 50 -> uangkuColors.warningText
                                        else -> uangkuColors.expenseText
                                    }

                                    val scoreLabel = when {
                                        insights.financialHealthScore >= 85 -> com.example.ui.util.AppStrings.fhsRatingOptimal(appLanguage)
                                        insights.financialHealthScore >= 70 -> com.example.ui.util.AppStrings.fhsRatingGood(appLanguage)
                                        insights.financialHealthScore >= 50 -> com.example.ui.util.AppStrings.fhsRatingCaution(appLanguage)
                                        else -> com.example.ui.util.AppStrings.fhsRatingCritical(appLanguage)
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = uangkuColors.aiSurface),
                                        border = BorderStroke(1.dp, uangkuColors.aiBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = com.example.ui.util.AppStrings.fhsTitle(appLanguage).uppercase(),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))

                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.size(110.dp)
                                            ) {
                                                val animatedSweepAngle by androidx.compose.animation.core.animateFloatAsState(
                                                    targetValue = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                    animationSpec = MotionTokens.dataProgressSpec,
                                                    label = "scoreSweepAnim"
                                                )
                                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                    drawArc(
                                                        color = scoreColor.copy(alpha = 0.18f),
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
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "${insights.financialHealthScore}",
                                                        fontSize = 30.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = com.example.ui.util.AppStrings.fhsScoreUnit(appLanguage),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(uangkuColors.cardSurface)
                                                    .border(1.dp, scoreColor.copy(alpha = 0.5f), RoundedCornerShape(50))
                                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    text = scoreLabel,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = scoreColor
                                                )
                                            }
                                        }
                                    }

                                    // 2. Spending Forecast Card
                                    val averageWeeklySpent = insights.totalExpense / 4.0
                                    val isSaving = insights.forecastedNextWeek < averageWeeklySpent
                                    val trendColor = if (isSaving) uangkuColors.incomeText else uangkuColors.expenseText

                                    Card(
                                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = uangkuColors.aiSurface),
                                        border = BorderStroke(1.dp, uangkuColors.aiBorder)
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
                                                        text = com.example.ui.util.AppStrings.forecastTitle(appLanguage).uppercase(),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(uangkuColors.aiContainer)
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = "Holt's Model",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = uangkuColors.onAiContainer
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = rp.format(insights.forecastedNextWeek),
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = if (isSaving) com.example.ui.util.AppStrings.forecastSavingDesc(appLanguage)
                                                       else com.example.ui.util.AppStrings.forecastOverspendDesc(appLanguage),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    // 3. Stats Metric Grid
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = com.example.ui.util.AppStrings.metricsSectionTitle(appLanguage).uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            letterSpacing = 0.5.sp
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(98.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = uangkuColors.elevatedSubSurface),
                                                    border = BorderStroke(1.dp, uangkuColors.cardBorder)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text(com.example.ui.util.AppStrings.dailyBurnRateTitle(appLanguage).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                            Icon(Icons.Default.TrendingDown, null, tint = uangkuColors.aiAccent, modifier = Modifier.size(14.dp))
                                                        }
                                                        Text(rp.format(insights.dailyBurnRate), fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                        Text(com.example.ui.util.AppStrings.dailyBurnRateDesc(appLanguage), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(98.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = uangkuColors.elevatedSubSurface),
                                                    border = BorderStroke(1.dp, uangkuColors.cardBorder)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text(com.example.ui.util.AppStrings.monthlyProjectionTitle(appLanguage).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                            Icon(Icons.Default.CalendarToday, null, tint = uangkuColors.aiAccent, modifier = Modifier.size(14.dp))
                                                        }
                                                        Text(rp.format(insights.projectedSpendRemainingMonth), fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                        Text(com.example.ui.util.AppStrings.monthlyProjectionDesc(appLanguage), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
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
                                                    modifier = Modifier.fillMaxWidth().height(98.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = uangkuColors.elevatedSubSurface),
                                                    border = BorderStroke(1.dp, uangkuColors.cardBorder)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text(com.example.ui.util.AppStrings.estimatedBalanceTitle(appLanguage).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                            Icon(Icons.Default.AccountBalanceWallet, null, tint = if (isPositive) uangkuColors.incomeText else uangkuColors.expenseText, modifier = Modifier.size(14.dp))
                                                        }
                                                        Text(rp.format(remainingBalance), fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                        Text(if (isPositive) com.example.ui.util.AppStrings.projectedSurplusDesc(appLanguage) else com.example.ui.util.AppStrings.projectedDeficitDesc(appLanguage), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                    }
                                                }
                                            }

                                            Box(modifier = Modifier.weight(1f)) {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().height(98.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = uangkuColors.elevatedSubSurface),
                                                    border = BorderStroke(1.dp, uangkuColors.cardBorder)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Text(com.example.ui.util.AppStrings.topSectorTitle(appLanguage).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                                            Icon(Icons.Default.CreditCard, null, tint = uangkuColors.aiAccent, modifier = Modifier.size(14.dp))
                                                        }
                                                        Text(com.example.ui.util.AppStrings.translateCategory(insights.topCategory, appLanguage), fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                        Text(com.example.ui.util.AppStrings.topSectorPortionDesc(appLanguage, String.format("%.1f", insights.topCategoryPercentage)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
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
                                            colors = CardDefaults.cardColors(containerColor = uangkuColors.aiSurface),
                                            border = BorderStroke(1.dp, uangkuColors.aiBorder)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.ListAlt,
                                                        contentDescription = null,
                                                        tint = uangkuColors.aiAccent,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "SEGMENTASI PERILAKU (K-Means)",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                                        val clusterIcon = when {
                                                            cleanLabel.contains("Mikro") -> Icons.Default.ShoppingCart
                                                            cleanLabel.contains("Lifestyle") -> Icons.Default.Movie
                                                            else -> Icons.Default.CreditCard
                                                        }

                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                                                .background(uangkuColors.elevatedSubSurface, RoundedCornerShape(12.dp))
                                                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(12.dp))
                                                                .padding(10.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(34.dp)
                                                                    .background(uangkuColors.aiContainer, CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = clusterIcon,
                                                                    contentDescription = null,
                                                                    tint = uangkuColors.onAiContainer,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = cleanLabel,
                                                                    fontSize = 12.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Text(
                                                                    text = detailsPart,
                                                                    fontSize = 11.sp,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    lineHeight = 14.sp
                                                                )
                                                            }
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(6.dp))
                                                                    .background(uangkuColors.aiContainer)
                                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                                            ) {
                                                                Text(
                                                                    text = countPart,
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = uangkuColors.onAiContainer
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Text(
                                                            text = summaryStr,
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            letterSpacing = 0.5.sp
                                        )

                                        insights.recommendations.forEach { recommendation ->
                                            val recMarker = when {
                                                recommendation.contains("🔴") -> "🔴"
                                                recommendation.contains("⚠️") -> "⚠️"
                                                recommendation.contains("✅") -> "✅"
                                                recommendation.contains("✨") -> "✨"
                                                recommendation.contains("📈") -> "📈"
                                                recommendation.contains("📉") -> "📉"
                                                else -> "💡"
                                            }

                                            var cleanRec = recommendation.trim()
                                            if (recMarker != "💡") {
                                                cleanRec = cleanRec.substringAfter(recMarker).trim()
                                            }

                                            val (bgColor, borderColor, accentTextCol, onBgColor) = when (recMarker) {
                                                "🔴" -> listOf(
                                                    uangkuColors.expenseContainer,
                                                    uangkuColors.expenseText.copy(alpha = 0.4f),
                                                    uangkuColors.expenseText,
                                                    uangkuColors.onExpenseContainer
                                                )
                                                "⚠️" -> listOf(
                                                    uangkuColors.warningContainer,
                                                    uangkuColors.warningText.copy(alpha = 0.4f),
                                                    uangkuColors.warningText,
                                                    uangkuColors.onWarningContainer
                                                )
                                                "✅", "✨" -> listOf(
                                                    uangkuColors.incomeContainer,
                                                    uangkuColors.incomeText.copy(alpha = 0.4f),
                                                    uangkuColors.incomeText,
                                                    uangkuColors.onIncomeContainer
                                                )
                                                "📈" -> listOf(
                                                    uangkuColors.aiContainer,
                                                    uangkuColors.aiBorder,
                                                    uangkuColors.aiAccent,
                                                    uangkuColors.onAiContainer
                                                )
                                                "📉" -> listOf(
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                else -> listOf(
                                                    uangkuColors.aiSurface,
                                                    uangkuColors.aiBorder,
                                                    uangkuColors.aiAccent,
                                                    uangkuColors.onAiContainer
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
                                                    val recIcon = when (recMarker) {
                                                        "🔴" -> Icons.Default.Info
                                                        "⚠️" -> Icons.Default.NotificationsActive
                                                        "✅", "✨" -> Icons.Default.Check
                                                        "📈" -> Icons.AutoMirrored.Filled.TrendingUp
                                                        "📉" -> Icons.AutoMirrored.Filled.TrendingDown
                                                        else -> Icons.Default.Lightbulb
                                                    }
                                                    Icon(
                                                        imageVector = recIcon,
                                                        contentDescription = null,
                                                        tint = accentTextCol,
                                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                                    )
                                                    Text(
                                                        text = parseMarkdownLine(
                                                            cleanRec,
                                                            defaultColor = onBgColor,
                                                            boldColor = onBgColor
                                                        ),
                                                        fontSize = 12.sp,
                                                        lineHeight = 16.sp,
                                                        color = onBgColor
                                                    )
                                                }
                                            }
                                        }
                                    }
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
                                                         Icon(
                                                             imageVector = Icons.Default.Check,
                                                             contentDescription = null,
                                                             tint = if (themeDark) Color(0xFF34D399) else Color(0xFF15803D),
                                                             modifier = Modifier.size(14.dp)
                                                         )
                                                         Spacer(modifier = Modifier.width(8.dp))
                                                         Text(
                                                             "Tidak terdeteksi anomali pengeluaran ekstrem dalam riwayat belanja Anda.",
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
                            .height(48.dp)
                            .testTag("ml_report_dialog_close_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Tutup Laporan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
    val uangkuColors = LocalUangkuColors.current
    val themeDark = uangkuColors.isDark
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
                        .background(uangkuColors.expenseContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = uangkuColors.expenseText,
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
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
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

                    ExpressiveSegmentedSelector(
                        options = listOf(
                            ExpressiveSegmentOption("EXPENSE", "Pengeluaran"),
                            ExpressiveSegmentOption("INCOME", "Pemasukan")
                        ),
                        selectedKey = type,
                        onSelect = { type = it },
                        activeColor = if (type == "EXPENSE") uangkuColors.expenseContainer else uangkuColors.incomeContainer,
                        onActiveColor = if (type == "EXPENSE") uangkuColors.onExpenseContainer else uangkuColors.onIncomeContainer
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text("Kategori:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val catRows = listOf("Makanan", "Belanja", "Transportasi", "Gaji", "Investasi", "Sewa", "Lainnya")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        catRows.forEach { cat ->
                            val isSelected = category == cat
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
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
        animationSpec = MotionTokens.effectsFloat,
        label = "alpha"
    )
    val offsetYAnim by animateDpAsState(
        targetValue = animatedOffsetY,
        animationSpec = MotionTokens.spatialSpringDp,
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
        colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, uangkuColors.cardBorder)
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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (tx.type == "INCOME") uangkuColors.incomeContainer else uangkuColors.expenseContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = tx.type,
                        tint = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText,
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
                    style = MaterialTheme.typography.bodyLarge.tabular(),
                    fontWeight = FontWeight.Black,
                    color = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText
                )

                if (tx.bankSource != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tx.bankSource,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
    val uangkuColors = LocalUangkuColors.current
    val cardBg = uangkuColors.cardSurface
    val cardBorderColor = uangkuColors.cardBorder

    val iconBg = MaterialTheme.colorScheme.secondaryContainer
    val iconTint = MaterialTheme.colorScheme.onSecondaryContainer

    val categoryColor = MaterialTheme.colorScheme.primary
    val amountColor = MaterialTheme.colorScheme.onSurface
    val subtextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recurring_bill_item_${bill.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, cardBorderColor)
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val billIcon = when {
                            bill.title.contains("Spotify", ignoreCase = true) -> Icons.Default.MusicNote
                            bill.title.contains("Netflix", ignoreCase = true) -> Icons.Default.Movie
                            bill.title.contains("YouTube", ignoreCase = true) -> Icons.Default.PlayCircle
                            bill.title.contains("WiFi", ignoreCase = true) || bill.title.contains("Indihome", ignoreCase = true) -> Icons.Default.Wifi
                            bill.title.contains("Listrik", ignoreCase = true) || bill.title.contains("Token", ignoreCase = true) -> Icons.Default.Bolt
                            bill.title.contains("Sewa", ignoreCase = true) || bill.title.contains("Kos", ignoreCase = true) -> Icons.Default.Home
                            else -> Icons.Default.CreditCard
                        }
                        Icon(
                            imageVector = billIcon,
                            contentDescription = bill.title,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
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
                    .background(cardBorderColor)
            )

            // BOTTOM ROW: Tempo Info & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Bayar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
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
    val uangkuColors = LocalUangkuColors.current
    val isIncome = type == "INCOME"
    val accentColor = if (isIncome) uangkuColors.incomeText else uangkuColors.expenseText
    val accentBg = if (isIncome) uangkuColors.incomeContainer else uangkuColors.expenseContainer
    val onAccentBg = if (isIncome) uangkuColors.onIncomeContainer else uangkuColors.onExpenseContainer
    val buttonContainerColor = MaterialTheme.colorScheme.primary
    val buttonContentColor = MaterialTheme.colorScheme.onPrimary
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
            color = uangkuColors.cardSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(1.dp, uangkuColors.cardBorder),
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
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
                                tint = onAccentBg,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isIncome) "Ringkasan Pemasukan" else "Ringkasan Pengeluaran",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "4 Bulan Terakhir • Reset Tiap Awal Bulan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_summary_dialog_button")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(uangkuColors.elevatedSubSurface)
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Total 4 Bulan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = viewModel.formatCurrency(total4Months),
                                    style = MaterialTheme.typography.titleMedium.tabular(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor,
                                    maxLines = 1
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(uangkuColors.elevatedSubSurface)
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Rata-rata / Bulan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = viewModel.formatCurrency(avg4Months),
                                    style = MaterialTheme.typography.titleMedium.tabular(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(uangkuColors.elevatedSubSurface)
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Puncak Tertinggi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = maxMonth?.shortMonthName ?: "-",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = viewModel.formatCurrency(maxMonth?.totalAmount ?: 0.0),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(uangkuColors.elevatedSubSurface)
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isIncome) "Sumber Terbesar" else "Kategori Terbesar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = topCatOverall?.key ?: "Belum ada",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (topCatOverall != null) viewModel.formatCurrency(topCatOverall.value) else "-",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .background(uangkuColors.elevatedSubSurface)
                            .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(28.dp))
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ketuk untuk pilih bulan",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

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
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.55f)
                                                .height((100 * heightFraction).dp)
                                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
                                                .background(
                                                    if (isSelected) accentColor else uangkuColors.cardBorder
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
                                                color = if (isSelected) onAccentBg else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${currentSelectedMonth.transactionCount} transaksi • Total: ${viewModel.formatCurrency(currentSelectedMonth.totalAmount)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                            color = onAccentBg,
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
                                        .background(uangkuColors.elevatedSubSurface)
                                        .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(24.dp))
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
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Belum ada transaksi ${if (isIncome) "pemasukan" else "pengeluaran"} di bulan ini",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                            .background(uangkuColors.elevatedSubSurface)
                                            .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(20.dp))
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
                                                        tint = onAccentBg,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = tx.getCleanTitle(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface,
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
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Text(
                                                            text = txDateFormat.format(Date(tx.timestamp)),
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = "${if (isIncome) "+" else "-"}${viewModel.formatCurrency(tx.amount)}",
                                                style = MaterialTheme.typography.bodyMedium.tabular(),
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
                        containerColor = buttonContainerColor,
                        contentColor = buttonContentColor
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

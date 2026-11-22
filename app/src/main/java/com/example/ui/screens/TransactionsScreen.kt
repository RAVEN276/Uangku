package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import com.example.ui.theme.MotionTokens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import com.example.service.ReceiptOcrScanner
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Transaction
import com.example.ui.FinanceViewModel
import com.example.ui.components.ExpressiveMorphChip
import com.example.ui.components.ExpressiveSegmentOption
import com.example.ui.components.ExpressiveSegmentedSelector
import com.example.ui.components.ReceiptScannerDialog
import com.example.ui.components.RupiahVisualTransformation
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.tabular
import com.example.ui.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val txs by viewModel.allTransactions.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var capturedReceiptBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var defaultNewType by remember { mutableStateOf("EXPENSE") }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

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
            Toast.makeText(context, AppStrings.cameraPermissionRequired(appLanguage), Toast.LENGTH_SHORT).show()
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
    
    // Filters
    var filterType by remember { mutableStateOf("ALL") } // ALL, INCOME, EXPENSE
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredTxs = txs.filter { tx ->
        val matchesType = when (filterType) {
            "INCOME" -> tx.type == "INCOME"
            "EXPENSE" -> tx.type == "EXPENSE"
            else -> true
        }
        val matchesSearch = tx.title.contains(searchQuery, ignoreCase = true) || 
                            tx.category.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
    }.sortedByDescending { it.timestamp }

    // Grouping by Date
    val df = SimpleDateFormat("dd MMM yyyy", if (AppStrings.isEn(appLanguage)) Locale.US else Locale("id", "ID"))
    val groupedTxs = filteredTxs.groupBy { df.format(Date(it.timestamp)) }

    val uangkuColors = LocalUangkuColors.current
    val themeDark = uangkuColors.isDark

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompactWidth = maxWidth < 600.dp

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
                        .widthIn(max = 900.dp)
                ) {
                    // SPATIAL HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = ExpressiveShapeTokens.HeroCorner, bottomEnd = ExpressiveShapeTokens.HeroCorner))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(bottomStart = ExpressiveShapeTokens.HeroCorner, bottomEnd = ExpressiveShapeTokens.HeroCorner)
                            )
                            .padding(top = 36.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AppStrings.transactionsTitle(appLanguage),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        onClick = { launchDirectCamera() },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("transactions_scan_receipt_btn")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = AppStrings.scanReceipt(appLanguage),
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            defaultNewType = "EXPENSE"
                                            transactionToEdit = null
                                            showAddDialog = true
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("transactions_add_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.quickAdd(appLanguage),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Bento Search Bar
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(AppStrings.searchPlaceholder(appLanguage)) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            // Expressive Shape-Morphing Filter Pills
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                listOf(
                                    "ALL" to AppStrings.filterAll(appLanguage),
                                    "INCOME" to AppStrings.income(appLanguage),
                                    "EXPENSE" to AppStrings.expense(appLanguage)
                                ).forEach { (type, label) ->
                                    val isSelected = filterType == type
                                    ExpressiveMorphChip(
                                        selected = isSelected,
                                        onClick = { filterType = type }
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // LIST OF TRANSACTIONS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = if (isCompactWidth) 112.dp else 40.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        if (groupedTxs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.FilterList,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = AppStrings.noTransactionsFound(appLanguage),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        for ((dateStr, txList) in groupedTxs) {
                            item(key = "group_$dateStr") {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.animateItem().padding(start = 8.dp, bottom = 8.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .animateItem()
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                            RoundedCornerShape(ExpressiveShapeTokens.BentoCorner)
                                        )
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (tx in txList) {
                                        key(tx.id) {
                                            TransactionItemRowBento(
                                                tx = tx,
                                                themeDark = themeDark,
                                                viewModel = viewModel,
                                                lang = appLanguage,
                                                onDelete = { transactionToDelete = it },
                                                onUpdate = {
                                                    transactionToEdit = it
                                                    showAddDialog = true
                                                }
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

    if (showAddDialog) {
        AddTransactionModalBento(
            viewModel = viewModel,
            themeDark = themeDark,
            initialTransaction = transactionToEdit,
            defaultType = defaultNewType,
            onDismiss = { 
                showAddDialog = false
                transactionToEdit = null
            },
            onDelete = { tx ->
                showAddDialog = false
                transactionToEdit = null
                transactionToDelete = tx
            }
        )
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
            onConfirmAdd = { title, amount, type, category ->
                viewModel.addTransaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category
                )
                capturedReceiptBitmap = null
                capturedReceiptUri = null
            }
        )
    }

    // Confirmation Alert Dialog before deleting transaction
    if (transactionToDelete != null) {
        val tx = transactionToDelete!!
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            modifier = Modifier.testTag("delete_confirmation_dialog"),
            shape = RoundedCornerShape(32.dp),
            containerColor = uangkuColors.cardSurface,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(uangkuColors.expenseContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = uangkuColors.onExpenseContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = AppStrings.deleteTxTitle(appLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = AppStrings.deleteTxDesc(appLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bento summary card preview
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = uangkuColors.elevatedSubSurface,
                        border = BorderStroke(1.dp, uangkuColors.cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
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
                                    tint = if (tx.type == "INCOME") uangkuColors.onIncomeContainer else uangkuColors.onExpenseContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = AppStrings.translateCategory(tx.category, appLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = (if (tx.type == "INCOME") "+" else "-") + viewModel.formatCurrency(tx.amount),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deletedTitle = tx.title
                        viewModel.deleteTransaction(tx)
                        transactionToDelete = null
                        Toast.makeText(context, AppStrings.txDeletedToast(appLanguage, deletedTitle), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.delete(appLanguage), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { transactionToDelete = null },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text(AppStrings.cancel(appLanguage), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItemRowBento(
    tx: Transaction,
    themeDark: Boolean,
    viewModel: FinanceViewModel,
    lang: String = "ID",
    onDelete: (Transaction) -> Unit,
    onUpdate: (Transaction) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val uangkuColors = LocalUangkuColors.current
    val currentTx by rememberUpdatedState(tx)
    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentOnUpdate by rememberUpdatedState(onUpdate)

    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { dismissValue ->
            dismissValue != SwipeToDismissBoxValue.Settled
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.28f }
    )

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                currentOnUpdate(currentTx)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.EndToStart -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                currentOnDelete(currentTx)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val bgTarget = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.Settled -> uangkuColors.elevatedSubSurface
                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                SwipeToDismissBoxValue.EndToStart -> uangkuColors.expenseContainer
            }
            val color by animateColorAsState(
                targetValue = bgTarget,
                animationSpec = MotionTokens.colorTransition,
                label = "swipe_color"
            )
            val iconTint = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onPrimaryContainer
                SwipeToDismissBoxValue.EndToStart -> uangkuColors.expenseText
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(ExpressiveShapeTokens.RowCardCorner))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (direction == SwipeToDismissBoxValue.StartToEnd) AppStrings.edit(lang) else AppStrings.delete(lang),
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (direction == SwipeToDismissBoxValue.StartToEnd) AppStrings.edit(lang) else AppStrings.delete(lang),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = iconTint
                        )
                    }
                }
            }
        },
        content = {
            val uangkuColors = LocalUangkuColors.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(ExpressiveShapeTokens.RowCardCorner))
                    .background(uangkuColors.cardSurface)
                    .clickable { onUpdate(tx) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
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
                        tint = if (tx.type == "INCOME") uangkuColors.onIncomeContainer else uangkuColors.onExpenseContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    uangkuColors.cardSurface,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = AppStrings.translateCategory(tx.category, lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!tx.bankSource.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tx.bankSource,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (if (tx.type == "INCOME") "+" else "-") + viewModel.formatCurrency(tx.amount),
                    style = MaterialTheme.typography.titleMedium.tabular(),
                    fontWeight = FontWeight.Black,
                    color = if (tx.type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionModalBento(
    viewModel: FinanceViewModel,
    themeDark: Boolean,
    initialTransaction: Transaction? = null,
    defaultType: String = "EXPENSE",
    onDismiss: () -> Unit,
    onDelete: ((Transaction) -> Unit)? = null
) {
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amount by remember { mutableStateOf(initialTransaction?.amount?.toLong()?.toString() ?: "") }
    var type by remember(initialTransaction, defaultType) { mutableStateOf(initialTransaction?.type ?: defaultType) }
    val categories = if (type == "INCOME") listOf("Gaji", "Bonus", "Investasi", "Penjualan", "Hadiah", "Lainnya") else listOf("Makanan", "Belanja", "Transportasi", "Tagihan", "Hiburan", "Kesehatan", "Sewa", "Lainnya")
    var category by remember(initialTransaction, type) {
        mutableStateOf(
            if (initialTransaction != null && initialTransaction.type == type) initialTransaction.category
            else if (type == "INCOME") "Gaji" else "Makanan"
        )
    }

    val uangkuColors = LocalUangkuColors.current
    val accentFg = if (type == "INCOME") uangkuColors.incomeText else uangkuColors.expenseText
    val accentBg = if (type == "INCOME") uangkuColors.incomeContainer else uangkuColors.expenseContainer
    val onAccentBg = if (type == "INCOME") uangkuColors.onIncomeContainer else uangkuColors.onExpenseContainer

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = uangkuColors.cardSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(1.dp, uangkuColors.cardBorder),
            modifier = Modifier.fillMaxWidth().testTag("add_transaction_modal_surface")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
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
                                .size(44.dp)
                                .background(
                                    color = accentBg,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (type == "INCOME") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = onAccentBg,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (initialTransaction == null) {
                                    if (type == "INCOME") AppStrings.recordIncome(appLanguage) else AppStrings.recordExpense(appLanguage)
                                } else {
                                    if (type == "INCOME") AppStrings.editIncome(appLanguage) else AppStrings.editExpense(appLanguage)
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (type == "INCOME") AppStrings.newIncomeDesc(appLanguage) else AppStrings.newExpenseDesc(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = accentFg,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = AppStrings.close(appLanguage),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ExpressiveSegmentedSelector(
                    options = listOf(
                        ExpressiveSegmentOption(key = "EXPENSE", label = AppStrings.expense(appLanguage)),
                        ExpressiveSegmentOption(key = "INCOME", label = AppStrings.income(appLanguage))
                    ),
                    selectedKey = type,
                    onSelect = { newType ->
                        type = newType
                        category = if (newType == "INCOME") "Gaji" else "Makanan"
                    }
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.description(appLanguage), fontWeight = FontWeight.Medium) },
                    placeholder = { Text(if (type == "INCOME") AppStrings.descPlaceholderIncome(appLanguage) else AppStrings.descPlaceholderExpense(appLanguage)) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = accentFg,
                        unfocusedBorderColor = uangkuColors.cardBorder,
                        focusedLabelColor = accentFg,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = accentFg
                    )
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        if (clean.length <= 15) amount = clean
                    },
                    label = { Text("${AppStrings.amount(appLanguage)} (${if (selectedCurrency == "USD") "USD $" else "Rupiah Rp"})", fontWeight = FontWeight.Medium) },
                    placeholder = { Text(AppStrings.amountPlaceholder(appLanguage, selectedCurrency == "USD")) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = accentFg,
                        unfocusedBorderColor = uangkuColors.cardBorder,
                        focusedLabelColor = accentFg,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = accentFg
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = AppStrings.category(appLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { category = cat }
                            ) {
                                Text(
                                    text = AppStrings.translateCategory(cat, appLanguage),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (initialTransaction != null && onDelete != null) {
                        OutlinedButton(
                            onClick = {
                                onDelete(initialTransaction)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = uangkuColors.expenseText
                            ),
                            border = BorderStroke(1.dp, uangkuColors.expenseText),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("delete_transaction_button")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = AppStrings.delete(appLanguage),
                                tint = uangkuColors.expenseText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.delete(appLanguage), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            val cleanAmt = amount.filter { it.isDigit() }
                            val dAmount = cleanAmt.toDoubleOrNull() ?: 0.0
                            if (title.isNotBlank() && dAmount > 0) {
                                if (initialTransaction != null) {
                                    viewModel.updateTransaction(
                                        initialTransaction.copy(
                                            title = title,
                                            amount = dAmount,
                                            type = type,
                                            category = category
                                        )
                                    )
                                } else {
                                    viewModel.addTransaction(
                                        title = title,
                                        amount = dAmount,
                                        type = type,
                                        category = category
                                    )
                                }
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        enabled = title.isNotBlank() && amount.filter { it.isDigit() }.isNotEmpty()
                    ) {
                        Text(
                            text = if (initialTransaction != null) AppStrings.update(appLanguage) else AppStrings.saveTransaction(appLanguage),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

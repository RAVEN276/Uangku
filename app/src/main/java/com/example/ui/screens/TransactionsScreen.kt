package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.LockerSpeedDialMenu
import com.example.ui.components.ReceiptScannerDialog
import com.example.ui.components.RupiahVisualTransformation
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
    var showAddDialog by remember { mutableStateOf(false) }
    var capturedReceiptBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isSpeedDialExpanded by remember { mutableStateOf(false) }
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
    val df = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val groupedTxs = filteredTxs.groupBy { df.format(Date(it.timestamp)) }

    val themeDark = MaterialTheme.colorScheme.surface.let { it.red * 0.299 + it.green * 0.587 + it.blue * 0.114 < 0.5f }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            LockerSpeedDialMenu(
                isExpanded = isSpeedDialExpanded,
                onToggleExpand = { isSpeedDialExpanded = !isSpeedDialExpanded },
                onSelectExpense = {
                    defaultNewType = "EXPENSE"
                    transactionToEdit = null
                    showAddDialog = true
                },
                onSelectIncome = {
                    defaultNewType = "INCOME"
                    transactionToEdit = null
                    showAddDialog = true
                },
                onSelectScanReceipt = {
                    launchDirectCamera()
                },
                modifier = Modifier
                    .padding(bottom = 72.dp, end = 8.dp)
                    .testTag("transactions_speed_dial_locker")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
                                 else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // SPATIAL HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            if (themeDark) Color(0xFF1E293B).copy(alpha = 0.9f) 
                            else Color.White.copy(alpha = 0.9f)
                        )
                        .border(
                            1.dp,
                            if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                            RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Riwayat Transaksi",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        )

                        // Bento Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari aktivitas...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                focusedBorderColor = Color(0xFF10B981)
                            )
                        )
                        
                        // Filter Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("ALL" to "Semua", "INCOME" to "Pemasukan", "EXPENSE" to "Pengeluaran").forEach { (type, label) ->
                                val isSelected = filterType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (isSelected) Color(0xFF10B981)
                                            else if (themeDark) Color(0xFF334155) else Color(0xFFF1F5F9)
                                        )
                                        .clickable { filterType = type }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
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
                    contentPadding = PaddingValues(top = 16.dp, bottom = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Tidak ada transaksi ditemukan",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    for ((dateStr, txList) in groupedTxs) {
                        item {
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
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(
                                        if (themeDark) Color(0xFF1E293B).copy(alpha = 0.6f) 
                                        else Color.White.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        1.dp,
                                        if (themeDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                                        RoundedCornerShape(32.dp)
                                    )
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (tx in txList) {
                                    TransactionItemRowBento(
                                        tx = tx,
                                        themeDark = themeDark,
                                        viewModel = viewModel,
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
            containerColor = if (themeDark) Color(0xFF1E293B) else Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Hapus Transaksi?",
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
                        text = "Apakah Anda yakin ingin menghapus data transaksi ini? Data yang dihapus tidak dapat dikembalikan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bento summary card preview
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (themeDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            1.dp,
                            if (themeDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
                        ),
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
                                        if (tx.type == "INCOME") Color(0xFF10B981).copy(alpha = 0.15f)
                                        else Color(0xFFEF4444).copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
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
                                    text = tx.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = (if (tx.type == "INCOME") "+" else "-") + viewModel.formatCurrency(tx.amount),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
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
                        Toast.makeText(context, "Transaksi \"$deletedTitle\" berhasil dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
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
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { transactionToDelete = null },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
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
    onDelete: (Transaction) -> Unit,
    onUpdate: (Transaction) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onUpdate(tx)
                    false // Bounce back
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(tx)
                    false // Keep item in place until user confirms deletion in the alert dialog
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF3B82F6) // Blue for edit
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF4444) // Red for delete
                }
            )
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
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (direction != SwipeToDismissBoxValue.Settled) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                    .clickable { onUpdate(tx) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (tx.type == "INCOME") Color(0xFF10B981).copy(alpha = 0.15f)
                            else Color(0xFFEF4444).copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.type == "INCOME") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
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
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tx.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Text(
                    text = (if (tx.type == "INCOME") "+" else "-") + viewModel.formatCurrency(tx.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
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
    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amount by remember { mutableStateOf(initialTransaction?.amount?.toLong()?.toString() ?: "") }
    val type = initialTransaction?.type ?: defaultType
    val categories = if (type == "INCOME") listOf("Gaji", "Bonus", "Investasi", "Penjualan", "Hadiah", "Lainnya") else listOf("Makanan", "Belanja", "Transportasi", "Tagihan", "Hiburan", "Kesehatan", "Sewa", "Lainnya")
    var category by remember { mutableStateOf(initialTransaction?.category ?: (if (type == "INCOME") "Gaji" else "Makanan")) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = if (themeDark) Color(0xFF1E293B) else Color.White,
            contentColor = if (themeDark) Color.White else Color(0xFF0F172A),
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (type == "INCOME") Color(0xFF10B981).copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.18f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (type == "INCOME") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (initialTransaction == null) {
                                    if (type == "INCOME") "Catat Pemasukan" else "Catat Pengeluaran"
                                } else {
                                    if (type == "INCOME") "Edit Pemasukan" else "Edit Pengeluaran"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (themeDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = if (type == "INCOME") "Pemasukan Dana Baru" else "Pengeluaran Biaya Baru",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = if (themeDark) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Keterangan", fontWeight = FontWeight.Medium) },
                    placeholder = { Text(if (type == "INCOME") "cth. Gaji Bulanan, Bonus Proyek" else "cth. Belanja Bulanan, Bensin") },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (themeDark) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (themeDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                        focusedBorderColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                        unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.15f),
                        focusedLabelColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                        unfocusedLabelColor = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        cursorColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        if (clean.length <= 15) amount = clean
                    },
                    label = { Text("Nominal (${if (selectedCurrency == "USD") "USD $" else "Rupiah Rp"})", fontWeight = FontWeight.Medium) },
                    placeholder = { Text(if (selectedCurrency == "USD") "misal: 25" else "misal: 45.000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (themeDark) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (themeDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                        focusedBorderColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                        unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.15f),
                        focusedLabelColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                        unfocusedLabelColor = if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        cursorColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (themeDark) Color.White else Color(0xFF0F172A)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) (if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444))
                                        else if (themeDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.Transparent
                                    else if (themeDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
                                ),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else if (themeDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("delete_transaction_button")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus Transaksi",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hapus", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                            containerColor = if (type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        enabled = title.isNotBlank() && amount.filter { it.isDigit() }.isNotEmpty()
                    ) {
                        Text(
                            text = if (initialTransaction != null) "Perbarui" else "Simpan Transaksi",
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

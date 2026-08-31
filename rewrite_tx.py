with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FinanceViewModel
import com.example.ui.TransactionEntity
import com.example.ui.utils.RupiahVisualTransformation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    
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
    }.sortedByDescending { it.date }

    // Grouping by Date
    val df = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val groupedTxs = filteredTxs.groupBy { df.format(Date(it.date)) }

    val themeDark = MaterialTheme.colorScheme.surface.let { it.red * 0.299 + it.green * 0.587 + it.blue * 0.114 < 0.5f }
    val rubelFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
                             else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                )
            )
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
                        text = "Transactions",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )

                    // BENTO: Search and Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search activity...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                focusedBorderColor = Color(0xFF10B981)
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (themeDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                                .clickable { filterType = if (filterType == "ALL") "INCOME" else if (filterType == "INCOME") "EXPENSE" else "ALL" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (filterType != "ALL") Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Filter Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("ALL", "INCOME", "EXPENSE").forEach { type ->
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
                                    text = type,
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
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
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
                                    text = "No transactions found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                groupedTxs.forEach { (dateStr, txList) ->
                    item {
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier
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
                            txList.forEach { tx ->
                                TransactionItemRowBento(
                                    tx = tx,
                                    themeDark = themeDark,
                                    rubelFormat = rubelFormat,
                                    onDelete = { viewModel.deleteTransaction(it) },
                                    onUpdate = { viewModel.updateTransaction(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp) // Offset for bottom nav
                .size(64.dp),
            shape = CircleShape,
            containerColor = Color(0xFF10B981),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction", modifier = Modifier.size(28.dp))
        }
    }

    if (showAddDialog) {
        AddTransactionModalBento(
            viewModel = viewModel,
            themeDark = themeDark,
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun TransactionItemRowBento(
    tx: TransactionEntity,
    themeDark: Boolean,
    rubelFormat: NumberFormat,
    onDelete: (TransactionEntity) -> Unit,
    onUpdate: (TransactionEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (themeDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = (if (tx.type == "INCOME") "+" else "-") + rubelFormat.format(tx.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (tx.type == "INCOME") Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }

        // Expanded Actions
        androidx.compose.animation.AnimatedVisibility(visible = expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onDelete(tx) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionModalBento(
    viewModel: FinanceViewModel,
    themeDark: Boolean,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }
    var category by remember { mutableStateOf("Makanan") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = if (themeDark) Color(0xFF1E293B) else Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Transaction",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Bento Segmented Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            if (themeDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                            RoundedCornerShape(28.dp)
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (type == "EXPENSE") Color(0xFFEF4444) else Color.Transparent)
                            .clickable { type = "EXPENSE" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            color = if (type == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (type == "INCOME") Color(0xFF10B981) else Color.Transparent)
                            .clickable { type = "INCOME" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            color = if (type == "INCOME") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Groceries, Salary") },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981)
                    )
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        if (clean.length <= 15) amount = clean
                    },
                    label = { Text("Amount (IDR)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = RupiahVisualTransformation(),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981)
                    )
                )

                Text("Category", fontWeight = FontWeight.Bold)
                
                val categories = listOf("Makanan", "Belanja", "Transportasi", "Gaji", "Investasi", "Sewa", "Lainnya")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFF10B981)
                                    else if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
                                )
                                .clickable { category = cat }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val cleanAmt = amount.filter { it.isDigit() }
                        val dAmount = cleanAmt.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && dAmount > 0) {
                            viewModel.addTransaction(
                                title = title,
                                amount = dAmount,
                                type = type,
                                category = category
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    ),
                    enabled = title.isNotBlank() && amount.filter { it.isDigit() }.isNotEmpty()
                ) {
                    Text("Save Transaction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
""")

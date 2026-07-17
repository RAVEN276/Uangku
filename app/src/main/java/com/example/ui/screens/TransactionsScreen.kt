package com.example.ui.screens

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clip
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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.RupiahVisualTransformation
import com.example.data.model.Transaction
import com.example.ui.FinanceViewModel

@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") } // ALL, INCOME, EXPENSE
    var selectedCategoryFilter by remember { mutableStateOf("ALL") } // ALL or categories

    var showAddDialog by remember { mutableStateOf(false) }

    val rubelFormat = remember {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        format
    }

    // List filtration
    val filteredTransactions = remember(transactions, searchQuery, selectedTypeFilter, selectedCategoryFilter) {
        transactions.filter { tx ->
            val matchesSearch = tx.title.contains(searchQuery, ignoreCase = true) ||
                    tx.category.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedTypeFilter == "ALL" || tx.type == selectedTypeFilter
            val matchesCategory = selectedCategoryFilter == "ALL" || tx.category == selectedCategoryFilter

            matchesSearch && matchesType && matchesCategory
        }
    }

    // Statistics for the currently filtered view
    val totalIncome = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    }
    val totalExpense = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }

    // Grouping by Date descending
    val groupedTransactions = remember(filteredTransactions) {
        val groups = filteredTransactions.groupBy { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

            val isToday = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            val isYesterday = cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)

            when {
                isToday -> "Hari Ini"
                isYesterday -> "Kemarin"
                else -> SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date(tx.timestamp))
            }
        }
        groups.toList().sortedWith { a, b ->
            val txA = a.second.maxByOrNull { it.timestamp }?.timestamp ?: 0L
            val txB = b.second.maxByOrNull { it.timestamp }?.timestamp ?: 0L
            txB.compareTo(txA)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("transactions_add_floating_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Title
            Text(
                text = "Daftar Transaksi",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Horizontal Summary Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (themeDark) Color(0xFF0F2D1E) else Color(0xFFE8F5E9)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (themeDark) Color(0xFF1B5E20) else Color(0xFFC8E6C9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (themeDark) Color(0xFF1B5E20) else Color(0xFFC8E6C9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (themeDark) Color(0xFFC8E6C9) else Color(0xFF1B5E20),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pemasukan", fontSize = 10.sp, color = if (themeDark) Color(0xFF81C784) else Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rubelFormat.format(totalIncome).replace(",00", ""),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (themeDark) Color(0xFFC8E6C9) else Color(0xFF1B5E20),
                            maxLines = 1
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (themeDark) Color(0xFF3E121E) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (themeDark) Color(0xFFB71C1C) else Color(0xFFFFCDD2))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (themeDark) Color(0xFFB71C1C) else Color(0xFFFFCDD2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (themeDark) Color(0xFFFFCDD2) else Color(0xFFB71C1C),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pengeluaran", fontSize = 10.sp, color = if (themeDark) Color(0xFFE57373) else Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rubelFormat.format(totalExpense).replace(",00", ""),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (themeDark) Color(0xFFFFCDD2) else Color(0xFFB71C1C),
                            maxLines = 1
                        )
                    }
                }
            }

            // Search Bar & Filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Transaksi...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transactions_search_input")
            )

            // Beautifully Reworked Pill Tabs Toggles (Semua, Pemasukan, Pengeluaran)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "Semua",
                    "INCOME" to "Pemasukan",
                    "EXPENSE" to "Pengeluaran"
                ).forEach { (filterVal, label) ->
                    val isSelected = selectedTypeFilter == filterVal
                    val bgCol = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    }
                    val txtCol = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val iconVector = when (filterVal) {
                        "INCOME" -> Icons.Default.ArrowUpward
                        "EXPENSE" -> Icons.Default.ArrowDownward
                        else -> Icons.Default.Info
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(bgCol, RoundedCornerShape(16.dp))
                            .clickable { selectedTypeFilter = filterVal }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = txtCol,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = txtCol
                            )
                        }
                    }
                }
            }

            // Category filter row buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kategori:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))

                // Scroll of categories
                val categoriesList = listOf("ALL", "Makanan", "Belanja", "Transportasi", "Gaji", "Investasi", "Sewa", "Lainnya")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    categoriesList.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Transaction lists scroll with Date Grouping Header
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Empty",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Transaksi tidak ditemukan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupedTransactions.forEach { (dateHeader, list) ->
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                Text(
                                    text = dateHeader,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                )
                            }
                        }

                        items(list, key = { "tx_${it.id}" }) { tx ->
                            TransactionItemRow(
                                tx = tx,
                                onDelete = { viewModel.deleteTransaction(it) },
                                onUpdate = { viewModel.updateTransaction(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog add screen
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("EXPENSE") }
        var category by remember { mutableStateOf("Makanan") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
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
                        text = "Tambah Transaksi Manual",
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
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val cleanAmt = amount.filter { it.isDigit() }
                                val dAmount = cleanAmt.toDoubleOrNull() ?: 0.0
                                if (title.isNotBlank() && dAmount > 0) {
                                    viewModel.addTransaction(
                                        title = title,
                                        amount = dAmount,
                                        type = type,
                                        category = if (type == "INCOME" && category == "Makanan") "Gaji" else category
                                    )
                                    showAddDialog = false
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

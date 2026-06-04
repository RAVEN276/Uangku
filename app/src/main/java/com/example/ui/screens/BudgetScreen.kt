package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Budget
import com.example.ui.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.allBudgets.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    var showAddBudgetDialog by remember { mutableStateOf(false) }

    val rubelFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    rubelFormat.maximumFractionDigits = 0

    // Group expenses by category
    val expenses = transactions.filter { it.type == "EXPENSE" }
    val totalExpenseSum = expenses.sumOf { it.amount }
    val expensesByCategory = expenses.groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Button(
                onClick = { showAddBudgetDialog = true },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("budget_add_floating_action_btn")
                    .padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.LibraryAdd, contentDescription = "Limit Baru")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Batas Baru")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Anggaran & Alarm",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Atur sasaran pengeluaran bulanan agar terhindar dari pemborosan berlebih.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Overall budget if configured
            val overallBudget = budgets.find { it.category == "ALL" }
            if (overallBudget != null) {
                item {
                    val progressPercent = if (overallBudget.limitAmount > 0) {
                        (totalExpenseSum / overallBudget.limitAmount * 100).toInt()
                    } else {
                        0
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("overall_budget_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Notification",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Limit Pengeluaran Bulanan",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteBudget(overallBudget) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Terpakai: ${rubelFormat.format(totalExpenseSum).replace(",00", "")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Limit: ${rubelFormat.format(overallBudget.limitAmount).replace(",00", "")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Colored progress bar based on warnings
                            val progressBarColor = if (progressPercent >= overallBudget.alertThresholdPercent) {
                                MaterialTheme.colorScheme.error
                            } else if (progressPercent >= 50) {
                                Color(0xFFFFB74D) // Amber/Orange
                            } else {
                                MaterialTheme.colorScheme.primary
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                val progressFraction = if (overallBudget.limitAmount > 0) {
                                    (totalExpenseSum / overallBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressFraction)
                                        .fillMaxSize()
                                        .background(progressBarColor)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Porsi penggunaan: $progressPercent%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (progressPercent >= overallBudget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Notifikasi aktif pada ${overallBudget.alertThresholdPercent}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Batas Bulanan Kosong", fontWeight = FontWeight.Bold)
                                Text("Kamu belum menyetel limit total pengeluaran belanja.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Categories list headers
            item {
                Text(
                    text = "Batas Anggaran Kategori",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val categoryBudgets = budgets.filter { it.category != "ALL" }
            if (categoryBudgets.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada limit khusus kategori. Klik tombol di bawah untuk menyetel batas per kategori.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(categoryBudgets, key = { it.category }) { budget ->
                    val spentOnCategory = expensesByCategory[budget.category] ?: 0.0
                    val percent = if (budget.limitAmount > 0) {
                        (spentOnCategory / budget.limitAmount * 100).toInt()
                    } else {
                        0
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("category_budget_${budget.category}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = budget.category.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = budget.category,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteBudget(budget) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Terpakai: ${rubelFormat.format(spentOnCategory).replace(",00", "")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sisa: ${rubelFormat.format((budget.limitAmount - spentOnCategory).coerceAtLeast(0.0)).replace(",00", "")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (spentOnCategory > budget.limitAmount) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val progressFraction = if (budget.limitAmount > 0) {
                                (spentOnCategory / budget.limitAmount).toFloat().coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (percent >= budget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$percent% terpakai",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (percent >= budget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Limit: ${rubelFormat.format(budget.limitAmount).replace(",00", "")}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Modal dialogue limit set up
    if (showAddBudgetDialog) {
        var categoryChoice by remember { mutableStateOf("ALL") }
        var limitInput by remember { mutableStateOf("") }
        var alertPercentInput by remember { mutableStateOf("80") }

        val catsOptions = listOf(
            "ALL" to "Total Belanja Bulanan",
            "Makanan" to "Makanan",
            "Belanja" to "Belanja",
            "Transportasi" to "Transportasi",
            "Sewa" to "Sewa",
            "Lainnya" to "Lainnya"
        )

        Dialog(onDismissRequest = { showAddBudgetDialog = false }) {
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
                        text = "Pasang Batas Anggaran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Pilih Target Kategori:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    // Simple select choices
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        catsOptions.forEach { (valStr, choiceLabel) ->
                            val isSelected = categoryChoice == valStr
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { categoryChoice = valStr }
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = choiceLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it },
                        label = { Text("Limit Anggaran Berbelanja (IDR)") },
                        placeholder = { Text("misal: 1500000") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = alertPercentInput,
                        onValueChange = { alertPercentInput = it },
                        label = { Text("Persentase Alarm Trigger (%)") },
                        placeholder = { Text("default: 80") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddBudgetDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val limAmount = limitInput.toDoubleOrNull() ?: 0.0
                                val percentTrigger = alertPercentInput.toIntOrNull() ?: 80
                                if (limAmount > 0) {
                                    viewModel.setBudget(
                                        category = categoryChoice,
                                        amount = limAmount,
                                        alertThreshold = percentTrigger
                                    )
                                    showAddBudgetDialog = false
                                }
                            },
                            enabled = limitInput.toDoubleOrNull() != null
                        ) {
                            Text("Terapkan")
                        }
                    }
                }
            }
        }
    }
}

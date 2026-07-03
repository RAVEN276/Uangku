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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.RupiahVisualTransformation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.ui.text.style.TextAlign
import com.example.data.model.Budget
import com.example.ui.FinanceViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.allBudgets.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val savingGoals by viewModel.allSavingGoals.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()

    val savingChallenges by viewModel.savingChallenges.collectAsStateWithLifecycle()
    val virtualBadges by viewModel.virtualBadges.collectAsStateWithLifecycle()
    val showBadgeUnlockDialog by viewModel.showBadgeUnlockDialog.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadSavingChallengesAndBadges()
    }

    var activeTab by remember { mutableStateOf(0) } // 0 = Anggaran, 1 = Target Menabung, 2 = Tantangan
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingGoalDialog by remember { mutableStateOf(false) }

    val rubelFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    rubelFormat.maximumFractionDigits = 0

    // Group expenses by category
    val expenses = transactions.filter { it.type == "EXPENSE" }
    
    // Filter expenses to current calendar month for monthly and category budgets
    val currentMonthExpenses = expenses.filter { tx ->
        val cal1 = java.util.Calendar.getInstance()
        cal1.timeInMillis = tx.timestamp
        val cal2 = java.util.Calendar.getInstance()
        cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
        cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH)
    }
    
    val totalExpenseSum = currentMonthExpenses.sumOf { it.amount }
    
    val expensesByCategory = currentMonthExpenses.groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }

    val todayExpenseSum = expenses.filter { tx ->
        val cal1 = java.util.Calendar.getInstance()
        cal1.timeInMillis = tx.timestamp
        val cal2 = java.util.Calendar.getInstance()
        cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
        cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }.sumOf { it.amount }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (activeTab == 0) {
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
            } else {
                Button(
                    onClick = { showAddSavingGoalDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .testTag("saving_goal_add_floating_action_btn")
                        .padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Target Baru")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Target Baru")
                }
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (activeTab == 0) "Anggaran & Alarm" else if (activeTab == 1) "Target Menabung" else "Tantangan & Lencana",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (activeTab == 0) "Atur sasaran pengeluaran bulanan agar terhindar dari pemborosan berlebih." else if (activeTab == 1) "Lacak kemajuan menabung Anda untuk membeli barang atau tujuan impian." else "Tantangan menabung interaktif seru untuk membangun kebiasaan finansial positif secara menyenangkan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeBtnColor = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    val inactiveBtnColor = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { activeTab = 0 },
                        colors = if (activeTab == 0) activeBtnColor else inactiveBtnColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Anggaran", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { activeTab = 1 },
                        colors = if (activeTab == 1) activeBtnColor else inactiveBtnColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Target", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { activeTab = 2 },
                        colors = if (activeTab == 2) activeBtnColor else inactiveBtnColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Tantangan", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            if (activeTab == 0) {
                // Overall and Daily budgets if configured
                val overallBudget = budgets.find { it.category == "ALL" }
                val dailyBudget = budgets.find { it.category == "DAILY" }

                if (overallBudget == null && dailyBudget == null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_budget_placeholder"),
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
                                    Text("Batas Pengeluaran Kosong", fontWeight = FontWeight.Bold)
                                    Text("Kamu belum menyetel limit total pengeluaran belanja bulanan atau harian.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                } else {
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
                    }

                    if (dailyBudget != null) {
                        item {
                            val progressPercentDaily = if (dailyBudget.limitAmount > 0) {
                                (todayExpenseSum / dailyBudget.limitAmount * 100).toInt()
                            } else {
                                0
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                        .testTag("daily_budget_card"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
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
                                                imageVector = Icons.Default.AddAlert,
                                                contentDescription = "Alert",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Limit Pengeluaran Harian",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteBudget(dailyBudget) }) {
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
                                            text = "Hari ini: ${rubelFormat.format(todayExpenseSum).replace(",00", "")}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Limit: ${rubelFormat.format(dailyBudget.limitAmount).replace(",00", "")}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Colored progress bar based on warnings
                                    val progressBarColorDaily = if (progressPercentDaily >= dailyBudget.alertThresholdPercent) {
                                        MaterialTheme.colorScheme.error
                                    } else if (progressPercentDaily >= 50) {
                                        Color(0xFFFFB74D) // Amber/Orange
                                    } else {
                                        MaterialTheme.colorScheme.secondary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        val progressFractionDaily = if (dailyBudget.limitAmount > 0) {
                                            (todayExpenseSum / dailyBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressFractionDaily)
                                                .fillMaxSize()
                                                .background(progressBarColorDaily)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Porsi penggunaan: $progressPercentDaily%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (progressPercentDaily >= dailyBudget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "Notifikasi aktif pada ${dailyBudget.alertThresholdPercent}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
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

                val categoryBudgets = budgets.filter { it.category != "ALL" && it.category != "DAILY" }
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
                    items(categoryBudgets, key = { "budget_${it.category}" }) { budget ->
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
            } else if (activeTab == 1) {
                if (savingGoals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Belum Ada Target Menabung",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Raih impian masa depan Anda dengan menyisihkan dana tabungan bulanan secara langsung.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(savingGoals, key = { "goal_${it.id}" }) { goal ->
                        SavingGoalCard(
                            goal = goal,
                            rubelFormat = rubelFormat,
                            currentBalance = totalBalance,
                            onDelete = { viewModel.deleteSavingGoal(goal) }
                        )
                    }
                }
            } else {
                // TAB 2: Saving Challenges & Badges
                item {
                    Text(
                        text = "Tantangan Menabung Finansial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (savingChallenges.isEmpty()) {
                    item {
                        Text(
                            text = "Memuat tantangan...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(savingChallenges, key = { "challenge_${it.id}" }) { challenge ->
                        val isChCompleted = challenge.status == "COMPLETED"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("challenge_card_${challenge.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChCompleted) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = challenge.title,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (isChCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "📅 " + challenge.scheduleText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (isChCompleted) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Selesai 🎉",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.checkInChallenge(challenge.id, context = viewModel.getApplication()) },
                                            modifier = Modifier
                                                .testTag("checkin_button_${challenge.id}"),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Check-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = challenge.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val progressFraction = challenge.currentProgress.toFloat() / challenge.targetProgress.toFloat()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Check-in: ${challenge.currentProgress}/${challenge.targetProgress}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Target: " + rubelFormat.format(challenge.targetAmount).replace(",00", ""),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progressFraction.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isChCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Menabung " + rubelFormat.format(challenge.amountPerCheckIn).replace(",00", "") + " setiap kali check-in.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Lemari Lencana Anda 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val chunkedBadges = virtualBadges.chunked(2)
                        chunkedBadges.forEach { rowBadges ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowBadges.forEach { badge ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("badge_card_${badge.id}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (badge.isUnlocked) {
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                            }
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .background(
                                                        if (badge.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
                                                        androidx.compose.foundation.shape.CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = badge.icon,
                                                    fontSize = 28.sp,
                                                    modifier = Modifier.alpha(if (badge.isUnlocked) 1f else 0.35f)
                                                )
                                            }
                                            
                                            Text(
                                                text = badge.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center,
                                                color = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            
                                            Text(
                                                text = badge.description,
                                                fontSize = 9.sp,
                                                lineHeight = 11.sp,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.heightIn(min = 36.dp)
                                            )
                                            
                                            Text(
                                                text = if (badge.isUnlocked) "🔓 Terbuka!" else "🔒 " + badge.unlockProgressText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (badge.isUnlocked) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                                if (rowBadges.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
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
        val hasMonthly = budgets.any { it.category == "ALL" }
        val hasDaily = budgets.any { it.category == "DAILY" }

        val catsOptions = listOfNotNull(
            if (!hasMonthly) "ALL" to "Total Belanja Bulanan" else null,
            if (!hasDaily) "DAILY" to "Total Belanja Harian" else null,
            "Makanan" to "Makanan",
            "Belanja" to "Belanja",
            "Transportasi" to "Transportasi",
            "Sewa" to "Sewa",
            "Lainnya" to "Lainnya"
        )

        val initialChoice = remember(catsOptions) { catsOptions.firstOrNull()?.first ?: "Makanan" }
        var categoryChoice by remember(catsOptions) { mutableStateOf(initialChoice) }
        var limitInput by remember { mutableStateOf("") }
        var alertPercentInput by remember { mutableStateOf("80") }

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

                    // Scrollable select choices to avoid keyboard overlap / small display bugs
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                limitInput = clean
                            }
                        },
                        label = { Text("Limit Anggaran Berbelanja (IDR)") },
                        placeholder = { Text("misal: 1.500.000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(),
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
                                val cleanLim = limitInput.filter { it.isDigit() }
                                val limAmount = cleanLim.toDoubleOrNull() ?: 0.0
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
                            enabled = limitInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text("Terapkan")
                        }
                    }
                }
            }
        }
    }

    if (showAddSavingGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var targetAmtInput by remember { mutableStateOf("") }
        var targetDateInput by remember { mutableStateOf("") }
        var categoryChoice by remember { mutableStateOf("Tabungan") }

        val catsOptions = listOf("Gawai", "Liburan", "Dana Darurat", "Kendaraan", "Lainnya")

        Dialog(onDismissRequest = { showAddSavingGoalDialog = false }) {
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
                        text = "Rencana Target Menabung Baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Nama Rencana / Impian") },
                        placeholder = { Text("misal: Beli Laptop Baru") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetAmtInput,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                targetAmtInput = clean
                            }
                        },
                        label = { Text("Target Minimal Nominal (IDR)") },
                        placeholder = { Text("misal: 10.000.000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetDateInput,
                        onValueChange = { targetDateInput = it },
                        label = { Text("Target Tanggal Pencapaian") },
                        placeholder = { Text("misal: Desember 2026") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Pilih Kategori:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        catsOptions.forEach { cat ->
                            val isSelected = categoryChoice == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) Color(0xFFF06292) else if (themeDark) Color(0xFF2C1423) else Color(0xFFF3E5F5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { categoryChoice = cat }
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
                        TextButton(onClick = { showAddSavingGoalDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val cleanAmt = targetAmtInput.filter { it.isDigit() }
                                val targetAmt = cleanAmt.toDoubleOrNull() ?: 0.0
                                if (goalTitle.isNotEmpty() && targetAmt > 0) {
                                    viewModel.addSavingGoal(
                                        title = goalTitle,
                                        targetAmount = targetAmt,
                                        currentAmount = 0.0,
                                        targetDate = targetDateInput.ifEmpty { "Segera" },
                                        category = categoryChoice
                                    )
                                    showAddSavingGoalDialog = false
                                }
                            },
                            enabled = goalTitle.isNotEmpty() && targetAmtInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text("Buat Rencana")
                        }
                    }
                }
            }
        }
    }

    if (showBadgeUnlockDialog != null) {
        val badge = showBadgeUnlockDialog!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.dismissBadgeUnlockDialog() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🎉 SELAMAT! 🎉",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.icon, fontSize = 56.sp)
                    }
                    
                    Text(
                        text = "Lencana Terbuka: ${badge.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = { viewModel.dismissBadgeUnlockDialog() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Klaim & Bagikan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingGoalCard(
    goal: com.example.data.model.SavingGoal,
    rubelFormat: NumberFormat,
    currentBalance: Double,
    onDelete: () -> Unit
) {
    val progressFraction = if (goal.targetAmount > 0) {
        (currentBalance / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressPercent = (progressFraction * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saving_goal_card_${goal.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp),
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
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kategori: ${goal.category} • Sasaran: ${goal.targetDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Target",
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
                    text = "Terkumpul: ${rubelFormat.format(currentBalance).replace(",00", "")}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Impian: ${rubelFormat.format(goal.targetAmount).replace(",00", "")}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mendeteksi otomatis saldo aktif: $progressPercent%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                val sisa = (goal.targetAmount - currentBalance).coerceAtLeast(0.0)
                if (sisa > 0) {
                    Text(
                        text = "Sisa: ${rubelFormat.format(sisa).replace(",00", "")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "🎉 Impian Terlaksana!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

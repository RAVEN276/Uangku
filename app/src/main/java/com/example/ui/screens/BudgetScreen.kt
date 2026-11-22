package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.animateContentSize
import com.example.ui.theme.MotionTokens
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.LocalUangkuColors
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.model.Budget
import com.example.ui.FinanceViewModel
import com.example.ui.components.ExpressiveMorphChip
import com.example.ui.components.ExpressiveSegmentOption
import com.example.ui.components.ExpressiveSegmentedSelector
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.tabular
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import java.text.NumberFormat
import java.util.Locale
import com.example.ui.util.AppStrings

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
    val uangkuColors = LocalUangkuColors.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val savingChallenges by viewModel.savingChallenges.collectAsStateWithLifecycle()
    val virtualBadges by viewModel.virtualBadges.collectAsStateWithLifecycle()
    val showBadgeUnlockDialog by viewModel.showBadgeUnlockDialog.collectAsStateWithLifecycle()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadSavingChallengesAndBadges()
    }

    var activeTab by remember { mutableStateOf(0) } // 0 = Anggaran, 1 = Target Menabung
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingGoalDialog by remember { mutableStateOf(false) }
    var editingSavingGoal by remember { mutableStateOf<com.example.data.model.SavingGoal?>(null) }

    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val rubelFormat = remember(selectedCurrency) {
        object {
            fun format(amt: Double): String = viewModel.formatCurrency(amt)
            fun format(amt: Long): String = viewModel.formatCurrency(amt.toDouble())
            fun format(amt: Number): String = viewModel.formatCurrency(amt.toDouble())
        }
    }

    // Group valid positive expenses by category
    val expenses = transactions.filter { it.type == "EXPENSE" && it.amount > 0.0 }
    
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
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AppStrings.budgetsTitle(appLanguage),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (activeTab == 0) {
                                    Button(
                                        onClick = { showAddBudgetDialog = true },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("budget_add_floating_action_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LibraryAdd,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.newBudget(appLanguage),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { showAddSavingGoalDialog = true },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("saving_goal_add_floating_action_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.newSavingGoal(appLanguage),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // M3 Expressive Segmented Selector
                            ExpressiveSegmentedSelector(
                                options = listOf(
                                    ExpressiveSegmentOption(key = "budgets", label = AppStrings.budgetTab(appLanguage)),
                                    ExpressiveSegmentOption(key = "goals", label = AppStrings.savingGoalTab(appLanguage))
                                ),
                                selectedKey = if (activeTab == 0) "budgets" else "goals",
                                onSelect = { key -> activeTab = if (key == "budgets") 0 else 1 }
                            )
                        }
                    }

                    // Content Below Header
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = if (isCompactWidth) 112.dp else 40.dp)
                    ) {
            if (activeTab == 0) {
                // Overall and Daily budgets if configured
                val overallBudget = budgets.find { it.category == "ALL" }
                val dailyBudget = budgets.find { it.category == "DAILY" }

                if (overallBudget == null && dailyBudget == null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .testTag("empty_budget_placeholder"),
                            colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, uangkuColors.cardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = AppStrings.emptyBudgetTitle(appLanguage),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppStrings.emptyBudgetDesc(appLanguage),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                    .animateContentSize()
                                    .testTag("overall_budget_card"),
                                colors = CardDefaults.cardColors(
                                    containerColor = uangkuColors.cardSurface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, uangkuColors.cardBorder),
                                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.NotificationsActive,
                                                    contentDescription = "Notification",
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = AppStrings.monthlyBudgetLimit(appLanguage),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteBudget(overallBudget) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = AppStrings.delete(appLanguage),
                                                tint = uangkuColors.expenseText
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = AppStrings.usedAmount(appLanguage, rubelFormat.format(totalExpenseSum).replace(",00", "")),
                                            style = MaterialTheme.typography.bodyMedium.tabular(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = AppStrings.limitAmount(appLanguage, rubelFormat.format(overallBudget.limitAmount).replace(",00", "")),
                                            style = MaterialTheme.typography.bodyMedium.tabular(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Colored progress bar based on warnings
                                    val progressBarColor = if (progressPercent >= overallBudget.alertThresholdPercent) {
                                        MaterialTheme.colorScheme.error
                                    } else if (progressPercent >= 50) {
                                        uangkuColors.warningText
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(uangkuColors.elevatedSubSurface)
                                    ) {
                                        val targetProgressFraction = if (overallBudget.limitAmount > 0) {
                                            (totalExpenseSum / overallBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }
                                        val progressFraction by androidx.compose.animation.core.animateFloatAsState(
                                            targetValue = targetProgressFraction, 
                                            animationSpec = MotionTokens.dataProgressSpec,
                                            label = "progressAnim1"
                                        )
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
                                            text = AppStrings.usagePortion(appLanguage, progressPercent),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (progressPercent >= overallBudget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = AppStrings.alertActiveAt(appLanguage, overallBudget.alertThresholdPercent),
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
                                    .animateContentSize()
                                    .testTag("daily_budget_card"),
                                colors = CardDefaults.cardColors(
                                    containerColor = uangkuColors.cardSurface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, uangkuColors.cardBorder),
                                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .background(uangkuColors.elevatedSubSurface, RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AddAlert,
                                                    contentDescription = "Alert",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = AppStrings.dailyBudgetLimit(appLanguage),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteBudget(dailyBudget) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = AppStrings.delete(appLanguage),
                                                tint = uangkuColors.expenseText
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = AppStrings.todayAmount(appLanguage, rubelFormat.format(todayExpenseSum).replace(",00", "")),
                                            style = MaterialTheme.typography.bodyMedium.tabular(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = AppStrings.limitAmount(appLanguage, rubelFormat.format(dailyBudget.limitAmount).replace(",00", "")),
                                            style = MaterialTheme.typography.bodyMedium.tabular(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Colored progress bar based on warnings
                                    val progressBarColorDaily = if (progressPercentDaily >= dailyBudget.alertThresholdPercent) {
                                        MaterialTheme.colorScheme.error
                                    } else if (progressPercentDaily >= 50) {
                                        uangkuColors.warningText
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(uangkuColors.elevatedSubSurface)
                                    ) {
                                        val targetProgressFractionDaily = if (dailyBudget.limitAmount > 0) {
                                            (todayExpenseSum / dailyBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }
                                        val progressFractionDaily by androidx.compose.animation.core.animateFloatAsState(
                                            targetValue = targetProgressFractionDaily,
                                            animationSpec = MotionTokens.dataProgressSpec,
                                            label = "progressAnim2"
                                        )
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
                                            text = AppStrings.usagePortion(appLanguage, progressPercentDaily),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (progressPercentDaily >= dailyBudget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = AppStrings.alertActiveAt(appLanguage, dailyBudget.alertThresholdPercent),
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
                        text = AppStrings.categoryBudgetTitle(appLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val categoryBudgets = budgets.filter { it.category != "ALL" && it.category != "DAILY" }
                if (categoryBudgets.isEmpty()) {
                    item {
                        Text(
                            text = AppStrings.emptyCategoryBudgetDesc(appLanguage),
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
                                .animateItem()
                                .fillMaxWidth()
                                .animateContentSize()
                                .testTag("category_budget_${budget.category}"),
                            colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, uangkuColors.cardBorder)
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
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = AppStrings.translateCategory(budget.category, appLanguage),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteBudget(budget) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = AppStrings.delete(appLanguage),
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
                                        text = AppStrings.usedAmount(appLanguage, rubelFormat.format(spentOnCategory).replace(",00", "")),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppStrings.remainingAmount(appLanguage, rubelFormat.format((budget.limitAmount - spentOnCategory).coerceAtLeast(0.0)).replace(",00", "")),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (spentOnCategory > budget.limitAmount) MaterialTheme.colorScheme.error else uangkuColors.incomeText,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val targetProgressFraction = if (budget.limitAmount > 0) {
                                    (spentOnCategory / budget.limitAmount).toFloat().coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                                val progressFraction by androidx.compose.animation.core.animateFloatAsState(
                                    targetValue = targetProgressFraction, 
                                    animationSpec = MotionTokens.dataProgressSpec,
                                    label = "progressAnim3"
                                )
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
                                        text = AppStrings.percentUsed(appLanguage, percent),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (percent >= budget.alertThresholdPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = AppStrings.limitAmount(appLanguage, rubelFormat.format(budget.limitAmount).replace(",00", "")),
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
                                .animateContentSize()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, uangkuColors.cardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
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
                                    text = AppStrings.emptySavingGoalTitle(appLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = AppStrings.emptySavingGoalDesc(appLanguage),
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
                            modifier = Modifier.animateItem(),
                            goal = goal,
                            rubelFormat = { rubelFormat.format(it) },
                            currentBalance = totalBalance,
                            appLanguage = appLanguage,
                            onEdit = { editingSavingGoal = goal },
                            onDelete = { viewModel.deleteSavingGoal(goal) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.navigationBarsPadding().height(8.dp))
            }
        }
        }
        }
        }
    }

    // Modal dialogue limit set up
    if (showAddBudgetDialog) {
        val hasMonthly = budgets.any { it.category == "ALL" }
        val hasDaily = budgets.any { it.category == "DAILY" }

        val catsOptions = listOfNotNull(
            if (!hasMonthly) "ALL" to AppStrings.totalMonthlySpending(appLanguage) else null,
            if (!hasDaily) "DAILY" to AppStrings.totalDailySpending(appLanguage) else null,
            "Makanan" to AppStrings.translateCategory("Makanan", appLanguage),
            "Belanja" to AppStrings.translateCategory("Belanja", appLanguage),
            "Transportasi" to AppStrings.translateCategory("Transportasi", appLanguage),
            "Sewa" to AppStrings.translateCategory("Sewa", appLanguage),
            "Lainnya" to AppStrings.translateCategory("Lainnya", appLanguage)
        )

        val initialChoice = remember(catsOptions) { catsOptions.firstOrNull()?.first ?: "Makanan" }
        var categoryChoice by remember(catsOptions) { mutableStateOf(initialChoice) }
        var limitInput by remember { mutableStateOf("") }
        var alertPercentInput by remember { mutableStateOf("80") }

        Dialog(onDismissRequest = { showAddBudgetDialog = false }) {
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
                        text = AppStrings.setBudgetTitle(appLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = AppStrings.selectCategoryTarget(appLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                                    .heightIn(min = 48.dp)
                                    .animateContentSize()
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { categoryChoice = valStr }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = choiceLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
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
                        label = { Text(AppStrings.budgetLimitLabel(appLanguage, selectedCurrency)) },
                        placeholder = { Text(AppStrings.budgetAmountPlaceholder(appLanguage, selectedCurrency == "USD")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = alertPercentInput,
                        onValueChange = { alertPercentInput = it },
                        label = { Text(AppStrings.alertTriggerLabel(appLanguage)) },
                        placeholder = { Text("default: 80") },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddBudgetDialog = false }) {
                            Text(AppStrings.cancel(appLanguage))
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
                            Text(AppStrings.apply(appLanguage))
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
                        text = AppStrings.newSavingGoalTitle(appLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text(AppStrings.goalNameLabel(appLanguage)) },
                        placeholder = { Text(AppStrings.goalNamePlaceholder(appLanguage)) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = targetAmtInput,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                targetAmtInput = clean
                            }
                        },
                        label = { Text(AppStrings.targetAmountLabel(appLanguage, selectedCurrency)) },
                        placeholder = { Text(AppStrings.savingTargetPlaceholder(appLanguage, selectedCurrency == "USD")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = targetDateInput,
                        onValueChange = { targetDateInput = it },
                        label = { Text(AppStrings.targetDateLabel(appLanguage)) },
                        placeholder = { Text(AppStrings.targetDatePlaceholder(appLanguage)) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        text = AppStrings.selectCategory(appLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        catsOptions.forEach { cat ->
                            val isSelected = categoryChoice == cat
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { categoryChoice = cat }
                            ) {
                                Text(
                                    text = AppStrings.translateCategory(cat, appLanguage),
                                    style = MaterialTheme.typography.labelLarge,
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
                        TextButton(onClick = { showAddSavingGoalDialog = false }) {
                            Text(AppStrings.cancel(appLanguage))
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
                                        targetDate = targetDateInput.ifEmpty { AppStrings.targetDateSoon(appLanguage) },
                                        category = categoryChoice
                                    )
                                    showAddSavingGoalDialog = false
                                }
                            },
                            enabled = goalTitle.isNotEmpty() && targetAmtInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text(AppStrings.createGoal(appLanguage))
                        }
                    }
                }
            }
        }
    }

    if (editingSavingGoal != null) {
        val currentGoal = editingSavingGoal!!
        var goalTitle by remember { mutableStateOf(currentGoal.title) }
        var targetAmtInput by remember { mutableStateOf(currentGoal.targetAmount.toLong().toString()) }
        var targetDateInput by remember { mutableStateOf(currentGoal.targetDate) }
        var categoryChoice by remember { mutableStateOf(currentGoal.category) }

        val catsOptions = listOf("Gawai", "Liburan", "Dana Darurat", "Kendaraan", "Lainnya")

        Dialog(onDismissRequest = { editingSavingGoal = null }) {
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
                        text = AppStrings.editSavingGoalTitle(appLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text(AppStrings.goalNameLabel(appLanguage)) },
                        placeholder = { Text(AppStrings.goalNamePlaceholder(appLanguage)) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = targetAmtInput,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 15) {
                                targetAmtInput = clean
                            }
                        },
                        label = { Text(AppStrings.targetAmountLabel(appLanguage, selectedCurrency)) },
                        placeholder = { Text(AppStrings.savingTargetPlaceholder(appLanguage, selectedCurrency == "USD")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RupiahVisualTransformation(isUsd = selectedCurrency == "USD"),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = targetDateInput,
                        onValueChange = { targetDateInput = it },
                        label = { Text(AppStrings.targetDateLabel(appLanguage)) },
                        placeholder = { Text(AppStrings.targetDatePlaceholder(appLanguage)) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        text = AppStrings.selectCategory(appLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        catsOptions.forEach { cat ->
                            val isSelected = categoryChoice == cat
                            ExpressiveMorphChip(
                                selected = isSelected,
                                onClick = { categoryChoice = cat }
                            ) {
                                Text(
                                    text = AppStrings.translateCategory(cat, appLanguage),
                                    style = MaterialTheme.typography.labelLarge,
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
                        TextButton(onClick = { editingSavingGoal = null }) {
                            Text(AppStrings.cancel(appLanguage))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val cleanAmt = targetAmtInput.filter { it.isDigit() }
                                val targetAmt = cleanAmt.toDoubleOrNull() ?: 0.0
                                if (goalTitle.isNotEmpty() && targetAmt > 0) {
                                    val updatedGoal = currentGoal.copy(
                                        title = goalTitle,
                                        targetAmount = targetAmt,
                                        targetDate = targetDateInput.ifEmpty { AppStrings.targetDateSoon(appLanguage) },
                                        category = categoryChoice
                                    )
                                    viewModel.updateSavingGoal(updatedGoal)
                                    editingSavingGoal = null
                                }
                            },
                            enabled = goalTitle.isNotEmpty() && targetAmtInput.filter { it.isDigit() }.isNotEmpty()
                        ) {
                            Text(AppStrings.saveChanges(appLanguage))
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
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = AppStrings.congratsTitle(appLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.icon, fontSize = 56.sp)
                    }
                    
                    Text(
                        text = AppStrings.badgeUnlocked(appLanguage, badge.name),
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
                        Text(AppStrings.claimAndShare(appLanguage), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingGoalCard(
    modifier: Modifier = Modifier,
    goal: com.example.data.model.SavingGoal,
    rubelFormat: (Double) -> String,
    currentBalance: Double,
    appLanguage: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val targetProgressFraction = if (goal.targetAmount > 0) {
        (currentBalance / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressFraction by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetProgressFraction,
        animationSpec = MotionTokens.dataProgressSpec,
        label = "progressAnim4"
    )
    val progressPercent = (progressFraction * 100).toInt()

    val uangkuColors = LocalUangkuColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("saving_goal_card_${goal.id}"),
        colors = CardDefaults.cardColors(
            containerColor = uangkuColors.cardSurface
        ),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, uangkuColors.cardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                            text = "${AppStrings.category(appLanguage)}: ${AppStrings.translateCategory(goal.category, appLanguage)} • ${AppStrings.target(appLanguage)}: ${goal.targetDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = AppStrings.edit(appLanguage),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = AppStrings.delete(appLanguage),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = AppStrings.collectedAmount(appLanguage, rubelFormat(currentBalance).replace(",00", "")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = AppStrings.targetAmountGoal(appLanguage, rubelFormat(goal.targetAmount).replace(",00", "")),
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
                    text = AppStrings.autoDetectBalance(appLanguage, progressPercent),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                val sisa = (goal.targetAmount - currentBalance).coerceAtLeast(0.0)
                if (sisa > 0) {
                    Text(
                        text = AppStrings.remainingAmount(appLanguage, rubelFormat(sisa).replace(",00", "")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = AppStrings.goalAchieved(appLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = uangkuColors.incomeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

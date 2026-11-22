package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.MotionTokens
import com.example.ui.util.AppStrings

@Composable
fun LockerSpeedDialMenu(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectExpense: () -> Unit,
    onSelectIncome: () -> Unit,
    onSelectScanReceipt: () -> Unit,
    modifier: Modifier = Modifier,
    lang: String = "id"
) {
    val uangkuColors = LocalUangkuColors.current
    val haptic = LocalHapticFeedback.current

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = MotionTokens.spatialSpringFloat,
        label = "plus_rotation"
    )

    val fabCorner by animateDpAsState(
        targetValue = if (isExpanded) ExpressiveShapeTokens.FabExpandedCorner else ExpressiveShapeTokens.FabRestingCorner,
        animationSpec = MotionTokens.spatialFastDp,
        label = "fab_corner_morph"
    )

    val fabContainerColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
        animationSpec = MotionTokens.colorTransition,
        label = "fab_container_color"
    )

    val fabContentColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary,
        animationSpec = MotionTokens.colorTransition,
        label = "fab_content_color"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Expandable M3 Expressive Locker Items
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(MotionTokens.effectsFloat) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                    )
                ),
            exit = fadeOut(MotionTokens.fastEffectsFloat) +
                slideOutVertically(
                    targetOffsetY = { it / 3 },
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
                    )
                ),
            modifier = Modifier.padding(bottom = 72.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 2.dp)
            ) {
                // Item 1: Scan Receipt (Warm Terracotta/Tertiary accent)
                LockerMenuItemCard(
                    title = AppStrings.speedDialScanReceipt(lang),
                    subtitle = AppStrings.speedDialScanReceiptDesc(lang),
                    icon = Icons.Default.DocumentScanner,
                    badgeContainer = MaterialTheme.colorScheme.tertiaryContainer,
                    badgeContent = MaterialTheme.colorScheme.onTertiaryContainer,
                    testTag = "speed_dial_scan_receipt",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleExpand()
                        onSelectScanReceipt()
                    }
                )

                // Item 2: Income (Emerald Income semantic)
                LockerMenuItemCard(
                    title = AppStrings.income(lang),
                    subtitle = AppStrings.speedDialIncomeDesc(lang),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    badgeContainer = uangkuColors.incomeContainer,
                    badgeContent = uangkuColors.onIncomeContainer,
                    testTag = "speed_dial_income",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleExpand()
                        onSelectIncome()
                    }
                )

                // Item 3: Expense (Rose Expense semantic)
                LockerMenuItemCard(
                    title = AppStrings.expense(lang),
                    subtitle = AppStrings.speedDialExpenseDesc(lang),
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    badgeContainer = uangkuColors.expenseContainer,
                    badgeContent = uangkuColors.onExpenseContainer,
                    testTag = "speed_dial_expense",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleExpand()
                        onSelectExpense()
                    }
                )
            }
        }

        // Main Shape-Morphing Expressive FAB
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggleExpand()
            },
            containerColor = fabContainerColor,
            contentColor = fabContentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp
            ),
            shape = RoundedCornerShape(fabCorner),
            modifier = Modifier
                .testTag("locker_speed_dial_main_fab")
                .size(58.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isExpanded) AppStrings.speedDialCloseContentDesc(lang) else AppStrings.speedDialOpenContentDesc(lang),
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        rotationZ = rotationAngle
                    }
            )
        }
    }
}

@Composable
private fun LockerMenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeContainer: Color,
    badgeContent: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .testTag(testTag)
            .heightIn(min = 52.dp)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(badgeContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = badgeContent,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


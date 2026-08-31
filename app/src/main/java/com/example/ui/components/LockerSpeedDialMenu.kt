package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LockerSpeedDialMenu(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectExpense: () -> Unit,
    onSelectIncome: () -> Unit,
    onSelectScanReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "plus_rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Expandable Locker Items
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                    ),
            exit = fadeOut(spring(stiffness = Spring.StiffnessHigh)) +
                    slideOutVertically(targetOffsetY = { it / 3 }),
            modifier = Modifier.padding(bottom = 72.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                // Item 1: Scan Receipt
                LockerMenuItemCard(
                    title = "Scan Receipt",
                    subtitle = "Ekstrak otomatis nota belanja via OCR",
                    icon = Icons.Default.DocumentScanner,
                    gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                    testTag = "speed_dial_scan_receipt",
                    onClick = {
                        onToggleExpand()
                        onSelectScanReceipt()
                    }
                )

                // Item 2: Pemasukan (Income)
                LockerMenuItemCard(
                    title = "Pemasukan",
                    subtitle = "Catat gaji, dividen & omset masuk",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981)),
                    testTag = "speed_dial_income",
                    onClick = {
                        onToggleExpand()
                        onSelectIncome()
                    }
                )

                // Item 3: Pengeluaran (Expense)
                LockerMenuItemCard(
                    title = "Pengeluaran",
                    subtitle = "Catat belanja & biaya harian",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    gradientColors = listOf(Color(0xFFDC2626), Color(0xFFEF4444)),
                    testTag = "speed_dial_expense",
                    onClick = {
                        onToggleExpand()
                        onSelectExpense()
                    }
                )
            }
        }

        // Main Plus FAB with Rotating Animation
        FloatingActionButton(
            onClick = onToggleExpand,
            containerColor = if (isExpanded) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
            contentColor = if (isExpanded) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp),
            shape = CircleShape,
            modifier = Modifier
                .testTag("locker_speed_dial_main_fab")
                .size(58.dp)
                .shadow(12.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isExpanded) "Tutup Menu Opsi" else "Buka Opsi Transaksi",
                modifier = Modifier
                    .size(28.dp)
                    .rotate(rotationAngle)
            )
        }
    }
}

@Composable
private fun LockerMenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Brush.linearGradient(gradientColors), CircleShape)
                .shadow(6.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

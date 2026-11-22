package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.MotionTokens

/**
 * Material 3 Expressive pill/chip with organic spring shape morphing
 * between resting squircle, selected pill, and pressed contour.
 */
@Composable
fun ExpressiveMorphChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    restingCorner: Dp = ExpressiveShapeTokens.PillRestingCorner,
    selectedCorner: Dp = ExpressiveShapeTokens.PillSelectedCorner,
    pressedCorner: Dp = ExpressiveShapeTokens.PillPressedCorner,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val cornerRadius by animateDpAsState(
        targetValue = when {
            isPressed -> pressedCorner
            selected -> selectedCorner
            else -> restingCorner
        },
        animationSpec = MotionTokens.spatialSpringDp,
        label = "chip_shape_morph"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.spatialSnappyFloat,
        label = "chip_scale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (selected) selectedContainerColor else unselectedContainerColor,
        animationSpec = MotionTokens.colorTransition,
        label = "chip_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) selectedContentColor else unselectedContentColor,
        animationSpec = MotionTokens.colorTransition,
        label = "chip_fg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = MotionTokens.colorTransition,
        label = "chip_border"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = if (!selected) BorderStroke(1.dp, borderColor) else null,
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                role = Role.Tab,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * Data item for ExpressiveSegmentedSelector.
 */
data class ExpressiveSegmentOption(
    val key: String,
    val label: String,
    val testTag: String? = null
)

/**
 * Material 3 Expressive segmented capsule switcher with shape-morphing options
 * and organic spring color/corner transitions.
 */
@Composable
fun ExpressiveSegmentedSelector(
    options: List<ExpressiveSegmentOption>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    onActiveColor: Color = MaterialTheme.colorScheme.onPrimary,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option.key == selectedKey
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val cornerRadius by animateDpAsState(
                targetValue = when {
                    isPressed -> 12.dp
                    isSelected -> 24.dp
                    else -> 14.dp
                },
                animationSpec = MotionTokens.spatialSpringDp,
                label = "seg_corner_${option.key}"
            )

            val bg by animateColorAsState(
                targetValue = if (isSelected) activeColor else Color.Transparent,
                animationSpec = MotionTokens.colorTransition,
                label = "seg_bg_${option.key}"
            )

            val fg by animateColorAsState(
                targetValue = if (isSelected) onActiveColor else inactiveContentColor,
                animationSpec = MotionTokens.colorTransition,
                label = "seg_fg_${option.key}"
            )

            val shape = RoundedCornerShape(cornerRadius)
            val baseModifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape)
                .background(bg)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    role = Role.Tab,
                    onClick = {
                        if (!isSelected) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelect(option.key)
                        }
                    }
                )
            val taggedModifier = if (option.testTag != null) {
                baseModifier.testTag(option.testTag)
            } else {
                baseModifier
            }

            Box(
                modifier = taggedModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = fg,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Expressive Shape Scale
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Shape corner radii used for M3 Expressive shape morphing and nested Bento cards.
 */
object ExpressiveShapeTokens {
    val HeroCorner = 32.dp
    val BentoCorner = 28.dp
    val BentoInnerCorner = 16.dp
    val RowCardCorner = 22.dp
    val PillRestingCorner = 14.dp
    val PillSelectedCorner = 26.dp
    val PillPressedCorner = 10.dp
    val FabRestingCorner = 20.dp
    val FabExpandedCorner = 30.dp
    val KeypadRestingCorner = 32.dp
    val KeypadPressedCorner = 16.dp
}

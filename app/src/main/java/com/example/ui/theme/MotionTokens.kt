package com.example.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Material 3 Expressive motion tokens adhering to MOTION.md and animate-compose standards.
 * - T-002: Token-based specs, no bare tween/spring at call sites.
 * - T-004: Expressive spatial springs stay off elements read as data (no bounce on numbers/charts).
 * - T-010: Exits use fewer properties / fast tier, avoiding lingering dismissals.
 * - T-011: Top-level destinations use cross-fade rather than directional lateral sliding.
 */
object MotionTokens {
    // Spatial Specs (Containers, Sheets, Shape Morphing, Floating Toolbar)
    val spatialSpringFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val spatialSpringDp: FiniteAnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val spatialFastDp: FiniteAnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val spatialSnappyFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Effects Specs (Alpha, Fades, Tonal Shifts) - DampingRatioNoBouncy = 1.0f
    val effectsFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val fastEffectsFloat: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val colorTransition: FiniteAnimationSpec<Color> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Data-calm specs (Numbers, Progress Bars, Charts) - T-004: strictly non-bouncy
    val dataProgressSpec: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Top-Level Screen Transition (T-011: Unordered peer destinations crossfade)
    val topLevelTransition: ContentTransform =
        fadeIn(animationSpec = effectsFloat) togetherWith fadeOut(animationSpec = fastEffectsFloat)

    // Modal / Sheet Dialog Transitions (T-010: Gentle spatial enter, effects-only exit)
    val modalEnter: EnterTransition =
        fadeIn(animationSpec = effectsFloat) + scaleIn(initialScale = 0.96f, animationSpec = spatialSpringFloat)
    val modalExit: ExitTransition = fadeOut(animationSpec = fastEffectsFloat)
}

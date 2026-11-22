package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class UangkuSemanticColors(
    val isDark: Boolean,
    val isDynamic: Boolean,
    val incomeText: Color,
    val incomeContainer: Color,
    val incomeContainerAlt: Color,
    val onIncomeContainer: Color,
    val incomeBorder: Color,
    val expenseText: Color,
    val expenseContainer: Color,
    val expenseContainerAlt: Color,
    val onExpenseContainer: Color,
    val expenseBorder: Color,
    val warningText: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val aiAccent: Color,
    val aiSurface: Color,
    val aiContainer: Color,
    val onAiContainer: Color,
    val aiBorder: Color,
    val cardSurface: Color,
    val elevatedSubSurface: Color,
    val cardBorder: Color,
    val subtleText: Color,
    val gradientBackgroundStart: Color,
    val gradientBackgroundEnd: Color,
    val heroStart: Color,
    val heroEnd: Color,
    val onHero: Color,
    val onHeroVariant: Color
)

val LightUangkuColors = UangkuSemanticColors(
    isDark = false,
    isDynamic = false,
    incomeText = IncomeGreen,
    incomeContainer = IncomeGreenContainerLight,
    incomeContainerAlt = IncomeGreenContainerAltLight,
    onIncomeContainer = OnIncomeGreenLight,
    incomeBorder = IncomeBorderLight,
    expenseText = ExpenseRose,
    expenseContainer = ExpenseRoseContainerLight,
    expenseContainerAlt = ExpenseRoseContainerAltLight,
    onExpenseContainer = OnExpenseRoseLight,
    expenseBorder = ExpenseBorderLight,
    warningText = WarningAmberLight,
    warningContainer = WarningContainerLight,
    onWarningContainer = OnWarningContainerLight,
    aiAccent = AiAccentLight,
    aiSurface = SurfaceContainerLowestLight,
    aiContainer = SurfaceContainerLowLight,
    onAiContainer = OnSurfaceLight,
    aiBorder = OutlineVariantLight,
    cardSurface = SurfaceContainerLowestLight,
    elevatedSubSurface = SurfaceContainerLowLight,
    cardBorder = OutlineVariantLight,
    subtleText = OnSurfaceVariantLight,
    gradientBackgroundStart = BackgroundLight,
    gradientBackgroundEnd = BackgroundLight,
    heroStart = SurfaceContainerLowestLight,
    heroEnd = SurfaceContainerLowestLight,
    onHero = OnSurfaceLight,
    onHeroVariant = OnSurfaceVariantLight
)

val DarkUangkuColors = UangkuSemanticColors(
    isDark = true,
    isDynamic = false,
    incomeText = IncomeGreenDark,
    incomeContainer = IncomeGreenContainerDark,
    incomeContainerAlt = IncomeGreenContainerAltDark,
    onIncomeContainer = OnIncomeGreenDark,
    incomeBorder = IncomeBorderDark,
    expenseText = ExpenseRoseDark,
    expenseContainer = ExpenseRoseContainerDark,
    expenseContainerAlt = ExpenseRoseContainerAltDark,
    onExpenseContainer = OnExpenseRoseDark,
    expenseBorder = ExpenseBorderDark,
    warningText = WarningAmberDark,
    warningContainer = WarningContainerDark,
    onWarningContainer = OnWarningContainerDark,
    aiAccent = AiAccentDark,
    aiSurface = SurfaceContainerDark,
    aiContainer = SurfaceContainerDark,
    onAiContainer = OnSurfaceDark,
    aiBorder = OutlineVariantDark,
    cardSurface = SurfaceContainerLowDark,
    elevatedSubSurface = SurfaceContainerHighDark,
    cardBorder = OutlineVariantDark,
    subtleText = OnSurfaceVariantDark,
    gradientBackgroundStart = BackgroundDark,
    gradientBackgroundEnd = BackgroundDark,
    heroStart = SurfaceContainerHighDark,
    heroEnd = SurfaceContainerHighDark,
    onHero = OnSurfaceDark,
    onHeroVariant = OnSurfaceVariantDark
)

val LocalUangkuColors = staticCompositionLocalOf { LightUangkuColors }

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = OnEmeraldPrimaryDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = WarmAmberSecondaryDark,
    onSecondary = OnWarmAmberSecondaryDark,
    secondaryContainer = WarmAmberContainerDark,
    onSecondaryContainer = OnWarmAmberContainerDark,
    tertiary = WarmCoralTertiaryDark,
    onTertiary = OnWarmCoralTertiaryDark,
    tertiaryContainer = WarmCoralContainerDark,
    onTertiaryContainer = OnWarmCoralContainerDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerHighDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ExpenseRoseDark,
    onError = Color(0xFF4C0519),
    errorContainer = ExpenseRoseContainerDark,
    onErrorContainer = OnExpenseRoseDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = OnEmeraldPrimaryLight,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = WarmAmberSecondaryLight,
    onSecondary = OnWarmAmberSecondaryLight,
    secondaryContainer = WarmAmberContainerLight,
    onSecondaryContainer = OnWarmAmberContainerLight,
    tertiary = WarmTerracottaTertiaryLight,
    onTertiary = OnWarmTerracottaTertiaryLight,
    tertiaryContainer = WarmTerracottaContainerLight,
    onTertiaryContainer = OnWarmTerracottaContainerLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerHighLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    error = ExpenseRose,
    onError = Color.White,
    errorContainer = ExpenseRoseContainerLight,
    onErrorContainer = OnExpenseRoseLight
)

@Composable
fun animateColorScheme(target: ColorScheme): ColorScheme {
    val spec = MotionTokens.colorTransition
    val primary by animateColorAsState(target.primary, spec, label = "primary")
    val onPrimary by animateColorAsState(target.onPrimary, spec, label = "onPrimary")
    val primaryContainer by animateColorAsState(target.primaryContainer, spec, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(target.onPrimaryContainer, spec, label = "onPrimaryContainer")
    val secondary by animateColorAsState(target.secondary, spec, label = "secondary")
    val onSecondary by animateColorAsState(target.onSecondary, spec, label = "onSecondary")
    val secondaryContainer by animateColorAsState(target.secondaryContainer, spec, label = "secondaryContainer")
    val onSecondaryContainer by animateColorAsState(target.onSecondaryContainer, spec, label = "onSecondaryContainer")
    val tertiary by animateColorAsState(target.tertiary, spec, label = "tertiary")
    val onTertiary by animateColorAsState(target.onTertiary, spec, label = "onTertiary")
    val tertiaryContainer by animateColorAsState(target.tertiaryContainer, spec, label = "tertiaryContainer")
    val onTertiaryContainer by animateColorAsState(target.onTertiaryContainer, spec, label = "onTertiaryContainer")
    val background by animateColorAsState(target.background, spec, label = "background")
    val onBackground by animateColorAsState(target.onBackground, spec, label = "onBackground")
    val surface by animateColorAsState(target.surface, spec, label = "surface")
    val onSurface by animateColorAsState(target.onSurface, spec, label = "onSurface")
    val surfaceVariant by animateColorAsState(target.surfaceVariant, spec, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(target.onSurfaceVariant, spec, label = "onSurfaceVariant")
    val surfaceContainerLowest by animateColorAsState(target.surfaceContainerLowest, spec, label = "scLowest")
    val surfaceContainerLow by animateColorAsState(target.surfaceContainerLow, spec, label = "scLow")
    val surfaceContainer by animateColorAsState(target.surfaceContainer, spec, label = "sc")
    val surfaceContainerHigh by animateColorAsState(target.surfaceContainerHigh, spec, label = "scHigh")
    val surfaceContainerHighest by animateColorAsState(target.surfaceContainerHighest, spec, label = "scHighest")
    val outline by animateColorAsState(target.outline, spec, label = "outline")
    val outlineVariant by animateColorAsState(target.outlineVariant, spec, label = "outlineVariant")
    val error by animateColorAsState(target.error, spec, label = "error")
    val onError by animateColorAsState(target.onError, spec, label = "onError")
    val errorContainer by animateColorAsState(target.errorContainer, spec, label = "errorContainer")
    val onErrorContainer by animateColorAsState(target.onErrorContainer, spec, label = "onErrorContainer")

    return target.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceContainerLowest = surfaceContainerLowest,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        outline = outline,
        outlineVariant = outlineVariant,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDynamicActive = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val neutralBaseScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val baseColorScheme = if (isDynamicActive) {
        val dynamicScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        // Keep all surfaces, backgrounds, and borders clean neutral slate matte.
        // Only apply wallpaper accent to active primary/interactive roles.
        neutralBaseScheme.copy(
            primary = dynamicScheme.primary,
            onPrimary = dynamicScheme.onPrimary,
            primaryContainer = dynamicScheme.primaryContainer,
            onPrimaryContainer = dynamicScheme.onPrimaryContainer,
            secondary = dynamicScheme.secondary,
            onSecondary = dynamicScheme.onSecondary
        )
    } else {
        neutralBaseScheme
    }

    val animatedColorScheme = animateColorScheme(baseColorScheme)
    val baseSemantic = if (darkTheme) DarkUangkuColors else LightUangkuColors
    val semanticColors = if (isDynamicActive) {
        baseSemantic.copy(isDynamic = true)
    } else {
        baseSemantic
    }

    CompositionLocalProvider(LocalUangkuColors provides semanticColors) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = Typography,
            shapes = Shapes
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = animatedColorScheme.background,
                contentColor = animatedColorScheme.onBackground,
                content = content
            )
        }
    }
}

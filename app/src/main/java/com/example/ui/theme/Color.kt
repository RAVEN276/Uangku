package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Editorial Neutral Slate & Forest Green Matte Palette (WCAG AA Verified)

// --- Light Mode Brand & Editorial Accent Roles ---
val EmeraldPrimaryLight = Color(0xFF156A46)          // Editorial Forest Green (~6.5:1 on white)
val OnEmeraldPrimaryLight = Color(0xFFFFFFFF)
val EmeraldContainerLight = Color(0xFFD8EFE3)
val OnEmeraldContainerLight = Color(0xFF0B3824)

val WarmAmberSecondaryLight = Color(0xFF53635C)      // Warm Slate Olive
val OnWarmAmberSecondaryLight = Color(0xFFFFFFFF)
val WarmAmberContainerLight = Color(0xFFE4EAE7)
val OnWarmAmberContainerLight = Color(0xFF1C2521)

val WarmTerracottaTertiaryLight = Color(0xFF8B4D36)  // Restrained Terracotta
val OnWarmTerracottaTertiaryLight = Color(0xFFFFFFFF)
val WarmTerracottaContainerLight = Color(0xFFF4E4DC)
val OnWarmTerracottaContainerLight = Color(0xFF3A190D)

// --- Light Mode Matte Slate Surface Hierarchy ---
val BackgroundLight = Color(0xFFF6F8F7)
val SurfaceLight = Color(0xFFF6F8F7)
val SurfaceDimLight = Color(0xFFDCE2DF)
val SurfaceBrightLight = Color(0xFFFFFFFF)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFEFF3F1)
val SurfaceContainerLight = Color(0xFFE8EDEA)
val SurfaceContainerHighLight = Color(0xFFE0E6E3)
val SurfaceContainerHighestLight = Color(0xFFD6DDD9)
val OnSurfaceLight = Color(0xFF131816)
val OnSurfaceVariantLight = Color(0xFF44524C)
val OutlineLight = Color(0xFF707E78)
val OutlineVariantLight = Color(0xFFC9D2CE)

// --- Dark Mode Brand & Editorial Accent Roles ---
val EmeraldPrimaryDark = Color(0xFF56C292)           // Editorial Sage Emerald
val OnEmeraldPrimaryDark = Color(0xFF06321F)
val EmeraldContainerDark = Color(0xFF1A3A2B)
val OnEmeraldContainerDark = Color(0xFFBCEAD3)

val WarmAmberSecondaryDark = Color(0xFF9FB3A9)
val OnWarmAmberSecondaryDark = Color(0xFF16231D)
val WarmAmberContainerDark = Color(0xFF232B2E)
val OnWarmAmberContainerDark = Color(0xFFDCE4E0)

val WarmCoralTertiaryDark = Color(0xFFD89D84)
val OnWarmCoralTertiaryDark = Color(0xFF3B1B0E)
val WarmCoralContainerDark = Color(0xFF3A2720)
val OnWarmCoralContainerDark = Color(0xFFF6D7C8)

// --- Dark Mode Matte Slate Surface Hierarchy ---
val BackgroundDark = Color(0xFF0F1315)
val SurfaceDark = Color(0xFF0F1315)
val SurfaceDimDark = Color(0xFF0B0E10)
val SurfaceBrightDark = Color(0xFF2B3339)
val SurfaceContainerLowestDark = Color(0xFF0B0E10)
val SurfaceContainerLowDark = Color(0xFF161B1E)
val SurfaceContainerDark = Color(0xFF1C2226)
val SurfaceContainerHighDark = Color(0xFF232A2F)
val SurfaceContainerHighestDark = Color(0xFF2B3339)
val OnSurfaceDark = Color(0xFFE6ECE9)
val OnSurfaceVariantDark = Color(0xFFA7B4AE)
val OutlineDark = Color(0xFF637178)
val OutlineVariantDark = Color(0xFF2D363C)

// --- Financial Semantic Colors (Matte, High-Contrast, Zero Neon) ---
val IncomeGreen = Color(0xFF136943)
val IncomeGreenDark = Color(0xFF6EE7B7)
val IncomeGreenContainerLight = Color(0xFFECFDF3)
val IncomeGreenContainerAltLight = Color(0xFFECFDF3)
val OnIncomeGreenLight = Color(0xFF0B3D27)
val IncomeGreenContainerDark = Color(0xFF14261E)
val IncomeGreenContainerAltDark = Color(0xFF14261E)
val OnIncomeGreenDark = Color(0xFFD1FAE5)
val IncomeBorderLight = Color(0xFFBBE7CE)
val IncomeBorderDark = Color(0xFF274A3A)

val ExpenseRose = Color(0xFFB42334)
val ExpenseRoseDark = Color(0xFFFCA5A5)
val ExpenseRoseContainerLight = Color(0xFFFEF2F2)
val ExpenseRoseContainerAltLight = Color(0xFFFEF2F2)
val OnExpenseRoseLight = Color(0xFF6B1520)
val ExpenseRoseContainerDark = Color(0xFF29171A)
val ExpenseRoseContainerAltDark = Color(0xFF29171A)
val OnExpenseRoseDark = Color(0xFFFEE2E2)
val ExpenseBorderLight = Color(0xFFF6C8CB)
val ExpenseBorderDark = Color(0xFF4D292F)

val WarningAmberLight = Color(0xFF9A5B13)
val WarningAmberDark = Color(0xFFFBBF24)
val WarningContainerLight = Color(0xFFFFF4E0)
val OnWarningContainerLight = Color(0xFF4E2B04)
val WarningContainerDark = Color(0xFF2D2112)
val OnWarningContainerDark = Color(0xFFFDE68A)

val AiAccentLight = WarmTerracottaTertiaryLight
val AiAccentDark = WarmCoralTertiaryDark
val AiContainerLight = WarmTerracottaContainerLight
val OnAiContainerLight = OnWarmTerracottaContainerLight
val AiContainerDark = WarmCoralContainerDark
val OnAiContainerDark = OnWarmCoralContainerDark

// Backwards-compatible aliases if referenced elsewhere
val EmeraldPrimary = EmeraldPrimaryLight
val EmeraldPrimaryLightAlias = EmeraldPrimaryLight
val EmeraldSecondary = WarmAmberSecondaryLight
val SurfaceCardDark = SurfaceContainerLowDark

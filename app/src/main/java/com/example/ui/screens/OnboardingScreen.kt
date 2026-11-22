package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FinanceViewModel
import com.example.ui.components.ExpressiveSegmentOption
import com.example.ui.components.ExpressiveSegmentedSelector
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.MotionTokens

@Composable
fun OnboardingScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    var step by remember { mutableStateOf(0) }

    // Step 1: Username State
    var userNameInput by remember { mutableStateOf("") }

    // Step 2: Security States
    var pinEnabled by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }

    val uangkuColors = LocalUangkuColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Bento Card Container
        Box(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(ExpressiveShapeTokens.HeroCorner))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(ExpressiveShapeTokens.HeroCorner)
                )
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Step Progress & Language Switcher Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated Step Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (idx in 0..2) {
                            val active = idx == step
                            val pillWidth by animateDpAsState(
                                targetValue = if (active) 28.dp else 10.dp,
                                animationSpec = MotionTokens.spatialSpringDp,
                                label = "step_dot_w_$idx"
                            )
                            val pillColor by animateColorAsState(
                                targetValue = if (idx <= step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                animationSpec = MotionTokens.colorTransition,
                                label = "step_dot_c_$idx"
                            )
                            Box(
                                modifier = Modifier
                                    .width(pillWidth)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(pillColor)
                            )
                        }
                    }

                    Box(modifier = Modifier.width(136.dp)) {
                        ExpressiveSegmentedSelector(
                            options = listOf(
                                ExpressiveSegmentOption(key = "id", label = "ID"),
                                ExpressiveSegmentOption(key = "en", label = "EN")
                            ),
                            selectedKey = if (appLanguage.equals("en", ignoreCase = true)) "en" else "id",
                            onSelect = { viewModel.setLanguage(it) }
                        )
                    }
                }

                // STEP 0: WELCOME INTRO
                AnimatedVisibility(
                    visible = step == 0,
                    enter = fadeIn(MotionTokens.effectsFloat) + slideInVertically(initialOffsetY = { 32 }),
                    exit = fadeOut(MotionTokens.fastEffectsFloat) + slideOutVertically(targetOffsetY = { -24 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Hero Icon Bento
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.HeroCorner))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Wallet Icon",
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingWelcomeTitle(appLanguage),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingWelcomeDesc(appLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Feature Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Badge(icon = Icons.Default.Security, text = com.example.ui.util.AppStrings.badgeSecure(appLanguage), themeDark = uangkuColors.isDark)
                            Badge(icon = Icons.Default.Star, text = com.example.ui.util.AppStrings.badgeSmart(appLanguage), themeDark = uangkuColors.isDark)
                            Badge(icon = Icons.Default.CheckCircle, text = com.example.ui.util.AppStrings.badgeOffline(appLanguage), themeDark = uangkuColors.isDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { step = 1 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_start_button"),
                            shape = RoundedCornerShape(ExpressiveShapeTokens.HeroCorner),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingGetStarted(appLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // STEP 1: CONFIGURE USERNAME
                AnimatedVisibility(
                    visible = step == 1,
                    enter = fadeIn(MotionTokens.effectsFloat) + slideInVertically(initialOffsetY = { 32 }),
                    exit = fadeOut(MotionTokens.fastEffectsFloat) + slideOutVertically(targetOffsetY = { -24 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.HeroCorner))
                                .background(uangkuColors.elevatedSubSurface)
                                .border(1.dp, uangkuColors.cardBorder, RoundedCornerShape(ExpressiveShapeTokens.HeroCorner)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Setup",
                                modifier = Modifier.size(46.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingNameTitle(appLanguage),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingNameDesc(appLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            label = { Text(com.example.ui.util.AppStrings.nicknameLabel(appLanguage)) },
                            placeholder = { Text(com.example.ui.util.AppStrings.onboardingNamePlaceholder(appLanguage)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_username_input"),
                            shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                if (userNameInput.isNotBlank()) {
                                    step = 2
                                } else {
                                    Toast.makeText(context, com.example.ui.util.AppStrings.onboardingNameEmptyToast(appLanguage), Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_next_button"),
                            shape = RoundedCornerShape(ExpressiveShapeTokens.HeroCorner),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = userNameInput.isNotBlank()
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingContinue(appLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // STEP 2: CONFIGURE SECURITY
                AnimatedVisibility(
                    visible = step == 2,
                    enter = fadeIn(MotionTokens.effectsFloat) + slideInVertically(initialOffsetY = { 32 }),
                    exit = fadeOut(MotionTokens.fastEffectsFloat) + slideOutVertically(targetOffsetY = { -24 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingSecurityTitle(appLanguage),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingSecurityDesc(appLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Bento Switch Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(ExpressiveShapeTokens.BentoCorner))
                                .padding(20.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "PIN Lock",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = com.example.ui.util.AppStrings.onboardingEnablePin(appLanguage),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = com.example.ui.util.AppStrings.onboardingPinOptional(appLanguage),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = pinEnabled,
                                        onCheckedChange = { pinEnabled = it },
                                        modifier = Modifier.testTag("onboarding_pin_switch")
                                    )
                                }

                                AnimatedVisibility(visible = pinEnabled) {
                                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = pinValue,
                                            onValueChange = { input ->
                                                val clean = input.filter { it.isDigit() }
                                                if (clean.length <= 4) {
                                                    pinValue = clean
                                                }
                                            },
                                            label = { Text(com.example.ui.util.AppStrings.onboardingPinLabel(appLanguage)) },
                                            placeholder = { Text(com.example.ui.util.AppStrings.onboardingPinPlaceholder(appLanguage)) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("onboarding_pin_input"),
                                            shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                            )
                                        )
                                        if (pinValue.length < 4 && pinValue.isNotEmpty()) {
                                            Text(
                                                text = com.example.ui.util.AppStrings.onboardingPinValidation(appLanguage),
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                if (pinEnabled && pinValue.length != 4) {
                                    Toast.makeText(context, com.example.ui.util.AppStrings.onboardingPinIncompleteToast(appLanguage), Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.setOnboardingCompleted(
                                        name = userNameInput.trim(),
                                        pinEnabled = pinEnabled,
                                        pin = pinValue,
                                        biomEnabled = false
                                    )
                                    Toast.makeText(context, com.example.ui.util.AppStrings.onboardingWelcomeToast(appLanguage, userNameInput.trim()), Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_finish_button"),
                            shape = RoundedCornerShape(ExpressiveShapeTokens.HeroCorner),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = !pinEnabled || pinValue.length == 4
                        ) {
                            Text(
                                text = com.example.ui.util.AppStrings.onboardingComplete(appLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, themeDark: Boolean) {
    val uangkuColors = LocalUangkuColors.current
    Row(
        modifier = Modifier
            .background(
                uangkuColors.incomeContainer,
                RoundedCornerShape(ExpressiveShapeTokens.PillRestingCorner)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = uangkuColors.onIncomeContainer, modifier = Modifier.size(14.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = uangkuColors.onIncomeContainer)
    }
}

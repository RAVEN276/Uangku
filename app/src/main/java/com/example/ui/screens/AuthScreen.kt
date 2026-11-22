package com.example.ui.screens

import androidx.biometric.BiometricManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FinanceViewModel
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.MotionTokens
import com.example.ui.theme.tabular

@Composable
fun AuthScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
    onTriggerBiometric: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uangkuColors = LocalUangkuColors.current
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var hasAutoPrompted by rememberSaveable { mutableStateOf(false) }

    val isBiometricSupported = remember(context) {
        try {
            val biometricManager = BiometricManager.from(context)
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            false
        }
    }

    LaunchedEffect(Unit) {
        if (biometricsEnabled && isBiometricSupported && !hasAutoPrompted) {
            hasAutoPrompted = true
            kotlinx.coroutines.delay(500)
            onTriggerBiometric?.invoke()
        }
    }

    LaunchedEffect(pinError) {
        if (pinError) {
            showErrorMessage = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(1000)
            enteredPin = ""
            pinError = false
        }
    }

    val handleDigitInput = { digit: String ->
        if (enteredPin.length < 4 && !pinError) {
            showErrorMessage = false
            enteredPin += digit
            if (enteredPin.length == 4) {
                val success = viewModel.login(enteredPin)
                if (!success) {
                    pinError = true
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 460.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper logo and header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 36.dp)
            ) {
                val badgeBg by animateColorAsState(
                    targetValue = if (showErrorMessage) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    animationSpec = MotionTokens.colorTransition,
                    label = "auth_badge_bg"
                )
                val badgeFg by animateColorAsState(
                    targetValue = if (showErrorMessage) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    animationSpec = MotionTokens.colorTransition,
                    label = "auth_badge_fg"
                )
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(ExpressiveShapeTokens.HeroCorner))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (showErrorMessage) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock Logo",
                        modifier = Modifier.size(36.dp),
                        tint = badgeFg
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = com.example.ui.util.AppStrings.authAppTitle(appLanguage),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = com.example.ui.util.AppStrings.authAppSubtitle(appLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Pin Indicators (animated dots)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val active = i < enteredPin.length
                        val dotColor by animateColorAsState(
                            targetValue = when {
                                pinError -> MaterialTheme.colorScheme.error
                                active -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outlineVariant
                            },
                            animationSpec = MotionTokens.colorTransition,
                            label = "dot_color_$i"
                        )
                        val dotScale by animateFloatAsState(
                            targetValue = if (active || pinError) 1.25f else 1f,
                            animationSpec = MotionTokens.spatialSnappyFloat,
                            label = "dot_scale_$i"
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer {
                                    scaleX = dotScale
                                    scaleY = dotScale
                                }
                                .background(dotColor, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (showErrorMessage) {
                        com.example.ui.util.AppStrings.authPinIncorrect(appLanguage)
                    } else if (enteredPin.isEmpty()) {
                        com.example.ui.util.AppStrings.authEnterPinPrompt(appLanguage)
                    } else {
                        com.example.ui.util.AppStrings.authVerifying(appLanguage)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (showErrorMessage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            // Numeric Keypad & Biometrics
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                val keyRow1 = listOf("1", "2", "3")
                val keyRow2 = listOf("4", "5", "6")
                val keyRow3 = listOf("7", "8", "9")

                listOf(keyRow1, keyRow2, keyRow3).forEach { rowKeys ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth(0.84f)
                    ) {
                        rowKeys.forEach { digit ->
                            KeypadButton(
                                text = digit,
                                modifier = Modifier.weight(1f),
                                onClick = { handleDigitInput(digit) }
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth(0.84f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Delete backspace icon
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (enteredPin.isNotEmpty() && !pinError) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    enteredPin = enteredPin.dropLast(1)
                                }
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Center: 0 key
                    KeypadButton(
                        text = "0",
                        modifier = Modifier.weight(1f),
                        onClick = { handleDigitInput("0") }
                    )

                    // Right side: Biometric fingerprint button
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (biometricsEnabled) {
                            val bioInteraction = remember { MutableInteractionSource() }
                            val bioPressed by bioInteraction.collectIsPressedAsState()
                            val bioCorner by animateDpAsState(
                                targetValue = if (bioPressed) ExpressiveShapeTokens.KeypadPressedCorner else ExpressiveShapeTokens.KeypadRestingCorner,
                                animationSpec = MotionTokens.spatialSpringDp,
                                label = "bio_corner"
                            )
                            val bioScale by animateFloatAsState(
                                targetValue = if (bioPressed) 0.93f else 1f,
                                animationSpec = MotionTokens.spatialSnappyFloat,
                                label = "bio_scale"
                            )
                            val bioShape = RoundedCornerShape(bioCorner)
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .graphicsLayer {
                                        scaleX = bioScale
                                        scaleY = bioScale
                                    }
                                    .clip(bioShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable(
                                        interactionSource = bioInteraction,
                                        indication = ripple(),
                                        role = Role.Button,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onTriggerBiometric?.invoke()
                                        }
                                    )
                                    .testTag("biometric_unlocked_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Native Biometrics",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) ExpressiveShapeTokens.KeypadPressedCorner else ExpressiveShapeTokens.KeypadRestingCorner,
        animationSpec = MotionTokens.spatialSpringDp,
        label = "keypad_morph_$text"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = MotionTokens.spatialSnappyFloat,
        label = "keypad_scale_$text"
    )
    val bg by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = MotionTokens.colorTransition,
        label = "keypad_bg_$text"
    )
    val fg by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = MotionTokens.colorTransition,
        label = "keypad_fg_$text"
    )

    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .height(64.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(shape)
            .background(bg)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .testTag("keypad_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.tabular(),
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

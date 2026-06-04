package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FinanceViewModel

@Composable
fun AuthScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
    onTriggerBiometric: (() -> Unit)? = null
) {
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (biometricsEnabled) {
            onTriggerBiometric?.invoke()
        }
    }

    LaunchedEffect(pinError) {
        if (pinError) {
            showErrorMessage = true
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

    val securityBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.surface
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(securityBrush)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper logo and header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (showErrorMessage) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Lock Logo",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Uangku Secure Access",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Keamanan Finansial Terenkripsi Militer",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        // Pin Indicators (dots)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val active = i < enteredPin.length
                    val dotColor = if (pinError) {
                        MaterialTheme.colorScheme.error
                    } else if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(dotColor, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (showErrorMessage) {
                    "PIN Salah! Silakan coba lagi."
                } else if (enteredPin.isEmpty()) {
                    "Masukkan Kode Keamanan Anda"
                } else {
                    "Masukkan PIN Anda..."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (showErrorMessage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        // Numeric Keypad & Biometrics simulator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            val keyRow1 = listOf("1", "2", "3")
            val keyRow2 = listOf("4", "5", "6")
            val keyRow3 = listOf("7", "8", "9")

            listOf(keyRow1, keyRow2, keyRow3).forEach { rowKeys ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(0.81f)
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
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(0.81f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Hapus (delete backspace icon)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (enteredPin.isNotEmpty() && !pinError) {
                                enteredPin = enteredPin.dropLast(1)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Center: 0 key
                KeypadButton(
                    text = "0",
                    modifier = Modifier.weight(1f),
                    onClick = { handleDigitInput("0") }
                )

                // Right side: Biometric fingerprint clicker or empty Box if disabled
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (biometricsEnabled) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .clip(CircleShape)
                                .clickable {
                                    onTriggerBiometric?.invoke()
                                }
                                .testTag("biometric_unlocked_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Native Biometrics",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
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
    Box(
        modifier = modifier
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag("keypad_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

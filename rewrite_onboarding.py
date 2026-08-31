import re

with open('app/src/main/java/com/example/ui/screens/OnboardingScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FinanceViewModel

@Composable
fun OnboardingScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) }
    
    // Step 1: Username State
    var userNameInput by remember { mutableStateOf("") }
    
    // Step 2: Security States
    var pinEnabled by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }

    // Theme Check
    val themeDark = MaterialTheme.colorScheme.surface.let { it.red * 0.299 + it.green * 0.587 + it.blue * 0.114 < 0.5f }

    // Emerald Luxe Background Gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = if (themeDark) listOf(Color(0xFF022C1A), Color(0xFF0B1310))
                 else listOf(Color(0xFFE6F4EA), Color(0xFFF8FAF9))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .safeDrawingPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Bento Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    if (themeDark) Color(0xFF15221D).copy(alpha = 0.8f) 
                    else Color.White.copy(alpha = 0.9f)
                )
                .border(
                    1.dp,
                    if (themeDark) Color(0xFF064E3B) else Color(0xFFA7F3D0),
                    RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // STEP 0: WELCOME INTRO
                AnimatedVisibility(
                    visible = step == 0,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Hero Icon Bento
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF0F5132), Color(0xFF10B981))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Wallet Icon",
                                modifier = Modifier.size(56.dp),
                                tint = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Welcome to Uangku",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Your premium personal finance manager. Secure, smart, and fully localized on your device.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        // Feature Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Badge(icon = Icons.Default.Security, text = "Secure", themeDark = themeDark)
                            Badge(icon = Icons.Default.Star, text = "Premium", themeDark = themeDark)
                            Badge(icon = Icons.Default.CheckCircle, text = "Offline", themeDark = themeDark)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { step = 1 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_start_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (themeDark) Color(0xFF10B981) else Color(0xFF0F5132),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // STEP 1: CONFIGURE USERNAME
                AnimatedVisibility(
                    visible = step == 1,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFD97706), Color(0xFFF59E0B))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Setup",
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "What should we call you?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "This name will be used to personalize your dashboard and reports.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            label = { Text("Nickname") },
                            placeholder = { Text("e.g. Lintar Arya") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_username_input"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = if (themeDark) Color(0xFF064E3B) else Color(0xFFA7F3D0)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (userNameInput.isNotBlank()) {
                                    step = 2
                                } else {
                                    Toast.makeText(context, "Please enter your nickname.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_next_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (themeDark) Color(0xFF10B981) else Color(0xFF0F5132),
                                contentColor = Color.White
                            ),
                            enabled = userNameInput.isNotBlank()
                        ) {
                            Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // STEP 2: CONFIGURE SECURITY
                AnimatedVisibility(
                    visible = step == 2,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Secure Your Vault",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Add a 4-digit PIN for extra security. You can enable biometric login later in settings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Glassmorphism/Bento Switch Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp))
                                .background(if (themeDark) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFFE6F4EA).copy(alpha = 0.5f))
                                .border(1.dp, if (themeDark) Color(0xFF064E3B) else Color(0xFFA7F3D0), RoundedCornerShape(32.dp))
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
                                                .background(if (themeDark) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF059669).copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "PIN Lock",
                                                tint = if (themeDark) Color(0xFF34D399) else Color(0xFF059669)
                                            )
                                        }
                                        Column {
                                            Text("Enable PIN Lock", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Optional extra security", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Switch(
                                        checked = pinEnabled,
                                        onCheckedChange = { pinEnabled = it },
                                        modifier = Modifier.testTag("onboarding_pin_switch"),
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = if (themeDark) Color(0xFF10B981) else Color(0xFF059669)
                                        )
                                    )
                                }

                                AnimatedVisibility(visible = pinEnabled) {
                                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = pinValue,
                                            onValueChange = { input ->
                                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                                    pinValue = input
                                                }
                                            },
                                            label = { Text("4-Digit PIN") },
                                            placeholder = { Text("e.g. 1234") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("onboarding_pin_input"),
                                            shape = RoundedCornerShape(24.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF10B981),
                                                unfocusedBorderColor = if (themeDark) Color(0xFF064E3B) else Color(0xFFA7F3D0)
                                            )
                                        )
                                        if (pinValue.length < 4 && pinValue.isNotEmpty()) {
                                            Text("PIN must be 4 digits long.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (pinEnabled && pinValue.length != 4) {
                                    Toast.makeText(context, "Please enter a 4-digit PIN or disable PIN lock.", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.setOnboardingCompleted(
                                        name = userNameInput.trim(),
                                        pinEnabled = pinEnabled,
                                        pin = pinValue,
                                        biomEnabled = false
                                    )
                                    Toast.makeText(context, "Welcome to Uangku, ${userNameInput.trim()}!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("onboarding_finish_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (themeDark) Color(0xFF10B981) else Color(0xFF0F5132),
                                contentColor = Color.White
                            ),
                            enabled = !pinEnabled || pinValue.length == 4
                        ) {
                            Text("Complete Setup", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, themeDark: Boolean) {
    Row(
        modifier = Modifier
            .background(
                if (themeDark) Color(0xFF064E3B) else Color(0xFFE6F4EA),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (themeDark) Color(0xFF34D399) else Color(0xFF059669), modifier = Modifier.size(12.dp))
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (themeDark) Color(0xFFA7F3D0) else Color(0xFF043820))
    }
}
""")

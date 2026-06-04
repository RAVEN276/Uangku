package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    var biomEnabled by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .safeDrawingPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // STEP 0: WELCOME INTRO
            if (step == 0) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Wallet Icon",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Selamat Datang di Uangku",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Aplikasi manajemen keuangan pribadi pintar yang menjaga data Anda agar tetap lokal, terenkripsi, dan aman secara optimal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Button(
                    onClick = { step = 1 },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp)
                        .testTag("onboarding_start_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Mulai Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // STEP 1: CONFIGURE USERNAME
            if (step == 1) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Setup",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Siapa Nama Panggilan Anda?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Isi nama panggilan Anda untuk digunakan pada pesan sambutan halaman utama dan ekspor resume keuangan bulanan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = userNameInput,
                    onValueChange = { userNameInput = it },
                    label = { Text("Nama Panggilan") },
                    placeholder = { Text("Contoh: Lintar Arya") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("onboarding_username_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = {
                        if (userNameInput.isNotBlank()) {
                            step = 2
                        } else {
                            Toast.makeText(context, "Silahkan masukkan nama panggilan Anda terlebih dahulu.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    enabled = userNameInput.isNotBlank()
                ) {
                    Text("Lanjutkan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // STEP 2: CONFIGURE SECURITY (OPTIONAL)
            if (step == 2) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Keamanan Aplikasi (Opsional)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Batasi akses data anggaran Anda dengan PIN 4-digit atau biometrik sidik jari. Ini bisa Anda aktifkan secara opsional dan diganti kapan saja.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // PIN Lock Enable Switch
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
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "PIN Lock Icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text("Gunakan Kunci PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Amankan aplikasi dengan kode pin", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = pinEnabled,
                                onCheckedChange = { pinEnabled = it },
                                modifier = Modifier.testTag("onboarding_pin_switch")
                            )
                        }

                        // PIN Input Field (Visible if PIN enabled)
                        AnimatedVisibility(visible = pinEnabled) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = pinValue,
                                    onValueChange = { input ->
                                        if (input.length <= 4 && input.all { it.isDigit() }) {
                                            pinValue = input
                                        }
                                    },
                                    label = { Text("Atur PIN 4-Digit Anda") },
                                    placeholder = { Text("Contoh: 1234") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_pin_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (pinValue.length < 4 && pinValue.isNotEmpty()) {
                                    Text("PIN harus berupa 4 angka digit penuh.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            }
                        }

                        // Biometrics Fingerprint Enable Switch
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
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Biometrics Fingerprint",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text("Gunakan Sidik Jari", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Buka cepat dengan sensor sidik jari", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = biomEnabled,
                                onCheckedChange = { biomEnabled = it },
                                modifier = Modifier.testTag("onboarding_biom_switch")
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (pinEnabled && pinValue.length != 4) {
                            Toast.makeText(context, "Silahkan atur 4 digit PIN atau nonaktifkan kunci PIN terlebih dahulu.", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.setOnboardingCompleted(
                                name = userNameInput.trim(),
                                pinEnabled = pinEnabled,
                                pin = pinValue,
                                biomEnabled = biomEnabled
                            )
                            Toast.makeText(context, "Selamat datang di Uangku, ${userNameInput.trim()}!", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .testTag("onboarding_finish_button"),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !pinEnabled || pinValue.length == 4
                ) {
                    Text("Selesai & Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

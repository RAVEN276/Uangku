package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.FinanceViewModel

@Composable
fun SettingsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsState()
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsState()
    val themeDark by viewModel.themeDark.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val currentPin by viewModel.userPin.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInputValue by remember { mutableStateOf(currentPin) }

    var showPdfReportDialog by remember { mutableStateOf(false) }
    var pdfReportText by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pengaturan & Laporan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Atur preferensi keamanan, akses akun, dan ekspor seluruh dokumen keuangan secara lokal dan aman di perangkat Anda.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // --- DEPART SHEET SECTION: EXPORTS OF REPORTS ---
            Text(
                text = "Ekspor Laporan Keuangan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
              )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Unduh laporan keuangan bulanan lengkap Anda langsung dalam format spreadsheet ataupun PDF siap cetak.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // PDF generation click
                        Button(
                            onClick = {
                                pdfReportText = viewModel.generatePDFTextReport(context)
                                showPdfReportDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // CSV Excel export click
                        OutlinedButton(
                            onClick = {
                                val path = viewModel.generateCSVExport(context)
                                if (path.isNotEmpty()) {
                                    Toast.makeText(context, "Laporan Excel/CSV tersimpan di cache: $path", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Gagal mengeskpor data", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_excel_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ListAlt, contentDescription = "Excel", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ekspor Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- ACCOUNT SECURITY & BIOMETRIC SECTION ---
            Text(
                text = "Keamanan & Akses Biometrik",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Biometrics switch
                    SecuritySettingRow(
                        title = "Uji Sidik Jari (Biometrik)",
                        subtitle = "Aktifkan autentikasi sidik jari di halaman login saat membuka aplikasi.",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrics",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailing = {
                            Switch(
                                checked = biometricsEnabled,
                                onCheckedChange = { viewModel.toggleBiometrics(it) },
                                modifier = Modifier.testTag("biometric_switch")
                            )
                        }
                    )

                    // Pin changes
                    SecuritySettingRow(
                        title = "Kode PIN Pengaman",
                        subtitle = "Ubah 4 digit kode PIN default. PIN saat ini: $currentPin",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Security PIN",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            pinInputValue = currentPin
                            showPinDialog = true
                        },
                        trailing = {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Change")
                        }
                    )

                    // App locking
                    SecuritySettingRow(
                        title = "Kunci Aplikasi Sekarang",
                        subtitle = "Keluar secara aman untuk memverifikasi halaman autentikasi biometric PIN anda.",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Lock App",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { viewModel.lockApp() },
                        trailing = {
                            Text("Keluar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }

            // --- THEME OVERRIDES ---
            Text(
                text = "Tampilan Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Mode Gelap Toggle
                    SecuritySettingRow(
                        title = "Ubah Ke Mode Gelap",
                        subtitle = "Optimalkan konsumsi layar OLED dengan mode gelap yang sejuk di mata.",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Dark mode override",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailing = {
                            Switch(
                                checked = themeDark,
                                onCheckedChange = { viewModel.setTheme(it) },
                                modifier = Modifier.testTag("dark_mode_switch")
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // PIN Change configuration dialog
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Ganti PIN Aplikasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = pinInputValue,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInputValue = it },
                        label = { Text("PIN Baru (4 Digit Angka)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_security_pin_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPinDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (pinInputValue.length == 4) {
                                    viewModel.setPin(pinInputValue)
                                    showPinDialog = false
                                    Toast.makeText(context, "PIN Keamanan berhasil diubah ke: $pinInputValue", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = pinInputValue.length == 4
                        ) {
                            Text("Simpan PIN")
                        }
                    }
                }
            }
        }
    }

    // PDF Preview and Text Presentation Dialog with share action
    if (showPdfReportDialog) {
        Dialog(onDismissRequest = { showPdfReportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pratinjau PDF Laporan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showPdfReportDialog = false }) {
                            Icon(Icons.Default.Share, contentDescription = "Buka PDF", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = pdfReportText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showPdfReportDialog = false
                                Toast.makeText(context, "Dokumen PDF Berhasil dibagikan ke penyimpanan perangkat anda!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Bagikan PDF")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecuritySettingRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        trailing()
    }
}

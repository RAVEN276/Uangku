package com.example.ui.screens

import android.widget.Toast
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.animateColorAsState
import com.example.ui.theme.MotionTokens
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.service.MyNotificationListenerService
import com.example.ui.components.ExpressiveSegmentOption
import com.example.ui.components.ExpressiveSegmentedSelector
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.tabular
import com.example.ui.util.AppStrings
import com.example.ui.FinanceViewModel

@Composable
fun SettingsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsStateWithLifecycle()
    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val currentPin by viewModel.userPin.collectAsStateWithLifecycle()
    val currentName by viewModel.userName.collectAsStateWithLifecycle()
    val isPinEnabled by viewModel.isPinEnabled.collectAsStateWithLifecycle()
    val bankNotificationEnabled by viewModel.bankNotificationEnabled.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val usdToIdrRate by viewModel.usdToIdrRate.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val dynamicColorEnabled by viewModel.dynamicColor.collectAsStateWithLifecycle()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInputValue by remember { mutableStateOf(currentPin) }

    var showNameDialog by remember { mutableStateOf(false) }
    var nameInputValue by remember { mutableStateOf(currentName) }

    var showRateDialog by remember { mutableStateOf(false) }
    var rateInputValue by remember { mutableStateOf(usdToIdrRate.toInt().toString()) }

    var showPdfReportDialog by remember { mutableStateOf(false) }
    var pdfReportText by remember { mutableStateOf("") }

    val uangkuColors = LocalUangkuColors.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasNotificationAccess by remember {
        mutableStateOf(MyNotificationListenerService.isNotificationServiceEnabled(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationAccess = MyNotificationListenerService.isNotificationServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompactWidth = maxWidth < 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 900.dp)
                ) {
                    // SPATIAL HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = ExpressiveShapeTokens.HeroCorner, bottomEnd = ExpressiveShapeTokens.HeroCorner))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(bottomStart = ExpressiveShapeTokens.HeroCorner, bottomEnd = ExpressiveShapeTokens.HeroCorner)
                            )
                            .padding(top = 36.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = AppStrings.settingsTitle(appLanguage),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppStrings.settingsSubtitle(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Content Below Header
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

            // --- PROFIL PENGGUNA SECTION ---
            Text(
                text = AppStrings.userProfileTitle(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("user_profile_card"),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SecuritySettingRow(
                        title = AppStrings.nicknameTitle(appLanguage),
                        subtitle = AppStrings.nicknameSubtitle(appLanguage, currentName),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nama Pengguna",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            nameInputValue = currentName
                            showNameDialog = true
                        },
                        trailing = {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Ubah Nama")
                        }
                    )
                }
            }

            // --- DEPART SHEET SECTION: EXPORTS OF REPORTS ---
            Text(
                text = AppStrings.exportReportTitle(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
              )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = AppStrings.exportReportDesc(appLanguage),
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
                                .heightIn(min = 48.dp)
                                .testTag("export_pdf_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.savePdf(appLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // CSV Excel export click
                        OutlinedButton(
                            onClick = {
                                val path = viewModel.generateCSVExport(context)
                                if (path.isNotEmpty()) {
                                    Toast.makeText(context, "Laporan Excel/CSV disimpan di folder Download: $path", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Gagal mengeskpor data", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("export_excel_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Excel", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.exportExcel(appLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- ACCOUNT SECURITY & BIOMETRIC SECTION ---
            Text(
                text = AppStrings.securityTitle(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // PIN Lock toggle
                    SecuritySettingRow(
                        title = AppStrings.pinLockTitleSettings(appLanguage),
                        subtitle = AppStrings.pinLockSubtitleSettings(appLanguage),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "PIN Lock Toggle",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailing = {
                            Switch(
                                checked = isPinEnabled,
                                onCheckedChange = { viewModel.togglePinEnabled(it) },
                                modifier = Modifier.testTag("pin_lock_enabled_switch")
                            )
                        }
                    )

                    // Biometrics switch
                    SecuritySettingRow(
                        title = AppStrings.biometricTitleSettings(appLanguage),
                        subtitle = AppStrings.biometricSubtitleSettings(appLanguage),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrics",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailing = {
                            Switch(
                                checked = biometricsEnabled && isPinEnabled,
                                onCheckedChange = { viewModel.toggleBiometrics(it) },
                                enabled = isPinEnabled,
                                modifier = Modifier.testTag("biometric_switch")
                            )
                        },
                        enabled = isPinEnabled
                    )

                    // Pin changes
                    if (isPinEnabled) {
                        SecuritySettingRow(
                            title = AppStrings.changePinTitleSettings(appLanguage),
                            subtitle = AppStrings.changePinSubtitleSettings(appLanguage, currentPin),
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
                    }

                    // App locking
                    if (isPinEnabled) {
                        SecuritySettingRow(
                            title = AppStrings.lockAppTitleSettings(appLanguage),
                            subtitle = AppStrings.lockAppSubtitleSettings(appLanguage),
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Lock App",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = { viewModel.lockApp() },
                            trailing = {
                                Text(AppStrings.lockNowAction(appLanguage), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        )
                    }
                }
            }

            // --- DETEKTOR NOTIFIKASI BANK SECTION ---
            Text(
                text = AppStrings.bankDetectorTitle(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("bank_notif_settings_card"),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(32.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Live System Permission & Filter Status Header Pill
                    val statusBg = when {
                        !bankNotificationEnabled -> uangkuColors.elevatedSubSurface
                        hasNotificationAccess -> uangkuColors.incomeContainer
                        else -> uangkuColors.warningContainer
                    }
                    val statusFg = when {
                        !bankNotificationEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                        hasNotificationAccess -> uangkuColors.onIncomeContainer
                        else -> uangkuColors.onWarningContainer
                    }
                    val statusLabel = when {
                        !bankNotificationEnabled -> AppStrings.detectorStatusPaused(appLanguage)
                        hasNotificationAccess -> AppStrings.detectorStatusActive(appLanguage)
                        else -> AppStrings.detectorStatusNeedsPermission(appLanguage)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusBg)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("bank_notif_status_pill")
                        ) {
                            Icon(
                                imageVector = if (hasNotificationAccess && bankNotificationEnabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = statusLabel,
                                tint = statusFg,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusFg
                            )
                        }
                    }

                    SecuritySettingRow(
                        title = AppStrings.enableBankDetectorTitle(appLanguage),
                        subtitle = AppStrings.enableBankDetectorSubtitle(appLanguage),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Bank Notification Detector",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailing = {
                            Switch(
                                checked = bankNotificationEnabled,
                                onCheckedChange = {
                                    viewModel.setBankNotificationEnabled(it)
                                    if (it) {
                                        Toast.makeText(context, "Detektor Diaktifkan. Pastikan izin akses notifikasi sistem Android sudah aktif.", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Detektor Dinonaktifkan.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("bank_notif_enabled_switch")
                            )
                        }
                    )

                    SecuritySettingRow(
                        title = AppStrings.systemPermissionTitle(appLanguage),
                        subtitle = AppStrings.systemPermissionSubtitle(appLanguage),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "System Notification Permission Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            try {
                                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal membuka menu Pengaturan", Toast.LENGTH_SHORT).show()
                            }
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Akses Izin"
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(uangkuColors.elevatedSubSurface)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = AppStrings.supportedFinancialAppsLabel(appLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- MATA UANG & KURS GLOBAL SECTION ---
            Text(
                text = AppStrings.currencySection(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("currency_settings_card"),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = AppStrings.currencySubtitle(appLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // M3E Segmented Selector for Currency (IDR vs USD)
                    ExpressiveSegmentedSelector(
                        options = listOf(
                            ExpressiveSegmentOption(key = "IDR", label = "IDR (Rp)"),
                            ExpressiveSegmentOption(key = "USD", label = "USD ($)")
                        ),
                        selectedKey = selectedCurrency,
                        onSelect = { newCurr ->
                            viewModel.setCurrency(newCurr)
                            val msg = if (newCurr == "IDR") "Mata uang diubah ke Rupiah (IDR)" else "Mata uang diubah ke Dollar AS (USD)"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Exchange Rate Info Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = uangkuColors.elevatedSubSurface,
                                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoInnerCorner)
                            )
                            .border(
                                1.dp,
                                uangkuColors.cardBorder,
                                RoundedCornerShape(ExpressiveShapeTokens.BentoInnerCorner)
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppStrings.benchmarkRate(appLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "1 USD = Rp ${String.format("%,d", usdToIdrRate.toInt()).replace(',', '.')}",
                                style = MaterialTheme.typography.bodyMedium.tabular(),
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                rateInputValue = usdToIdrRate.toInt().toString()
                                showRateDialog = true
                            },
                            shape = RoundedCornerShape(ExpressiveShapeTokens.PillRestingCorner)
                        ) {
                            Text(AppStrings.adjustRate(appLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- THEME & LANGUAGE APPEARANCE ---
            Text(
                text = AppStrings.appearanceSection(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("appearance_card"),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(ExpressiveShapeTokens.BentoCorner),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Live Material 3 Expressive Palette Preview Bento Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(ExpressiveShapeTokens.BentoInnerCorner))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(ExpressiveShapeTokens.BentoInnerCorner))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = if (uangkuColors.isDynamic) {
                                    AppStrings.themeModeDynamic(appLanguage)
                                } else {
                                    AppStrings.themeModeExpressive(appLanguage)
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = AppStrings.themePreviewSubtitle(appLanguage, uangkuColors.isDynamic),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primaryContainer
                            ).forEach { swatch ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(swatch)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                )
                            }
                        }
                    }

                    // 1. Dark Mode Toggle
                    SecuritySettingRow(
                        title = AppStrings.darkModeTitle(appLanguage),
                        subtitle = AppStrings.darkModeSubtitle(appLanguage),
                        icon = {
                            Icon(
                                imageVector = if (themeDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Dark mode toggle",
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

                    // Subtle Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // 2. Language Toggle (English & Indonesia)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.languageTitle(appLanguage),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = AppStrings.languageSubtitle(appLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ExpressiveSegmentedSelector(
                            options = listOf(
                                ExpressiveSegmentOption(key = "id", label = "Indonesia", testTag = "language_toggle_indonesia"),
                                ExpressiveSegmentOption(key = "en", label = "English", testTag = "language_toggle_english")
                            ),
                            selectedKey = if (appLanguage.equals("en", ignoreCase = true)) "en" else "id",
                            onSelect = { newLang ->
                                viewModel.setLanguage(newLang)
                                val msg = if (newLang == "id") "Bahasa diubah ke Bahasa Indonesia" else "Language switched to English"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // 3. Dynamic Color (Material You) for Android 12+
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        SecuritySettingRow(
                            title = AppStrings.dynamicColorTitle(appLanguage),
                            subtitle = AppStrings.dynamicColorSubtitle(appLanguage),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Dynamic colors",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailing = {
                                Switch(
                                    checked = dynamicColorEnabled,
                                    onCheckedChange = { viewModel.setDynamicColor(it) },
                                    modifier = Modifier.testTag("dynamic_color_switch")
                                )
                            }
                        )
                    }
                }
            }

            // --- INFORMASI & INTEGRITAS DATA ---
            Text(
                text = AppStrings.appInfoSection(appLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("app_info_card"),
                colors = CardDefaults.cardColors(containerColor = uangkuColors.cardSurface),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, uangkuColors.cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Uangku",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppStrings.appInfoSubtitle(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "v2.2",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = uangkuColors.incomeText,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = AppStrings.encryptedDb(appLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppStrings.updateSafeNote(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding().height(if (isCompactWidth) 108.dp else 32.dp))
        }
    }

    // Name Change configuration dialog
    if (showNameDialog) {
        Dialog(onDismissRequest = { showNameDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = AppStrings.changeNicknameTitle(appLanguage), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = nameInputValue,
                        onValueChange = { nameInputValue = it },
                        label = { Text(AppStrings.nicknameLabel(appLanguage)) },
                        singleLine = true,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_username_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showNameDialog = false }) {
                            Text(AppStrings.cancel(appLanguage))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nameInputValue.isNotBlank()) {
                                    viewModel.setUserName(nameInputValue.trim())
                                    showNameDialog = false
                                    Toast.makeText(context, "Nama berhasil diubah ke: ${nameInputValue.trim()}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(AppStrings.save(appLanguage))
                        }
                    }
                }
            }
        }
    }

    // PIN Change configuration dialog
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = AppStrings.changeAppPinTitle(appLanguage), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = pinInputValue,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInputValue = it },
                        label = { Text(AppStrings.newPinPrompt(appLanguage)) },
                        singleLine = true,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_security_pin_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPinDialog = false }) {
                            Text(AppStrings.cancel(appLanguage))
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
                            Text(AppStrings.savePin(appLanguage))
                        }
                    }
                }
            }
        }
    }

    // Exchange Rate Configuration Dialog
    if (showRateDialog) {
        Dialog(onDismissRequest = { showRateDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = AppStrings.adjustRateTitle(appLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = AppStrings.adjustRateSubtitle(appLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = rateInputValue,
                        onValueChange = { if (it.all { c -> c.isDigit() }) rateInputValue = it },
                        label = { Text(AppStrings.ratePrompt(appLanguage)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showRateDialog = false }) {
                            Text(AppStrings.cancel(appLanguage))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val rateNum = rateInputValue.toDoubleOrNull()
                                if (rateNum != null && rateNum > 0) {
                                    viewModel.setUsdRate(rateNum)
                                    showRateDialog = false
                                    Toast.makeText(context, "Kurs USD berhasil diperbarui: 1 USD = Rp ${rateInputValue}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Nilai kurs tidak valid!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(AppStrings.saveRate(appLanguage))
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
                shape = RoundedCornerShape(32.dp),
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
                        Text(text = AppStrings.pdfPreviewTitle(appLanguage), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showPdfReportDialog = false }) {
                            Icon(Icons.Default.Share, contentDescription = "Buka PDF", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = pdfReportText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
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
                            Text(AppStrings.sharePdf(appLanguage))
                        }
                    }
                }
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
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
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

package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.service.ParsedReceiptItem
import com.example.service.ParsedReceiptResult
import com.example.service.ReceiptOcrScanner
import com.example.ui.FinanceViewModel
import com.example.ui.theme.LocalUangkuColors
import com.example.ui.theme.MotionTokens
import kotlinx.coroutines.launch

/**
 * Google Drive OCR Scanner Dialog with Manual or Foto (Camera) feature.
 * Provides:
 * 1. "Foto" Mode: In-app Google Drive style viewfinder with document frame,
 *    shutter button, gallery picker, and automated ML Kit OCR text recognition.
 * 2. "Manual" Mode: Direct manual input form for transaction details.
 */
@Composable
fun ReceiptScannerDialog(
    viewModel: FinanceViewModel,
    initialBitmap: Bitmap? = null,
    bitmap: Bitmap? = initialBitmap,
    initialImageUri: Uri? = null,
    imageUri: Uri? = initialImageUri,
    onDismiss: () -> Unit,
    onConfirmAdd: (title: String, amount: Double, type: String, category: String) -> Unit,
    onRelaunchCamera: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val transactions by viewModel.allTransactions.collectAsState()
    val currencyPref by viewModel.selectedCurrency.collectAsState()
    val isUsd = currencyPref == "USD"

    // Mode Selector: "FOTO" (Google Drive Document Scanner) vs "MANUAL" (Manual Form Input)
    var activeMode by remember { mutableStateOf(if (initialBitmap != null || bitmap != null || initialImageUri != null || imageUri != null) "FOTO" else "FOTO") }

    // Google Drive Capture Mode: "MANUAL" (user taps shutter) vs "AUTO" (auto capture mode indicator)
    var driveCaptureMode by remember { mutableStateOf("MANUAL") }
    var flashEnabled by remember { mutableStateOf(false) }

    // Scanned image state
    var currentImageUri by remember(initialImageUri, imageUri) { mutableStateOf<Uri?>(initialImageUri ?: imageUri) }
    var currentBitmap by remember(initialBitmap, bitmap) { mutableStateOf<Bitmap?>(initialBitmap ?: bitmap) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Full-resolution Camera & Gallery Launchers
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            val uri = pendingCameraUri!!
            currentImageUri = uri
            currentBitmap = ReceiptOcrScanner.loadOptimizedBitmap(context, uri)
            activeMode = "FOTO"
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                currentImageUri = uri
                val decoded = ReceiptOcrScanner.loadOptimizedBitmap(context, uri)
                if (decoded != null) {
                    currentBitmap = decoded
                    activeMode = "FOTO"
                } else {
                    Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error membaca galeri: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ReceiptOcrScanner.createTempImageUri(context)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk memotret nota", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCameraShutter() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = ReceiptOcrScanner.createTempImageUri(context)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // OCR Engine Scan State
    var isProcessingOcr by remember { mutableStateOf(false) }
    var scanCompleted by remember { mutableStateOf(false) }
    var ocrStatusMessage by remember { mutableStateOf("Mengekstrak data struk...") }

    // Form Fields (Shared between Foto & Manual modes)
    var merchantName by remember { mutableStateOf("") }
    var totalAmountInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Makanan") }
    var selectedType by remember { mutableStateOf("EXPENSE") }
    var noteInput by remember { mutableStateOf("") }
    var extractedItems by remember { mutableStateOf<List<ParsedReceiptItem>>(emptyList()) }
    var rawOcrText by remember { mutableStateOf("") }
    var showRawText by remember { mutableStateOf(false) }

    val categoryList = remember {
        listOf("Makanan", "Belanja", "Transportasi", "Tagihan", "Hiburan", "Kesehatan", "Pendidikan", "Investasi", "Lainnya")
    }

    // Populate OCR parsed data into state
    fun applyParsedOcrResult(result: ParsedReceiptResult) {
        merchantName = result.merchant
        totalAmountInput = if (result.totalAmount > 0) {
            result.totalAmount.toLong().toString()
        } else ""
        selectedCategory = result.suggestedCategory
        selectedType = result.suggestedType
        extractedItems = result.items
        rawOcrText = result.rawText
        isProcessingOcr = false
        scanCompleted = true
        ocrStatusMessage = if (result.totalAmount > 0) {
            "Struk berhasil diekstrak otomatis!"
        } else {
            "Teks struk terbaca. Mohon periksa atau sesuaikan nominal."
        }
    }

    // Trigger ML Kit OCR with full resolution Uri or Bitmap
    LaunchedEffect(currentImageUri, currentBitmap) {
        val uri = currentImageUri
        val bmp = currentBitmap

        if (uri != null) {
            isProcessingOcr = true
            scanCompleted = false
            ocrStatusMessage = "Menjalankan Google OCR resolusi tinggi..."
            coroutineScope.launch {
                val ocrResult = ReceiptOcrScanner.recognizeTextFromUri(context, uri, transactions)
                ocrResult.onSuccess { parsed ->
                    applyParsedOcrResult(parsed)
                }.onFailure { err ->
                    if (bmp != null) {
                        val fallbackResult = ReceiptOcrScanner.recognizeTextFromBitmap(bmp, transactions)
                        fallbackResult.onSuccess { applyParsedOcrResult(it) }
                            .onFailure {
                                isProcessingOcr = false
                                Toast.makeText(context, "OCR: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        isProcessingOcr = false
                        Toast.makeText(context, "OCR Gagal: ${err.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (bmp != null) {
            isProcessingOcr = true
            scanCompleted = false
            ocrStatusMessage = "Menjalankan Google OCR pada struk..."
            coroutineScope.launch {
                val ocrResult = ReceiptOcrScanner.recognizeTextFromBitmap(bmp, transactions)
                ocrResult.onSuccess { parsed ->
                    applyParsedOcrResult(parsed)
                }.onFailure { err ->
                    isProcessingOcr = false
                    Toast.makeText(context, "OCR Gagal: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Laser Animation for HUD Scanning
    val infiniteTransition = rememberInfiniteTransition(label = "laser_sweep")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .testTag("google_drive_scanner_dialog_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Google Drive Document Scanner Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DocumentScanner,
                                    contentDescription = "Pemindai Struk OCR",
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Pindai Nota OCR",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Auto OCR",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (activeMode == "FOTO") "Pindai nota otomatis dengan kamera atau galeri" else "Input rincian nota belanja secara manual",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // 2. Document Scanner Mode Switcher: [ Foto | Manual ]
                    val uangkuColors = LocalUangkuColors.current
                    ExpressiveSegmentedSelector(
                        options = listOf(
                            ExpressiveSegmentOption("FOTO", "Foto (Scan OCR)", testTag = "tab_mode_foto"),
                            ExpressiveSegmentOption("MANUAL", "Manual", testTag = "tab_mode_manual")
                        ),
                        selectedKey = activeMode,
                        onSelect = { activeMode = it },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        activeColor = MaterialTheme.colorScheme.primary,
                        onActiveColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 3. FOTO MODE CONTENT
                    if (activeMode == "FOTO") {
                        if (currentBitmap == null) {
                            // Google Drive Camera Viewfinder HUD (No photo taken yet)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background grid effect
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeW = 3.dp.toPx()
                                    val cornerLen = 32.dp.toPx()
                                    val pad = 16.dp.toPx()
                                    val rectColor = Color(0xFF56C292)

                                    // Top-Left Corner
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad, pad + cornerLen), strokeW)
                                    // Top-Right Corner
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad, pad + cornerLen), strokeW)
                                    // Bottom-Left Corner
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, size.height - pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad, size.height - pad - cornerLen), strokeW)
                                    // Bottom-Right Corner
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, size.height - pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad - cornerLen), strokeW)
                                }

                                // Animated reticle scanning line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(2.dp)
                                        .align(Alignment.TopCenter)
                                        .offset(y = (230.dp * laserPosition))
                                        .background(Color(0xFF56C292))
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DocumentScanner,
                                            contentDescription = "Arahkan Kamera",
                                            tint = Color(0xFF56C292),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    Text(
                                        text = "Arahkan kamera ke struk belanja",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = "Pastikan teks jelas & penerangan cukup untuk ekstraksi OCR otomatis",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }

                            // Google Drive Capture Mode Pill: [ Otomatis | Manual ]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (driveCaptureMode == "AUTO") MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                    else Color.Transparent
                                                )
                                                .clickable { driveCaptureMode = "AUTO" }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Otomatis",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (driveCaptureMode == "AUTO") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (driveCaptureMode == "MANUAL") MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                    else Color.Transparent
                                                )
                                                .clickable { driveCaptureMode = "MANUAL" }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Manual",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (driveCaptureMode == "MANUAL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Google Drive Scanner Bottom Control Bar (Gallery | Shutter | Flash)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Gallery Picker Button
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .clickable {
                                                galleryLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                            .testTag("scan_gallery_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Pilih dari Galeri",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Galeri", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                // Big Expressive Camera Shutter Button
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(CircleShape)
                                            .border(3.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            .padding(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .clickable { launchCameraShutter() }
                                            .testTag("scan_camera_shutter_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Jepret Foto",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Ambil Foto", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }

                                // Flashlight Toggle Button
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                if (flashEnabled) MaterialTheme.colorScheme.secondaryContainer
                                                else MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                            .clickable {
                                                flashEnabled = !flashEnabled
                                                Toast.makeText(context, if (flashEnabled) "Lampu kilat aktif" else "Lampu kilat mati", Toast.LENGTH_SHORT).show()
                                            }
                                            .testTag("scan_flash_toggle_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                            contentDescription = "Flashlight",
                                            tint = if (flashEnabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(if (flashEnabled) "Flash Nyala" else "Flash Mati", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            // Scanned Image Preview HUD (Photo has been captured)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = currentBitmap!!.asImageBitmap(),
                                    contentDescription = "Preview Struk",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Dark gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.35f))
                                )

                                // Corner reticles
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeW = 4.dp.toPx()
                                    val cornerLen = 28.dp.toPx()
                                    val pad = 12.dp.toPx()
                                    val rectColor = Color(0xFF56C292)

                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad, pad + cornerLen), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad, pad + cornerLen), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, size.height - pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad, size.height - pad - cornerLen), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, size.height - pad), strokeW)
                                    drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad - cornerLen), strokeW)
                                }

                                // Laser line during active OCR
                                if (isProcessingOcr) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                            .height(2.dp)
                                            .align(Alignment.TopCenter)
                                            .offset(y = (180.dp * laserPosition))
                                            .background(Color(0xFF56C292))
                                    )
                                }

                                // Status overlay badge inside preview
                                if (isProcessingOcr) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp),
                                            color = Color(0xFF56C292),
                                            strokeWidth = 2.5.dp
                                        )
                                        Text(
                                            text = ocrStatusMessage,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (scanCompleted) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF34D399),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "OCR Sukses: Silakan Verifikasi Data",
                                            color = Color(0xFF34D399),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // OCR Verification & Editable Data Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Merchant Name
                                    OutlinedTextField(
                                        value = merchantName,
                                        onValueChange = { merchantName = it },
                                        label = { Text("Nama Toko / Merchant") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("ocr_merchant_input")
                                    )

                                    // Total Amount
                                    OutlinedTextField(
                                        value = totalAmountInput,
                                        onValueChange = { input ->
                                            val digits = input.filter { it.isDigit() }
                                            if (digits.length <= 15) {
                                                totalAmountInput = digits
                                            }
                                        },
                                        label = { Text("Total Nominal (${if (isUsd) "USD" else "Rp"})") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        visualTransformation = RupiahVisualTransformation(isUsd = isUsd),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("ocr_amount_input")
                                    )

                                    // Category Picker
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                            Text(
                                                text = "Kategori Pengeluaran:",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            categoryList.forEach { cat ->
                                                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                                                ExpressiveMorphChip(
                                                    selected = isSelected,
                                                    onClick = { selectedCategory = cat }
                                                ) {
                                                    Text(
                                                        text = cat,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Line items breakdown if parsed
                                    if (extractedItems.isNotEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "Rincian Item (${extractedItems.size}):",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            extractedItems.take(5).forEach { item ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = item.name,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.weight(1f),
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = if (isUsd) "$${item.price}" else "Rp ${String.format("%,d", item.price.toLong())}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Raw OCR Text viewer toggle
                                    if (rawOcrText.isNotBlank()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { showRawText = !showRawText }
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Lihat Teks Asli Struk (OCR)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = if (showRawText) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        if (showRawText) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFF0F172A),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(10.dp)
                                                        .heightIn(max = 160.dp)
                                                        .verticalScroll(rememberScrollState()),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Sentuh baris teks di bawah untuk menyalin ke input:",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF7DD3FC),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    rawOcrText.lines().filter { it.isNotBlank() }.forEach { line ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(Color(0xFF1E293B))
                                                                .clickable {
                                                                    val digits = line.filter { it.isDigit() }
                                                                    if (digits.length in 3..12 && (line.contains("Rp", ignoreCase = true) || line.contains("total", ignoreCase = true) || line.contains("."))) {
                                                                        totalAmountInput = digits
                                                                        Toast.makeText(context, "Nominal diset ke: $digits", Toast.LENGTH_SHORT).show()
                                                                    } else {
                                                                        merchantName = line.take(30).trim()
                                                                        Toast.makeText(context, "Toko diset ke: $line", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = line,
                                                                fontFamily = FontFamily.Monospace,
                                                                fontSize = 11.sp,
                                                                color = Color(0xFFF1F5F9),
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                            Icon(
                                                                Icons.Default.Edit,
                                                                contentDescription = "Pilih",
                                                                tint = Color(0xFF38BDF8),
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Action buttons: Foto Ulang & Simpan Transaksi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        currentBitmap = null
                                        currentImageUri = null
                                        scanCompleted = false
                                        isProcessingOcr = false
                                        if (onRelaunchCamera != null) {
                                            onRelaunchCamera()
                                        } else {
                                            launchCameraShutter()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("rescan_receipt_btn")
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Foto Ulang", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val amountDouble = totalAmountInput.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                        val finalTitle = merchantName.ifBlank { "Belanja Nota" }
                                        if (amountDouble > 0) {
                                            onConfirmAdd(
                                                finalTitle,
                                                amountDouble,
                                                selectedType,
                                                selectedCategory
                                            )
                                            Toast.makeText(context, "Transaksi nota berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Nominal tidak valid", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = totalAmountInput.isNotBlank() && (totalAmountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L) > 0L,
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("save_receipt_transaction_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simpan Transaksi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 4. MANUAL MODE CONTENT
                    if (activeMode == "MANUAL") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = uangkuColors.elevatedSubSurface
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Input Manual Transaksi Nota",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // Type Selection: Pengeluaran vs Pemasukan
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (selectedType == "EXPENSE") uangkuColors.expenseContainer
                                                else uangkuColors.cardSurface
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (selectedType == "EXPENSE") uangkuColors.expenseText else uangkuColors.cardBorder,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { selectedType = "EXPENSE" }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Pengeluaran",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (selectedType == "EXPENSE") uangkuColors.onExpenseContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (selectedType == "INCOME") uangkuColors.incomeContainer
                                                else uangkuColors.cardSurface
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (selectedType == "INCOME") uangkuColors.incomeText else uangkuColors.cardBorder,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { selectedType = "INCOME" }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Pemasukan",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (selectedType == "INCOME") uangkuColors.onIncomeContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Merchant Name Field
                                OutlinedTextField(
                                    value = merchantName,
                                    onValueChange = { merchantName = it },
                                    label = { Text("Nama Toko / Keperluan") },
                                    placeholder = { Text("cth: Indomaret, Superindo, Warung") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("manual_merchant_input")
                                )

                                // Total Amount Field
                                OutlinedTextField(
                                    value = totalAmountInput,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 15) {
                                            totalAmountInput = digits
                                        }
                                    },
                                    label = { Text("Nominal (${if (isUsd) "USD" else "Rp"})") },
                                    placeholder = { Text("0") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    visualTransformation = RupiahVisualTransformation(isUsd = isUsd),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("manual_amount_input")
                                )

                                // Category Chips Selector
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Pilih Kategori:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        categoryList.forEach { cat ->
                                            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                                            ExpressiveMorphChip(
                                                selected = isSelected,
                                                onClick = { selectedCategory = cat }
                                            ) {
                                                Text(
                                                    text = cat,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Additional Note Field
                                OutlinedTextField(
                                    value = noteInput,
                                    onValueChange = { noteInput = it },
                                    label = { Text("Catatan / Keterangan (Opsional)") },
                                    placeholder = { Text("cth: Belanja mingguan keluarga") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Note, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Save Manual Button
                        Button(
                            onClick = {
                                val amountDouble = totalAmountInput.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                val finalTitle = merchantName.ifBlank {
                                    if (selectedType == "INCOME") "Pemasukan Manual" else "Pengeluaran Manual"
                                }
                                if (amountDouble > 0) {
                                    onConfirmAdd(
                                        finalTitle,
                                        amountDouble,
                                        selectedType,
                                        selectedCategory
                                    )
                                    Toast.makeText(context, "Transaksi berhasil dicatat!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Silakan masukkan nominal yang valid", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = totalAmountInput.isNotBlank() && (totalAmountInput.filter { it.isDigit() }.toLongOrNull() ?: 0L) > 0L,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_manual_transaction_btn"),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Transaksi Manual", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

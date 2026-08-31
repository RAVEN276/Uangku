package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import com.example.service.ParsedReceiptItem
import com.example.service.ParsedReceiptResult
import com.example.service.ReceiptOcrScanner
import com.example.ui.FinanceViewModel
import kotlinx.coroutines.launch

/**
 * Direct Camera OCR Scanner Result Modal (Google Drive Scanner style).
 * Rendered when a picture of a receipt is captured by the direct camera viewfinder.
 * Automatically runs Google ML Kit Text Recognition to extract total, merchant, and items.
 */
@Composable
fun ReceiptScannerDialog(
    viewModel: FinanceViewModel,
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onRelaunchCamera: () -> Unit,
    onConfirmAdd: (title: String, amount: Double, type: String, category: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val transactions by viewModel.allTransactions.collectAsState()
    val currencyPref by viewModel.selectedCurrency.collectAsState()
    val isUsd = currencyPref == "USD"

    // Scan State
    var isProcessingOcr by remember { mutableStateOf(true) }
    var scanCompleted by remember { mutableStateOf(false) }
    var ocrStatusMessage by remember { mutableStateOf("Mengekstrak data struk...") }

    // Extracted Fields for User Verification / Editing
    var merchantName by remember { mutableStateOf("") }
    var totalAmountInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Makanan") }
    var selectedType by remember { mutableStateOf("EXPENSE") }
    var extractedItems by remember { mutableStateOf<List<ParsedReceiptItem>>(emptyList()) }
    var rawOcrText by remember { mutableStateOf("") }
    var showRawText by remember { mutableStateOf(false) }

    val categoryList = remember {
        listOf("Makanan", "Belanja", "Transportasi", "Tagihan", "Hiburan", "Kesehatan", "Pendidikan", "Investasi", "Lainnya")
    }

    // Function to populate extracted data into state
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
        ocrStatusMessage = "Struk berhasil diekstrak!"
    }

    // Trigger ML Kit Text Recognition on the passed bitmap immediately
    LaunchedEffect(bitmap) {
        isProcessingOcr = true
        scanCompleted = false
        ocrStatusMessage = "Menjalankan OCR ML Kit pada struk..."
        coroutineScope.launch {
            val ocrResult = ReceiptOcrScanner.recognizeTextFromBitmap(bitmap, transactions)
            ocrResult.onSuccess { parsed ->
                applyParsedOcrResult(parsed)
            }.onFailure { err ->
                isProcessingOcr = false
                Toast.makeText(context, "OCR Gagal: ${err.message}", Toast.LENGTH_SHORT).show()
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
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .testTag("receipt_scanner_dialog_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Dialog Header
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Scan Struk",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Hasil Scan Struk OCR",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "ML Kit Engine",
                                            color = Color(0xFF10B981),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isProcessingOcr) "Mengekstrak total & rincian belanja..." else "Tinjau data sebelum disimpan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // 2. Viewfinder & Scanned Image Preview HUD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Preview Struk",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Dark gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )

                        // HUD Target Corner Reticles
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 4.dp.toPx()
                            val cornerLen = 28.dp.toPx()
                            val pad = 12.dp.toPx()
                            val rectColor = Color(0xFF38BDF8)

                            // Top-Left
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, pad), strokeW)
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, pad), androidx.compose.ui.geometry.Offset(pad, pad + cornerLen), strokeW)
                            // Top-Right
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, pad), strokeW)
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, pad), androidx.compose.ui.geometry.Offset(size.width - pad, pad + cornerLen), strokeW)
                            // Bottom-Left
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad + cornerLen, size.height - pad), strokeW)
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(pad, size.height - pad), androidx.compose.ui.geometry.Offset(pad, size.height - pad - cornerLen), strokeW)
                            // Bottom-Right
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad - cornerLen, size.height - pad), strokeW)
                            drawLine(rectColor, androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad), androidx.compose.ui.geometry.Offset(size.width - pad, size.height - pad - cornerLen), strokeW)
                        }

                        // Animated Laser Line during OCR scanning
                        if (isProcessingOcr) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .align(Alignment.TopCenter)
                                    .offset(y = (170.dp * laserPosition))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0xFF38BDF8),
                                                Color(0xFF67E8F9),
                                                Color(0xFF38BDF8),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Status message overlay inside viewfinder
                        if (isProcessingOcr) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color(0xFF38BDF8),
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
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "OCR Sukses: Data Siap Ditinjau",
                                    color = Color(0xFF34D399),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 3. Extracted Data Form & Verification Card
                    if (scanCompleted) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Merchant Name Field
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

                                // Total Amount Field
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
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.surfaceVariant
                                                    )
                                                    .clickable { selectedCategory = cat }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = cat,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // Extracted Items Breakdown (if parsed)
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

                                // Raw OCR Debugging toggle
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
                                            text = "Lihat Teks Asli Struk",
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
                                            Text(
                                                text = rawOcrText,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = Color(0xFF94A3B8),
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .heightIn(max = 120.dp)
                                                    .verticalScroll(rememberScrollState())
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Action Buttons (Foto Ulang or Simpan Transaksi)
                    if (scanCompleted) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRelaunchCamera,
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
                                        Toast.makeText(context, "Transaksi struk berhasil disimpan!", Toast.LENGTH_SHORT).show()
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
            }
        }
    }
}

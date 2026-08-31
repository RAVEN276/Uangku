import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

# Add missing imports if needed
imports = """import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
"""
if "import androidx.compose.ui.graphics.Brush" not in content:
    content = content.replace("import androidx.compose.ui.graphics.Color\n", "import androidx.compose.ui.graphics.Color\n" + imports)

target = """    Scaffold(
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
            )"""

replacement = """    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
                                 else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // SPATIAL HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            if (themeDark) Color(0xFF1E293B).copy(alpha = 0.9f) 
                            else Color.White.copy(alpha = 0.9f)
                        )
                        .border(
                            1.dp,
                            if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                            RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Pengaturan",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Atur preferensi keamanan, akses akun, dan ekspor seluruh dokumen keuangan secara lokal.",
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
                    Spacer(modifier = Modifier.height(8.dp))"""

if target in content:
    content = content.replace(target, replacement)
    
    # We need to add one extra closing brace at the end of the Scaffold block to close the Box
    # Let's find the closing brace for Scaffold. It's usually `}` after the last dialog.
    
    # Actually, we replaced `Column(...) {` with `Box(...) { Column(...) { Box(...) {} Column(...) {`. We need 2 closing braces for Box and Column.
    # The original had 1 closing brace for `Column(...) {`
    
    # So we need to add 2 closing braces at the end of the Scaffold content block.
    # We can just look for the end of the Scaffold content.
    # Let's do it by finding `    }` that closes the Scaffold block.
    
    # Look for the last `    }` before Dialog definitions.
    # Let's see the end of the file.
    
with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)
print("Settings updated initially")

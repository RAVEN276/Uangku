import re

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

# Add imports
if 'import androidx.compose.ui.graphics.Brush' not in content:
    content = content.replace('import androidx.compose.ui.graphics.Color\n', 'import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.Brush\nimport androidx.compose.foundation.border\n')

# We need to replace from `Scaffold(` up to `if (activeTab == 0) {`
# but handle the FAB block properly. Let's find exactly the replacement points.

pattern = r"    Scaffold\(\s*modifier = modifier\.fillMaxSize\(\),\s*floatingActionButton = \{\s*if \(activeTab == 0\) \{\s*Button\([\s\S]*?\} else \{\s*Button\([\s\S]*?\}\s*\}\s*\) \{ innerPadding ->\s*LazyColumn\(\s*modifier = Modifier\s*\.fillMaxSize\(\)\s*\.padding\(innerPadding\)\s*\.padding\(16\.dp\),\s*verticalArrangement = Arrangement\.spacedBy\(16\.dp\)\s*\) \{\s*item \{\s*Column\(verticalArrangement = Arrangement\.spacedBy\(4\.dp\)\) \{\s*Text\(\s*text = if \(activeTab == 0\) \"Anggaran & Alarm\" else \"Target Menabung\",[\s\S]*?item \{\s*Row\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.background\([\s\S]*?\}\s*\}\s*\)\s*\}\s*\}\s*if \(activeTab == 0\) \{"

replacement = """    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (activeTab == 0) {
                Button(
                    onClick = { showAddBudgetDialog = true },
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .testTag("budget_add_floating_action_btn")
                        .padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.LibraryAdd, contentDescription = "Limit Baru")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Batas Baru")
                }
            } else {
                Button(
                    onClick = { showAddSavingGoalDialog = true },
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .testTag("saving_goal_add_floating_action_btn")
                        .padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Target Baru")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Target Baru")
                }
            }
        }
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
                            text = "Anggaran & Target",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        )
                        
                        // Tab Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (themeDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val activeBtnColor = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                            val inactiveBtnColor = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { activeTab = 0 },
                                colors = if (activeTab == 0) activeBtnColor else inactiveBtnColor,
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text("Anggaran", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { activeTab = 1 },
                                colors = if (activeTab == 1) activeBtnColor else inactiveBtnColor,
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text("Target", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
                
                // Content Below Header
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
            if (activeTab == 0) {"""

content = re.sub(pattern, replacement, content)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Budget formatted")

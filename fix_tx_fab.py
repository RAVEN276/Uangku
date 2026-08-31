with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

import re

# We will change the Box to Scaffold
# Find the start of Box(
pattern = r"Box\(\s*modifier = modifier\s*\.fillMaxSize\(\)\s*\.background\([^)]+\)\s*\)\s*\{\s*Column\(\s*modifier = Modifier\.fillMaxSize\(\)\s*\)\s*\{"

replacement = """Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    showAddDialog = true
                },
                modifier = Modifier
                    .padding(bottom = 8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
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
            ) {"""

content = re.sub(pattern, replacement, content, count=1)

# Now we need to remove the old FAB and one closing brace for the old Box
old_fab_pattern = r"// Floating Action Button\s*FloatingActionButton\([\s\S]*?\}\s*\}"
content = re.sub(old_fab_pattern, "", content)

# Remove the extra closing brace that belonged to the Box
# The old Box closed right before `if (showAddDialog) {`
content = content.replace("            }\n        }\n\n    }\n\n    if (showAddDialog) {", "            }\n        }\n    }\n\n    if (showAddDialog) {")

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("Updated Transactions to use Scaffold")

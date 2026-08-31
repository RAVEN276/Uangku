with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

import re

new_fab = """        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("quick_add_transaction_fab")
                    .padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Quick Add Expense")
            }
        }"""

pattern = r"floatingActionButton = \{\s*Column\(\s*horizontalAlignment = Alignment\.End,\s*verticalArrangement = Arrangement\.spacedBy\(10\.dp\)\s*\)\s*\{\s*// BUTTON FOR PRIVATE LOCAL ML ENGINE REPORT.*?\}\s*\}"

content = re.sub(pattern, new_fab, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)

print("Dashboard FAB fixed")

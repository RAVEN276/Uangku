with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

import re

# Find:
#    Box(
#        modifier = modifier
#            .fillMaxSize()
#            .background(
#                Brush.verticalGradient(
#                    colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
#                             else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
#                )
#            )
#    ) {
#        Column(
#            modifier = Modifier.fillMaxSize()
#        ) {

old_str = """    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (themeDark) listOf(Color(0xFF0F172A), Color(0xFF020617))
                             else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {"""

new_str = """    Scaffold(
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

content = content.replace(old_str, new_str)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("Updated Transactions to use Scaffold (v2)")

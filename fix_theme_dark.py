with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "    val currencyFormat = NumberFormat.getCurrencyInstance(Locale(\"id\", \"ID\"))",
    "    val themeDark = androidx.compose.foundation.isSystemInDarkTheme()\n    val currencyFormat = NumberFormat.getCurrencyInstance(Locale(\"id\", \"ID\"))"
)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
print("themeDark added to TransactionItemRow")

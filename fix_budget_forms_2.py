with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "placeholder = { Text(\"default: 80\") },\n                        modifier = Modifier.fillMaxWidth(),",
    "placeholder = { Text(\"default: 80\") },\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth(),"
)

content = content.replace(
    "placeholder = { Text(\"misal: Beli Laptop Baru\") },\n                        modifier = Modifier.fillMaxWidth(),",
    "placeholder = { Text(\"misal: Beli Laptop Baru\") },\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth(),"
)

content = content.replace(
    "placeholder = { Text(\"misal: Desember 2026\") },\n                        modifier = Modifier.fillMaxWidth(),",
    "placeholder = { Text(\"misal: Desember 2026\") },\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth(),"
)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Budget forms fixed!")

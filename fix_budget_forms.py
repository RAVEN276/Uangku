with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "visualTransformation = RupiahVisualTransformation(),\n                        modifier = Modifier.fillMaxWidth()",
    "visualTransformation = RupiahVisualTransformation(),\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                        modifier = Modifier.fillMaxWidth()",
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "singleLine = true,\n                        modifier = Modifier.fillMaxWidth()",
    "singleLine = true,\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Budget forms updated!")

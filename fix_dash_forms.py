with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Add RoundedCornerShape(24.dp) to OutlinedTextFields in QuickAdd and AddBill dialogs
content = content.replace(
    "visualTransformation = RupiahVisualTransformation(),\n                        modifier = Modifier.fillMaxWidth()",
    "visualTransformation = RupiahVisualTransformation(),\n                        shape = RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                        modifier = Modifier.fillMaxWidth()",
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                        shape = RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "singleLine = true,\n                        modifier = Modifier.fillMaxWidth()",
    "singleLine = true,\n                        shape = RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "singleLine = true,\n                            modifier = Modifier.weight(1f)",
    "singleLine = true,\n                            shape = RoundedCornerShape(24.dp),\n                            modifier = Modifier.weight(1f)"
)

# Also fix the surface sizes of the dialogs. 
content = content.replace("shape = RoundedCornerShape(24.dp),\n                color = MaterialTheme.colorScheme.surface,", "shape = RoundedCornerShape(32.dp),\n                color = MaterialTheme.colorScheme.surface,")
content = content.replace("shape = RoundedCornerShape(28.dp),\n                color = MaterialTheme.colorScheme.surface,", "shape = RoundedCornerShape(32.dp),\n                color = MaterialTheme.colorScheme.surface,")

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
print("Dashboard forms updated!")

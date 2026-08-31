with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "shape = RoundedCornerShape(24.dp)\n            ) {",
    "shape = RoundedCornerShape(32.dp)\n            ) {"
)

content = content.replace(
    "singleLine = true,\n                        modifier = Modifier.fillMaxWidth()",
    "singleLine = true,\n                        shape = RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

content = content.replace(
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),\n                        modifier = Modifier.fillMaxWidth()",
    "keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),\n                        shape = RoundedCornerShape(24.dp),\n                        modifier = Modifier.fillMaxWidth()"
)

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)
print("Settings forms updated!")

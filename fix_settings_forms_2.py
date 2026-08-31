with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "singleLine = true,\n                        modifier = Modifier",
    "singleLine = true,\n                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),\n                        modifier = Modifier"
)

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(content)
print("Settings forms fixed!")

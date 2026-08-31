with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

target = "    }\n}\n\n@Composable\nfun SecuritySettingRow("
replacement = "    }\n        }\n    }\n}\n\n@Composable\nfun SecuritySettingRow("

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
        f.write(content)
    print("Fixed settings braces")
else:
    print("Target not found")

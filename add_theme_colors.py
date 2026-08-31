import os

files_to_check = [
    'app/src/main/java/com/example/ui/screens/DashboardScreen.kt',
    'app/src/main/java/com/example/ui/screens/SettingsScreen.kt',
    'app/src/main/java/com/example/ui/screens/BudgetScreen.kt'
]

colors_code = """
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color(0xFFC084FC) else androidx.compose.ui.graphics.Color(0xFF6D28D9),
                            unfocusedBorderColor = if (themeDark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.15f)
                        )"""

for file_path in files_to_check:
    with open(file_path, 'r') as f:
        content = f.read()

    # Budget forms have: modifier = Modifier.fillMaxWidth()
    # But wait, there are also OutlinedTextField defaults we need to add.
    
    # We can replace: modifier = Modifier.fillMaxWidth()
    # with: modifier = Modifier.fillMaxWidth(), colors = ...
    
    if "OutlinedTextFieldDefaults" not in content or file_path.endswith('BudgetScreen.kt'):
        content = content.replace(
            "modifier = Modifier.fillMaxWidth()\n                    )",
            "modifier = Modifier.fillMaxWidth()," + colors_code + "\n                    )"
        )
        content = content.replace(
            "modifier = Modifier.fillMaxWidth()\n                )",
            "modifier = Modifier.fillMaxWidth()," + colors_code + "\n                )"
        )

        with open(file_path, 'w') as f:
            f.write(content)
        print(f"Updated {file_path}")


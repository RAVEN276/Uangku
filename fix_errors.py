import re

# Fix MainActivity.kt
with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Fix the import and package declaration
content = content.replace('import androidx.compose.animation.togetherWithpackage com.example', 'package com.example\nimport androidx.compose.animation.togetherWith')

# Fix fillMaxWidth and others by adding imports to MainActivity
imports = """
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.sp
"""
content = content.replace('import androidx.compose.ui.unit.dp', 'import androidx.compose.ui.unit.dp' + imports)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)


# Fix TransactionsScreen.kt
with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.ui.TransactionEntity', 'import com.example.data.model.Transaction')
content = content.replace('import com.example.ui.utils.RupiahVisualTransformation', 'import com.example.ui.components.RupiahVisualTransformation')
content = content.replace('TransactionEntity', 'Transaction')

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)

print("Fixed!")

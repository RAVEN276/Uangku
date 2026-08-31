with open('app/src/main/java/com/example/ui/FinanceViewModel.kt', 'r') as f:
    content = f.read()

if "import com.example.MainActivity" not in content:
    content = content.replace("package com.example.ui\n", "package com.example.ui\n\nimport com.example.MainActivity\n")

with open('app/src/main/java/com/example/ui/FinanceViewModel.kt', 'w') as f:
    f.write(content)

print("FinanceViewModel fixed")

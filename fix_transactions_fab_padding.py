with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

import re

pattern = r"\.padding\(bottom = 100\.dp, end = 24\.dp\), // Offset for bottom nav"
replacement = r".padding(bottom = 24.dp, end = 16.dp),"

content = re.sub(pattern, replacement, content)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("FAB padding updated")

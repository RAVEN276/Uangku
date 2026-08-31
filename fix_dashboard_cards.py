with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

import re

# Replace aspectRatio(1f) with height(130.dp) for the income/expense cards
pattern = r"\.weight\(1f\)\s*\.aspectRatio\(1f\)"
replacement = r".weight(1f)\n                        .height(130.dp)"
content = re.sub(pattern, replacement, content)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
print("Cards resized")

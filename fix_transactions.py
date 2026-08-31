with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

import re

# Remove filter button
pattern_filter = r"Box\(\s*modifier = Modifier\s*\.size\(56\.dp\)\s*\.clip\(RoundedCornerShape\(24\.dp\)\)\s*\.background\(if \(themeDark\) Color\(0xFF334155\) else Color\(0xFFF1F5F9\)\)\s*\.clickable \{ filterType = if \(filterType == \"ALL\"\) \"INCOME\" else if \(filterType == \"INCOME\"\) \"EXPENSE\" else \"ALL\" \},\s*contentAlignment = Alignment\.Center\s*\)\s*\{\s*Icon\(\s*Icons\.Default\.FilterList,\s*contentDescription = \"Filter\",\s*tint = if \(filterType != \"ALL\"\) Color\(0xFF10B981\) else MaterialTheme\.colorScheme\.onSurfaceVariant\s*\)\s*\}"

content = re.sub(pattern_filter, "", content)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("Filter button removed")

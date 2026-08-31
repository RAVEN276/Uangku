with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "Column(\n                    modifier = Modifier\n                        .fillMaxWidth()\n                        .clip(RoundedCornerShape(32.dp))",
    "Column(\n                    modifier = Modifier\n                        .fillMaxWidth()\n                        .animateContentSize()\n                        .clip(RoundedCornerShape(32.dp))"
)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
print("Dashboard animation added")

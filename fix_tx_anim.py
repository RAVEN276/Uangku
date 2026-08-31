with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)\n                        )",
    "modifier = Modifier.animateItem().padding(start = 8.dp, bottom = 8.dp)\n                        )"
)

content = content.replace(
    "Column(\n                            modifier = Modifier\n                                .fillMaxWidth()",
    "Column(\n                            modifier = Modifier\n                                .animateItem()\n                                .fillMaxWidth()"
)

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)
print("Transactions animation added")

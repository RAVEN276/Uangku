with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('viewModel.transactions', 'viewModel.allTransactions')
# Let's also fix the map iteration just to be absolutely safe
content = content.replace('groupedTxs.forEach { (dateStr, txList) ->', 'for ((dateStr, txList) in groupedTxs) {')
# Ensure closing brace for `for` loop if we replaced a `forEach` block!
# Wait, `groupedTxs.forEach { ... }` ends with `}`. `for (...) { ... }` also ends with `}`.
# So replace is exactly 1-to-1.

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.write(content)

print("Fixed TransactionsScreen.kt")

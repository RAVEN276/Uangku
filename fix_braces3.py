with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    lines = f.readlines()

# Remove the bad braces at lines 43 and 44
# Let's find exactly where they are
new_lines = []
for line in lines:
    if line.strip() == "}" and "import" in "".join(new_lines[-3:]):
        continue
    new_lines.append(line)

# Now we need to insert the braces right before `fun TransactionItemRowBento`
for i, line in enumerate(new_lines):
    if "fun TransactionItemRowBento" in line:
        # Go backwards to find the `@OptIn` and `@Composable` for this function
        insert_idx = i
        while insert_idx > 0 and ("@OptIn" in new_lines[insert_idx-1] or "@Composable" in new_lines[insert_idx-1]):
            insert_idx -= 1
        new_lines.insert(insert_idx, "}\n}\n")
        break

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.writelines(new_lines)
print("Braces properly moved")

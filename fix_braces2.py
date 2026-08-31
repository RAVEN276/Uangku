with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    lines = f.readlines()

# Remove the last two lines if they are just "}"
if lines[-1].strip() == "}":
    lines = lines[:-1]
if lines[-1].strip() == "}":
    lines = lines[:-1]

# Now insert two braces before "@OptIn(ExperimentalMaterial3Api::class)"
for i, line in enumerate(lines):
    if "@OptIn(ExperimentalMaterial3Api::class)" in line and "@Composable" in lines[i+1]:
        lines.insert(i, "}\n}\n")
        break

with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'w') as f:
    f.writelines(lines)
print("Braces moved to correct place")

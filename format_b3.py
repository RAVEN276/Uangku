with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    lines = f.readlines()

depth = 0
for i, line in enumerate(lines):
    depth += line.count('{')
    depth -= line.count('}')
    if "LazyColumn(" in line:
        print(f"LazyColumn found at Line {i+1} [Depth after line: {depth}]: {line.strip()}")

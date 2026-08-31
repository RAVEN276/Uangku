with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    lines = f.readlines()

depth = 0
for i, line in enumerate(lines):
    depth += line.count('{')
    depth -= line.count('}')
    if 680 <= i+1 <= 720:
        print(f"Line {i+1} [Depth: {depth}]: {line.strip()}")

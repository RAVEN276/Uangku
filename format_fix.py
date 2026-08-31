with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    lines = f.readlines()
depth = 0
for i, line in enumerate(lines):
    depth += line.count('{')
    depth -= line.count('}')
    if depth < 0:
        print(f"L{i+1}: D{depth} : {line.strip()[:40]}")

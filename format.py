with open('app/src/main/java/com/example/ui/screens/TransactionsScreen.kt', 'r') as f:
    lines = f.readlines()

depth = 0
for i, line in enumerate(lines):
    depth += line.count('{')
    depth -= line.count('}')
    
print("Final depth:", depth)

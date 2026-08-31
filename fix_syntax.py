with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

bad_str = """        }
        }
    ) { innerPadding ->"""

good_str = """        }
    ) { innerPadding ->"""

if bad_str in content:
    content = content.replace(bad_str, good_str, 1)
    with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
        f.write(content)
    print("Fixed extra bracket.")
else:
    print("Pattern not found!")

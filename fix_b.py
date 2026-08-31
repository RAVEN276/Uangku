with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

old_close = """            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
        }
        }
    }

    // Modal dialogue limit set up"""

new_close = """            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Modal dialogue limit set up"""

content = content.replace(old_close, new_close)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Braces fixed")

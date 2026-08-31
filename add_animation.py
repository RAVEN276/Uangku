with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.animation.animateContentSize" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.animation.animateContentSize\nimport androidx.compose.runtime.Composable")

# For the cards, I will replace:
# modifier = Modifier
#     .fillMaxWidth()
# with
# modifier = Modifier
#     .fillMaxWidth()
#     .animateContentSize()

content = content.replace(
    "modifier = Modifier\n                                    .fillMaxWidth()",
    "modifier = Modifier\n                                    .fillMaxWidth()\n                                    .animateContentSize()"
)

content = content.replace(
    "modifier = Modifier\n                                .fillMaxWidth()",
    "modifier = Modifier\n                                .fillMaxWidth()\n                                .animateContentSize()"
)

content = content.replace(
    "modifier = Modifier\n            .fillMaxWidth()",
    "modifier = Modifier\n            .fillMaxWidth()\n            .animateContentSize()"
)

# Also let's animate the progress bars. We have LinearProgressIndicator. 
# It takes `progress = { progressFraction }` or `progress = progressFraction`.
# We can use `animateFloatAsState` for the progress.

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Added animateContentSize")

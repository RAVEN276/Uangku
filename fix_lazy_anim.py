with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

# For category budgets:
content = content.replace(
    "modifier = Modifier\n                                .fillMaxWidth()\n                                .animateContentSize()\n                                .testTag(\"category_budget_${budget.category}\"),",
    "modifier = Modifier\n                                .animateItem()\n                                .fillMaxWidth()\n                                .animateContentSize()\n                                .testTag(\"category_budget_${budget.category}\"),"
)

# For saving goals block:
content = content.replace(
    "SavingGoalCard(\n                            goal = goal,\n                            rubelFormat = rubelFormat,",
    "SavingGoalCard(\n                            modifier = Modifier.animateItem(),\n                            goal = goal,\n                            rubelFormat = rubelFormat,"
)

content = content.replace(
    "fun SavingGoalCard(\n    goal: com.example.data.model.SavingGoal,\n    rubelFormat: NumberFormat,",
    "fun SavingGoalCard(\n    modifier: Modifier = Modifier,\n    goal: com.example.data.model.SavingGoal,\n    rubelFormat: NumberFormat,"
)

content = content.replace(
    "Card(\n        modifier = Modifier\n            .fillMaxWidth()\n            .animateContentSize()\n            .testTag(\"saving_goal_card_${goal.id}\"),",
    "Card(\n        modifier = modifier\n            .fillMaxWidth()\n            .animateContentSize()\n            .testTag(\"saving_goal_card_${goal.id}\"),"
)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Added animateItem()")

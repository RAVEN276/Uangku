with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'r') as f:
    content = f.read()

# I will replace:
# val progressFraction = if ...
# with
# val targetProgressFraction = if ...
# val progressFraction by androidx.compose.animation.core.animateFloatAsState(targetValue = targetProgressFraction, animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessLow))

replace1 = """val progressFraction = if (overallBudget.limitAmount > 0) {
                                            (totalExpenseSum / overallBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }"""
target1 = """val targetProgressFraction = if (overallBudget.limitAmount > 0) {
                                            (totalExpenseSum / overallBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }
                                        val progressFraction by androidx.compose.animation.core.animateFloatAsState(
                                            targetValue = targetProgressFraction, 
                                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                            label = "progressAnim1"
                                        )"""

replace2 = """val progressFractionDaily = if (dailyBudget.limitAmount > 0) {
                                            (todayExpenseSum / dailyBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }"""
target2 = """val targetProgressFractionDaily = if (dailyBudget.limitAmount > 0) {
                                            (todayExpenseSum / dailyBudget.limitAmount).toFloat().coerceIn(0f, 1f)
                                        } else {
                                            0f
                                        }
                                        val progressFractionDaily by androidx.compose.animation.core.animateFloatAsState(
                                            targetValue = targetProgressFractionDaily,
                                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                            label = "progressAnim2"
                                        )"""

replace3 = """val progressFraction = if (budget.limitAmount > 0) {
                                    (spentOnCategory / budget.limitAmount).toFloat().coerceIn(0f, 1f)
                                } else {
                                    0f
                                }"""
target3 = """val targetProgressFraction = if (budget.limitAmount > 0) {
                                    (spentOnCategory / budget.limitAmount).toFloat().coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                                val progressFraction by androidx.compose.animation.core.animateFloatAsState(
                                    targetValue = targetProgressFraction,
                                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                    label = "progressAnim3"
                                )"""

replace4 = """val progressFraction = if (goal.targetAmount > 0) {
        (currentBalance / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }"""
target4 = """val targetProgressFraction = if (goal.targetAmount > 0) {
        (currentBalance / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressFraction by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetProgressFraction,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "progressAnim4"
    )"""

if replace1 in content: content = content.replace(replace1, target1)
if replace2 in content: content = content.replace(replace2, target2)
if replace3 in content: content = content.replace(replace3, target3)
if replace4 in content: content = content.replace(replace4, target4)

with open('app/src/main/java/com/example/ui/screens/BudgetScreen.kt', 'w') as f:
    f.write(content)
print("Animated progress added")

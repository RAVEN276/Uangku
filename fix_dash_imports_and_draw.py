with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Add animateContentSize import if not present
if "import androidx.compose.animation.animateContentSize" not in content:
    content = content.replace(
        "import androidx.compose.runtime.Composable", 
        "import androidx.compose.animation.animateContentSize\nimport androidx.compose.runtime.Composable"
    )

# The animatedSweepAngle is called inside Canvas { drawArc() }, which is DrawScope, not @Composable context. 
# We need to move the animation *outside* the Canvas block.

target = """                                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                    drawArc(
                                                        color = scoreColor.copy(alpha = if (themeDark) 0.15f else 0.1f),
                                                        startAngle = 140f,
                                                        sweepAngle = 260f,
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                            width = 9.dp.toPx(),
                                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                                        )
                                                    )
                                                    val animatedSweepAngle by androidx.compose.animation.core.animateFloatAsState(
                                                        targetValue = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                                        label = "scoreSweepAnim"
                                                    )
                                                    drawArc(
                                                        color = scoreColor,
                                                        startAngle = 140f,
                                                        sweepAngle = animatedSweepAngle,
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke("""

replacement = """                                                val animatedSweepAngle by androidx.compose.animation.core.animateFloatAsState(
                                                    targetValue = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                                    label = "scoreSweepAnim"
                                                )
                                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                    drawArc(
                                                        color = scoreColor.copy(alpha = if (themeDark) 0.15f else 0.1f),
                                                        startAngle = 140f,
                                                        sweepAngle = 260f,
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                            width = 9.dp.toPx(),
                                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                                        )
                                                    )
                                                    drawArc(
                                                        color = scoreColor,
                                                        startAngle = 140f,
                                                        sweepAngle = animatedSweepAngle,
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke("""

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
        f.write(content)
    print("Fixed animation position and import")
else:
    print("Target not found")

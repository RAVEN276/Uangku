with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

target = """                                                    drawArc(
                                                        color = scoreColor,
                                                        startAngle = 140f,
                                                        sweepAngle = 260f * (insights.financialHealthScore / 100f).coerceIn(0f, 1f),
                                                        useCenter = false,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke("""

replacement = """                                                    val animatedSweepAngle by androidx.compose.animation.core.animateFloatAsState(
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

if target in content:
    content = content.replace(target, replacement)
    
    # Let's change the card shape inside the dialog as well to 24.dp
    content = content.replace(
        "Card(\n                                        modifier = Modifier.fillMaxWidth(),\n                                        shape = RoundedCornerShape(20.dp),",
        "Card(\n                                        modifier = Modifier.fillMaxWidth().animateContentSize(),\n                                        shape = RoundedCornerShape(24.dp),"
    )

    with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
        f.write(content)
    print("Updated ML animation and shapes")
else:
    print("Target not found")

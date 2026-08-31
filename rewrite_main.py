import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# I will replace the Scaffold bottomBar and main content area

main_screen_pattern = r"(@Composable\s*fun MainScreenContent\(viewModel: FinanceViewModel\) \{)(.*)"

def new_main_content():
    return """@Composable
fun MainScreenContent(viewModel: FinanceViewModel) {
    var activeScreen by remember { mutableStateOf<SubScreen>(SubScreen.Dashboard) }

    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_authenticated_scaffold"),
        bottomBar = {
            // Bento Floating Navigation Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp) // Floating margin
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
                        .androidx.compose.foundation.background(
                            if (themeDark) androidx.compose.ui.graphics.Color(0xFF15221D).copy(alpha = 0.9f)
                            else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f)
                        )
                        .androidx.compose.foundation.border(
                            1.dp,
                            if (themeDark) androidx.compose.ui.graphics.Color(0xFF064E3B)
                            else androidx.compose.ui.graphics.Color(0xFFA7F3D0),
                            androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    val screenList = listOf(
                        SubScreen.Dashboard,
                        SubScreen.Transactions,
                        SubScreen.Budgets,
                        SubScreen.Settings
                    )
                    screenList.forEach { screen ->
                        val isSelected = activeScreen == screen
                        val animatedWeight by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (isSelected) 1.5f else 1f,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                            )
                        )
                        val animatedColor by androidx.compose.animation.animateColorAsState(
                            targetValue = if (isSelected) {
                                if (themeDark) androidx.compose.ui.graphics.Color(0xFF10B981) else androidx.compose.ui.graphics.Color(0xFF059669)
                            } else {
                                androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            }
                        )

                        androidx.compose.foundation.layout.Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(animatedWeight)
                                .androidx.compose.foundation.clickable(
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null,
                                    onClick = { activeScreen = screen }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = animatedColor,
                                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                            )
                            androidx.compose.animation.AnimatedVisibility(
                                visible = isSelected,
                                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                            ) {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = animatedColor,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        // Animated Non-Linear Transitions for Main Content
        androidx.compose.animation.AnimatedContent(
            targetState = activeScreen,
            transitionSpec = {
                // Determine direction based on route index
                val routes = listOf(SubScreen.Dashboard.route, SubScreen.Transactions.route, SubScreen.Budgets.route, SubScreen.Settings.route)
                val isForward = routes.indexOf(targetState.route) > routes.indexOf(initialState.route)
                
                if (isForward) {
                    (androidx.compose.animation.slideInHorizontally(initialOffsetX = { it }) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))) togetherWith
                        androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -it }) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                } else {
                    (androidx.compose.animation.slideInHorizontally(initialOffsetX = { -it }) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))) togetherWith
                        androidx.compose.animation.slideOutHorizontally(targetOffsetX = { it }) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { currentScreen ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding() - 24.dp) // Adjust for floating bar
            ) {
                when (currentScreen) {
                    SubScreen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTransactions = { activeScreen = SubScreen.Transactions }
                    )
                    SubScreen.Transactions -> TransactionsScreen(viewModel = viewModel)
                    SubScreen.Budgets -> BudgetScreen(viewModel = viewModel)
                    SubScreen.Settings -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
"""

match = re.search(main_screen_pattern, content, flags=re.DOTALL)
if match:
    new_content = content[:match.start()] + new_main_content()
    
    # Also add import for togetherWith
    new_content = "import androidx.compose.animation.togetherWith\n" + new_content
    
    with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
        f.write(new_content)
    print("MainActivity rewritten.")
else:
    print("MainScreenContent not found.")

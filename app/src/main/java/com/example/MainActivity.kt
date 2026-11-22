package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FinanceViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.ExpressiveShapeTokens
import com.example.ui.theme.MotionTokens
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.AppStrings

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FinanceViewModel = viewModel()
            val systemDark = isSystemInDarkTheme()

            // Update system default theme on startup to ensure VM and UI are synchronized
            viewModel.updateSystemThemeDefault(systemDark)

            LaunchedEffect(Unit) {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    val permission = "android.permission.POST_NOTIFICATIONS"
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission
                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(arrayOf(permission), 101)
                    }
                }
                viewModel.checkAndTriggerBillReminders(this@MainActivity)
                viewModel.loadSavingChallengesAndBadges()
            }

            val themeDarkOverride by viewModel.themeDark.collectAsStateWithLifecycle()
            val dynamicColorEnabled by viewModel.dynamicColor.collectAsStateWithLifecycle()
            val useDarkTheme = themeDarkOverride

            MyApplicationTheme(darkTheme = useDarkTheme, dynamicColor = dynamicColorEnabled) {
                val isOnboarded by viewModel.isOnboarded.collectAsStateWithLifecycle()
                val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
                val initialRoute = intent?.getStringExtra("EXTRA_NAVIGATE_TO") ?: intent?.getStringExtra("NAVIGATE_TO")

                if (!isOnboarded) {
                    OnboardingScreen(viewModel = viewModel)
                } else if (isLocked) {
                    AuthScreen(
                        viewModel = viewModel,
                        onTriggerBiometric = {
                            showBiometricPrompt(
                                onSuccess = {
                                    viewModel.unlockWithBiometrics()
                                    Toast.makeText(this@MainActivity, "Autentikasi sidik jari berhasil!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                } else {
                    MainScreenContent(viewModel = viewModel, initialDestination = initialRoute)
                }
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Sidik jari tidak cocok / tidak dikenali.")
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autentikasi Biometrik Uangku")
            .setSubtitle("Gunakan sensor sidik jari perangkat untuk masuk secara aman.")
            .setNegativeButtonText("Gunakan PIN")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Biometrik tidak didukung.")
        }
    }
}

sealed class SubScreen(val route: String, val icon: ImageVector) {
    fun getTitle(lang: String): String = when (this) {
        is Dashboard -> AppStrings.navDashboard(lang)
        is Transactions -> AppStrings.navTransactions(lang)
        is Budgets -> AppStrings.navBudgets(lang)
        is Settings -> AppStrings.navSettings(lang)
    }

    object Dashboard : SubScreen("dashboard", Icons.Default.Home)
    object Transactions : SubScreen("transactions", Icons.AutoMirrored.Filled.ListAlt)
    object Budgets : SubScreen("budgets", Icons.Default.NotificationsActive)
    object Settings : SubScreen("settings", Icons.Default.Settings)
}

@Composable
fun MainScreenContent(
    viewModel: FinanceViewModel,
    initialDestination: String? = null
) {
    val initialSubScreen = when (initialDestination?.lowercase()) {
        "budget", "budgets" -> SubScreen.Budgets
        "transactions", "transaction" -> SubScreen.Transactions
        "settings" -> SubScreen.Settings
        else -> SubScreen.Dashboard
    }
    var activeScreen by remember { mutableStateOf<SubScreen>(initialSubScreen) }
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val screenList = remember {
        listOf(
            SubScreen.Dashboard,
            SubScreen.Transactions,
            SubScreen.Budgets,
            SubScreen.Settings
        )
    }

    // Handle back button on sub-screens to return to Dashboard per guidelines
    androidx.activity.compose.BackHandler(enabled = activeScreen != SubScreen.Dashboard) {
        activeScreen = SubScreen.Dashboard
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("main_authenticated_container")
    ) {
        val isWideLayout = maxWidth >= 600.dp

        if (isWideLayout) {
            Row(modifier = Modifier.fillMaxSize()) {
                // M3 Expressive Side NavigationRail on Tablet / Foldable / Landscape
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("floating_navigation_bar")
                ) {
                    NavigationRail(
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 6.dp, vertical = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        screenList.forEach { screen ->
                            val isSelected = activeScreen == screen
                            val title = screen.getTitle(appLanguage)
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        activeScreen = screen
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .testTag("nav_tab_${screen.route}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    AnimatedContent(
                        targetState = activeScreen,
                        transitionSpec = { MotionTokens.topLevelTransition },
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 960.dp),
                        label = "wide_screen_content"
                    ) { currentScreen ->
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
        } else {
            // Compact Portrait Phone: Fullscreen content + Floating Expressive Toolbar
            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = { MotionTokens.topLevelTransition },
                modifier = Modifier.fillMaxSize(),
                label = "compact_screen_content"
            ) { currentScreen ->
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

            // Material 3 Expressive Floating Toolbar Dock
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("floating_navigation_bar"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.widthIn(max = 460.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 6.dp,
                    shadowElevation = 10.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        screenList.forEach { screen ->
                            val isSelected = activeScreen == screen
                            val screenTitle = screen.getTitle(appLanguage)
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()

                            val pillCorner by animateDpAsState(
                                targetValue = when {
                                    isPressed -> ExpressiveShapeTokens.PillPressedCorner
                                    isSelected -> ExpressiveShapeTokens.PillSelectedCorner
                                    else -> ExpressiveShapeTokens.PillRestingCorner
                                },
                                animationSpec = MotionTokens.spatialSpringDp,
                                label = "nav_corner_${screen.route}"
                            )

                            val pillScale by animateFloatAsState(
                                targetValue = if (isPressed) 0.94f else 1f,
                                animationSpec = MotionTokens.spatialSnappyFloat,
                                label = "nav_scale_${screen.route}"
                            )

                            val pillBackground by animateColorAsState(
                                targetValue = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else Color.Transparent,
                                animationSpec = MotionTokens.colorTransition,
                                label = "nav_bg_${screen.route}"
                            )

                            val contentColor by animateColorAsState(
                                targetValue = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                animationSpec = MotionTokens.colorTransition,
                                label = "nav_fg_${screen.route}"
                            )

                            val pillShape = RoundedCornerShape(pillCorner)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 52.dp, minHeight = 48.dp)
                                    .heightIn(min = 48.dp)
                                    .graphicsLayer {
                                        scaleX = pillScale
                                        scaleY = pillScale
                                    }
                                    .clip(pillShape)
                                    .testTag("nav_tab_${screen.route}")
                                    .background(pillBackground)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = ripple(),
                                        role = Role.Tab,
                                        onClick = {
                                            if (!isSelected) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                activeScreen = screen
                                            }
                                        }
                                    )
                                    .padding(
                                        horizontal = if (isSelected) 16.dp else 12.dp,
                                        vertical = 8.dp
                                    )
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screenTitle,
                                    tint = contentColor,
                                    modifier = Modifier.size(22.dp)
                                )

                                AnimatedVisibility(
                                    visible = isSelected,
                                    enter = expandHorizontally(MotionTokens.spatialSpringDp.let {
                                        androidx.compose.animation.core.spring(
                                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                                            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                                        )
                                    }) + fadeIn(MotionTokens.effectsFloat),
                                    exit = shrinkHorizontally(
                                        androidx.compose.animation.core.spring(
                                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                                            stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
                                        )
                                    ) + fadeOut(MotionTokens.fastEffectsFloat)
                                ) {
                                    Text(
                                        text = screenTitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor,
                                        modifier = Modifier.padding(start = 7.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.MyApplicationTheme

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
            val useDarkTheme = themeDarkOverride
            
            MyApplicationTheme(darkTheme = useDarkTheme) {
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

sealed class SubScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : SubScreen("dashboard", "Dasbor", Icons.Default.Home)
    object Transactions : SubScreen("transactions", "Transaksi", Icons.AutoMirrored.Filled.ListAlt)
    object Budgets : SubScreen("budgets", "Anggaran", Icons.Default.NotificationsActive)
    object Settings : SubScreen("settings", "Pengaturan", Icons.Default.Settings)
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
    val themeDark by viewModel.themeDark.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_authenticated_container")
    ) {
        // Screen Content
        AnimatedContent(
            targetState = activeScreen,
            transitionSpec = {
                val routes = listOf(SubScreen.Dashboard.route, SubScreen.Transactions.route, SubScreen.Budgets.route, SubScreen.Settings.route)
                val isForward = routes.indexOf(targetState.route) > routes.indexOf(initialState.route)
                
                if (isForward) {
                    (slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300))) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(300))
                } else {
                    (slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(300))) togetherWith
                        slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(300))
                }
            },
            modifier = Modifier.fillMaxSize()
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

        // Floating Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .testTag("floating_navigation_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        if (themeDark) Color(0xFF0F172A).copy(alpha = 0.92f)
                        else Color(0xFFFFFFFF).copy(alpha = 0.92f)
                    )
                    .border(
                        1.5.dp,
                        if (themeDark) Color(0xFF10B981).copy(alpha = 0.35f)
                        else Color(0xFF10B981).copy(alpha = 0.25f),
                        RoundedCornerShape(36.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val screenList = listOf(
                    SubScreen.Dashboard,
                    SubScreen.Transactions,
                    SubScreen.Budgets,
                    SubScreen.Settings
                )

                screenList.forEach { screen ->
                    val isSelected = activeScreen == screen

                    val pillBackground by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (themeDark) Color(0xFF10B981).copy(alpha = 0.2f)
                            else Color(0xFF10B981).copy(alpha = 0.15f)
                        } else Color.Transparent,
                        animationSpec = tween(300),
                        label = "pill_bg"
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (themeDark) Color(0xFF34D399) else Color(0xFF059669)
                        } else {
                            if (themeDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        },
                        animationSpec = tween(300),
                        label = "content_color"
                    )

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "icon_scale"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(pillBackground)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = androidx.compose.material3.ripple(),
                                onClick = { activeScreen = screen }
                            )
                            .padding(
                                horizontal = if (isSelected) 16.dp else 12.dp,
                                vertical = 10.dp
                            )
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = contentColor,
                            modifier = Modifier
                                .size(24.dp)
                                .animateContentSize()
                        )

                        AnimatedVisibility(
                            visible = isSelected,
                            enter = expandHorizontally(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
                            exit = shrinkHorizontally(spring(stiffness = Spring.StiffnessMedium)) + fadeOut()
                        ) {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = contentColor,
                                modifier = Modifier.padding(start = 6.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

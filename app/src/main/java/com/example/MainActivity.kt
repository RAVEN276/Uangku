package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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

            androidx.compose.runtime.LaunchedEffect(Unit) {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    val permission = "android.permission.POST_NOTIFICATIONS"
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
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
                    MainScreenContent(viewModel = viewModel)
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
fun MainScreenContent(viewModel: FinanceViewModel) {
    var activeScreen by remember { mutableStateOf<SubScreen>(SubScreen.Dashboard) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_authenticated_scaffold"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_bottom_nav_bar")
            ) {
                val screenList = listOf(
                    SubScreen.Dashboard,
                    SubScreen.Transactions,
                    SubScreen.Budgets,
                    SubScreen.Settings
                )

                screenList.forEach { screen ->
                    val isSelected = activeScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeScreen = screen },
                        label = { Text(text = screen.title) },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeScreen) {
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

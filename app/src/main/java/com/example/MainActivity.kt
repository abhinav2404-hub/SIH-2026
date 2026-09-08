package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.OtpVerificationScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.guide.LegalMetrologyGuideScreen
import com.example.ui.guide.LegalMetrologyHelpScreen
import com.example.ui.history.InspectionHistoryScreen
import com.example.ui.scanner.AnalysisResultScreen
import com.example.ui.scanner.CameraScreen
import com.example.ui.scanner.ScannerScreen
import com.example.ui.theme.LegalMetrologyTheme
import com.example.ui.tools.LegalMetrologyToolsScreen

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                com.example.ui.ThemeMode.DARK -> true
                com.example.ui.ThemeMode.LIGHT -> false
                com.example.ui.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            LegalMetrologyTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LegalMetrologyApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LegalMetrologyApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (currentScreen) {
        AppScreen.LOGIN -> LoginScreen(viewModel = viewModel)
        AppScreen.OTP_VERIFY -> OtpVerificationScreen(viewModel = viewModel)
        AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
        AppScreen.SCANNER -> ScannerScreen(viewModel = viewModel)
        AppScreen.CAMERA -> CameraScreen(viewModel = viewModel)
        AppScreen.ANALYSIS_RESULT -> AnalysisResultScreen(viewModel = viewModel)
        AppScreen.HISTORY -> InspectionHistoryScreen(viewModel = viewModel)
        AppScreen.TOOLS -> LegalMetrologyToolsScreen(viewModel = viewModel)
        AppScreen.RULE_GUIDE -> LegalMetrologyGuideScreen(viewModel = viewModel)
        AppScreen.HELP_SEARCH -> LegalMetrologyHelpScreen(viewModel = viewModel)
        else -> DashboardScreen(viewModel = viewModel)
    }
}

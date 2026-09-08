package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MetrologyNavyDark,
    onPrimary = Color(0xFF0C3900),
    primaryContainer = Color(0xFF205107),
    onPrimaryContainer = Color(0xFFB8F397),
    secondary = MetrologyGoldDark,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = MetrologyTealLight,
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Color(0xFFFFB4AB),
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PolishGreen,
    onPrimary = Color.White,
    primaryContainer = PolishGreenContainer,
    onPrimaryContainer = Color(0xFF072100),
    secondary = MetrologyGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B8),
    onSecondaryContainer = Color(0xFF2B1700),
    tertiary = PolishGreenDark,
    background = SurfaceLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceCardLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ComplianceFail,
    outline = BorderLight
)

@Composable
fun LegalMetrologyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

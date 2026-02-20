package com.example.smartread.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Theme state holder
data class AppThemeState(
    val isDarkTheme: Boolean,
    val toggleTheme: () -> Unit
)

val LocalAppTheme = compositionLocalOf<AppThemeState> {
    error("No AppThemeState provided")
}

private val DarkColorScheme = darkColorScheme(
    primary = StudyBlueDark,
    onPrimary = DarkBackground,
    primaryContainer = StudyBlueDarkVariant,
    onPrimaryContainer = Color.White,
    secondary = AccentPurpleDark,
    onSecondary = DarkBackground,
    tertiary = RewardOrangeDark,
    onTertiary = DarkBackground,
    tertiaryContainer = RewardOrangeDarkVariant,
    onTertiaryContainer = DarkBackground,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0)
)

private val LightColorScheme = lightColorScheme(
    primary = StudyBlue,
    onPrimary = Color.White,
    primaryContainer = StudyBlueLight,
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = AccentPurple,
    onSecondary = Color.White,
    tertiary = RewardOrange,
    onTertiary = Color.White,
    tertiaryContainer = RewardOrangeLight,
    onTertiaryContainer = Color(0xFFE65100),
    background = LightBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = LightSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE3E7EB),
    onSurfaceVariant = Color(0xFF424242)
)

@Composable
fun SmartReadTheme(
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
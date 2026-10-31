package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Milkify Vibrant Color Scheme: Red, White, Black, Blue, Green, Yellow
private val LightColorScheme = lightColorScheme(
    primary = MilkifyRed,
    onPrimary = MilkifyWhite,
    primaryContainer = MilkifyRedLight,
    onPrimaryContainer = MilkifyRedDark,
    secondary = MilkifyBlue,
    onSecondary = MilkifyWhite,
    secondaryContainer = MilkifyBlueContainer,
    onSecondaryContainer = MilkifyBlueDark,
    tertiary = MilkifyGreen,
    onTertiary = MilkifyWhite,
    tertiaryContainer = MilkifyGreenContainer,
    onTertiaryContainer = MilkifyGreenDark,
    background = MilkifyBackground,
    surface = MilkifyWhite,
    onBackground = MilkifyTextPrimary,
    onSurface = MilkifyTextPrimary,
    surfaceVariant = MilkifyOffWhite,
    onSurfaceVariant = MilkifyTextSecondary,
    outline = MilkifyBorder,
    error = MilkifyRedDark,
    errorContainer = MilkifyRedLight
)

private val DarkColorScheme = darkColorScheme(
    primary = MilkifyRedAccent,
    onPrimary = MilkifyBlack,
    primaryContainer = MilkifyRedDark,
    onPrimaryContainer = MilkifyWhite,
    secondary = MilkifyBlueLight,
    onSecondary = MilkifyBlack,
    tertiary = MilkifyGreenLight,
    background = MilkifyBlack,
    surface = MilkifyCharcoal,
    onBackground = MilkifyWhite,
    onSurface = MilkifyWhite,
    surfaceVariant = MilkifyDarkGrey,
    onSurfaceVariant = Color(0xFFDDDDDD),
    outline = Color(0xFF444444)
)

@Composable
fun MilkifyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MilkifyTheme(darkTheme = darkTheme, content = content)
}

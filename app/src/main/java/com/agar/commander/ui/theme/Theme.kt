package com.agar.commander.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AgarColorScheme = darkColorScheme(
    primary = AgarGreen,
    onPrimary = Color(0xFF092116),
    primaryContainer = AgarGreenDark,
    onPrimaryContainer = AgarText,
    secondary = AgarBlue,
    onSecondary = Color(0xFF0B1F27),
    tertiary = AgarAmber,
    onTertiary = Color(0xFF271A04),
    error = AgarRed,
    onError = Color(0xFF2B0909),
    errorContainer = AgarRedDark,
    onErrorContainer = AgarText,
    background = AgarBackground,
    onBackground = AgarText,
    surface = AgarSurface,
    onSurface = AgarText,
    surfaceVariant = AgarSurfaceRaised,
    onSurfaceVariant = AgarTextMuted,
    outline = AgarBorder,
)

@Composable
fun AgarCommanderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AgarColorScheme,
        typography = AgarTypography,
        content = content,
    )
}

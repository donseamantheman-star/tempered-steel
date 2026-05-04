package com.temperedsteel.tracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary            = Accent,
    onPrimary          = Color(0xFF001E2E),
    primaryContainer   = AccentDim,
    onPrimaryContainer = Accent,
    secondary          = ProgressGreen,
    tertiary           = ProgressAmber,
    background         = Background,
    surface            = Surface,
    surfaceVariant     = SurfaceVariant,
    onBackground       = OnSurface,
    onSurface          = OnSurface,
    onSurfaceVariant   = OnSurfaceDim,
    outline            = OnSurfaceFaint,
    error              = ProgressRed
)

@Composable
fun TemperedSteelTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = AppTypography, content = content)
}

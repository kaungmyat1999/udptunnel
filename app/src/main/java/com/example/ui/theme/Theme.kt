package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberDarkBackground,
    primaryContainer = CyberDarkSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberEmerald,
    onSecondary = CyberDarkBackground,
    secondaryContainer = CyberDarkSurface,
    onSecondaryContainer = CyberEmerald,
    tertiary = CyberPurple,
    onTertiary = CyberDarkBackground,
    background = CyberDarkBackground,
    onBackground = CyberTextPrimary,
    surface = CyberDarkSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberDarkSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberDarkCardBorder,
    error = CyberNeonPink,
    onError = CyberDarkBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = CyberColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

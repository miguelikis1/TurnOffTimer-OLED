package com.oled.sleeptimer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OledDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = OledBlack,
    primaryContainer = AccentCyanDark,
    onPrimaryContainer = TextWhite,
    secondary = AccentIndigo,
    onSecondary = TextWhite,
    background = OledBlack,
    onBackground = TextWhite,
    surface = OledBlack,
    onSurface = TextWhite,
    surfaceVariant = OledSurface,
    onSurfaceVariant = TextSecondary,
    outline = OledBorder,
    outlineVariant = OledBorderSubtle
)

@Composable
fun OledSleepTimerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = OledDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = OledBlack.toArgb()
                window.navigationBarColor = OledBlack.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

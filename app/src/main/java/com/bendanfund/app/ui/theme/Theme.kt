package com.bendanfund.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = TextOnPrimary,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Yellow500,
    onSecondary = TextPrimary,
    secondaryContainer = Yellow700,
    onSecondaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Green50,
    onSurfaceVariant = TextSecondary,
    error = Red500,
    onError = TextOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = Green400,
    onPrimary = TextPrimary,
    primaryContainer = Green700,
    onPrimaryContainer = Green100,
    secondary = Yellow500,
    onSecondary = TextPrimary,
    secondaryContainer = Yellow700,
    onSecondaryContainer = TextPrimary,
    background = BackgroundDark,
    onBackground = TextOnPrimary,
    surface = SurfaceDark,
    onSurface = TextOnPrimary,
    surfaceVariant = Green900,
    onSurfaceVariant = Green200,
    error = Red500,
    onError = TextOnPrimary
)

@Composable
fun BenDanFundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.gmaldonado.taxflow_movil.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TaxflowNavy,
    onPrimary = TaxflowOnPrimary,
    primaryContainer = TaxflowPrimaryContainer,
    onPrimaryContainer = TaxflowOnPrimary,
    secondary = TaxflowEmerald,
    onSecondary = TaxflowOnPrimary,
    secondaryContainer = TaxflowEmeraldContainer,
    onSecondaryContainer = TaxflowOnSecondaryContainer,
    tertiary = TaxflowAmber,
    onTertiary = TaxflowNavy,
    tertiaryContainer = TaxflowAmberContainer,
    background = TaxflowBackground,
    onBackground = TaxflowTextPrimary,
    surface = TaxflowSurface,
    onSurface = TaxflowTextPrimary,
    surfaceVariant = TaxflowSurfaceContainerLow,
    onSurfaceVariant = TaxflowTextMuted,
    outline = TaxflowOutline,
    outlineVariant = TaxflowBorder,
    error = TaxflowCoralRed,
    errorContainer = TaxflowRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = TaxflowEmerald,
    onPrimary = TaxflowNavy,
    primaryContainer = TaxflowDarkSlate,
    onPrimaryContainer = TaxflowSurfaceContainerLowest,
    secondary = TaxflowEmerald,
    onSecondary = TaxflowNavy,
    secondaryContainer = TaxflowEmeraldDark,
    onSecondaryContainer = TaxflowEmeraldContainer,
    tertiary = TaxflowAmber,
    background = TaxflowNavy,
    onBackground = TaxflowSurfaceContainerLowest,
    surface = TaxflowDarkSlate,
    onSurface = TaxflowSurfaceContainerLowest,
    surfaceVariant = TaxflowNavy,
    onSurfaceVariant = TaxflowSurfaceContainerHigh,
    outline = TaxflowOutline,
    outlineVariant = TaxflowDarkSlate,
    error = TaxflowCoralRed
)

@Composable
fun Taxflow_MovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to ensure Taxflow brand colors are respected
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
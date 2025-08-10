package com.android.expensetracker.ui.theme

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel


private val DarkColorScheme = darkColorScheme(
    primary = DarkGreenPrimary,
    onPrimary = DarkGreenOnPrimary,
    primaryContainer = DarkGreenPrimaryContainer,
    onPrimaryContainer = DarkGreenOnPrimaryContainer,
    secondary = DarkGreenSecondary,
    onSecondary = DarkGreenOnSecondary,
    secondaryContainer = DarkGreenSecondaryContainer,
    onSecondaryContainer = DarkGreenOnSecondaryContainer,
    tertiary = DarkGreenTertiary,
    onTertiary = DarkGreenOnTertiary,
    tertiaryContainer = DarkGreenTertiaryContainer,
    onTertiaryContainer = DarkGreenOnTertiaryContainer,
    error = DarkGreenError,
    onError = DarkGreenOnError,
    background = DarkGreenBackground,
    onBackground = DarkGreenOnBackground,
    surface = DarkGreenSurface,
    onSurface = DarkGreenOnSurface,
    surfaceVariant = DarkGreenSurfaceVariant,
    onSurfaceVariant = DarkGreenOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreenPrimary,
    onPrimary = LightGreenOnPrimary,
    primaryContainer = LightGreenPrimaryContainer,
    onPrimaryContainer = LightGreenOnPrimaryContainer,
    secondary = LightGreenSecondary,
    onSecondary = LightGreenOnSecondary,
    secondaryContainer = LightGreenSecondaryContainer,
    onSecondaryContainer = LightGreenOnSecondaryContainer,
    tertiary = LightGreenTertiary,
    onTertiary = LightGreenOnTertiary,
    tertiaryContainer = LightGreenTertiaryContainer,
    onTertiaryContainer = LightGreenOnTertiaryContainer,
    error = LightGreenError,
    onError = LightGreenOnError,
    background = LightGreenBackground,
    onBackground = LightGreenOnBackground,
    surface = LightGreenSurface,
    onSurface = LightGreenOnSurface,
    surfaceVariant = LightGreenSurfaceVariant,
    onSurfaceVariant = LightGreenOnSurfaceVariant
)

@Composable
fun ExpenseTrackerTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val currentThemeSetting by themeViewModel.currentTheme.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    val useDarkTheme = when (currentThemeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM_DEFAULT -> systemIsDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Make sure Typography is defined in ui/theme/Typography.kt
        content = content
    )
}

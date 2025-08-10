package com.android.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4 // Dark theme
import androidx.compose.material.icons.filled.Brightness5 // System / Auto
import androidx.compose.material.icons.filled.Brightness7 // Light theme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.expensetracker.ui.theme.ThemeSetting
import com.android.expensetracker.ui.theme.ThemeViewModel

@Composable
fun ThemeSwitcherIcon(
    themeViewModel: ThemeViewModel = viewModel() // Use Hilt viewModel in a real app
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    val nextTheme: () -> Unit
    val icon: ImageVector
    val contentDescription: String

    when (currentTheme) {
        ThemeSetting.LIGHT -> {
            icon = Icons.Filled.Brightness7
            contentDescription = "Switch to Dark Theme"
            nextTheme = { themeViewModel.changeTheme(ThemeSetting.DARK) }
        }
        ThemeSetting.DARK -> {
            icon = Icons.Filled.Brightness4
            contentDescription = "Switch to System Default Theme"
            nextTheme = { themeViewModel.changeTheme(ThemeSetting.SYSTEM_DEFAULT) }
        }
        ThemeSetting.SYSTEM_DEFAULT -> {
            icon = Icons.Filled.Brightness5
            contentDescription = "Switch to Light Theme"
            nextTheme = { themeViewModel.changeTheme(ThemeSetting.LIGHT) }
        }
    }

    IconButton(onClick = nextTheme) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

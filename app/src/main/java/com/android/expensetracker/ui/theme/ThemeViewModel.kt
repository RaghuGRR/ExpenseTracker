package com.android.expensetracker.ui.theme

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

// No explicit viewModelScope.launch needed for SharedPreferences basic ops

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadThemeSetting())
    val currentTheme: StateFlow<ThemeSetting> = _currentTheme.asStateFlow()

    private fun loadThemeSetting(): ThemeSetting {
        val themeName = sharedPreferences.getString(KEY_THEME_SETTING, ThemeSetting.SYSTEM_DEFAULT.name)
        return try {
            ThemeSetting.valueOf(themeName ?: ThemeSetting.SYSTEM_DEFAULT.name)
        } catch (e: IllegalArgumentException) {
            ThemeSetting.SYSTEM_DEFAULT // Fallback if stored value is invalid
        }
    }

    private fun saveThemeSetting(themeSetting: ThemeSetting) {
        sharedPreferences.edit { putString(KEY_THEME_SETTING, themeSetting.name) }
    }

    fun changeTheme(newThemeSetting: ThemeSetting) {
        _currentTheme.value = newThemeSetting
        saveThemeSetting(newThemeSetting)
    }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_SETTING = "theme_setting_key"
    }
}
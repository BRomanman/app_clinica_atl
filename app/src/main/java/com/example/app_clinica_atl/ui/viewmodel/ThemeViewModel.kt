package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado del tema (Claro/Oscuro/Sistema).
 */
class ThemeViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Expone el Flow de preferencias de tema directamente
    val themeFlow = userPreferences.themeFlow

    /**
     * Guarda la nueva preferencia de tema.
     * @param theme El valor a guardar (ej: "LIGHT", "DARK", "SYSTEM")
     */
    fun saveThemePreference(theme: String) {
        viewModelScope.launch {
            userPreferences.saveThemePreference(theme)
        }
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.data.local.storage.UserPreferences // <-- NUEVO IMPORT

// Factory para crear AuthViewModel con UserRepository Y UserPreferences.
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences // <-- NUEVA DEPENDENCIA
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con ambas dependencias.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // *** CAMBIO CLAVE: Pasar ambas dependencias ***
            return AuthViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.data.local.storage.UserPreferences
// --- 1. IMPORTAR LA DEPENDENCIA QUE FALTA ---
import com.example.app_clinica_atl.data.repository.AppointmentRepository

// Factory para crear AuthViewModel con todas sus dependencias
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences,
    // --- 2. AÑADIR EL REPOSITORIO DE CITAS AL CONSTRUCTOR ---
    private val appointmentRepo: AppointmentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con las TRES dependencias.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // --- 3. PASAR LAS TRES DEPENDENCIAS AL VIEWMODEL ---
            return AuthViewModel(repository, userPreferences, appointmentRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
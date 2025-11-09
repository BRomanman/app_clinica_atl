package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.AppointmentRepository
// --- 1. IMPORTAR LA DEPENDENCIA QUE FALTA ---
import com.example.app_clinica_atl.data.repository.DoctorRepository

// Factory para crear AuthViewModel con todas sus dependencias
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences,
    private val appointmentRepo: AppointmentRepository,
    // --- 2. AÑADIR EL REPOSITORIO DE DOCTOR AL CONSTRUCTOR ---
    private val doctorRepo: DoctorRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con las CUATRO dependencias.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // --- 3. PASAR LAS CUATRO DEPENDENCIAS AL VIEWMODEL ---
            return AuthViewModel(repository, userPreferences, appointmentRepo, doctorRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
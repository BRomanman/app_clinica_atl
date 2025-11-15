package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.InsuranceRepository
import com.example.app_clinica_atl.data.repository.UserRepository

/**
 * Factory manual para crear PatientViewModel.
 * ¡CAMBIO! Ahora también inyecta AppointmentRepository.
 */
class PatientViewModelFactory(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: InsuranceRepository,
    private val appointmentRepository: AppointmentRepository // <-- ¡AÑADIDO!
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            return PatientViewModel(
                userRepository,
                userPreferences,
                insuranceRepository,
                appointmentRepository // <-- ¡PASADO AL VIEWMODEL!
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
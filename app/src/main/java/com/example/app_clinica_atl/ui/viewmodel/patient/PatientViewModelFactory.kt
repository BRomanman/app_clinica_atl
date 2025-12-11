package com.example.app_clinica_atl.ui.viewmodel.patient

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

/**
 * Factory manual para crear PatientViewModel.
 * ¡CAMBIO! Ahora también inyecta CitasRepository.
 */
class PatientViewModelFactory(
    private val application: Application,
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: SegurosRepository,
    private val appointmentRepository: CitasRepository // <-- ¡AÑADIDO!
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            return PatientViewModel(
                application,
                userRepository,
                userPreferences,
                insuranceRepository,
                appointmentRepository // <-- ¡PASADO AL VIEWMODEL!
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
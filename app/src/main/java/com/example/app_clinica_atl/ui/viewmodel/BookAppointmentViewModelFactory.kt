package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.DoctorRepository

/**
 * Factory manual para crear BookAppointmentViewModel.
 * Ahora inyecta TODAS las dependencias necesarias.
 */
class BookAppointmentViewModelFactory(
    private val doctorRepository: DoctorRepository,
    private val appointmentRepository: CitasRepository,
    private val userPreferences: UserPreferences // <-- Dependencia añadida
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookAppointmentViewModel::class.java)) {
            return BookAppointmentViewModel(
                doctorRepository,
                appointmentRepository,
                userPreferences // <-- Se la pasamos al ViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

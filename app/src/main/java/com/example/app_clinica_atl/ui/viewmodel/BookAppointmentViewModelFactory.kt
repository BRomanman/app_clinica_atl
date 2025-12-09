package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.citas.CitasApiService
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

class BookAppointmentViewModelFactory(
    private val doctorRepository: DoctorRepository,
    private val citasApiService: CitasApiService,
    private val userPreferences: UserPreferences,
    private val usuariosRepository: UsuariosRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookAppointmentViewModel::class.java)) {
            return BookAppointmentViewModel(
                doctorRepository,
                citasApiService,
                userPreferences,
                usuariosRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

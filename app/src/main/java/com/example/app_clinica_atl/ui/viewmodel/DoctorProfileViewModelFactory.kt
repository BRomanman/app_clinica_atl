package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.DoctorProfileRepository
import com.example.app_clinica_atl.data.repository.HistorialRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

/**
 * Factory manual para crear DoctorProfileViewModel.
 * Recibe el DoctorRepository y se lo pasa al ViewModel.
 */
class DoctorProfileViewModelFactory(
    private val doctorProfileRepository: DoctorProfileRepository,
    private val usuariosRepository: UsuariosRepository,
    private val historialRepository: HistorialRepository,
    private val citasRepository: CitasRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorProfileViewModel::class.java)) {
            return DoctorProfileViewModel(
                doctorProfileRepository,
                usuariosRepository,
                historialRepository,
                citasRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

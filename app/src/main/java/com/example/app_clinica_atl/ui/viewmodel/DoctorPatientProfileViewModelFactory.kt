package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.HistorialRepository
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

class DoctorPatientProfileViewModelFactory(
    private val usuariosRepository: UsuariosRepository,
    private val citasRepository: CitasRepository,
    private val segurosRepository: SegurosRepository,
    private val historialRepository: HistorialRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorPatientProfileViewModel::class.java)) {
            return DoctorPatientProfileViewModel(
                usuariosRepository,
                citasRepository,
                segurosRepository,
                historialRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

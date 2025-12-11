package com.example.app_clinica_atl.ui.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

class DoctorScheduleViewModelFactory(
    private val citasRepository: CitasRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorScheduleViewModel::class.java)) {
            return DoctorScheduleViewModel(citasRepository, usuariosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.app_clinica_atl.ui.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

/**
 * Factory manual para crear DoctorSearchViewModel.
 * Recibe el DoctorRepository y se lo pasa al ViewModel.
 */
class DoctorSearchViewModelFactory(
    private val doctorRepository: DoctorRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorSearchViewModel::class.java)) {
            return DoctorSearchViewModel(doctorRepository, usuariosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

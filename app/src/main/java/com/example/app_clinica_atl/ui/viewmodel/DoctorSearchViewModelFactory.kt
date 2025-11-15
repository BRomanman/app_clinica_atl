package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.DoctorRepository

/**
 * Factory manual para crear DoctorSearchViewModel.
 * Recibe el DoctorRepository y se lo pasa al ViewModel.
 */
class DoctorSearchViewModelFactory(
    private val doctorRepository: DoctorRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorSearchViewModel::class.java)) {
            return DoctorSearchViewModel(doctorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
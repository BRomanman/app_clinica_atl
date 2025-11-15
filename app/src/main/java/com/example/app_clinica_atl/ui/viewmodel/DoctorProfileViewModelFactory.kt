package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.DoctorRepository

/**
 * Factory manual para crear DoctorProfileViewModel.
 * Recibe el DoctorRepository y se lo pasa al ViewModel.
 */
class DoctorProfileViewModelFactory(
    private val doctorRepository: DoctorRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorProfileViewModel::class.java)) {
            return DoctorProfileViewModel(doctorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
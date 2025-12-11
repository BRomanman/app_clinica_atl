package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository

class AdminAddDoctorViewModelFactory(
    private val userRepository: UsuariosRepository,
    private val specialtyRepository: SpecialtyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminAddDoctorViewModel::class.java)) {
            return AdminAddDoctorViewModel(userRepository, specialtyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
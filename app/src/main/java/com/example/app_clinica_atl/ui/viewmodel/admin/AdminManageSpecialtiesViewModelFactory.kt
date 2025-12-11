package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.UsuariosRepository

class AdminManageSpecialtiesViewModelFactory(
    private val usuariosRepository: UsuariosRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminManageSpecialtiesViewModel::class.java)) {
            return AdminManageSpecialtiesViewModel(usuariosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.SpecialtyRepository

class AdminManageSpecialtiesViewModelFactory(
    private val specialtyRepository: SpecialtyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminManageSpecialtiesViewModel::class.java)) {
            return AdminManageSpecialtiesViewModel(specialtyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.app_clinica_atl.ui.viewmodel.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.data.repository.WeatherRepository
import com.example.app_clinica_atl.ui.viewmodel.patient.HomeViewModel

class HomeViewModelFactory(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences,
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(userRepository, userPreferences, weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

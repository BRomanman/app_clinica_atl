package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UsuariosRepository

/*
* un factory es un intermediario especializado en construir objetos,
* útil cuando el constructor necesita parámetros o hay lógica
* adicional para crear la instancia correcta.
*
*
*
*es el puente que le dice al sistema cómo crear un AuthViewModel
* con UsuariosRepository y UserPreferences ya inyectados;
* sin la fábrica, el ViewModelProvider no sabría qué constructor usar
* ni cómo pasar dependencias a AuthViewModel.
*
*/

class AuthViewModelFactory(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            // Le pasa solo las 2 dependencias que necesita
            return AuthViewModel(userRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

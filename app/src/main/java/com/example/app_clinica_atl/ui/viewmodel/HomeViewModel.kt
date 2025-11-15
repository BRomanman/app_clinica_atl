package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val userName: String = ""
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // --- ¡¡LÓGICA ACTUALIZADA!! ---
    // Ya no usamos 'init'.
    // 'uiState' ahora es un Flow que reacciona a los cambios en 'userIdFlow'.
    val uiState: StateFlow<HomeUiState> = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            // flatMapLatest cancela la corutina anterior si el userId cambia (ej. al hacer login/logout)
            if (userId == null) {
                // Si no hay ID (logout), devuelve un estado por defecto
                flowOf(HomeUiState(userName = "Usuario"))
            } else {
                // Si hay ID, busca el nombre de ese usuario
                // y lo transforma en un HomeUiState
                userRepository.getUserByIdAsFlow(userId) // (Crearemos esta función en el sig. paso)
                    .map { user ->
                        HomeUiState(userName = user?.name ?: "Usuario")
                    }
            }
        }.stateIn(
            // Convierte el Flow en un StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(userName = "Cargando...")
        )
}
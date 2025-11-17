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

// --- ¡¡ESTADO DE UI ACTUALIZADO!! ---
data class HomeUiState(
    val userName: String = "",
    val profileImageUrl: String? = null // <-- ¡¡CAMPO AÑADIDO!!
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) {
                // Si no hay ID (logout), devuelve un estado por defecto
                flowOf(HomeUiState(userName = "Usuario", profileImageUrl = null))
            } else {
                // Si hay ID, busca el usuario
                userRepository.getUserByIdAsFlow(userId)
                    .map { user ->
                        // --- ¡¡LÓGICA ACTUALIZADA!! ---
                        // Ahora transforma el UserEntity en un HomeUiState
                        // con el nombre Y la URL de la imagen.
                        HomeUiState(
                            userName = user?.name ?: "Usuario",
                            profileImageUrl = user?.profileImageUrl
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(userName = "Cargando...")
        )
}
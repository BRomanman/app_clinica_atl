package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- ¡¡ESTADO DE UI ACTUALIZADO!! ---
data class HomeUiState(
    val userName: String = "",
    val debugInfo: String? = null,
    val profileImageUrl: String? = null // <-- ¡¡CAMPO AÑADIDO!!
)

class HomeViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Estado auxiliar para depurar un llamado manual a datos de usuario (botón "Probar API")
    private val _debugUserInfo = MutableStateFlow<String?>(null)
    val debugUserInfo: StateFlow<String?> = _debugUserInfo.asStateFlow()

    // --- ¡¡LÓGICA ACTUALIZADA!! ---
    // Ya no usamos 'init'.
    // 'uiState' ahora es un Flow que reacciona a los cambios en 'userIdFlow'.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) {
                // Si no hay ID (logout), devuelve un estado por defecto
                flowOf(HomeUiState(userName = "Usuario", profileImageUrl = null))
            } else {
                // Si hay ID, busca el usuario
                userRepository.getUserByIdAsFlow(userId)
                    .map { user ->
                        HomeUiState(
                            userName = user?.name ?: "Usuario",
                            debugInfo = _debugUserInfo.value,
                            profileImageUrl = user?.profileImageUrl
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(userName = "Cargando...")
        )


    fun fetchDebugUser(userId: Long) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            _debugUserInfo.value = result.fold(
                onSuccess = { user ->
                    """
                        ID: ${user.id}
                        Nombre: ${user.name}
                        Correo: ${user.email}
                        Rol: ${user.role}
                    """.trimIndent()
                },
                onFailure = { error -> "Error: ${error.message ?: "desconocido"}" }
            )
            // Refresca el uiState para reflejar el valor de debug actual
            // (stateIn reemitirá el último valor cuando cambie el flow base).
            // No se hace nada más porque uiState lee _debugUserInfo.value en el map.
        }
    }
}

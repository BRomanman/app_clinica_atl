package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.UsuarioUpdateRequestDto
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val updateSuccess: Boolean = false,
    val errorMsg: String? = null
)

class AdminProfileViewModel(
    private val repository: UsuariosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: Long? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val id = userPreferences.userIdFlow.first()
                if (id == null) {
                    _uiState.update { it.copy(isLoading = false, errorMsg = "Error de sesión") }
                    return@launch
                }
                currentUserId = id
                val result = repository.getUserById(id)

                if (result.isSuccess) {
                    val u = result.getOrNull()!!
                    // Separar nombre si viene junto
                    val parts = u.name.split(" ", limit = 2)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nombre = parts.getOrElse(0){""},
                            apellido = parts.getOrElse(1){""},
                            email = u.email,
                            telefono = u.phone
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMsg = result.exceptionOrNull()?.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
            }
        }
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v) }
    fun onTelefonoChange(v: String) = _uiState.update { it.copy(telefono = v) }

    fun toggleEdit() { _uiState.update { it.copy(isEditing = !it.isEditing) } }

    fun updateProfile() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, updateSuccess = false) }
            val dto = UsuarioUpdateRequestDto(
                nombre = _uiState.value.nombre,
                apellido = _uiState.value.apellido,
                telefono = _uiState.value.telefono
            )
            val result = repository.updateUser(uid, dto)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, updateSuccess = true, isEditing = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMsg() { _uiState.update { it.copy(updateSuccess = false, errorMsg = null) } }
}

class AdminProfileViewModelFactory(
    private val repo: UsuariosRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminProfileViewModel(repo, prefs) as T
}
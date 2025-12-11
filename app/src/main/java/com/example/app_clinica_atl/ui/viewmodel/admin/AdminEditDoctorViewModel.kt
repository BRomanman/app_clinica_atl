package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.UsuarioUpdateRequestDto
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminEditDoctorUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val isLoading: Boolean = true,
    val updateSuccess: Boolean = false,
    val errorMsg: String? = null
)

class AdminEditDoctorViewModel(
    private val repository: UsuariosRepository,
    private val doctorId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEditDoctorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        viewModelScope.launch {
            val result = repository.getUserById(doctorId)
            if (result.isSuccess) {
                val doc = result.getOrNull()!!
                val parts = doc.name.split(" ", limit = 2)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombre = parts.getOrElse(0) { "" },
                        apellido = parts.getOrElse(1) { "" },
                        email = doc.email,
                        telefono = doc.phone
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error cargando doctor") }
            }
        }
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v) }
    fun onTelefonoChange(v: String) = _uiState.update { it.copy(telefono = v) }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, updateSuccess = false) }
            val updateDto = UsuarioUpdateRequestDto(
                nombre = _uiState.value.nombre,
                apellido = _uiState.value.apellido,
                telefono = _uiState.value.telefono
            )
            val result = repository.updateUser(doctorId, updateDto)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }
}

// Esta es la Factory que el NavGraph estaba buscando y no encontraba
class AdminEditDoctorViewModelFactory(
    private val repo: UsuariosRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminEditDoctorViewModel(repo, id) as T
    }
}
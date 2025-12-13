package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.repository.AdminRepository
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
    private val repository: AdminRepository,
    private val doctorId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEditDoctorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getDoctorById(doctorId)
            if (result.isSuccess) {
                val doc = result.getOrNull()!!
                val firstName = doc.nombre ?: doc.usuario?.nombre ?: ""
                val lastName = doc.apellido ?: doc.usuario?.apellido ?: ""
                val email = doc.usuario?.correo ?: doc.correo.orEmpty()
                val telefono = doc.usuario?.telefono ?: doc.telefono.orEmpty()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombre = firstName,
                        apellido = lastName,
                        email = email,
                        telefono = telefono
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
            val updateDto = DoctorDto(
                id = doctorId,
                nombre = _uiState.value.nombre,
                apellido = _uiState.value.apellido,
                correo = _uiState.value.email,
                telefono = _uiState.value.telefono
            )
            val result = repository.updateDoctor(doctorId, updateDto)
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
    private val repo: AdminRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminEditDoctorViewModel(repo, id) as T
    }
}

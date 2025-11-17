package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.data.repository.DoctorRepository
// NO MÁS IMPORTS DE HILT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de perfil del doctor.
 * Ahora contiene un 'UsuarioEntity' (que puede ser nulo) en lugar de 'DoctorInfo'.
 */
data class DoctorProfileUiState(
    val isLoading: Boolean = true,
    val doctor: UsuarioEntity? = null,
    val errorMsg: String? = null
)

// NO MÁS @HiltViewModel
class DoctorProfileViewModel( // <-- Constructor normal
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState())
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    /**
     * Carga el perfil de un doctor específico usando su ID.
     * Esta función es llamada desde la UI (Screen) cuando recibe el ID.
     */
    fun loadDoctorProfile(doctorId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            // Usamos la función del repositorio real
            val result = doctorRepository.getDoctorById(doctorId)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        doctor = result.getOrNull() // <-- Guardamos el UsuarioEntity real
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        doctor = null,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar el perfil"
                    )
                }
            }
        }
    }
}
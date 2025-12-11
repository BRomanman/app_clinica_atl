package com.example.app_clinica_atl.ui.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.roleToId
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de búsqueda de pacientes.
 */
data class DoctorSearchPatientUiState(
    val query: String = "",
    val patients: List<UsuarioDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class DoctorSearchPatientViewModel(
    private val userRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorSearchPatientUiState())
    val uiState: StateFlow<DoctorSearchPatientUiState> = _uiState.asStateFlow()

    /**
     * Llamado cada vez que el doctor escribe en la barra de búsqueda.
     */
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, errorMsg = null) }
        searchPatients(newQuery)
    }

    /**
     * Ejecuta la búsqueda en el repositorio.
     */
    private fun searchPatients(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(isLoading = false, patients = emptyList(), errorMsg = null) }
            return
        }

        val patientId = query.toLongOrNull()
        if (patientId == null) {
            _uiState.update { it.copy(isLoading = false, patients = emptyList(), errorMsg = "Ingrese un ID válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null, patients = emptyList()) }

            val result = userRepository.getUserById(patientId)

            _uiState.update {
                if (result.isSuccess) {
                    val patient = result.getOrNull()
                    if (patient != null && roleToId(patient.role) != 1L) {
                        return@update it.copy(
                            isLoading = false,
                            patients = emptyList(),
                            errorMsg = "Solo puedes buscar pacientes (rol 1)."
                        )
                    }

                    it.copy(
                        isLoading = false,
                        patients = patient?.let { listOf(it) } ?: emptyList(),
                        errorMsg = null
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        patients = emptyList(),
                        errorMsg = result.exceptionOrNull()?.message ?: "Error en la búsqueda por ID"
                    )
                }
            }
        }
    }
}

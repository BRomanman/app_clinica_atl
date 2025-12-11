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
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            _uiState.update { it.copy(isLoading = false, patients = emptyList(), errorMsg = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null, patients = emptyList()) }

            val searchResult = userRepository.searchPatients(trimmed)
            _uiState.update { state ->
                if (searchResult.isSuccess) {
                    val patients = searchResult.getOrDefault(emptyList())
                    state.copy(
                        isLoading = false,
                        patients = patients,
                        errorMsg = if (patients.isEmpty()) "No se encontraron pacientes." else null
                    )
                } else {
                    state.copy(
                        isLoading = false,
                        patients = emptyList(),
                        errorMsg = searchResult.exceptionOrNull()?.message ?: "No se pudo buscar pacientes."
                    )
                }
            }
        }
    }
}

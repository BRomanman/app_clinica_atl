package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
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
    val patients: List<UsuarioEntity> = emptyList(),
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
        _uiState.update { it.copy(query = newQuery) }
        searchPatients(newQuery)
    }

    /**
     * Ejecuta la búsqueda en el repositorio.
     */
    private fun searchPatients(query: String) {
        // Si la búsqueda está vacía, no mostramos resultados.
        if (query.isBlank()) {
            _uiState.update { it.copy(isLoading = false, patients = emptyList(), errorMsg = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = userRepository.searchPatients(query)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        patients = result.getOrNull() ?: emptyList()
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error en la búsqueda"
                    )
                }
            }
        }
    }
}

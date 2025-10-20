package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.model.Patient
import com.example.app_clinica_atl.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la pantalla de búsqueda
data class PatientUiState(
    val searchText: String = "",
    val patients: List<Patient> = emptyList()
)

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    // Guardamos la lista completa de pacientes
    private var allPatients: List<Patient> = emptyList()

    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> = _uiState.asStateFlow()

    init {
        // Al crearse el VM, carga todos los pacientes
        viewModelScope.launch {
            allPatients = repository.getPatients()
            _uiState.update { it.copy(patients = allPatients) }
        }
    }

    // Se llama cada vez que el usuario escribe en el buscador
    fun onSearchTextChange(text: String) {
        _uiState.update { it.copy(searchText = text) }

        // Filtra la lista de pacientes
        val filteredPatients = if (text.isBlank()) {
            allPatients // Si no hay texto, muestra todos
        } else {
            allPatients.filter {
                // Busca por nombre o correo
                it.nombre.contains(text, ignoreCase = true) ||
                        it.correo.contains(text, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(patients = filteredPatients) }
    }
}
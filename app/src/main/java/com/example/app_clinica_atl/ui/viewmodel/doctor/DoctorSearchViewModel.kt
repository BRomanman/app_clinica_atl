package com.example.app_clinica_atl.ui.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
// NO MÁS IMPORTS DE HILT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI
data class DoctorSearchUiState(
    val specialties: List<String> = emptyList(),
    val selectedSpecialty: String = "",
    val doctors: List<UsuarioDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

// NO MÁS @HiltViewModel
class DoctorSearchViewModel( // <-- Constructor normal
    private val doctorRepository: DoctorRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorSearchUiState())
    val uiState: StateFlow<DoctorSearchUiState> = _uiState.asStateFlow()

    init {
        loadSpecialties()
    }

    fun onSpecialtyChange(newSpecialty: String) {
        _uiState.update { it.copy(selectedSpecialty = newSpecialty) }
        loadDoctorsBySpecialty(newSpecialty)
    }

    private fun loadDoctorsBySpecialty(specialty: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            val result = doctorRepository.getDoctorsBySpecialty(specialty)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoading = false,
                        doctors = result.getOrNull() ?: emptyList()
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        doctors = emptyList(),
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar doctores"
                    )
                }
            }
        }
    }

    private fun loadSpecialties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            val result = usuariosRepository.getAllSpecialties()
            if (result.isSuccess) {
                val names = result.getOrNull()
                    ?.mapNotNull { it.nombre?.trim() }
                    ?.filter { it.isNotBlank() }
                    ?.distinct()
                    .orEmpty()
                val default = names.firstOrNull().orEmpty()
                _uiState.update {
                    it.copy(
                        specialties = names,
                        selectedSpecialty = default,
                        isLoading = false
                    )
                }
                if (default.isNotBlank()) {
                    loadDoctorsBySpecialty(default)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar especialidades"
                    )
                }
            }
        }
    }
}

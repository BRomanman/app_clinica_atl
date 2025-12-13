package com.example.app_clinica_atl.ui.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI
data class DoctorSearchUiState(
    val specialties: List<String> = emptyList(),
    val selectedSpecialty: String = "",
    val doctors: List<DoctorDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

// NO MÁS @HiltViewModel
class DoctorSearchViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorSearchUiState())
    val uiState: StateFlow<DoctorSearchUiState> = _uiState.asStateFlow()

    private var allDoctors: List<DoctorDto> = emptyList()

    init {
        loadDoctors()
    }

    fun onSpecialtyChange(newSpecialty: String) {
        val filtered = filterDoctors(newSpecialty)
        _uiState.update {
            it.copy(
                selectedSpecialty = newSpecialty,
                doctors = filtered,
                errorMsg = null
            )
        }
    }

    private fun filterDoctors(specialty: String): List<DoctorDto> {
        val trimmed = specialty.trim()
        if (trimmed.isBlank()) return allDoctors
        return allDoctors.filter {
            it.especialidad?.equals(trimmed, ignoreCase = true) == true
        }
    }

    private fun loadDoctors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            val result = adminRepository.getAllDoctors()
            if (result.isSuccess) {
                val doctors = result.getOrNull().orEmpty()
                allDoctors = doctors
                val specialties = doctors
                    .mapNotNull { it.especialidad?.trim()?.takeIf(String::isNotBlank) }
                    .distinct()
                    .sorted()
                val defaultSelection = specialties.firstOrNull().orEmpty()
                val filtered = filterDoctors(defaultSelection)
                _uiState.update {
                    it.copy(
                        specialties = specialties,
                        selectedSpecialty = defaultSelection,
                        doctors = filtered,
                        isLoading = false,
                        errorMsg = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        specialties = emptyList(),
                        selectedSpecialty = "",
                        doctors = emptyList(),
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error cargando doctores"
                    )
                }
            }
        }
    }
}

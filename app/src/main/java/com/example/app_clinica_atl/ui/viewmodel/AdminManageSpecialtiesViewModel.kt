package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.specialty.SpecialtyEntity
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de gestión de especialidades.
 */
data class AdminSpecialtiesUiState(
    val specialties: List<SpecialtyEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null
)

class AdminManageSpecialtiesViewModel(
    private val specialtyRepository: SpecialtyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSpecialtiesUiState())
    val uiState: StateFlow<AdminSpecialtiesUiState> = _uiState.asStateFlow()

    init {
        // Observa la base de datos. Cada vez que cambie,
        // la lista en la UI se actualizará automáticamente.
        viewModelScope.launch {
            specialtyRepository.getAllSpecialties()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }
                .collect { specialtyList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            specialties = specialtyList,
                            errorMsg = null
                        )
                    }
                }
        }
    }

    /**
     * Intenta añadir una nueva especialidad.
     */
    fun addSpecialty(name: String, priceStr: String) {
        viewModelScope.launch {
            val price = priceStr.toDoubleOrNull()
            if (price == null || price <= 0) {
                _uiState.update { it.copy(errorMsg = "El precio debe ser un número válido mayor a 0.") }
                return@launch
            }
            if (name.isBlank()) {
                _uiState.update { it.copy(errorMsg = "El nombre no puede estar vacío.") }
                return@launch
            }

            val newSpecialty = SpecialtyEntity(name = name, price = price)
            val result = specialtyRepository.addSpecialty(newSpecialty)

            if (result.isFailure) {
                // Muestra el error (ej. "Nombre duplicado")
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            } else {
                // Éxito, el Flow se actualizará solo. Limpiamos el error.
                _uiState.update { it.copy(errorMsg = null) }
            }
        }
    }

    /**
     * Elimina una especialidad.
     */
    fun deleteSpecialty(specialty: SpecialtyEntity) {
        viewModelScope.launch {
            val result = specialtyRepository.deleteSpecialty(specialty)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    /**
     * Limpia el mensaje de error de la UI.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMsg = null) }
    }
}
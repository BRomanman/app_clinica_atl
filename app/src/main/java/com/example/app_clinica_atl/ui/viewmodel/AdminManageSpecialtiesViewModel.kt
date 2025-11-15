package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.specialty.SpecialtyEntity
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.domain.validation.validateRequired // <-- ¡IMPORT AÑADIDO!
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ¡ESTADO DE UI ACTUALIZADO!
 * Ahora guarda los campos del formulario y sus errores.
 */
data class AdminSpecialtiesUiState(
    val specialties: List<SpecialtyEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    // --- Campos del formulario ---
    val newSpecialtyName: String = "",
    val newSpecialtyPrice: String = "",
    val nameError: String? = null,
    val priceError: String? = null
)

class AdminManageSpecialtiesViewModel(
    private val specialtyRepository: SpecialtyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSpecialtiesUiState())
    val uiState: StateFlow<AdminSpecialtiesUiState> = _uiState.asStateFlow()

    init {
        // Observa la base de datos (sin cambios)
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

    // --- ¡¡FUNCIONES AÑADIDAS!! ---
    // (Estas son las funciones que faltaban y causaban el error)
    fun onNameChange(name: String) {
        val error = validateRequired(name, "Nombre")
        _uiState.update {
            it.copy(
                newSpecialtyName = name,
                nameError = error,
                errorMsg = null // Limpia el error general
            )
        }
    }

    fun onPriceChange(priceStr: String) {
        val price = priceStr.toDoubleOrNull()
        val error = if (price == null || price <= 0) "Debe ser un número válido" else null
        _uiState.update {
            it.copy(
                newSpecialtyPrice = priceStr,
                priceError = error,
                errorMsg = null // Limpia el error general
            )
        }
    }

    /**
     * ¡¡FUNCIÓN ACTUALIZADA!!
     * Usa el estado del ViewModel para añadir la especialidad.
     */
    fun addSpecialty() {
        val s = _uiState.value

        val nameError = validateRequired(s.newSpecialtyName, "Nombre")
        val price = s.newSpecialtyPrice.toDoubleOrNull()
        val priceError = if (price == null || price <= 0) "Precio no válido" else null

        if (nameError != null || priceError != null) {
            _uiState.update { it.copy(nameError = nameError, priceError = priceError) }
            return
        }

        viewModelScope.launch {
            val newSpecialty = SpecialtyEntity(name = s.newSpecialtyName, price = price!!)
            val result = specialtyRepository.addSpecialty(newSpecialty)

            if (result.isFailure) {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            } else {
                // Éxito, limpia los campos y el error
                _uiState.update {
                    it.copy(
                        errorMsg = null,
                        newSpecialtyName = "",
                        newSpecialtyPrice = "",
                        nameError = null,
                        priceError = null
                    )
                }
            }
        }
    }

    /**
     * Elimina una especialidad. (Sin cambios)
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
     * Limpia el mensaje de error de la UI. (Sin cambios)
     */
    fun clearError() {
        _uiState.update { it.copy(errorMsg = null) }
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Seguros.
 */
data class InsuranceUiState(
    val availableInsurances: List<InsuranceEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class InsuranceViewModel(
    private val insuranceRepository: InsuranceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsuranceUiState())
    val uiState: StateFlow<InsuranceUiState> = _uiState.asStateFlow()

    init {
        // Carga la lista de seguros disponibles
        viewModelScope.launch {
            insuranceRepository.getAvailableInsurances()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }
                .collect { insuranceList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableInsurances = insuranceList
                        )
                    }
                }
        }
    }

    /**
     * Intenta suscribir al usuario a un seguro.
     */
    fun subscribeToInsurance(insuranceId: Long) {
        viewModelScope.launch {
            // Primero, obtenemos el ID del paciente logueado
            val patientId = userPreferences.userIdFlow.firstOrNull()
            if (patientId == null) {
                _uiState.update { it.copy(errorMsg = "No se pudo identificar al usuario. Inicie sesión de nuevo.") }
                return@launch
            }

            val result = insuranceRepository.subscribeToInsurance(patientId, insuranceId)

            if (result.isSuccess) {
                _uiState.update { it.copy(successMsg = "¡Seguro contratado con éxito!") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}
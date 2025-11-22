package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.ui.screen.BeneficiarioForm
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InsuranceUiState(
    val healthInsurances: List<SeguroDto> = emptyList(),
    val lifeInsurances: List<SeguroDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class InsuranceViewModel(
    private val insuranceRepository: SegurosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsuranceUiState())
    val uiState: StateFlow<InsuranceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            insuranceRepository.getAvailableInsurances()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
                }
                .collect { insuranceList ->
                    val health = insuranceList.filter {
                        it.name.contains("Salud", ignoreCase = true)
                    }

                    val life = insuranceList.filter {
                        it.name.contains("Vida", ignoreCase = true)
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            healthInsurances = health,
                            lifeInsurances = life
                        )
                    }
                }
        }
    }

    /**
     *  NUEVO: contratación completa de seguro con beneficiarios y método de pago
     */
    fun contratarSeguro(
        seguroId: Long,
        beneficiarios: List<BeneficiarioForm>,
        metodoPago: String,
        estado: String
    ) {
        viewModelScope.launch {

            val userId = userPreferences.userIdFlow.firstOrNull()
            if (userId == null) {
                _uiState.update { it.copy(errorMsg = "Usuario no identificado") }
                return@launch
            }

            val result = insuranceRepository.contratarSeguro(
                userId = userId,
                seguroId = seguroId,
                beneficiarios = beneficiarios,
                metodoPago = metodoPago,
                estado = estado
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(successMsg = "Seguro contratado correctamente") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}

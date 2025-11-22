package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.ui.screen.BeneficiarioForm
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

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
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            healthInsurances = insuranceList.filter { s -> s.name.contains("Salud") },
                            lifeInsurances = insuranceList.filter { s -> s.name.contains("Vida") }
                        )
                    }
                }
        }
    }

    fun contratarSeguro(
        seguroId: Long,
        beneficiarios: List<BeneficiarioForm>,
        metodoPago: String
    ) {
        viewModelScope.launch {

            val userId = userPreferences.userIdFlow.firstOrNull()
            if (userId == null) {
                _uiState.update { it.copy(errorMsg = "Usuario no identificado") }
                return@launch
            }

            try {
                beneficiarios.forEach { ben ->

                    val contrato = ContratoSeguroDto(
                        id = 0,
                        idSeguro = seguroId,
                        idUsuario = userId,
                        rut_beneficiarios = ben.rut,
                        nombre_beneficiarios = ben.nombre,
                        apellido_beneficiarios = ben.apellido,
                        fecha_nacimiento_beneficiarios = ben.fechaNacimiento,
                        correo_contacto = "",
                        telefono_contacto = "",
                        metodo_pago = metodoPago,
                        fecha_contratacion = LocalDate.now().toString(),
                        fecha_cancelacion = "",
                        estado = "ACTIVO"
                    )

                    insuranceRepository.crearContrato(contrato)
                }

                _uiState.update { it.copy(successMsg = "Contrato generado con éxito") }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMsg = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}

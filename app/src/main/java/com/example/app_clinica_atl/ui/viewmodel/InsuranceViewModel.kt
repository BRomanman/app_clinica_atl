package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.ContratoSeguroDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.ui.screen.Patient.BeneficiarioForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Estado de la UI para la pantalla de Seguros.
 */
data class InsuranceUiState(
    val availableInsurances: List<SeguroDto> = emptyList(),
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
                            availableInsurances = insuranceList,
                            healthInsurances = insuranceList.filter { s -> s.name.orEmpty().contains("Salud", true) },
                            lifeInsurances = insuranceList.filter { s -> s.name.orEmpty().contains("Vida", true) }
                        )
                    }
                }
        }
    }

    fun contratarSeguro(
        seguroId: Long,
        beneficiarios: List<BeneficiarioForm>,
        metodoPago: String,
        correoContacto: String,
        telefonoContacto: String
    ) {
        viewModelScope.launch {
            // Primero, obtenemos el ID del paciente logueado
            val patientId = userPreferences.userIdFlow.firstOrNull()
            if (patientId == null) {
                _uiState.update { it.copy(errorMsg = "No se pudo identificar al usuario. Inicie sesión de nuevo.") }
                return@launch
            }

            try {
                val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val outputDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val outputDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val nowIso = LocalDateTime.now().format(outputDateTimeFormatter)

                val ruts = beneficiarios.joinToString(",") { it.rut }
                val nombres = beneficiarios.joinToString(",") { it.nombre.trim() }
                val apellidos = beneficiarios.joinToString(",") { it.apellido.trim() }

                val fechasNacimiento = beneficiarios.joinToString(",") { ben ->
                    val parsedBirth = runCatching { LocalDate.parse(ben.fechaNacimiento, inputFormatter) }.getOrNull()
                    parsedBirth?.format(outputDateFormatter) ?: ben.fechaNacimiento
                }

                val contrato = ContratoSeguroDto(
                    id = 0,
                    idSeguro = seguroId,
                    idUsuario = patientId,
                    rutBeneficiarios = ruts,
                    nombreBeneficiarios = nombres,
                    apellidoBeneficiarios = apellidos,
                    fechaNacimientoBeneficiarios = fechasNacimiento,
                    correoContacto = correoContacto.trim(),
                    telefonoContacto = telefonoContacto.trim(),
                    metodoPago = metodoPago,
                    fechaContratacion = nowIso,
                    fechaCancelacion = "",
                    estado = "ACTIVO"
                )

                val result = insuranceRepository.crearContrato(contrato)
                if (result.isSuccess) {
                    _uiState.update { it.copy(successMsg = "Contrato generado con éxito") }
                } else {
                    _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message ?: "No se pudo generar el contrato.") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMsg = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}

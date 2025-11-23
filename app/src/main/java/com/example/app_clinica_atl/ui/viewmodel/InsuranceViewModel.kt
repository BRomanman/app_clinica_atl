package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.repository.SegurosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
<<<<<<< Updated upstream
=======
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
>>>>>>> Stashed changes

/**
 * Estado de la UI para la pantalla de Seguros.
 */
data class InsuranceUiState(
    val availableInsurances: List<SeguroDto> = emptyList(),
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
                            availableInsurances = insuranceList
                        )
                    }
                }
        }
    }

<<<<<<< Updated upstream
    /**
     * Intenta suscribir al usuario a un seguro.
     */
    fun subscribeToInsurance(insuranceId: Long) {
=======
    fun contratarSeguro(
        seguroId: Long,
        beneficiarios: List<BeneficiarioForm>,
        metodoPago: String,
        correoContacto: String,
        telefonoContacto: String
    ) {
>>>>>>> Stashed changes
        viewModelScope.launch {
            // Primero, obtenemos el ID del paciente logueado
            val patientId = userPreferences.userIdFlow.firstOrNull()
            if (patientId == null) {
                _uiState.update { it.copy(errorMsg = "No se pudo identificar al usuario. Inicie sesión de nuevo.") }
                return@launch
            }

<<<<<<< Updated upstream
            val result = insuranceRepository.subscribeToInsurance(patientId, insuranceId)

            if (result.isSuccess) {
                _uiState.update { it.copy(successMsg = "¡Seguro contratado con éxito!") }
            } else {
                _uiState.update { it.copy(errorMsg = result.exceptionOrNull()?.message) }
=======
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
                    idUsuario = userId,
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

                insuranceRepository.crearContrato(contrato)


                _uiState.update { it.copy(successMsg = "Contrato generado con éxito") }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMsg = e.message) }
>>>>>>> Stashed changes
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null, successMsg = null) }
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.HistorialRepository
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DoctorPatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UsuarioDto? = null,
    val appointments: List<CitaDto> = emptyList(),
    val insurances: List<SeguroDto> = emptyList(),
    val histories: List<HistorialDto> = emptyList(),
    val errorMsg: String? = null
)

class DoctorPatientProfileViewModel(
    private val usuariosRepository: UsuariosRepository,
    private val citasRepository: CitasRepository,
    private val segurosRepository: SegurosRepository,
    private val historialRepository: HistorialRepository
) : ViewModel() {

    private var currentPatientId: Long? = null
    private val _uiState = MutableStateFlow(DoctorPatientProfileUiState())
    val uiState: StateFlow<DoctorPatientProfileUiState> = _uiState.asStateFlow()

    fun loadPatient(patientId: Long) {
        if (currentPatientId == patientId && uiState.value.patient != null) return
        currentPatientId = patientId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null)

            val userResult = usuariosRepository.getUserById(patientId)
            if (userResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = userResult.exceptionOrNull()?.message ?: "No se pudo cargar el paciente"
                )
                return@launch
            }

            val patient = userResult.getOrNull()

            val appointmentsResult = citasRepository.getAppointmentsForPatientOnce(patientId)
            val historyResult = historialRepository.getHistorialForUser(patientId)
            val insurancesResult = try {
                segurosRepository.getInsurancesForPatient(patientId)
            } catch (e: Exception) {
                Result.failure(e)
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                patient = patient,
                appointments = appointmentsResult.getOrElse { emptyList() },
                histories = historyResult.getOrElse { emptyList() },
                insurances = insurancesResult.getOrElse { emptyList() },
                errorMsg = null // No bloqueamos la UI si alguna sección falla
            )
        }
    }
}

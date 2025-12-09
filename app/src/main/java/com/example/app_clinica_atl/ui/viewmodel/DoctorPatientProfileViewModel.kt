package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.CitaDto
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class DoctorPatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UsuarioDto? = null,
    val appointments: List<CitaDto> = emptyList(),
    val insurances: List<SeguroDto> = emptyList(),
    val histories: List<HistorialDto> = emptyList(),
    val errorMsg: String? = null,
    val isCancellingId: Long? = null,
    val isFinishingId: Long? = null
)

class DoctorPatientProfileViewModel(
    private val usuariosRepository: UsuariosRepository,
    private val citasRepository: CitasRepository,
    private val segurosRepository: SegurosRepository,
    private val historialRepository: HistorialRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var currentPatientId: Long? = null
    private var currentDoctorId: Long? = null
    private val _uiState = MutableStateFlow(DoctorPatientProfileUiState())
    val uiState: StateFlow<DoctorPatientProfileUiState> = _uiState.asStateFlow()

    fun loadPatient(patientId: Long) {
        viewModelScope.launch {
            val doctorId = resolveDoctorId()
            if (doctorId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "No se pudo identificar al doctor."
                )
                return@launch
            }
            if (currentPatientId == patientId && _uiState.value.patient != null) return@launch
            currentPatientId = patientId
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

            val appointmentsResult = runCatching {
                citasRepository.getProximasCitasPacienteConDoctor(patientId, doctorId)
            }
            val historyResult = historialRepository.getHistorialForUser(patientId)
            val insurancesResult = segurosRepository.getInsurancesForPatient(patientId)

            val loadingErrors = buildList {
                appointmentsResult.exceptionOrNull()?.let { add("No se pudieron cargar las próximas citas.") }
                historyResult.exceptionOrNull()?.let { add("No se pudo cargar el historial médico.") }
                insurancesResult.exceptionOrNull()?.let { add("No se pudieron cargar los seguros.") }
            }.takeIf { it.isNotEmpty() }?.joinToString(" ")

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                patient = patient,
                appointments = appointmentsResult.getOrElse { emptyList() },
                histories = historyResult.getOrElse { emptyList() },
                insurances = insurancesResult.getOrElse { emptyList() },
                errorMsg = loadingErrors
            )
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCancellingId = appointmentId, errorMsg = null)
            val patientId = currentPatientId
            val doctorId = resolveDoctorId()
            if (patientId == null || doctorId == null) {
                _uiState.value = _uiState.value.copy(
                    isCancellingId = null,
                    errorMsg = "No se pudo cancelar la cita."
                )
                return@launch
            }
            val result = citasRepository.cancelarCita(appointmentId)
            _uiState.value = _uiState.value.copy(isCancellingId = null)
            if (result.isSuccess) {
                loadPatient(patientId)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMsg = result.exceptionOrNull()?.message ?: "No se pudo cancelar la cita."
                )
            }
        }
    }

    fun finalizeAppointment(appointmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFinishingId = appointmentId, errorMsg = null)
            val patientId = currentPatientId
            val doctorId = resolveDoctorId()
            val cita = _uiState.value.appointments.firstOrNull { it.id == appointmentId }
            if (patientId == null || doctorId == null || cita == null) {
                _uiState.value = _uiState.value.copy(
                    isFinishingId = null,
                    errorMsg = "No se pudo finalizar la cita."
                )
                return@launch
            }
            val result = citasRepository.finalizarCita(appointmentId)
            _uiState.value = _uiState.value.copy(isFinishingId = null)
            if (result.isSuccess) {
                loadPatient(patientId)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMsg = result.exceptionOrNull()?.message ?: "No se pudo finalizar la cita."
                )
            }
        }
    }

    private suspend fun resolveDoctorId(): Long? {
        currentDoctorId?.let { return it }
        return userPreferences.userDoctorIdFlow.firstOrNull().also { currentDoctorId = it }
    }
}

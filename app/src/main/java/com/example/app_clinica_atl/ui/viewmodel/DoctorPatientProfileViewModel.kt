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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

            val (appointmentsResult, historyResult, insurancesResult) = supervisorScope {
                val appointmentsDeferred = async {
                    runCatching { citasRepository.getUpcomingAppointmentsForPatient(patientId) }
                        .getOrElse { Result.failure(it) }
                }
                val historyDeferred = async {
                    runCatching { historialRepository.getHistorialForUser(patientId) }
                        .getOrElse { Result.failure(it) }
                }
                val insurancesDeferred = async {
                    runCatching { segurosRepository.getInsurancesForPatient(patientId) }
                        .getOrElse { Result.failure(it) }
                }

                Triple(
                    appointmentsDeferred.await(),
                    historyDeferred.await(),
                    insurancesDeferred.await()
                )
            }

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
                errorMsg = loadingErrors // No bloqueamos la UI si alguna sección falla
            )
        }
    }
}

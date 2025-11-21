package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DoctorAgendaItem(
    val appointmentId: Long,
    val patientId: Long?,
    val patientName: String,
    val date: String,
    val time: String,
    val status: String
)

data class DoctorScheduleUiState(
    val isLoading: Boolean = true,
    val appointments: List<DoctorAgendaItem> = emptyList(),
    val errorMsg: String? = null
)

class DoctorScheduleViewModel(
    private val citasRepository: CitasRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorScheduleUiState())
    val uiState: StateFlow<DoctorScheduleUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAgenda(userId: Long) {
        viewModelScope.launch {
            _uiState.value = DoctorScheduleUiState(isLoading = true)

            // Aseguramos que la consulta se haga con el id propio del doctor, no el id de usuario.
            val doctorId = usuariosRepository.getDoctorIdForUser(userId).getOrNull()
                ?: run {
                    _uiState.value = DoctorScheduleUiState(
                        isLoading = false,
                        errorMsg = "No se encontró un registro de doctor para este usuario."
                    )
                    return@launch
                }

            val result = citasRepository.getAppointmentsForDoctorOnce(doctorId)
            if (result.isFailure) {
                _uiState.value = DoctorScheduleUiState(
                    isLoading = false,
                    errorMsg = result.exceptionOrNull()?.message ?: "No se pudieron cargar las citas."
                )
                return@launch
            }

            val upcoming = result.getOrNull().orEmpty().filter { (it.patientId != null || !it.available) && isUpcoming(it) }
            val patients = fetchPatientNames(upcoming)
            val mapped = upcoming
                .sortedWith(compareBy({ it.date }, { it.startTime }))
                .map { cita ->
                    DoctorAgendaItem(
                        appointmentId = cita.id,
                        patientId = cita.patientId,
                        patientName = patients[cita.patientId] ?: "Paciente #${cita.patientId ?: "-"}",
                        date = cita.date,
                        time = cita.time,
                        status = cita.status
                    )
                }

            _uiState.value = DoctorScheduleUiState(isLoading = false, appointments = mapped)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isUpcoming(cita: CitaDto): Boolean {
        val today = LocalDate.now()
        val date = runCatching { LocalDate.parse(cita.date, DateTimeFormatter.ISO_DATE) }.getOrNull() ?: return false
        if (date.isAfter(today)) return true
        if (date.isEqual(today)) {
            val startTime = cita.startTime.ifBlank { "${cita.time}:00" }
            val time = runCatching { LocalTime.parse(startTime) }.getOrNull() ?: return true
            return time.isAfter(LocalTime.now())
        }
        return false
    }

    private suspend fun fetchPatientNames(appointments: List<CitaDto>): Map<Long, String> {
        val ids = appointments.mapNotNull { it.patientId }.distinct()
        if (ids.isEmpty()) return emptyMap()
        val map = mutableMapOf<Long, String>()
        for (id in ids) {
            val result = usuariosRepository.getUserById(id)
            map[id] = result.getOrNull()?.name ?: "Paciente #$id"
        }
        return map
    }
}

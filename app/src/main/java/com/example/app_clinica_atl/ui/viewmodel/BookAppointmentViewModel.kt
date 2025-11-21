package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// todo traer especialidades desde el backend hacer un get doctor para traer los doctores
data class BookAppointmentUiState(
    val specialties: List<String> = listOf(
        "Cardiología", "Dermatología", "Medicina General", "Pediatría", "Psicología"
    ),
    val doctors: List<UsuarioDto> = emptyList(),
    val availableTimes: List<String> = emptyList(),

    // Estado de los campos
    val selectedSpecialty: String = "",
    val selectedDoctorId: Long? = null,
    val selectedDoctorName: String = "",
    val selectedDate: String = "", // Formato "YYYY-MM-DD"
    val selectedTime: String = "", // Formato "HH:MM"

    // Errores de validación en tiempo real
    val dateError: String? = null,
    val timeError: String? = null,

    // Estado de la UI
    val isLoadingDoctors: Boolean = false,
    val isLoadingTimes: Boolean = false,
    val isBooking: Boolean = false,
    val errorMsg: String? = null,

    // Nuevos estados para el calendario y la redirección
    val isDatePickerVisible: Boolean = false,
    val bookingSuccess: Boolean = false // ¡¡AÑADIDO!!
)

class BookAppointmentViewModel(
    private val doctorRepository: DoctorRepository,
    private val appointmentRepository: CitasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    private val allDaySlots = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
        "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
    )

    // --- Handlers de UI ---

    fun onSpecialtyChange(specialty: String) {
        _uiState.update {
            it.copy(
                selectedSpecialty = specialty,
                doctors = emptyList(),
                selectedDoctorId = null,
                selectedDoctorName = "",
                availableTimes = emptyList(),
                selectedDate = "",
                selectedTime = "",
                dateError = null,
                timeError = null
            )
        }
        loadDoctorsBySpecialty(specialty)
    }

    fun onDoctorChange(doctor: UsuarioDto) {
        _uiState.update {
            it.copy(
                selectedDoctorId = doctor.id,
                selectedDoctorName = doctor.name,
                availableTimes = emptyList(),
                selectedDate = "",
                selectedTime = "",
                dateError = null,
                timeError = null
            )
        }
    }

    // --- ¡¡Nuevas funciones de Fecha!! ---
    fun onDateSelected(date: String) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                availableTimes = emptyList(),
                selectedTime = "",
                dateError = null, // Limpia el error al seleccionar
                timeError = null,
                isDatePickerVisible = false // Oculta el calendario
            )
        }
        val doctorId = _uiState.value.selectedDoctorId
        if (doctorId != null) {
            loadAvailableTimes(doctorId, date)
        }
    }

    fun showDatePicker() {
        if (_uiState.value.selectedDoctorId != null) {
            _uiState.update { it.copy(isDatePickerVisible = true) }
        } else {
            _uiState.update { it.copy(errorMsg = "Seleccione un doctor primero") }
        }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }
    // --- Fin de nuevas funciones ---

    fun onTimeChange(time: String) {
        val error = if (time.isBlank()) "Seleccione una hora" else null
        _uiState.update { it.copy(selectedTime = time, timeError = error) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMsg = null) }
    }

    /**
     * Resetea el estado del formulario y el flag de éxito.
     * Se llama después de que la navegación se completa.
     */
    fun onBookingSuccessHandled() {
        _uiState.update {
            it.copy(
                bookingSuccess = false,
                selectedSpecialty = "",
                selectedDoctorId = null,
                selectedDoctorName = "",
                selectedDate = "",
                selectedTime = "",
                doctors = emptyList(),
                availableTimes = emptyList()
            )
        }
    }

    // --- Lógica de Carga de Datos (sin cambios) ---

    private fun loadDoctorsBySpecialty(specialty: String) {
        // ... (código sin cambios)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDoctors = true, errorMsg = null) }
            val result = doctorRepository.getDoctorsBySpecialty(specialty)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoadingDoctors = false, doctors = result.getOrNull() ?: emptyList())
                } else {
                    it.copy(isLoadingDoctors = false, errorMsg = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun loadAvailableTimes(doctorId: Long, date: String) {
        // ... (código sin cambios)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTimes = true, errorMsg = null) }
            val result = appointmentRepository.getBookedTimes(doctorId, date)
            _uiState.update {
                if (result.isSuccess) {
                    val bookedTimes = result.getOrNull() ?: emptyList()
                    val availableSlots = allDaySlots.filter { it !in bookedTimes }
                    it.copy(isLoadingTimes = false, availableTimes = availableSlots)
                } else {
                    it.copy(isLoadingTimes = false, errorMsg = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    // --- ¡¡ACCIÓN PRINCIPAL ACTUALIZADA!! ---

    fun submitBooking() {
        val s = _uiState.value

        // Validación final
        val dateError = if (s.selectedDate.isBlank()) "Seleccione una fecha" else null
        val timeError = if (s.selectedTime.isBlank()) "Seleccione una hora" else null

        if (s.isBooking || s.selectedDoctorId == null || dateError != null || timeError != null) {
            _uiState.update {
                it.copy(
                    errorMsg = "Debe seleccionar doctor, fecha y hora.",
                    dateError = dateError,
                    timeError = timeError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isBooking = true, errorMsg = null, bookingSuccess = false) }

            val patientId = userPreferences.userIdFlow.firstOrNull()
            if (patientId == null) {
                _uiState.update { it.copy(isBooking = false, errorMsg = "No se pudo identificar al usuario.") }
                return@launch
            }

            val startWithSeconds = formatTimeWithSeconds(s.selectedTime)
            val endWithSeconds = calculateEndTime(s.selectedTime, 30)

            val newAppointment = CitaDto(
                patientId = patientId,
                doctorId = s.selectedDoctorId,
                dateTime = "${s.selectedDate}T$startWithSeconds",
                startTime = startWithSeconds,
                endTime = endWithSeconds,
                durationMinutes = 30,
                status = "CONFIRMADA",
                available = false
            )

            val result = appointmentRepository.bookAppointment(newAppointment)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isBooking = false, bookingSuccess = true) // <-- ¡CAMBIO! Pone el flag en true
                } else {
                    it.copy(isBooking = false, errorMsg = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun formatTimeWithSeconds(time: String): String {
        val normalized = if (time.contains(":")) time.take(5) else time
        return "$normalized:00"
    }

    private fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        val parts = startTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return formatTimeWithSeconds(startTime)
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return formatTimeWithSeconds(startTime)
        val totalMinutes = (hour * 60 + minute + durationMinutes) % (24 * 60)
        val endHour = totalMinutes / 60
        val endMinute = totalMinutes % 60
        return String.format("%02d:%02d:00", endHour, endMinute)
    }
}

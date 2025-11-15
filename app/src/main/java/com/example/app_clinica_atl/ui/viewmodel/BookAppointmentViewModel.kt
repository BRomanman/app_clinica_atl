package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.DoctorRepository
// NO MÁS HILT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para Agendar Cita
data class BookAppointmentUiState(
    val specialties: List<String> = listOf(
        "Cardiología",
        "Dermatología",
        "Medicina General",
        "Pediatría",
        "Psicología"
    ),
    val doctors: List<UserEntity> = emptyList(), // <-- CAMBIO: UserEntity real
    val availableTimes: List<String> = emptyList(),
    val selectedSpecialty: String = "",
    val selectedDoctorId: Long? = null,
    val selectedDoctorName: String = "",
    val selectedDate: String = "", // Formato "YYYY-MM-DD"
    val selectedTime: String = "", // Formato "HH:MM"
    val isLoadingDoctors: Boolean = false,
    val isLoadingTimes: Boolean = false,
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false,
    val errorMsg: String? = null
)

// NO MÁS @HiltViewModel
class BookAppointmentViewModel( // <-- Constructor normal
    private val doctorRepository: DoctorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    // Horas disponibles (lógica de negocio)
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
                selectedTime = ""
            )
        }
        loadDoctorsBySpecialty(specialty)
    }

    fun onDoctorChange(doctor: UserEntity) { // <-- CAMBIO: Recibe UserEntity
        _uiState.update {
            it.copy(
                selectedDoctorId = doctor.id,
                selectedDoctorName = doctor.name,
                availableTimes = emptyList(),
                selectedDate = "",
                selectedTime = ""
            )
        }
    }

    fun onDateChange(date: String) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                availableTimes = emptyList(),
                selectedTime = ""
            )
        }
        val doctorId = _uiState.value.selectedDoctorId
        if (doctorId != null) {
            loadAvailableTimes(doctorId, date)
        }
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun clearBookingResult() {
        _uiState.update { it.copy(bookingSuccess = false, errorMsg = null) }
    }

    // --- Lógica de Carga de Datos ---

    private fun loadDoctorsBySpecialty(specialty: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDoctors = true, errorMsg = null) }
            val result = doctorRepository.getDoctorsBySpecialty(specialty)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isLoadingDoctors = false,
                        doctors = result.getOrNull() ?: emptyList()
                    )
                } else {
                    it.copy(
                        isLoadingDoctors = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar doctores"
                    )
                }
            }
        }
    }

    private fun loadAvailableTimes(doctorId: Long, date: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTimes = true, errorMsg = null) }
            val result = appointmentRepository.getBookedTimes(doctorId, date)
            _uiState.update {
                if (result.isSuccess) {
                    val bookedTimes = result.getOrNull() ?: emptyList()
                    val availableSlots = allDaySlots.filter { it !in bookedTimes }
                    it.copy(
                        isLoadingTimes = false,
                        availableTimes = availableSlots
                    )
                } else {
                    it.copy(
                        isLoadingTimes = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar horas"
                    )
                }
            }
        }
    }

    // --- Acción Principal ---

    fun submitBooking() {
        val s = _uiState.value

        if (s.isBooking || s.selectedDoctorId == null || s.selectedDate.isBlank() || s.selectedTime.isBlank()) {
            _uiState.update { it.copy(errorMsg = "Debe seleccionar especialidad, doctor, fecha y hora.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isBooking = true, errorMsg = null, bookingSuccess = false) }

            // ¡OBTENEMOS EL PACIENTE REAL!
            val patientId = userPreferences.userIdFlow.firstOrNull()

            if (patientId == null) {
                _uiState.update {
                    it.copy(
                        isBooking = false,
                        errorMsg = "No se pudo identificar al usuario. Inicie sesión de nuevo."
                    )
                }
                return@launch
            }

            val newAppointment = AppointmentEntity(
                patientId = patientId, // <-- ID Real del paciente
                doctorId = s.selectedDoctorId,
                date = s.selectedDate,
                time = s.selectedTime,
                status = "agendada"
            )

            val result = appointmentRepository.bookAppointment(newAppointment)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isBooking = false, bookingSuccess = true)
                } else {
                    it.copy(
                        isBooking = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al agendar"
                    )
                }
            }
        }
    }
}
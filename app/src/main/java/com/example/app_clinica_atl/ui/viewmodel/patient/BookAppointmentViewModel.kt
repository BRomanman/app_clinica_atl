package com.example.app_clinica_atl.ui.viewmodel.patient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.citas.CitasApiService
import com.example.app_clinica_atl.data.remote.dto.ReservarCitaRequest
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.specialty.SpecialtyCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CitaSlotUi(
    val id: Long,
    val horaInicio: String,
    val horaFin: String
) {
    val displayLabel: String
        get() = "$horaInicio - $horaFin"
}

data class BookAppointmentUiState(
    val specialties: List<String> = emptyList(),
    val doctors: List<UsuarioDto> = emptyList(),
    val slots: List<CitaSlotUi> = emptyList(),
    val selectedSpecialty: String = "",
    val selectedDoctorUserId: Long? = null,
    val selectedDoctorBackendId: Long? = null,
    val selectedDoctorName: String = "",
    val selectedDate: String = "",
    val selectedSlotId: Long? = null,
    val isLoadingSpecialties: Boolean = false,
    val isLoadingDoctors: Boolean = false,
    val isLoadingSlots: Boolean = false,
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false,
    val errorMessage: String? = null,
    val dateError: String? = null,
    val slotError: String? = null,
    val isDatePickerVisible: Boolean = false
)

class BookAppointmentViewModel(
    private val doctorRepository: DoctorRepository,
    private val citasApiService: CitasApiService,
    private val userPreferences: UserPreferences,
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    init {
        loadSpecialties()
    }

    fun onSpecialtyChange(specialty: String) {
        _uiState.update {
            it.copy(
                selectedSpecialty = specialty,
                doctors = emptyList(),
                selectedDoctorUserId = null,
                selectedDoctorBackendId = null,
                selectedDoctorName = "",
                selectedDate = "",
                slots = emptyList(),
                selectedSlotId = null,
                slotError = null,
                errorMessage = null,
                bookingSuccess = false
            )
        }
        loadDoctorsBySpecialty(specialty)
    }

    fun onDoctorChange(doctor: UsuarioDto) {
        _uiState.update {
            it.copy(
                selectedDoctorUserId = doctor.id,
                selectedDoctorBackendId = null,
                selectedDoctorName = "Dr/a ${doctor.name}",
                slots = emptyList(),
                selectedSlotId = null,
                slotError = null,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val doctorIdResult = usuariosRepository.getDoctorIdForUser(doctor.id)
            if (doctorIdResult.isSuccess) {
                _uiState.update { it.copy(selectedDoctorBackendId = doctorIdResult.getOrNull()) }
                loadSlotsIfReady()
            } else {
                _uiState.update {
                    it.copy(errorMessage = "No se pudo identificar el registro de doctor seleccionado.")
                }
            }
        }
    }

    fun onDateSelected(date: String) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                selectedSlotId = null,
                slots = emptyList(),
                slotError = null,
                dateError = null,
                isDatePickerVisible = false
            )
        }
        loadSlotsIfReady()
    }

    fun showDatePicker() {
        val doctorId = resolveDoctorId()
        if (doctorId != null) {
            _uiState.update { it.copy(isDatePickerVisible = true, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Seleccione un doctor primero") }
        }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(isDatePickerVisible = false) }
    }

    private fun resolveDoctorId(): Long? {
        val state = _uiState.value
        // Usar primero el id de doctor de backend (si existe),
        // si no, caer al id de usuario del doctor.
        return state.selectedDoctorBackendId ?: state.selectedDoctorUserId
    }

    fun onSlotSelected(slotId: Long) {
        _uiState.update { it.copy(selectedSlotId = slotId, slotError = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, slotError = null, dateError = null) }
    }

    fun onBookingSuccessHandled() {
        _uiState.update {
            it.copy(
                bookingSuccess = false,
                selectedSpecialty = "",
                selectedDoctorUserId = null,
                selectedDoctorBackendId = null,
                selectedDoctorName = "",
                selectedDate = "",
                selectedSlotId = null,
                slots = emptyList(),
                isLoadingSlots = false,
                errorMessage = null,
                slotError = null,
                dateError = null,
                isDatePickerVisible = false
            )
        }
    }

    private fun loadSpecialties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSpecialties = true, errorMessage = null) }
            val result = usuariosRepository.getAllSpecialties()
            _uiState.update {
                if (result.isSuccess) {
                    val availableOfficial = result.getOrNull()
                        ?.mapNotNull { spec -> SpecialtyCatalog.canonicalName(spec.nombre) }
                        ?.toSet()
                        .orEmpty()
                    val names = if (availableOfficial.isEmpty()) {
                        SpecialtyCatalog.officialSpecialties
                    } else {
                        SpecialtyCatalog.officialSpecialties.filter { it in availableOfficial }
                    }
                    it.copy(isLoadingSpecialties = false, specialties = names)
                } else {
                    it.copy(
                        isLoadingSpecialties = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    private fun loadDoctorsBySpecialty(specialty: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDoctors = true, errorMessage = null) }
            val result = doctorRepository.getDoctorsBySpecialty(specialty)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoadingDoctors = false, doctors = result.getOrNull() ?: emptyList())
                } else {
                    it.copy(
                        isLoadingDoctors = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    private fun loadSlotsIfReady() {
        val doctorId = resolveDoctorId()
        val date = _uiState.value.selectedDate

        if (doctorId == null || date.isBlank()) {
            // No tenemos suficientes datos para consultar las citas aún (falta doctor o fecha)
            Log.d(
                "BookAppointmentVM",
                "loadSlotsIfReady: doctorId=$doctorId date=$date (esperando selección)"
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoadingSlots = true, errorMessage = null) }
            try {
                Log.d("BookAppointmentVM", "Solicitando citas doctor=$doctorId fecha=$date")
                val citas = citasApiService.getCitasDoctorFecha(doctorId, date)
                Log.d("BookAppointmentVM", "Citas recibidas: ${citas.size}")

                val disponibles = citas
                    .filter { it.disponible && it.estado.equals("Disponible", ignoreCase = true) }
                    .map {
                        CitaSlotUi(
                            id = it.id,
                            horaInicio = it.horaInicio?.take(5).orEmpty(),
                            horaFin = it.horaFin?.take(5).orEmpty()
                        )
                    }

                _uiState.update {
                    it.copy(
                        slots = disponibles,
                        selectedSlotId = null,
                        isLoadingSlots = false,
                        slotError = null
                    )
                }
            } catch (e: Exception) {
                Log.e("BookAppointmentVM", "Error cargando citas", e)
                _uiState.update {
                    it.copy(
                        slots = emptyList(),
                        selectedSlotId = null,
                        isLoadingSlots = false,
                        errorMessage = "No se pudieron cargar los horarios disponibles."
                    )
                }
            }
        }
    }

    fun onConfirmBooking() {
        val state = _uiState.value
        if (state.selectedDate.isBlank()) {
            _uiState.update { it.copy(dateError = "Seleccione una fecha") }
            return
        }
        val slotId = state.selectedSlotId
        if (slotId == null) {
            _uiState.update { it.copy(slotError = "Seleccione un horario") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isBooking = true, errorMessage = null) }
            try {
                val userId = obtenerUserIdLogueado()
                if (userId == null) {
                    Log.e(
                        "BookAppointmentVM",
                        "No se encontró userId para reservar la cita."
                    )
                    _uiState.update {
                        it.copy(
                            isBooking = false,
                            errorMessage = "No se pudo identificar al usuario para reservar."
                        )
                    }
                    return@launch
                }
                val request = ReservarCitaRequest(idUsuario = userId)
                Log.d(
                    "BookAppointmentVM",
                    "Reservando cita slotId=$slotId para userId=$userId"
                )
                citasApiService.reservarCita(slotId, request)
                _uiState.update { it.copy(isBooking = false, bookingSuccess = true) }
            } catch (e: Exception) {
                Log.e("BookAppointmentVM", "Error reservando cita", e)
                _uiState.update {
                    it.copy(
                        isBooking = false,
                        errorMessage = "Error al reservar la cita. Intenta nuevamente."
                    )
                }
            }
        }
    }

    private suspend fun obtenerUserIdLogueado(): Long? {
        return userPreferences.userIdFlow.firstOrNull()
    }
}

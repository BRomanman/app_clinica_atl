package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.AppointmentRepository
// --- 1. IMPORTAR USER REPOSITORY ---
import com.example.app_clinica_atl.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Estado de la UI para la pantalla de agendamiento.
 * Esta es la definición completa que la UI (BookAppointmentScreen) espera.
 */
data class BookAppointmentUiState(
    val departments: List<String> = emptyList(),
    val doctors: List<DoctorInfo> = emptyList(),
    val availableTimes: List<LocalTime> = emptyList(),

    val selectedDepartment: String? = null,
    val selectedDoctor: DoctorInfo? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    val departmentExpanded: Boolean = false,
    val doctorExpanded: Boolean = false,
    val timeExpanded: Boolean = false,
    val showDatePicker: Boolean = false, // Para controlar el DatePickerDialog

    val isSubmitting: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val submissionError: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class BookAppointmentViewModel(
    private val repository: AppointmentRepository,
    // --- 2. INYECTAR EL USER REPOSITORY (AHORA SÍ EXISTE) ---
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // Obtenemos los departamentos y horarios "mockeados" del repositorio
        _uiState.update {
            it.copy(
                departments = repository.getDepartments(),
                availableTimes = repository.getAvailableSlots(LocalDate.now(), DoctorInfo()) // (La fecha/doctor aquí no importan para tu lógica actual)
            )
        }
    }

    // --- Handlers de Selección (Sin cambios) ---
    fun onDepartmentSelected(department: String) {
        val doctors = repository.getDoctorsByDepartment(department)
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                doctors = doctors,
                // Si el doctor seleccionado ya no está en la nueva lista, lo limpiamos
                selectedDoctor = if (it.selectedDoctor !in doctors) null else it.selectedDoctor,
                departmentExpanded = false,
                doctorExpanded = false
            )
        }
    }

    fun onDoctorSelected(doctor: DoctorInfo) {
        _uiState.update { it.copy(selectedDoctor = doctor, doctorExpanded = false) }
    }

    fun onDateSelected(millis: Long?) { // Recibe milisegundos del DatePicker
        val date = millis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        _uiState.update { it.copy(selectedDate = date, showDatePicker = false) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time, timeExpanded = false) }
    }

    // --- Handlers de UI (Sin cambios) ---
    fun onDepartmentExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(departmentExpanded = expanded) }
    }
    fun onDoctorExpandedChange(expanded: Boolean) {
        if (_uiState.value.doctors.isNotEmpty()) {
            _uiState.update { it.copy(doctorExpanded = expanded) }
        }
    }
    fun onTimeExpandedChange(expanded: Boolean) {
        if (_uiState.value.selectedDate != null) {
            _uiState.update { it.copy(timeExpanded = expanded) }
        }
    }
    fun showDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }
    fun dismissConfirmationDialog() {
        _uiState.update { it.copy(showConfirmationDialog = false) }
    }

    // --- Lógica de Negocio: Guardar la Cita (AHORA SÍ FUNCIONA) ---
    fun submitAppointment() {
        val state = _uiState.value
        val doctor = state.selectedDoctor
        val date = state.selectedDate
        val time = state.selectedTime
        val department = state.selectedDepartment

        // Validación simple
        if (doctor == null || date == null || time == null || department == null) {
            _uiState.update { it.copy(submissionError = "Por favor, completa todos los campos.") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, submissionError = null) }

        viewModelScope.launch {
            try {
                // 3. OBTENER EL ID DEL USUARIO (PACIENTE)
                // (Ahora 'getLoggedInUser()' SÍ existe en el UserRepository)
                val currentUser = userRepository.getLoggedInUser().firstOrNull()

                // (Ahora 'id_rol' SÍ existe en UserEntity)
                if (currentUser == null || currentUser.id_rol != 1L) { // Asegurarse de que sea un paciente
                    _uiState.update { it.copy(isSubmitting = false, submissionError = "Error: No se encontró un paciente válido. Vuelve a iniciar sesión.") }
                    return@launch
                }

                // 4. GUARDAR LA CITA REAL en la base de datos
                repository.saveAppointment(
                    patientId = currentUser.id, // <-- (Ahora 'id' SÍ existe)
                    doctorName = doctor.name,
                    department = department,
                    date = date,
                    time = time
                )

                // 5. Éxito
                _uiState.update { it.copy(isSubmitting = false, showConfirmationDialog = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, submissionError = "Error al guardar la cita: ${e.message}") }
            }
        }
    }
}
package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

// Data class para la solicitud (la mantenemos aquí por simplicidad)
data class AppointmentRequest(
    val department: String,
    val doctor: DoctorInfo,
    val date: LocalDate,
    val time: LocalTime
)

// Estado de la UI
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
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    init { loadInitialData() }

    private fun loadInitialData() {
        _uiState.update {
            it.copy(
                departments = repository.getDepartments(),
                availableTimes = repository.getAvailableTimes()
            )
        }
    }

    // --- Handlers ---
    fun onDepartmentSelected(department: String) {
        val doctors = repository.getDoctorsByDepartment(department)
        _uiState.update {
            it.copy(
                selectedDepartment = department, doctors = doctors,
                selectedDoctor = if (it.selectedDoctor !in doctors) null else it.selectedDoctor,
                departmentExpanded = false, doctorExpanded = false
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

    // Control de menús y diálogos
    fun onDepartmentExpandedChange(expanded: Boolean) { _uiState.update { it.copy(departmentExpanded = expanded) } }
    fun onDoctorExpandedChange(expanded: Boolean) { if (_uiState.value.doctors.isNotEmpty()) _uiState.update { it.copy(doctorExpanded = expanded) } }
    fun onTimeExpandedChange(expanded: Boolean) { if (_uiState.value.selectedDate != null) _uiState.update { it.copy(timeExpanded = expanded) } }
    fun showDatePicker(show: Boolean) { _uiState.update { it.copy(showDatePicker = show) } }
    fun dismissConfirmationDialog() { _uiState.update { it.copy(showConfirmationDialog = false) } }

    // Envío
    fun submitAppointment() {
        val s = _uiState.value
        if (s.selectedDepartment == null || s.selectedDoctor == null || s.selectedDate == null || s.selectedTime == null || s.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submissionError = null) }
            val request = AppointmentRequest(s.selectedDepartment, s.selectedDoctor, s.selectedDate, s.selectedTime)
            val result = repository.submitAppointment(request)
            _uiState.update {
                if (result.isSuccess) it.copy(isSubmitting = false, showConfirmationDialog = true)
                else it.copy(isSubmitting = false, submissionError = result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}
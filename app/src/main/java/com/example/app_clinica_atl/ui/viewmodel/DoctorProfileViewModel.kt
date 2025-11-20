package com.example.app_clinica_atl.ui.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.DoctorMonthlyStatDto
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioResponseDto
import com.example.app_clinica_atl.data.repository.DoctorProfileRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DoctorProfileInfo(
    val userId: Long,
    val doctorId: Long?,
    val name: String,
    val email: String,
    val specialty: String?,
    val phone: String,
    val role: String?,
    val bono: Long?,
    val sueldo: Long?,
    val tarifaConsulta: Int?,
    val profileImageUrl: String?
)

data class DoctorProfileUiState(
    val isLoading: Boolean = true,
    val doctor: DoctorProfileInfo? = null,
    val phoneInput: String = "",
    val phoneError: String? = null,
    val isSavingPhone: Boolean = false,
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSavingPassword: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val errorMsg: String? = null,
    val transientError: String? = null,
    val successMsg: String? = null,
    val stats: List<DoctorMonthlyStatDto> = emptyList(),
    val totalAppointments: Int = 0,
    val bonusAmount: Double = 0.0
)

class DoctorProfileViewModel(
    private val doctorProfileRepository: DoctorProfileRepository,
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState())
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: Long? = null
    private var currentDoctorId: Long? = null
    private var statsJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDoctorProfile(userId: Long) {
        currentUserId = userId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            val result = doctorProfileRepository.getDoctorProfile(userId)
            if (result.isSuccess) {
                val dto = result.getOrNull()!!
                val info = dto.toDoctorProfileInfo(_uiState.value.doctor?.profileImageUrl)
                currentDoctorId = info.doctorId
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        doctor = info,
                        phoneInput = info.phone,
                        errorMsg = null
                    )
                }
                info.doctorId?.let { observeStats(it) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        doctor = null,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar el perfil"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeStats(doctorId: Long) {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            val result = doctorProfileRepository.getAppointmentsForDoctor(doctorId)
            if (result.isSuccess) {
                val stats = calculateMonthlyStats(result.getOrNull().orEmpty())
                val total = stats.sumOf { it.totalAppointments }
                _uiState.update {
                    it.copy(
                        stats = stats,
                        totalAppointments = total,
                        bonusAmount = total * 0.1
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        transientError = result.exceptionOrNull()?.message ?: "No se pudieron cargar las estadísticas."
                    )
                }
            }
        }
    }

    fun onPhoneChange(phone: String) {
        val phoneError = validateChileanPhoneNumber(phone)
        _uiState.update { it.copy(phoneInput = phone, phoneError = phoneError) }
    }

    fun savePhone() {
        val userId = currentUserId ?: return
        val phone = _uiState.value.phoneInput
        val phoneError = validateChileanPhoneNumber(phone)
        if (phoneError != null) {
            _uiState.update { it.copy(phoneError = phoneError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingPhone = true) }
            val result = usuariosRepository.updatePhoneNumber(userId, phone)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isSavingPhone = false,
                        doctor = it.doctor?.copy(phone = phone),
                        phoneError = null,
                        successMsg = "Teléfono actualizado correctamente.",
                        transientError = null
                    )
                } else {
                    it.copy(
                        isSavingPhone = false,
                        transientError = result.exceptionOrNull()?.message ?: "Ocurrió un error al actualizar el teléfono."
                    )
                }
            }
        }
    }

    fun onPasswordChange(value: String) {
        val confirm = _uiState.value.confirmPasswordInput
        val validation = validateRegisterPassword(value, confirm)
        val msg = validation.exceptionOrNull()?.message
        _uiState.update {
            it.copy(
                passwordInput = value,
                passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) },
                transientError = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        val current = _uiState.value.passwordInput
        val validation = validateRegisterPassword(current, value)
        val msg = validation.exceptionOrNull()?.message
        _uiState.update {
            it.copy(
                confirmPasswordInput = value,
                passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) },
                transientError = null
            )
        }
    }

    fun savePassword() {
        val userId = currentUserId ?: return
        val state = _uiState.value
        val validation = validateRegisterPassword(state.passwordInput, state.confirmPasswordInput)
        if (validation.isFailure) {
            val msg = validation.exceptionOrNull()?.message
            _uiState.update {
                it.copy(
                    passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                    confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) },
                    transientError = msg?.takeIf { text -> !text.contains("débil", true) && !text.contains("coinciden", true) }
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingPassword = true) }
            val result = usuariosRepository.updatePassword(userId, state.passwordInput)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isSavingPassword = false,
                        passwordInput = "",
                        confirmPasswordInput = "",
                        passwordError = null,
                        confirmPasswordError = null,
                        successMsg = "Contraseña actualizada.",
                        transientError = null
                    )
                } else {
                    it.copy(
                        isSavingPassword = false,
                        transientError = result.exceptionOrNull()?.message ?: "No se pudo actualizar la contraseña."
                    )
                }
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true) }
            val result = usuariosRepository.updateProfileImageUrl(userId, uri.toString())
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        isUploadingPhoto = false,
                        doctor = it.doctor?.copy(profileImageUrl = uri.toString()),
                        successMsg = "Foto de perfil actualizada.",
                        transientError = null
                    )
                } else {
                    it.copy(
                        isUploadingPhoto = false,
                        transientError = result.exceptionOrNull()?.message ?: "No se pudo actualizar la foto."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(transientError = null, successMsg = null) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyStats(appointments: List<CitaDto>): List<DoctorMonthlyStatDto> {
        val formatter = DateTimeFormatter.ISO_DATE
        val counts = mutableMapOf<String, Int>()
        appointments
            .filter { it.status.equals("CONFIRMADA", true) || it.status.equals("COMPLETADA", true) }
            .forEach { cita ->
                val ym = runCatching {
                    YearMonth.from(LocalDate.parse(cita.date, formatter))
                }.getOrNull() ?: return@forEach
                val key = ym.toString()
                counts[key] = (counts[key] ?: 0) + 1
            }
        return counts.entries
            .sortedByDescending { it.key }
            .map { DoctorMonthlyStatDto(month = it.key, totalAppointments = it.value) }
            .take(6)
    }
}

private fun UsuarioResponseDto.toDoctorProfileInfo(existingImage: String?): DoctorProfileInfo {
    val displayName = listOfNotNull(nombre, apellido).joinToString(" ").trim().ifBlank { correo.orEmpty() }
    val specialtyText = "Especialidad no disponible" // TODO: obtenerla cuando el backend la exponga
    return DoctorProfileInfo(
        userId = id,
        doctorId = doctor?.id,
        name = displayName,
        email = correo.orEmpty(),
        specialty = specialtyText,
        phone = telefono ?: "",
        role = rol,
        bono = doctor?.bono,
        sueldo = doctor?.sueldo,
        tarifaConsulta = doctor?.tarifaConsulta,
        profileImageUrl = existingImage
    )
}

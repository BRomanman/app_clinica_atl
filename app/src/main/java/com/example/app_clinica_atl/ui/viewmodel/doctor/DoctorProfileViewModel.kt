package com.example.app_clinica_atl.ui.viewmodel.doctor

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.CitaDto
import com.example.app_clinica_atl.data.remote.dto.DoctorMonthlyStatDto
import com.example.app_clinica_atl.data.remote.dto.HistorialDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.RolRequest
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.DoctorProfileRepository
import com.example.app_clinica_atl.data.repository.HistorialRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.util.copyUriToTempFile
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.get

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
    private val usuariosRepository: UsuariosRepository,
    private val historialRepository: HistorialRepository,
    private val citasRepository: CitasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState())
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    private val _profilePhotoUrl = MutableStateFlow<String?>(null)
    val profilePhotoUrl: StateFlow<String?> = _profilePhotoUrl.asStateFlow()
    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto.asStateFlow()
    private val _photoErrorMessage = MutableStateFlow<String?>(null)
    val photoErrorMessage: StateFlow<String?> = _photoErrorMessage.asStateFlow()
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private var currentUserId: Long? = null
    private var currentDoctorId: Long? = null
    private var statsJob: Job? = null
    private var specialtiesJob: Job? = null

    private fun refreshPhotoUrl(doctorId: Long?) {
        _profilePhotoUrl.value = doctorId
            ?.let { usuariosRepository.buildDoctorProfilePhotoUrl(it) }
            ?.let { appendTimestamp(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDoctorProfile(doctorId: Long) {
        currentUserId = doctorId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            _authToken.value = usuariosRepository.currentToken()
            val result = doctorProfileRepository.getDoctorProfile(doctorId)
            if (result.isSuccess) {
                val dto = result.getOrNull()!!
                val info = dto.toDoctorProfileInfo(_uiState.value.doctor?.profileImageUrl)
                currentDoctorId = info.doctorId ?: dto.id
                currentUserId = dto.usuario?.id ?: dto.id ?: doctorId
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        doctor = info,
                        phoneInput = info.phone,
                        errorMsg = null
                    )
                }
                val doctorKey = info.doctorId ?: dto.id ?: doctorId
                refreshPhotoUrl(doctorKey)
                if (doctorKey != null) {
                    observeStats(doctorKey)
                    observeSpecialties(doctorKey)
                } else {
                    _uiState.update { it.copy(transientError = "No se encontró id de doctor para calcular estadísticas.") }
                }
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
            val statsResult = runCatching {
                val histories = loadDoctorHistoriesOnly(doctorId).getOrThrow()
                val stats = calculateMonthlyStats(histories)
                if (stats.isNotEmpty()) stats else fallbackStatsFromAppointments(doctorId).getOrThrow()
            }.recoverCatching {
                fallbackStatsFromAppointments(doctorId).getOrThrow()
            }

            statsResult.onSuccess { stats ->
                val latestMonthTotal = stats.firstOrNull()?.totalAppointments ?: 0
                val tarifa = _uiState.value.doctor?.tarifaConsulta ?: 0
                _uiState.update {
                    it.copy(
                        stats = stats,
                        totalAppointments = latestMonthTotal,
                        bonusAmount = latestMonthTotal * tarifa * 0.1,
                        transientError = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        transientError = error.message ?: "No se pudieron cargar las estadísticas.",
                        stats = emptyList(),
                        totalAppointments = 0,
                        bonusAmount = 0.0
                    )
                }
            }
        }
    }

    private suspend fun loadDoctorHistoriesOnly(doctorId: Long): Result<List<HistorialDto>> {
        return historialRepository.getHistorialForDoctor(doctorId)
    }

    private fun observeSpecialties(doctorId: Long) {
        specialtiesJob?.cancel()
        specialtiesJob = viewModelScope.launch {
            val result = doctorProfileRepository.getSpecialtiesForDoctor(doctorId)
            if (result.isSuccess) {
                val names = result.getOrNull()
                    .orEmpty()
                    .mapNotNull { it.nombre?.trim()?.takeIf { name -> name.isNotBlank() } }
                    .distinct()
                _uiState.update { state ->
                    state.doctor?.let { current ->
                        state.copy(doctor = current.copy(specialty = names.joinToString(", ").ifBlank { null }))
                    } ?: state
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

    fun onNewProfilePhotoSelected(uri: Uri, context: Context) {
        val doctorId = currentDoctorId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isUploadingPhoto.value = true
            _photoErrorMessage.value = null
            _uiState.update { it.copy(isUploadingPhoto = true) }
            try {
                val tempFile = copyUriToTempFile(uri, context)
                val result = usuariosRepository.uploadDoctorProfilePhoto(tempFile)
                val baseUrl = usuariosRepository.buildDoctorProfilePhotoUrl(doctorId)
                if (result.isSuccess) {
                    _profilePhotoUrl.value = appendTimestamp(baseUrl)
                    _uiState.update {
                        it.copy(
                            isUploadingPhoto = false,
                            successMsg = "Foto de perfil actualizada.",
                            transientError = null
                        )
                    }
                } else {
                    val message = result.exceptionOrNull()?.message ?: "No se pudo actualizar la foto."
                    _photoErrorMessage.value = message
                    _uiState.update {
                        it.copy(isUploadingPhoto = false, transientError = message)
                    }
                }
            } catch (e: Exception) {
                val message = e.message ?: "No se pudo procesar la imagen."
                _photoErrorMessage.value = message
                _uiState.update {
                    it.copy(isUploadingPhoto = false, transientError = message)
                }
            } finally {
                _isUploadingPhoto.value = false
            }
        }
    }

    private fun appendTimestamp(url: String): String {
        val cleanUrl = url.substringBefore("?")
        return "$cleanUrl?ts=${System.currentTimeMillis()}"
    }

    fun clearMessages() {
        _uiState.update { it.copy(transientError = null, successMsg = null) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyStats(histories: List<HistorialDto>): List<DoctorMonthlyStatDto> {
        return histories
            .asSequence()
            .filter { hist ->
                val status = hist.estado?.lowercase()?.trim().orEmpty()
                !status.contains("cancel")
            }
            .mapNotNull { hist -> parseYearMonth(hist.fechaConsulta) }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.key }
            .map { DoctorMonthlyStatDto(month = it.key.toString(), totalAppointments = it.value) }
            .take(6)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fallbackStatsFromAppointments(doctorId: Long): Result<List<DoctorMonthlyStatDto>> {
        return citasRepository.getAppointmentsForDoctorOnce(doctorId)
            .map { calculateMonthlyStatsFromCitas(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyStatsFromCitas(citas: List<CitaDto>): List<DoctorMonthlyStatDto> {
        return citas
            .asSequence()
            .filter { cita ->
                !cita.status.contains("cancel", ignoreCase = true) &&
                        cita.patientId != null &&
                        cita.patientId!! > 0 &&
                        !cita.available
            }
            .mapNotNull { cita -> parseYearMonth(cita.date) }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.key }
            .map { DoctorMonthlyStatDto(month = it.key.toString(), totalAppointments = it.value) }
            .take(6)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseYearMonth(rawDate: String?): YearMonth? {
        if (rawDate.isNullOrBlank()) return null
        val trimmed = rawDate.trim()

        // Intentamos primero con formatos de fecha simples.
        val dateCandidates = listOf(
            trimmed,
            trimmed.substringBefore('T', trimmed).substringBefore(' ', trimmed),
            trimmed.substringBefore('+', trimmed)
        ).filter { it.isNotBlank() }.distinct()

        for (candidate in dateCandidates) {
            val parsedLocalDate = runCatching { LocalDate.parse(candidate, DateTimeFormatter.ISO_DATE) }.getOrNull()
                ?: runCatching { LocalDate.parse(candidate, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
            if (parsedLocalDate != null) return YearMonth.from(parsedLocalDate)
        }

        // Fallback: cadenas ISO con hora y/o zona.
        val withOffset = runCatching { OffsetDateTime.parse(trimmed) }.getOrNull()
        if (withOffset != null) return YearMonth.from(withOffset.toLocalDate())

        val withTime = runCatching { LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }.getOrNull()
        return withTime?.let { YearMonth.from(it.toLocalDate()) }
    }
}

private fun DoctorDto.toDoctorProfileInfo(existingImage: String?): DoctorProfileInfo {
    val user = usuario
    val displayName = listOfNotNull(
        user?.nombre ?: nombre,
        user?.apellido ?: apellido
    ).joinToString(" ")
        .trim()
        .ifBlank { (user?.correo ?: correo).orEmpty() }
    val specialtyText = especialidad?.takeIf { it.isNotBlank() } ?: "Especialidad no disponible"
    val resolvedEmail = (user?.correo ?: correo).orEmpty()
    val resolvedPhone = user?.telefono ?: telefono.orEmpty()
    val resolvedRole = when (val raw = user?.rol ?: tipo) {
        is String -> raw
        is Number -> raw.toLong().toString()
        is RolRequest -> raw.nombre ?: raw.id.toString()
        is Map<*, *> -> {
            val roleMap = raw as Map<*, *>
            val idCandidate = (roleMap["idRol"] ?: roleMap["id_rol"] ?: roleMap["id"]) as? Number
            val nameCandidate = (roleMap["nombre"] ?: roleMap["rol"] ?: roleMap["role"]) as? String
            nameCandidate ?: idCandidate?.toLong()?.toString()
        }
        else -> null
    }
    val resolvedDoctorId = id ?: user?.doctor?.id ?: user?.id
    return DoctorProfileInfo(
        userId = user?.id ?: (id ?: -1L),
        doctorId = resolvedDoctorId,
        name = displayName,
        email = resolvedEmail,
        specialty = specialtyText,
        phone = resolvedPhone,
        role = resolvedRole,
        bono = bono,
        sueldo = sueldo,
        tarifaConsulta = tarifaConsulta,
        profileImageUrl = existingImage ?: user?.imagenPerfil
    )
}

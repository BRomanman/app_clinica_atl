package com.example.app_clinica_atl.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.CitaDetalleDto
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioSeguroDto
import com.example.app_clinica_atl.data.repository.CitasRepository
import com.example.app_clinica_atl.data.repository.SegurosRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.domain.validation.validateRegisterPassword
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PatientProfileUiState(
    val isLoading: Boolean = true,
    val patient: UsuarioDto? = null,
    val activeInsuranceDetails: SeguroDto? = null,
    val activeSubscription: UsuarioSeguroDto? = null,
    val activeAppointments: List<CitaDetalleDto> = emptyList(),
    val errorMsg: String? = null,
    val successMsg: String? = null,
    val phoneInput: String = "",
    val phoneError: String? = null,
    val isSavingPhone: Boolean = false,
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSavingPassword: Boolean = false
)

class PatientViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences,
    private val insuranceRepository: SegurosRepository,
    private val appointmentRepository: CitasRepository
) : ViewModel() {

    // --- LÓGICA REACTIVA ---
    private val _messageState = MutableStateFlow(Pair<String?, String?>(null, null))
    private val _editState = MutableStateFlow(PatientProfileEditState())
    private val _profileImageOverride = MutableStateFlow<String?>(null)
    private var cachedUserId: Long? = null

    private data class PatientProfileEditState(
        val phoneInput: String = "",
        val phoneError: String? = null,
        val isSavingPhone: Boolean = false,
        val passwordInput: String = "",
        val confirmPasswordInput: String = "",
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val isSavingPassword: Boolean = false
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PatientProfileUiState> = userPreferences.userIdFlow
        .flatMapLatest { userId ->
            cachedUserId = userId
            if (userId == null) {
                flowOf(
                    PatientProfileUiState(
                        isLoading = false,
                        errorMsg = "Usuario no encontrado. Inicie sesión."
                    )
                )
            } else {
                val base = combine(
                    userRepository.getUserByIdAsFlow(userId),
                    insuranceRepository.getActiveSubscriptionDetails(userId),
                    insuranceRepository.getActiveSubscription(userId),
                    appointmentRepository.getAppointmentsForPatient(userId),
                    _profileImageOverride
                ) { patient, insuranceDetails, insuranceSub, appointments, overrideImage ->
                    val enrichedPatient = patient?.copy(
                        profileImageUrl = overrideImage ?: patient.profileImageUrl
                    )
                    PatientProfileUiState(
                        isLoading = false,
                        patient = enrichedPatient,
                        activeInsuranceDetails = insuranceDetails,
                        activeSubscription = insuranceSub,
                        activeAppointments = appointments
                    )
                }

                base.combine(_editState) { dataState, editState ->
                    dataState.copy(
                        phoneInput = editState.phoneInput.ifBlank { dataState.patient?.phone.orEmpty() },
                        phoneError = editState.phoneError,
                        isSavingPhone = editState.isSavingPhone,
                        passwordInput = editState.passwordInput,
                        confirmPasswordInput = editState.confirmPasswordInput,
                        passwordError = editState.passwordError,
                        confirmPasswordError = editState.confirmPasswordError,
                        isSavingPassword = editState.isSavingPassword
                    )
                }
            }
        }.catch { e ->
            emit(PatientProfileUiState(isLoading = false, errorMsg = e.message))
        }
        .combine(_messageState) { dataState, messageState ->
            dataState.copy(
                errorMsg = messageState.first,
                successMsg = messageState.second
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PatientProfileUiState()
        )
    // --- FIN LÓGICA REACTIVA ---


    fun cancelSubscription() {
        viewModelScope.launch {
            val subscriptionId = uiState.value.activeSubscription?.id
            if (subscriptionId == null) {
                _messageState.update { it.copy(first = "No se encontró suscripción para cancelar.") }
                return@launch
            }
            val result = insuranceRepository.cancelSubscription(subscriptionId)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Seguro cancelado con éxito.") }
            } else {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            val result = appointmentRepository.cancelAppointment(appointmentId)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Cita cancelada con éxito.") }
            } else {
                // --- ¡¡AQUÍ ESTÁ LA CORRECCIÓN!! ---
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) } // Era _messageS
            }
        }
    }

    fun onPhoneChange(phone: String) {
        val error = validateChileanPhoneNumber(phone)
        _editState.update { it.copy(phoneInput = phone, phoneError = error) }
    }

    fun savePhone() {
        val phone = _editState.value.phoneInput
        val phoneError = validateChileanPhoneNumber(phone)
        if (phoneError != null) {
            _editState.update { it.copy(phoneError = phoneError) }
            return
        }
        val userId = cachedUserId ?: return
        viewModelScope.launch {
            _editState.update { it.copy(isSavingPhone = true) }
            val result = userRepository.updatePhoneNumber(userId, phone)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Teléfono actualizado.") }
                _editState.update { it.copy(isSavingPhone = false, phoneError = null) }
            } else {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
                _editState.update { it.copy(isSavingPhone = false) }
            }
        }
    }

    fun onPasswordChange(value: String) {
        val validation = validateRegisterPassword(value, _editState.value.confirmPasswordInput)
        val msg = validation.exceptionOrNull()?.message
        _editState.update {
            it.copy(
                passwordInput = value,
                passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) }
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        val validation = validateRegisterPassword(_editState.value.passwordInput, value)
        val msg = validation.exceptionOrNull()?.message
        _editState.update {
            it.copy(
                confirmPasswordInput = value,
                passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) }
            )
        }
    }

    fun savePassword() {
        val state = _editState.value
        val validation = validateRegisterPassword(state.passwordInput, state.confirmPasswordInput)
        val msg = validation.exceptionOrNull()?.message
        if (validation.isFailure) {
            _editState.update {
                it.copy(
                    passwordError = msg?.takeIf { text -> text.contains("débil", ignoreCase = true) },
                    confirmPasswordError = msg?.takeIf { text -> text.contains("coinciden", ignoreCase = true) }
                )
            }
            if (msg != null) _messageState.update { it.copy(first = msg) }
            return
        }
        val userId = cachedUserId ?: return
        viewModelScope.launch {
            _editState.update { it.copy(isSavingPassword = true) }
            val result = userRepository.updatePassword(userId, state.passwordInput)
            if (result.isSuccess) {
                _messageState.update { it.copy(second = "Contraseña actualizada.") }
                _editState.update {
                    it.copy(
                        isSavingPassword = false,
                        passwordInput = "",
                        confirmPasswordInput = "",
                        passwordError = null,
                        confirmPasswordError = null
                    )
                }
            } else {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
                _editState.update { it.copy(isSavingPassword = false) }
            }
        }
    }

    fun clearMessages() {
        _messageState.update { Pair(null, null) }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            val userId = userPreferences.userIdFlow.firstOrNull()
            if (userId == null) {
                _messageState.update { it.copy(first = "No se pudo encontrar al usuario.") }
                return@launch
            }
            val result = userRepository.updateProfileImageUrl(userId, uri.toString())
            if (result.isFailure) {
                _messageState.update { it.copy(first = result.exceptionOrNull()?.message) }
            } else {
                _profileImageOverride.update { uri.toString() }
                _messageState.update { it.copy(second = "Foto de perfil actualizada.") }
            }
        }
    }
}

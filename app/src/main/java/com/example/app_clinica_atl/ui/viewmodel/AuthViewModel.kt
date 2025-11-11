package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// --- 1. IMPORTAR AppointmentEntity ---
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
// --- 2. IMPORTAR LOS 3 REPOSITORIOS ---
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// (LoginUiState y RegisterUiState se mantienen igual)
data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val loggedUser: UserEntity? = null
)

data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val fecha_nacimiento: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",

    val nombreError: String? = null,
    val apellidoError: String? = null,
    val fechaNacimientoError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// (BookAppointmentUiState se movió a su propio ViewModel)


@RequiresApi(Build.VERSION_CODES.O)
class AuthViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences,
    // --- 3. AÑADIR LOS NUEVOS REPOSITORIOS ---
    private val appointmentRepo: AppointmentRepository,
    private val doctorRepo: DoctorRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _userDisplayName = MutableStateFlow("Bienvenido/a a la Clínica")
    val userDisplayName: StateFlow<String> = _userDisplayName

    private val _currentUserData = MutableStateFlow<UserEntity?>(null)
    val currentUserData: StateFlow<UserEntity?> = _currentUserData.asStateFlow()

    // --- 4. ¡NUEVOS ESTADOS! ---
    // (Estos son los estados que movimos desde los otros ViewModels)

    // Para Perfil Paciente
    private val _userAppointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val userAppointments: StateFlow<List<AppointmentEntity>> = _userAppointments.asStateFlow()

    // Para Perfil Doctor
    private val _doctorAppointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val doctorAppointments: StateFlow<List<AppointmentEntity>> = _doctorAppointments.asStateFlow() // <-- ¡AQUÍ ESTABA EL BUG SUTIL!

    private val _currentDoctorInfo = MutableStateFlow<DoctorInfo?>(null)
    val currentDoctorInfo: StateFlow<DoctorInfo?> = _currentDoctorInfo.asStateFlow()

    private val _isSavingProfile = MutableStateFlow(false)
    val isSavingProfile: StateFlow<Boolean> = _isSavingProfile.asStateFlow()

    private val _saveProfileSuccess = MutableStateFlow(false)
    val saveProfileSuccess: StateFlow<Boolean> = _saveProfileSuccess.asStateFlow()
    // --- FIN 4 ---


    // (BookAppointmentUiState ya no está aquí)

    init {
        viewModelScope.launch {
            // (La carga de departamentos se movió a BookAppointmentViewModel)

            // --- 7. ¡ACTUALIZADO! Carga los datos del usuario al iniciar ---
            val loggedInEmail = userPreferences.loggedInUserEmail.first()
            if (loggedInEmail != null) {
                // Usamos .first() aquí porque init solo corre una vez.
                val user = repository.getLoggedInUser().firstOrNull()
                if (user != null) {
                    _currentUserData.value = user
                    _userDisplayName.value = "Hola, ${user.nombre} (${if(user.id_rol == 1L) "Paciente" else if(user.id_rol == 2L) "Doctor" else "Admin"})."

                    // --- ¡NUEVO! Cargar datos según el rol ---
                    loadRoleSpecificData(user)
                }
            }
        }
    }


    // ----------------- LOGIN: Handlers -----------------
    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }
    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nombreError, s.apellidoError, s.fechaNacimientoError, s.emailError,
            s.phoneError, s.passError, s.confirmError
        ).all { it == null }
        val filled = s.nombre.isNotBlank() && s.apellido.isNotBlank() &&
                s.fecha_nacimiento.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled && s.fecha_nacimiento.length == 10) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)
            val result = repository.login(s.email.trim(), s.pass)
            val user = result.getOrNull()
            val errorMessage = result.exceptionOrNull()?.message ?: "Error de autenticación"
            _login.update {
                if (user != null) {

                    // --- ¡¡¡AQUÍ ESTÁ LA CORRECCIÓN!!! ---
                    // (Restauramos las líneas que borré por error)
                    _userDisplayName.value = "Hola, ${user.nombre} (${if(user.id_rol == 1L) "Paciente" else if(user.id_rol == 2L) "Doctor" else "Admin"})."
                    _currentUserData.value = user

                    // *** ¡LLAMADA A LA LÓGICA CENTRALIZADA! ***
                    loadRoleSpecificData(user)
                    // --- FIN DE LA CORRECCIÓN ---

                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null,
                        loggedUser = user
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = errorMessage,
                        loggedUser = null
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null, loggedUser = null) }
    }

    suspend fun logout() {
        _login.value = LoginUiState()
        _register.value = RegisterUiState()
        _userDisplayName.value = "Bienvenido/a a la Clínica"
        _currentUserData.value = null

        // --- ¡NUEVO! Limpiar datos de rol ---
        _userAppointments.value = emptyList()
        _doctorAppointments.value = emptyList()
        _currentDoctorInfo.value = null
        // --- FIN ---

        userPreferences.setLoggedIn(false)
    }

    // (La lógica de Registro se mantiene igual)
    fun onNombreChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nombre = filtered, nombreError = validateNamePart(filtered, "El nombre"))
        }
        recomputeRegisterCanSubmit()
    }
    fun onApellidoChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(apellido = filtered, apellidoError = validateNamePart(filtered, "El apellido"))
        }
        recomputeRegisterCanSubmit()
    }
    fun onFechaNacimientoChange(value: String) {
        val digits = value.filter(Char::isDigit).take(8)
        val formatted = buildString {
            for ((index, char) in digits.withIndex()) {
                append(char)
                if ((index == 1 || index == 3)) {
                    if (digits.length > index + 1) {
                        append('-')
                    }
                }
            }
        }
        _register.update {
            var error: String?
            if (formatted.isBlank()) {
                error = "La fecha es obligatoria"
            } else if (formatted.length < 10) {
                error = "Formato debe ser DD-MM-YYYY"
            } else {
                error = validateFechaNacimiento(formatted)
                if (error == null) {
                    error = validateEdadMinima(formatted, 18)
                }
            }
            it.copy(
                fecha_nacimiento = formatted,
                fechaNacimientoError = error
            )
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }
    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateSimplePassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }
    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }
    fun submitRegister() {
        val s = _register.value
        val nombreError = validateNamePart(s.nombre.trim(), "El nombre")
        val apellidoError = validateNamePart(s.apellido.trim(), "El apellido")
        val fechaError = if (s.fecha_nacimiento.length < 10) "Formato debe ser DD-MM-YYYY"
        else validateFechaNacimiento(s.fecha_nacimiento) ?: validateEdadMinima(s.fecha_nacimiento, 18)
        val emailError = validateEmail(s.email.trim())
        val phoneError = validatePhoneDigitsOnly(s.phone.trim())
        val passError = validateSimplePassword(s.pass)
        val confirmError = validateConfirm(s.pass, s.confirm)
        val errors = listOf(
            nombreError, apellidoError, fechaError, emailError,
            phoneError, passError, confirmError
        )
        val hasError = errors.any { it != null }
        if (hasError) {
            _register.update {
                it.copy(
                    nombreError = nombreError,
                    apellidoError = apellidoError,
                    fechaNacimientoError = fechaError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passError = passError,
                    confirmError = confirmError
                )
            }
            return
        }
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)
            val result = repository.register(
                nombre = s.nombre.trim(),
                apellido = s.apellido.trim(),
                fecha_nacimiento = s.fecha_nacimiento.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.pass
            )
            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }
    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // --- ¡NUEVA FUNCIÓN CENTRALIZADA Y CORREGIDA! ---
    private fun loadRoleSpecificData(user: UserEntity) {
        viewModelScope.launch {
            when (user.id_rol) {
                1L -> {
                    // Rol 1: Paciente
                    // Carga sus citas
                    loadUserAppointments(user.id)
                }
                2L -> {
                    // Rol 2: Doctor
                    // 1. Carga su perfil de DoctorInfo
                    val doctor = doctorRepo.getDoctorByEmail(user.email)
                    _currentDoctorInfo.value = doctor

                    // 2. SI lo encuentra, usa su ID (String) para cargar su agenda
                    if (doctor != null) {
                        loadDoctorAppointments(doctor.id) // <--- ¡CORREGIDO!
                    }
                }
                3L -> {
                    // Rol 3: Admin (no necesita cargar nada extra... por ahora)
                }
            }
        }
    }


    // --- 8. LÓGICA DE PERFIL (MOVIMOS TODO AQUÍ) ---

    // Carga las citas del PACIENTE
    private fun loadUserAppointments(patientId: Long) {
        viewModelScope.launch {
            appointmentRepo.getAppointmentsForUser(patientId).collect { appointments ->
                _userAppointments.value = appointments
            }
        }
    }

    // Carga las citas del DOCTOR
    private fun loadDoctorAppointments(doctorId: String) {
        viewModelScope.launch {
            appointmentRepo.getAppointmentsForDoctor(doctorId).collect { appointments ->
                _doctorAppointments.value = appointments
            }
        }
    }

    // Carga el perfil del DOCTOR
    private fun loadDoctorInfo(email: String) {
        viewModelScope.launch {
            _currentDoctorInfo.value = doctorRepo.getDoctorByEmail(email)
        }
    }

    // (Esta es la función que usa el Perfil Paciente y Doctor)
    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            appointmentRepo.deleteAppointment(appointmentId)
            // No necesitamos actualizar la UI, el .collect() lo hará automáticamente
        }
    }

    // (Estas son las funciones que usa el Perfil Doctor)
    fun saveDoctorProfile(
        newContactNumber: String,
        newAddress: String,
        newEmail: String
    ) {
        val currentDoctor = _currentDoctorInfo.value
        if (currentDoctor == null) return

        viewModelScope.launch {
            _isSavingProfile.value = true
            delay(1500) // Simular guardado

            val updatedInfo = currentDoctor.copy(
                contactNumber = newContactNumber,
                address = newAddress,
                email = newEmail
            )

            val success = doctorRepo.updateDoctor(updatedInfo)
            if (success) {
                _currentDoctorInfo.value = updatedInfo
                _saveProfileSuccess.value = true
            }
            _isSavingProfile.value = false
        }
    }

    fun clearSaveDoctorProfileStatus() {
        _saveProfileSuccess.value = false
    }

    fun updatePhotoUri(uriString: String?) {
        val currentDoctor = _currentDoctorInfo.value ?: return
        val updatedInfo = currentDoctor.copy(photoUri = uriString)
        _currentDoctorInfo.value = updatedInfo
        viewModelScope.launch {
            doctorRepo.updateDoctor(updatedInfo)
        }
    }
    fun updateUserPhoto(photoUri: String?) {
        val user = _currentUserData.value
        if (user == null) return
        val updatedUser = user.copy(photoUri = photoUri)
        _currentUserData.value = updatedUser
        viewModelScope.launch {
            repository.updateUserPhoto(user.id, photoUri)
        }
    }


    // --- FIN 8 ---
}

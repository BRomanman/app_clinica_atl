package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserEntity
// --- 1. IMPORTAR LA ENTIDAD DE CITAS ---
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.repository.UserRepository
// --- 1. IMPORTAR LA DEPENDENCIA QUE FALTA ---
import com.example.app_clinica_atl.data.repository.AppointmentRepository
// --- 1. IMPORTAR DoctorRepository Y DoctorInfo ---
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (Data classes LoginUiState y RegisterUiState se mantienen igual) ...

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


class AuthViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences,
    private val appointmentRepo: AppointmentRepository,
    // --- 2. AÑADIR DOCTORREPOSITORY AL CONSTRUCTOR ---
    private val doctorRepo: DoctorRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _userDisplayName = MutableStateFlow("Bienvenido/a a la Clínica")
    val userDisplayName: StateFlow<String> = _userDisplayName

    // --- ¡CAMBIO 1: AÑADIR ESTADO PARA EL USUARIO ACTUAL! ---
    // Este Flow "recordará" al usuario que inició sesión.
    private val _currentUserData = MutableStateFlow<UserEntity?>(null)
    val currentUserData: StateFlow<UserEntity?> = _currentUserData.asStateFlow()
    // --- FIN CAMBIO 1 ---

    // --- 2. NUEVO ESTADO PARA LAS CITAS DEL USUARIO ---
    private val _userAppointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())
    val userAppointments: StateFlow<List<AppointmentEntity>> = _userAppointments.asStateFlow()
    // --- FIN 2 ---

    // --- 3. NUEVOS ESTADOS PARA EL PERFIL DEL DOCTOR ---
    private val _currentDoctorInfo = MutableStateFlow<DoctorInfo?>(null)
    val currentDoctorInfo: StateFlow<DoctorInfo?> = _currentDoctorInfo.asStateFlow()

    private val _isSavingProfile = MutableStateFlow(false)
    val isSavingProfile: StateFlow<Boolean> = _isSavingProfile.asStateFlow()

    private val _saveProfileSuccess = MutableStateFlow(false)
    val saveProfileSuccess: StateFlow<Boolean> = _saveProfileSuccess.asStateFlow()
    // --- FIN 3 ---


    // --- 4. MODIFICAR EL INIT PARA CARGAR DOCTOR O PACIENTE ---
    init {
        viewModelScope.launch {
            repository.getLoggedInUser().collect { user ->
                _currentUserData.value = user

                // Limpiar estados anteriores
                _userAppointments.value = emptyList()
                _currentDoctorInfo.value = null

                if (user != null) {
                    // Cargar datos según el rol
                    _userDisplayName.value = "Hola, ${user.nombre} (${if(user.id_rol == 1L) "Paciente" else if(user.id_rol == 2L) "Doctor" else "Admin"})."

                    if (user.id_rol == 1L) { // Rol Paciente
                        appointmentRepo.getAppointmentsForUser(user.id).collect { appointments ->
                            _userAppointments.value = appointments
                        }
                    } else if (user.id_rol == 2L) { // Rol Doctor
                        // ¡Aquí cargamos el perfil del doctor!
                        _currentDoctorInfo.value = doctorRepo.getDoctorByEmail(user.email)
                    }

                } else {
                    // No hay usuario, limpiar todo
                    _userDisplayName.value = "Bienvenido/a a la Clínica"
                }
            }
        }
    }
    // --- FIN 4 ---


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

    // recomputeRegisterCanSubmit ahora SOLO habilita el botón,
    // la validación final se hace en submitRegister
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
                    userPreferences.setLoggedIn(true)
                    // --- 4. QUITAR LÓGICA DE AQUÍ ---
                    // (El 'init' collector se encargará de esto automáticamente)
                    // _userDisplayName.value = "Hola, ${user.nombre} (${if(user.id_rol == 1L) "Paciente" else if(user.id_rol == 2L) "Doctor" else "Admin"})."
                    // _currentUserData.value = user
                    // --- FIN 4 ---

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
        // --- 5. SIMPLIFICAR LOGOUT ---
        // (El 'init' collector se encargará de limpiar los states
        //  cuando el 'setLoggedIn' dispare el cambio en el Flow)
        userPreferences.setLoggedIn(false)
        // --- FIN 5 ---
    }

    // --- 5. AÑADIR FUNCIÓN PARA GUARDAR PERFIL DE DOCTOR ---
    fun saveDoctorProfile(
        newContactNumber: String,
        newAddress: String,
        newEmail: String
    ) {
        if (_isSavingProfile.value) return
        val currentDoctor = _currentDoctorInfo.value ?: return

        viewModelScope.launch {
            _isSavingProfile.value = true
            _saveProfileSuccess.value = false

            // Simulación de guardado (el repo es mockeado)
            delay(1500)

            val updatedDoctor = currentDoctor.copy(
                contactNumber = newContactNumber,
                address = newAddress,
                email = newEmail
            )

            // Aquí llamarías a repository.updateDoctor(updatedDoctor)
            // Como nuestro repo es mockeado, solo actualizamos el estado local
            _currentDoctorInfo.value = updatedDoctor

            _isSavingProfile.value = false
            _saveProfileSuccess.value = true
        }
    }

    // Función para limpiar el estado de "Guardado!"
    fun clearSaveDoctorProfileStatus() {
        _saveProfileSuccess.value = false
    }
    // --- FIN 5 ---


    // --- 6. FUNCIÓN DE CANCELAR CITA (ya la teníamos) ---
    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            appointmentRepo.deleteAppointment(appointmentId)
            // La lista se actualizará sola gracias al Flow del 'init'
        }
    }
    // --- FIN 6 ---

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

    @RequiresApi(Build.VERSION_CODES.O)
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

    // --- ¡¡¡ESTA ES LA FUNCIÓN CORREGIDA!!! ---
    fun submitRegister() {
        // Ya no confiamos en 'canSubmit'. Volvemos a validar todo AHORA.
        val s = _register.value

        // 1. Validamos todos los campos uno por uno
        val nombreError = validateNamePart(s.nombre.trim(), "El nombre")
        val apellidoError = validateNamePart(s.apellido.trim(), "El apellido")
        val fechaError = if (s.fecha_nacimiento.length < 10) "Formato debe ser DD-MM-YYYY"
        else validateFechaNacimiento(s.fecha_nacimiento) ?: validateEdadMinima(s.fecha_nacimiento, 18)
        val emailError = validateEmail(s.email.trim())
        val phoneError = validatePhoneDigitsOnly(s.phone.trim())
        val passError = validateSimplePassword(s.pass)
        val confirmError = validateConfirm(s.pass, s.confirm)

        // 2. Creamos una lista de todos los errores
        val errors = listOf(
            nombreError, apellidoError, fechaError, emailError,
            phoneError, passError, confirmError
        )
        val hasError = errors.any { it != null }

        // 3. Si hay CUALQUIER error, actualizamos la UI con todos los errores y detenemos.
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
            return // <-- DETENEMOS EL REGISTRO
        }

        // 4. Si llegamos aquí, NO hay errores. Procedemos a registrar.
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
    // --- FIN DE LA FUNCIÓN CORREGIDA ---

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}
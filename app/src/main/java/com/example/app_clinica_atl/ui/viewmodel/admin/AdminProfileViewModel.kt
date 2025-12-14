package com.example.app_clinica_atl.ui.viewmodel.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.AdministradorUpdateRequestDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import com.example.app_clinica_atl.util.copyUriToTempFile
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isChangingPassword: Boolean = false,
    val isPasswordUpdating: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val passwordChangeError: String? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val updateSuccess: Boolean = false,
    val errorMsg: String? = null
)

class AdminProfileViewModel(
    private val repository: AdminRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    private val _profilePhotoUrl = MutableStateFlow<String?>(null)
    val profilePhotoUrl: StateFlow<String?> = _profilePhotoUrl.asStateFlow()
    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto.asStateFlow()
    private val _photoErrorMessage = MutableStateFlow<String?>(null)
    val photoErrorMessage: StateFlow<String?> = _photoErrorMessage.asStateFlow()
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private var currentUserId: Long? = null

    private fun refreshPhotoUrl(userId: Long?) {
        _profilePhotoUrl.value = userId
            ?.let { repository.buildAdminProfilePhotoUrl(it) }
            ?.let { appendTimestamp(it) }
    }

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (!userPreferences.isAdmin()) {
                    _uiState.update { it.copy(isLoading = false, errorMsg = "Sesión inválida para admin") }
                    return@launch
                }
                _authToken.value = userPreferences.currentToken()
                val id = userPreferences.adminIdFlow.first()
                if (id == null) {
                    _uiState.update { it.copy(isLoading = false, errorMsg = "No se pudo determinar el administrador") }
                    return@launch
                }
                currentUserId = id
                refreshPhotoUrl(id)
                val result = repository.getAdminProfile(id)

                if (result.isSuccess) {
                    val admin = result.getOrNull()!!
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombre = admin.nombre.orEmpty(),
                        apellido = admin.apellido.orEmpty(),
                        email = admin.correo.orEmpty(),
                        telefono = admin.telefono.orEmpty()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = result.exceptionOrNull()?.message) }
            }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMsg = e.message) }
            }
        }
    }

    fun onNewProfilePhotoSelected(uri: Uri, context: Context) {
        val adminId = currentUserId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isUploadingPhoto.value = true
            _photoErrorMessage.value = null
            _uiState.update { it.copy(isLoading = true, updateSuccess = false, errorMsg = null) }
            try {
                val tempFile = copyUriToTempFile(uri, context)
                val result = repository.uploadAdminProfilePhoto(adminId, tempFile)
                val baseUrl = repository.buildAdminProfilePhotoUrl(adminId)
                if (result.isSuccess) {
                    _profilePhotoUrl.value = appendTimestamp(baseUrl)
                    _uiState.update { it.copy(isLoading = false, updateSuccess = true, errorMsg = null) }
                } else {
                    val message = result.exceptionOrNull()?.message ?: "No se pudo actualizar la foto."
                    _photoErrorMessage.value = message
                    _uiState.update { it.copy(isLoading = false, updateSuccess = false, errorMsg = message) }
                }
            } catch (e: Exception) {
                val message = e.message ?: "No se pudo procesar la imagen."
                _photoErrorMessage.value = message
                _uiState.update { it.copy(isLoading = false, updateSuccess = false, errorMsg = message) }
            } finally {
                _isUploadingPhoto.value = false
            }
        }
    }

    private fun appendTimestamp(url: String): String {
        val cleanUrl = url.substringBefore("?")
        return "$cleanUrl?ts=${System.currentTimeMillis()}"
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v) }
    fun onTelefonoChange(v: String) = _uiState.update { it.copy(telefono = v) }

    fun onCurrentPasswordChange(v: String) {
        _uiState.update {
            it.copy(
                currentPassword = v,
                currentPasswordError = null,
                passwordChangeError = null,
                passwordChangeSuccess = false
            )
        }
    }

    fun onNewPasswordChange(v: String) {
        _uiState.update { state ->
            val errors = mutableListOf<String>()
            if (v.length < 8) errors += "Debe tener al menos 8 caracteres."
            if (!v.any { it.isUpperCase() }) errors += "Debe tener al menos una letra mayúscula."
            if (v.count { it.isDigit() } < 2) errors += "Debe tener al menos dos números."
            val confirmMismatch = state.confirmPassword.isNotEmpty() && state.confirmPassword != v
            state.copy(
                newPassword = v,
                newPasswordError = errors.takeIf { it.isNotEmpty() }?.joinToString(" "),
                confirmPasswordError = if (confirmMismatch) "Las contraseñas no coinciden." else null,
                passwordChangeError = null,
                passwordChangeSuccess = false
            )
        }
    }

    fun onConfirmPasswordChange(v: String) {
        _uiState.update { state ->
            val mismatch = v.isNotEmpty() && state.newPassword != v
            state.copy(
                confirmPassword = v,
                confirmPasswordError = if (mismatch) "Las contraseñas no coinciden." else null,
                passwordChangeError = null,
                passwordChangeSuccess = false
            )
        }
    }

    fun toggleChangePassword() {
        _uiState.update { state ->
            if (!state.isChangingPassword) {
                state.copy(
                    isChangingPassword = true,
                    currentPassword = "",
                    newPassword = "",
                    confirmPassword = "",
                    currentPasswordError = null,
                    newPasswordError = null,
                    confirmPasswordError = null,
                    isPasswordUpdating = false,
                    passwordChangeError = null,
                    passwordChangeSuccess = false
                )
            } else {
                state.copy(
                    isChangingPassword = false,
                    currentPassword = "",
                    newPassword = "",
                    confirmPassword = "",
                    currentPasswordError = null,
                    newPasswordError = null,
                    confirmPasswordError = null,
                    passwordChangeError = null,
                    isPasswordUpdating = false
                )
            }
        }
    }

    fun changePassword() {
        val adminId = currentUserId
        if (adminId == null) {
            _uiState.update { it.copy(passwordChangeError = "No se pudo determinar el administrador.") }
            return
        }
        val state = _uiState.value
        val currentPasswordError = state.currentPassword.takeIf { it.isBlank() }
            ?.let { "Debes ingresar tu contraseña actual." }
        val newPasswordError = state.newPasswordError
            ?: state.newPassword.takeIf { it.isBlank() }?.let { "Debes ingresar una nueva contraseña." }
        val confirmPasswordError = state.confirmPasswordError
            ?: when {
                state.confirmPassword.isBlank() -> "Debes confirmar la nueva contraseña."
                state.newPassword != state.confirmPassword -> "Las contraseñas no coinciden."
                else -> null
            }

        if (listOf(currentPasswordError, newPasswordError, confirmPasswordError).any { it != null }) {
            _uiState.update {
                it.copy(
                    currentPasswordError = currentPasswordError,
                    newPasswordError = newPasswordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        _uiState.update {
            it.copy(isPasswordUpdating = true, passwordChangeError = null, passwordChangeSuccess = false)
        }

        viewModelScope.launch {
            val result = try {
                repository.changeAdminPassword(
                    adminId,
                    state.currentPassword,
                    state.newPassword
                )
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isPasswordUpdating = false,
                        passwordChangeError = "Tiempo de espera agotado. Verifica tu conexión e inténtalo de nuevo.",
                        passwordChangeSuccess = false
                    )
                }
                return@launch
            }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        currentPasswordError = null,
                        newPasswordError = null,
                        confirmPasswordError = null,
                        isPasswordUpdating = false,
                        isChangingPassword = false,
                        passwordChangeSuccess = true,
                        passwordChangeError = null
                    )
                }
            } else {
                val message = result.exceptionOrNull()?.message
                if (message == "WRONG_CURRENT_PASSWORD") {
                    _uiState.update {
                        it.copy(
                            isPasswordUpdating = false,
                            currentPasswordError = "Contraseña actual incorrecta.",
                            passwordChangeSuccess = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isPasswordUpdating = false,
                            passwordChangeError = message ?: "No se pudo actualizar la contraseña.",
                            passwordChangeSuccess = false
                        )
                    }
                }
            }
        }
    }
    fun toggleEdit() { _uiState.update { it.copy(isEditing = !it.isEditing) } }

    fun updateProfile() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, updateSuccess = false) }
            val dto = AdministradorUpdateRequestDto(
                nombre = _uiState.value.nombre,
                apellido = _uiState.value.apellido,
                correo = _uiState.value.email,
                telefono = _uiState.value.telefono
            )
            val result = repository.updateAdminProfile(uid, dto)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, updateSuccess = true, isEditing = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMsg() {
        _photoErrorMessage.value = null
        _uiState.update {
            it.copy(
                updateSuccess = false,
                errorMsg = null,
                passwordChangeSuccess = false,
                passwordChangeError = null
            )
        }
    }
}

class AdminProfileViewModelFactory(
    private val repo: AdminRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminProfileViewModel(repo, prefs) as T
}

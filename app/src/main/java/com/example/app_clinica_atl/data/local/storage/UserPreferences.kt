package com.example.app_clinica_atl.data.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.app_clinica_atl.data.remote.dto.normalizeRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * Clase para gestionar la sesiÇün del usuario (ID, Token, Rol y datos básicos) y preferencias (Tema).
 */
class UserPreferences(context: Context) {

    private val dataStore = context.dataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tokenCache = AtomicReference<String?>(null)

    private object PreferencesKeys {
        val USER_ID = longPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_DOCTOR_ID = longPreferencesKey("user_doctor_id")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val USER_NOMBRE = stringPreferencesKey("user_nombre")
        val USER_APELLIDO = stringPreferencesKey("user_apellido")
        val USER_EMAIL = stringPreferencesKey("user_correo")
        // --- ¶­¶­LLAVE AÇ'ADIDA!! ---
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
    }

    init {
        scope.launch {
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) emit(emptyPreferences()) else throw exception
                }.collect { preferences ->
                    tokenCache.set(preferences[PreferencesKeys.USER_TOKEN])
                }
        }
    }

    private fun profileImageKey(userId: Long) = stringPreferencesKey("profile_image_url_$userId")

    // --- Flujos de SesiÇün ---
    val userIdFlow: Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    val userRoleFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_ROLE]
        }

    val normalizedUserRoleFlow: Flow<String> = userRoleFlow.map { normalizeRole(it) }

    val isAdminFlow: Flow<Boolean> = normalizedUserRoleFlow.map { it == "administrador" }

    /**
     * Id que representa al admin autenticado (corresponde a Administradores.id_admin cuando es admin).
     */
    val adminIdFlow: Flow<Long?> = userIdFlow

    val isDoctorFlow: Flow<Boolean> = normalizedUserRoleFlow.map { it == "doctor" }

    val isPacienteFlow: Flow<Boolean> = normalizedUserRoleFlow.map { it == "paciente" }

    val userDoctorIdFlow: Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_DOCTOR_ID]
        }

    val tokenFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_TOKEN]
        }

    val userNombreFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_NOMBRE]
        }

    val userApellidoFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_APELLIDO]
        }

    val userEmailFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL]
        }

    // --- ¶­¶­FLUJO AÇ'ADIDO PARA EL TEMA!! ---
    /**
     * Devuelve la preferencia de tema guardada (ej: "LIGHT", "DARK", "SYSTEM").
     * Devuelve "SYSTEM" como valor por defecto.
     */
    val themeFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] ?: "SYSTEM"
        }

    // --- Funciones de guardado ---

    suspend fun saveUserSession(
        id: Long,
        role: String,
        doctorId: Long? = null,
        nombre: String = "",
        apellido: String = "",
        correo: String = "",
        token: String? = null
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
            preferences[PreferencesKeys.USER_ROLE] = normalizeRole(role)
            if (doctorId != null) {
                preferences[PreferencesKeys.USER_DOCTOR_ID] = doctorId
            } else {
                preferences.remove(PreferencesKeys.USER_DOCTOR_ID)
            }
            preferences[PreferencesKeys.USER_NOMBRE] = nombre
            preferences[PreferencesKeys.USER_APELLIDO] = apellido
            preferences[PreferencesKeys.USER_EMAIL] = correo
            if (token != null) {
                preferences[PreferencesKeys.USER_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.USER_TOKEN)
            }
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_ROLE)
            preferences.remove(PreferencesKeys.USER_DOCTOR_ID)
            preferences.remove(PreferencesKeys.USER_NOMBRE)
            preferences.remove(PreferencesKeys.USER_APELLIDO)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_TOKEN)
        }
    }

    fun profileImageFlow(userId: Long): Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) { emit(emptyPreferences()) } else { throw exception }
        }.map { preferences ->
            preferences[profileImageKey(userId)]
        }

    suspend fun saveProfileImage(userId: Long, uri: String) {
        dataStore.edit { preferences ->
            preferences[profileImageKey(userId)] = uri
        }
    }

    // --- ¶­¶­FUNCIÇ"N AÇ'ADIDA PARA EL TEMA!! ---
    /**
     * Guarda la nueva preferencia de tema del usuario.
     */
    suspend fun saveThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme
        }
    }

    suspend fun isAdmin(): Boolean = isAdminFlow.firstOrNull() == true

    suspend fun isDoctor(): Boolean = isDoctorFlow.firstOrNull() == true

    suspend fun isPaciente(): Boolean = isPacienteFlow.firstOrNull() == true

    suspend fun currentAdminId(): Long? = adminIdFlow.firstOrNull()

    suspend fun normalizedUserRole(): String = normalizedUserRoleFlow.firstOrNull() ?: "paciente"

    fun currentToken(): String? = tokenCache.get()
}

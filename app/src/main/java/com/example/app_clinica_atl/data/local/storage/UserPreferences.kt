package com.example.app_clinica_atl.data.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * Clase para gestionar la sesión del usuario (ID y Rol) y preferencias (Tema).
 */
class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val USER_ID = longPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
        // --- ¡¡LLAVE AÑADIDA!! ---
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
    }

    private fun profileImageKey(userId: Long) = stringPreferencesKey("profile_image_url_$userId")

    // --- Flujos de Sesión (sin cambios) ---
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

    // --- ¡¡FLUJO AÑADIDO PARA EL TEMA!! ---
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

    suspend fun saveUserSession(id: Long, role: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_ROLE)
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

    // --- ¡¡FUNCIÓN AÑADIDA PARA EL TEMA!! ---
    /**
     * Guarda la nueva preferencia de tema del usuario.
     */
    suspend fun saveThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme
        }
    }
}

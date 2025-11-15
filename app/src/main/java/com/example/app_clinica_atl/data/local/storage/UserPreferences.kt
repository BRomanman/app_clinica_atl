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

// Nombre del archivo de preferencias
private const val USER_PREFERENCES_NAME = "user_preferences"

// Creamos la instancia de DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * Clase para gestionar la sesión del usuario (ID y Rol) en DataStore.
 */
class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    // Definición de las "llaves" para guardar los datos
    private object PreferencesKeys {
        val USER_ID = longPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    /**
     * Un Flow público que emite el ID del usuario cada vez que cambia.
     * Esto reemplaza a 'getUserId()'.
     * El ViewModel (y el error de tu imagen) depende de ESTE flow.
     */
    val userIdFlow: Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    /**
     * Un Flow público que emite el Rol del usuario ("paciente", "doctor", "admin")
     */
    val userRoleFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.USER_ROLE]
        }

    /**
     * Guarda el ID y el Rol del usuario al iniciar sesión.
     * Reemplaza a 'saveUserId()'.
     */
    suspend fun saveUserSession(id: Long, role: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    /**
     * Limpia la sesión al cerrar sesión.
     */
    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
package com.example.app_clinica_atl.data.local.storage // Asegúrate que sea este

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
// --- 1. IMPORTAR STRING PREFERENCES KEY ---
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el DataStore asociado al contexto de la app
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences (private val context: Context){
    // Define una "llave" para saber si el usuario está logueado
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    // --- 2. NUEVA LLAVE PARA GUARDAR EL EMAIL ---
    private val loggedInUserEmailKey = stringPreferencesKey("logged_in_user_email")

    // --- 3. NUEVAS FUNCIONES PARA GUARDAR/LIMPIAR EL EMAIL ---
    suspend fun setLoggedInUserEmail(email: String?) {
        context.dataStore.edit { prefs ->
            if (email == null) {
                prefs.remove(loggedInUserEmailKey)
            } else {
                prefs[loggedInUserEmailKey] = email
            }
        }
    }

    // --- 4. NUEVO FLOW PARA LEER EL EMAIL ---
    val loggedInUserEmail: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[loggedInUserEmailKey] }
    // ---

    // Función para GUARDAR el estado de login (true/false)
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
            // Si cerramos sesión (value = false), también borramos el email
            if (!value) {
                prefs.remove(loggedInUserEmailKey)
            }
        }
    }

    // Flow para LEER el estado de login (devuelve false si no hay nada guardado)
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isLoggedInKey] ?: false }
}
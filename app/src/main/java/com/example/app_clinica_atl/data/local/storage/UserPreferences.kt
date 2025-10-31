package com.example.app_clinica_atl.data.local.storage // Asegúrate que sea este

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el DataStore asociado al contexto de la app
val Context.dataStore by preferencesDataStore(name = "user_prefs")






//LOGIN
class UserPreferences (private val context: Context){
    // Define una "llave" para saber si el usuario está logueado
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in") // Corregido: nombre de variable

    // Función para GUARDAR el estado de login (true/false)
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    // Flow para LEER el estado de login (devuelve false si no hay nada guardado)
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isLoggedInKey] ?: false }
}
package com.example.app_clinica_atl

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.data.local.database.AppDatabase
// Importaciones de Repositorio
import com.example.app_clinica_atl.data.repository.PatientRepository // <-- Importación para Pacientes
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.navigation.AppNavGraph
// Importa tu Tema
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
// Importaciones de ViewModels y Factories
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot() // Llama a AppRoot una sola vez
        }
    }
}

/*
* ... (Tu comentario sobre Surface)
*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRoot() { // Se define AppRoot UNA SOLA VEZ

    // --- 1. Construcción de Dependencias ---
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()

    // Repositorio de Autenticación
    val userRepository = UserRepository(userDao)

    // Repositorio de Pacientes
    val patientRepository = PatientRepository()

    // ViewModel de Autenticación
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    // ViewModel de Pacientes
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(patientRepository)
    )

    // Controlador de Navegación (se define una sola vez)
    val navController = rememberNavController()

    // --- 2. Aplicar el Tema Correcto (se llama una sola vez) ---
    AppClinicaATLTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,     // Pasa el VM de Auth
                patientViewModel = patientViewModel  // Pasa el VM de Pacientes
            )
        }
    }
    // (Se eliminó el bloque MaterialTheme y la llamada duplicada a AppNavGraph)
}
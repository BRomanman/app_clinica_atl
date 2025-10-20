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
import com.example.app_clinica_atl.data.repository.PatientRepository // <-- NUEVO
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.navigation.AppNavGraph
<<<<<<< HEAD
=======
// Importa tu Tema
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
// Importaciones de ViewModels y Factories
>>>>>>> 48b8491 (agregado de buscador para doctores/admin para buscar pacientes)
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel     // <-- NUEVO
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory // <-- NUEVO

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

<<<<<<< HEAD

/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRoot() {

    // ====== NUEVO: construcción de dependencias (Composition Root) ======
=======
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRoot() {
    // --- 1. Construcción de Dependencias ---
>>>>>>> 48b8491 (agregado de buscador para doctores/admin para buscar pacientes)
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()

    // Repositorio de Autenticación (como en 2.0)
    val userRepository = UserRepository(userDao)

<<<<<<< HEAD


=======
    // Repositorio de Pacientes (NUEVO)
    val patientRepository = PatientRepository()

    // ViewModel de Autenticación (como en 2.0)
>>>>>>> 48b8491 (agregado de buscador para doctores/admin para buscar pacientes)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

<<<<<<< HEAD

    val navController = rememberNavController() // Controlador de navegación (igual que antes)
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )

=======
    // ViewModel de Pacientes (NUEVO, con su propia factory)
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(patientRepository)
    )

    val navController = rememberNavController()

    // --- 2. Aplicar el Tema Correcto ---
    AppClinicaATLTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,     // Pasa el VM de Auth
                patientViewModel = patientViewModel  // <-- Pasa el VM de Pacientes
            )
>>>>>>> 48b8491 (agregado de buscador para doctores/admin para buscar pacientes)
        }
    }
}
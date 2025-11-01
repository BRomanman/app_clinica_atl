package com.example.app_clinica_atl

import android.os.Build // Necesario para RequiresApi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi // Necesario para la anotación
import androidx.compose.material3.MaterialTheme // Necesario para Surface
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember // Para recordar UserPreferences
import androidx.compose.ui.platform.LocalContext // Necesario para obtener el contexto
import androidx.lifecycle.viewmodel.compose.viewModel // Necesario para crear ViewModels
import androidx.navigation.compose.rememberNavController // Necesario para la navegación
import com.example.app_clinica_atl.data.local.database.AppDatabase // Base de datos Room
// Importaciones de Storage (DataStore)
import com.example.app_clinica_atl.data.local.storage.UserPreferences // Importa UserPreferences
// Importaciones de Repositorios
import com.example.app_clinica_atl.data.repository.AppointmentRepository // Repositorio de Citas
import com.example.app_clinica_atl.data.repository.PatientRepository // Repositorio de Pacientes
import com.example.app_clinica_atl.data.repository.UserRepository // Repositorio de Usuarios
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.navigation.AppNavGraph // El grafo de navegación
import com.example.app_clinica_atl.notifications.NotificationHelper
// Importa tu Tema
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
// Importaciones de ViewModels y Factories
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModelFactory
// *** NUEVOS IMPORTS ***
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(applicationContext)
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

    // --- 1. Construcción de Dependencias (Composition Root) ---
    val context = LocalContext.current.applicationContext // Contexto para Room y DataStore
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()

    // Repositorios
    val userRepository = UserRepository(userDao)
    val patientRepository = PatientRepository()
    val doctorRepository = DoctorRepository()
    val appointmentRepository = AppointmentRepository(doctorRepository)

    // --- Instancia de UserPreferences (DataStore) ---
    val userPreferences = remember { UserPreferences(context) }

    // ViewModels
    // *** 1. AUTHVIEWMODEL (Inyección de UserRepository y UserPreferences) ***
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository, userPreferences)
    )

    val patientViewModel: PatientViewModel = viewModel(factory = PatientViewModelFactory(patientRepository))
    val doctorSearchViewModel: DoctorSearchViewModel = viewModel(factory = DoctorSearchViewModelFactory(doctorRepository))
    val bookAppointmentViewModel: BookAppointmentViewModel = viewModel(
        factory = BookAppointmentViewModelFactory(appointmentRepository)
    )
    // *** 2. DOCTORPROFILEVIEWMODEL (Inyección de DoctorRepository) ***
    val doctorProfileViewModel: DoctorProfileViewModel = viewModel(
        factory = DoctorProfileViewModelFactory(doctorRepository)
    )

    // Controlador de Navegación
    val navController = rememberNavController()

    // --- 2. Aplicar el Tema Correcto y Configurar la Navegación ---
    AppClinicaATLTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                patientViewModel = patientViewModel,
                bookAppointmentViewModel = bookAppointmentViewModel,
                doctorSearchViewModel = doctorSearchViewModel,
                doctorProfileViewModel = doctorProfileViewModel // <-- Pasar el VM del perfil
            )
        }
    }
}
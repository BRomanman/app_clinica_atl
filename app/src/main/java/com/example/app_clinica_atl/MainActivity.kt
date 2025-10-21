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
import androidx.compose.ui.platform.LocalContext // Necesario para obtener el contexto
import androidx.lifecycle.viewmodel.compose.viewModel // Necesario para crear ViewModels
import androidx.navigation.compose.rememberNavController // Necesario para la navegación
import com.example.app_clinica_atl.data.local.database.AppDatabase // Base de datos Room
// Importaciones de Repositorios
import com.example.app_clinica_atl.data.repository.AppointmentRepository // Repositorio de Citas
import com.example.app_clinica_atl.data.repository.PatientRepository // Repositorio de Pacientes
import com.example.app_clinica_atl.data.repository.UserRepository // Repositorio de Usuarios
import com.example.app_clinica_atl.navigation.AppNavGraph // El grafo de navegación
// Importa tu Tema
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
// Importaciones de ViewModels y Factories
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory

class MainActivity : ComponentActivity() {
    // La anotación @RequiresApi indica que esta Activity (y lo que llama)
    // requiere Android Oreo (API 26) o superior debido a BookAppointmentScreen
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo borde a borde (opcional)
        setContent {
            // Llama a la función Composable raíz
            AppRoot()
        }
    }
}

// La anotación se propaga a la función raíz Composable
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRoot() {

    // --- 1. Construcción de Dependencias (Composition Root) ---
    // Se crean aquí para que haya una única instancia compartida en la app
    val context = LocalContext.current.applicationContext // Contexto para Room
    val db = AppDatabase.getInstance(context) // Instancia única de la BD
    val userDao = db.userDao() // DAO para usuarios

    // Repositorios: Encapsulan el acceso a datos
    val userRepository = UserRepository(userDao)
    val patientRepository = PatientRepository() // Repositorio con datos fijos de pacientes
    val appointmentRepository = AppointmentRepository() // Repositorio con datos fijos de doctores/citas

    // ViewModels: Contienen la lógica de negocio y el estado de la UI
    // Se crean usando `viewModel()` y sus respectivas Factories para inyectar los repositorios
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository))
    val patientViewModel: PatientViewModel = viewModel(factory = PatientViewModelFactory(patientRepository))
    val bookAppointmentViewModel: BookAppointmentViewModel = viewModel(
        factory = BookAppointmentViewModelFactory(appointmentRepository)
    )

    // Controlador de Navegación: Gestiona las pantallas
    val navController = rememberNavController()

    // --- 2. Aplicar el Tema Personalizado y Configurar la Navegación ---
    AppClinicaATLTheme { // Envuelve toda la UI con tu tema (colores, tipografía)
        // Surface actúa como el lienzo principal con el color de fondo del tema
        Surface(color = MaterialTheme.colorScheme.background) {
            // AppNavGraph define todas las pantallas y cómo navegar entre ellas
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,          // Pasa el ViewModel de Autenticación
                patientViewModel = patientViewModel,      // Pasa el ViewModel de Búsqueda de Pacientes
                bookAppointmentViewModel = bookAppointmentViewModel // Pasa el ViewModel de Reserva de Hora
            )
        }
    }
}
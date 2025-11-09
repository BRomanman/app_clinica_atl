package com.example.app_clinica_atl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.app_clinica_atl.data.local.database.AppDatabase
// --- 1. IMPORTAR LA NUEVA MIGRACIÓN ---
import com.example.app_clinica_atl.data.local.database.MIGRATION_2_3
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.PatientRepository
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.navigation.AppNavGraph
import com.example.app_clinica_atl.notifications.NotificationHelper
import com.example.app_clinica_atl.ui.theme.AppClinicaATLTheme
import com.example.app_clinica_atl.ui.viewmodel.AdminManageDoctorViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel

class MainActivity : ComponentActivity() {

    // --- 2. INICIALIZAR LA BASE DE DATOS (AÑADIENDO LA MIGRACIÓN) ---
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "clinica_atl_v3.db" // Usamos el nuevo nombre de la DB
        )
            .addMigrations(MIGRATION_2_3) // <-- AÑADIDO AQUÍ
            .fallbackToDestructiveMigration() // Como plan B
            .build()
    }
    // ---

    private val userPreferences by lazy { UserPreferences(applicationContext) }

    // --- 3. CREAR LOS REPOSITORIOS (AÑADIENDO EL DE CITAS) ---
    // (Ahora UserRepository SÍ recibe el UserPreferences que necesita)
    private val userRepo by lazy { UserRepository(db.userDao(), userPreferences) }
    private val patientRepo by lazy { PatientRepository() } // Sigue mockeado
    // (Inyectamos el nuevo AppointmentDao)
    private val appointmentRepo by lazy { AppointmentRepository(db.appointmentDao()) } // <-- MODIFICADO
    private val doctorRepo by lazy { DoctorRepository() } // Sigue mockeado
    private val doctorProfileRepo by lazy { DoctorRepository() } // Sigue mockeado
    private val adminDoctorRepo by lazy { DoctorRepository() } // Sigue mockeado
    // ---


    // --- 4. CREAR LAS "FÁBRICAS" DE VIEWMODELS (ACTUALIZARLAS) ---
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // (Ahora AuthViewModel también necesita el repo de citas)
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(userRepo, userPreferences, appointmentRepo) as T // <-- MODIFICADO
            }
        }
    }
    private val patientViewModel: PatientViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PatientViewModel(patientRepo) as T
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // (Ahora BookAppointmentViewModel también necesita el repo de usuario)
                @Suppress("UNCHECKED_CAST")
                return BookAppointmentViewModel(appointmentRepo, userRepo) as T // <-- MODIFICADO
            }
        }
    }
    private val doctorSearchViewModel: DoctorSearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DoctorSearchViewModel(doctorRepo) as T
            }
        }
    }
    private val doctorProfileViewModel: DoctorProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DoctorProfileViewModel(doctorProfileRepo) as T
            }
        }
    }
    private val adminManageDoctorViewModel: AdminManageDoctorViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AdminManageDoctorViewModel(adminDoctorRepo) as T
            }
        }
    }
    // --- FIN 4 ---


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // (Crear canales de notificación se mantiene igual)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannel(this)
        }

        setContent {
            val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
            val navController = rememberNavController()

            AppClinicaATLTheme {
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    patientViewModel = patientViewModel,
                    bookAppointmentViewModel = bookAppointmentViewModel,
                    doctorSearchViewModel = doctorSearchViewModel,
                    doctorProfileViewModel = doctorProfileViewModel,
                    adminManageDoctorViewModel = adminManageDoctorViewModel
                )
            }
        }
    }
}
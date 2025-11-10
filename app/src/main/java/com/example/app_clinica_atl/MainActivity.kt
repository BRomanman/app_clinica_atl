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
// --- 1. IMPORTAR LO NECESARIO PARA EL CALLBACK ---
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.database.AppDatabase
import com.example.app_clinica_atl.data.local.database.MIGRATION_2_3
import com.example.app_clinica_atl.data.local.database.MIGRATION_3_4
import com.example.app_clinica_atl.data.local.database.MIGRATION_4_5
import com.example.app_clinica_atl.data.local.database.MIGRATION_5_6
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

class MainActivity : ComponentActivity() {

    // --- 3. MODIFICAR LA CREACIÓN DE LA BASE DE DATOS ---
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "clinica_atl_v6.db" // Usamos el nuevo nombre de la DB (v6)
        )
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Añadida 5_6
            // --- ¡AÑADIR ESTE BLOQUE COMPLETO! ---
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Usamos raw SQL para insertar los usuarios semilla
                    // Esto se ejecuta SÓLO la primera vez que la BD es creada

                    // --- ¡CORRECCIÓN CRÍTICA! ---
                    // Ahora especificamos las 9 columnas para que coincida con la v6
                    val columns = "(nombre, apellido, fecha_nacimiento, email, phone, password, id_rol, photoUri, isLoggedIn)"

                    // Rol 3: Administrador
                    db.execSQL("INSERT OR REPLACE INTO users $columns VALUES ('Admin', 'Clinica', '01-01-1990', 'admin@duoc.cl', '12345678', 'Admin123!', 3, NULL, 0)")

                    // Rol 2: Doctor (Tus datos)
                    db.execSQL("INSERT OR REPLACE INTO users $columns VALUES ('Víctor', 'Rosendo', '10-05-2000', 'victor@duoc.cl', '56922222222', '123456', 2, NULL, 0)")

                    // Rol 1: Paciente (Tus datos)
                    db.execSQL("INSERT OR REPLACE INTO users $columns VALUES ('Carlos', 'Sainz', '01-09-1994', 'csainz@duoc.cl', '56933333333', '123456', 1, NULL, 0)")
                }
            })
            // --- FIN DEL BLOQUE AÑADIDO ---
            .fallbackToDestructiveMigration() // Como plan B
            .build()
    }
    // --- FIN 3 ---

    private val userPreferences by lazy { UserPreferences(applicationContext) }

    // --- 4. REPOSITORIOS (ACTUALIZADOS) ---
    private val userRepo by lazy { UserRepository(db.userDao(), userPreferences) }
    private val patientRepo by lazy { PatientRepository() }
    private val doctorRepo by lazy { DoctorRepository() } // Sigue mockeado
    private val adminDoctorRepo by lazy { DoctorRepository() } // Sigue mockeado
    // (Actualizado para pasar doctorRepo)
    private val appointmentRepo by lazy { AppointmentRepository(db.appointmentDao(), doctorRepo) }
    // --- FIN 4 ---


    // --- 5. FÁBRICAS DE VIEWMODELS (ACTUALIZADAS) ---
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(
                    userRepo,
                    userPreferences,
                    appointmentRepo,
                    doctorRepo // <-- La 4ta dependencia que añadimos
                ) as T
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

    @get:RequiresApi(Build.VERSION_CODES.O)
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                // (Actualizado para pasar doctorRepo)
                return BookAppointmentViewModel(appointmentRepo, userRepo, doctorRepo) as T
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

    private val adminManageDoctorViewModel: AdminManageDoctorViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AdminManageDoctorViewModel(adminDoctorRepo) as T
            }
        }
    }
    // --- FIN 5 ---


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
                    adminManageDoctorViewModel = adminManageDoctorViewModel
                )
            }
        }
    }
}
package com.example.app_clinica_atl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.data.local.database.AppDatabase
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.AppointmentRepositoryImpl
import com.example.app_clinica_atl.data.repository.DoctorRepository
import com.example.app_clinica_atl.data.repository.DoctorRepositoryImpl
import com.example.app_clinica_atl.data.repository.InsuranceRepository
import com.example.app_clinica_atl.data.repository.InsuranceRepositoryImpl
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.SpecialtyRepositoryImpl
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.navigation.AppNavGraph
import com.example.app_clinica_atl.ui.theme.App_clinica_atlTheme
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModel
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.AdminManageSpecialtiesViewModel
import com.example.app_clinica_atl.ui.viewmodel.AdminManageSpecialtiesViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModelFactory
// --- ¡¡IMPORTS AÑADIDOS!! ---
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModelFactory
// --- FIN IMPORTS ---
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModel
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory

class MainActivity : ComponentActivity() {

    // --- Instancias de Dependencias ---
    private val database by lazy { AppDatabase.getInstance(this) }
    private val userPreferences by lazy { UserPreferences(this) }

    private val userRepository by lazy { UserRepository(database.userDao()) }
    private val doctorRepository: DoctorRepository by lazy {
        DoctorRepositoryImpl(database.userDao())
    }
    private val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl(database.appointmentDao())
    }
    private val specialtyRepository: SpecialtyRepository by lazy {
        SpecialtyRepositoryImpl(database.specialtyDao())
    }
    private val insuranceRepository: InsuranceRepository by lazy {
        InsuranceRepositoryImpl(database.insuranceDao())
    }

    // --- ViewModels ---
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(userRepository, userPreferences)
    }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(userRepository, userPreferences)
    }
    private val patientViewModel: PatientViewModel by viewModels {
        PatientViewModelFactory(userRepository, userPreferences, insuranceRepository)
    }
    private val doctorSearchViewModel: DoctorSearchViewModel by viewModels {
        DoctorSearchViewModelFactory(doctorRepository)
    }
    private val doctorProfileViewModel: DoctorProfileViewModel by viewModels {
        DoctorProfileViewModelFactory(doctorRepository)
    }
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels {
        BookAppointmentViewModelFactory(
            doctorRepository = doctorRepository,
            appointmentRepository = appointmentRepository,
            userPreferences = userPreferences
        )
    }
    private val insuranceViewModel: InsuranceViewModel by viewModels {
        InsuranceViewModelFactory(insuranceRepository, userPreferences)
    }

    // --- ¡¡NUEVO VIEWMODEL DE DOCTOR AÑADIDO!! ---
    private val doctorSearchPatientViewModel: DoctorSearchPatientViewModel by viewModels {
        DoctorSearchPatientViewModelFactory(userRepository)
    }
    // --- FIN ---

    // --- ViewModels de Admin ---
    private val adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel by viewModels {
        AdminManageSpecialtiesViewModelFactory(specialtyRepository)
    }
    private val adminAddDoctorViewModel: AdminAddDoctorViewModel by viewModels {
        AdminAddDoctorViewModelFactory(userRepository, specialtyRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_clinica_atlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        patientViewModel = patientViewModel,
                        doctorSearchViewModel = doctorSearchViewModel,
                        doctorProfileViewModel = doctorProfileViewModel,
                        bookAppointmentViewModel = bookAppointmentViewModel,
                        adminManageSpecialtiesViewModel = adminManageSpecialtiesViewModel,
                        adminAddDoctorViewModel = adminAddDoctorViewModel,
                        insuranceViewModel = insuranceViewModel,
                        // --- ¡¡NUEVO VIEWMODEL PASADO!! ---
                        doctorSearchPatientViewModel = doctorSearchPatientViewModel
                    )
                }
            }
        }
    }
}
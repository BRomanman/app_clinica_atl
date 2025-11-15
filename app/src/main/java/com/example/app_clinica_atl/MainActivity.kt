package com.example.app_clinica_atl

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.example.app_clinica_atl.navigation.Route
import com.example.app_clinica_atl.ui.components.AppDrawerVm
import com.example.app_clinica_atl.ui.components.AppTopBar
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
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModel
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // --- Dependencias (igual que antes) ---
    private val database by lazy { AppDatabase.getInstance(this) }
    private val userPreferences by lazy { UserPreferences(this) }
    private val userRepository by lazy { UserRepository(database.userDao()) }
    private val doctorRepository: DoctorRepository by lazy { DoctorRepositoryImpl(database.userDao()) }
    private val appointmentRepository: AppointmentRepository by lazy { AppointmentRepositoryImpl(database.appointmentDao()) }
    private val specialtyRepository: SpecialtyRepository by lazy { SpecialtyRepositoryImpl(database.specialtyDao()) }
    private val insuranceRepository: InsuranceRepository by lazy { InsuranceRepositoryImpl(database.insuranceDao()) }

    // --- ViewModels (igual que antes) ---
    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(userRepository, userPreferences) }
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository, userPreferences) }
    private val patientViewModel: PatientViewModel by viewModels { PatientViewModelFactory(userRepository, userPreferences, insuranceRepository) }
    private val doctorSearchViewModel: DoctorSearchViewModel by viewModels { DoctorSearchViewModelFactory(doctorRepository) }
    private val doctorProfileViewModel: DoctorProfileViewModel by viewModels { DoctorProfileViewModelFactory(doctorRepository) }
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels { BookAppointmentViewModelFactory(doctorRepository, appointmentRepository, userPreferences) }
    private val insuranceViewModel: InsuranceViewModel by viewModels { InsuranceViewModelFactory(insuranceRepository, userPreferences) }
    private val doctorSearchPatientViewModel: DoctorSearchPatientViewModel by viewModels { DoctorSearchPatientViewModelFactory(userRepository) }
    private val adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel by viewModels { AdminManageSpecialtiesViewModelFactory(specialtyRepository) }
    private val adminAddDoctorViewModel: AdminAddDoctorViewModel by viewModels { AdminAddDoctorViewModelFactory(userRepository, specialtyRepository) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_clinica_atlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    // --- Lógica Condicional (Tu Arquitectura) ---
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val userRole by authViewModel.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)

                    // El Drawer (menú lateral) solo existe si el rol es "paciente"
                    val isPatient = userRole == "paciente"
                    // Bloqueamos el drawer si no es paciente
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { isPatient })

                    // Define las rutas que NO deben mostrar el TopBar (como Login)
                    val topBarVisible = when (currentRoute) {
                        Route.Login.path, Route.Register.path -> false
                        else -> isPatient // Muestra la TopBar solo si es paciente
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = isPatient, // Solo permite deslizar si es paciente
                        drawerContent = {
                            AppDrawerVm(
                                vm = authViewModel,
                                currentRoute = currentRoute,
                                onGoToPatientProfile = { navController.navigate(Route.PatientProfile.path); scope.launch { drawerState.close() } },
                                onGoToDoctorProfile = { /* no-op */ },
                                onHome = { navController.navigate(Route.Home.path); scope.launch { drawerState.close() } },
                                onInsurance = { navController.navigate(Route.Seguros.path); scope.launch { drawerState.close() } },
                                onBookAppointment = { navController.navigate(Route.BookAppointment.path); scope.launch { drawerState.close() } }
                            )
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                if (topBarVisible) {
                                    AppTopBar(
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onLogoutClick = {
                                            scope.launch { authViewModel.logout() } // (Añadiremos esto)
                                            navController.navigate(Route.Login.path) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        ) { paddingValues ->
                            // El NavGraph ahora vive DENTRO del Scaffold
                            AppNavGraph(
                                navController = navController,
                                paddingValues = paddingValues, // Pasa el padding
                                authViewModel = authViewModel,
                                homeViewModel = homeViewModel,
                                patientViewModel = patientViewModel,
                                doctorSearchViewModel = doctorSearchViewModel,
                                doctorProfileViewModel = doctorProfileViewModel,
                                bookAppointmentViewModel = bookAppointmentViewModel,
                                adminManageSpecialtiesViewModel = adminManageSpecialtiesViewModel,
                                adminAddDoctorViewModel = adminAddDoctorViewModel,
                                insuranceViewModel = insuranceViewModel,
                                doctorSearchPatientViewModel = doctorSearchPatientViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
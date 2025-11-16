package com.example.app_clinica_atl

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.app_clinica_atl.data.repository.*
import com.example.app_clinica_atl.navigation.AppNavGraph
import com.example.app_clinica_atl.navigation.Route
import com.example.app_clinica_atl.ui.components.AppDrawerVm
import com.example.app_clinica_atl.ui.components.AppTopBar
import com.example.app_clinica_atl.ui.theme.App_clinica_atlTheme
import com.example.app_clinica_atl.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // --- Dependencias (sin cambios) ---
    private val database by lazy { AppDatabase.getInstance(this) }
    private val userPreferences by lazy { UserPreferences(this) }
    private val userRepository by lazy { UserRepository(database.userDao()) }
    private val doctorRepository: DoctorRepository by lazy { DoctorRepositoryImpl(database.userDao()) }
    private val appointmentRepository: AppointmentRepository by lazy { AppointmentRepositoryImpl(database.appointmentDao()) }
    private val specialtyRepository: SpecialtyRepository by lazy { SpecialtyRepositoryImpl(database.specialtyDao()) }
    private val insuranceRepository: InsuranceRepository by lazy { InsuranceRepositoryImpl(database.insuranceDao()) }

    // --- ViewModels (sin cambios) ---
    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(userRepository, userPreferences) }
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository, userPreferences) }
    private val patientViewModel: PatientViewModel by viewModels { PatientViewModelFactory(userRepository, userPreferences, insuranceRepository, appointmentRepository) }
    private val doctorSearchViewModel: DoctorSearchViewModel by viewModels { DoctorSearchViewModelFactory(doctorRepository) }
    private val doctorProfileViewModel: DoctorProfileViewModel by viewModels { DoctorProfileViewModelFactory(doctorRepository) }
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels { BookAppointmentViewModelFactory(doctorRepository, appointmentRepository, userPreferences) }
    private val insuranceViewModel: InsuranceViewModel by viewModels { InsuranceViewModelFactory(insuranceRepository, userPreferences) }
    private val doctorSearchPatientViewModel: DoctorSearchPatientViewModel by viewModels { DoctorSearchPatientViewModelFactory(userRepository) }
    private val adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel by viewModels { AdminManageSpecialtiesViewModelFactory(specialtyRepository) }
    private val adminAddDoctorViewModel: AdminAddDoctorViewModel by viewModels { AdminAddDoctorViewModelFactory(userRepository, specialtyRepository) }
    private val adminViewDoctorsViewModel: AdminViewDoctorsViewModel by viewModels { AdminViewDoctorsViewModelFactory(userRepository) }
    private val themeViewModel: ThemeViewModel by viewModels { ThemeViewModelFactory(userPreferences) }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by themeViewModel.themeFlow.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val isDark = when (themePreference) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            App_clinica_atlTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val userRole by authViewModel.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)

                    val isPatient = userRole == "paciente"
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { isPatient })

                    val topBarVisible = when (currentRoute) {
                        Route.Login.path, Route.Register.path -> false
                        Route.AdminMenu.path, Route.AdminAddSpecialty.path, Route.AdminAddDoctor.path, Route.AdminViewDoctors.path -> false
                        Route.DoctorMenu.path, Route.DoctorSchedule.path, Route.DoctorSearchPatient.path, Route.DoctorProfile.path -> false
                        null -> false
                        else -> true
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = isPatient,
                        drawerContent = {
                            AppDrawerVm(
                                vm = authViewModel, currentRoute = currentRoute,
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

                                        // --- ¡¡PARÁMETRO onLogoutClick ELIMINADO!! ---

                                        isDarkTheme = isDark,
                                        onToggleTheme = {
                                            val newTheme = if (isDark) "LIGHT" else "DARK"
                                            themeViewModel.saveThemePreference(newTheme)
                                        }
                                    )
                                }
                            }
                        ) { paddingValues ->
                            AppNavGraph(
                                navController = navController, paddingValues = paddingValues,
                                authViewModel = authViewModel, homeViewModel = homeViewModel,
                                patientViewModel = patientViewModel, doctorSearchViewModel = doctorSearchViewModel,
                                doctorProfileViewModel = doctorProfileViewModel, bookAppointmentViewModel = bookAppointmentViewModel,
                                adminManageSpecialtiesViewModel = adminManageSpecialtiesViewModel,
                                adminAddDoctorViewModel = adminAddDoctorViewModel,
                                insuranceViewModel = insuranceViewModel,
                                doctorSearchPatientViewModel = doctorSearchPatientViewModel,
                                adminViewDoctorsViewModel = adminViewDoctorsViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.app_clinica_atl

import android.content.Intent // <-- ¡¡IMPORT AÑADIDO!!
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
import androidx.compose.runtime.LaunchedEffect // <-- ¡¡IMPORT AÑADIDO!!
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.*
import com.example.app_clinica_atl.navigation.AppNavGraph
import com.example.app_clinica_atl.navigation.Route
import com.example.app_clinica_atl.ui.components.AppDrawerVm
import com.example.app_clinica_atl.ui.components.AppTopBar
import com.example.app_clinica_atl.ui.theme.App_clinica_atlTheme
import com.example.app_clinica_atl.ui.viewmodel.*
import kotlinx.coroutines.launch
import com.example.app_clinica_atl.data.remote.RetrofitClient
import java.lang.Runtime // <-- ¡¡IMPORT AÑADIDO!!

class MainActivity : ComponentActivity() {

    // --- Dependencias (sin cambios) ---
    private val userPreferences by lazy { UserPreferences(this) }
    private val usuariosRepository by lazy { UsuariosRepository() }
    private val doctorRepository: DoctorRepository by lazy { DoctorRepositoryImpl() }
    private val doctorProfileRepository by lazy { DoctorProfileRepository() }
    private val citasRepository: CitasRepository by lazy { CitasRepositoryImpl() }
    private val specialtyRepository: SpecialtyRepository by lazy { SpecialtyRepositoryImpl() }
    private val segurosRepository: SegurosRepository by lazy { SegurosRepositoryImpl() }
    private val historialRepository: HistorialRepository by lazy { HistorialRepository() }

    // --- ViewModels (sin cambios) ---
    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(usuariosRepository, userPreferences) }
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(usuariosRepository, userPreferences) }
    private val patientViewModel: PatientViewModel by viewModels { PatientViewModelFactory(application, usuariosRepository, userPreferences, segurosRepository, citasRepository) }
    private val doctorSearchViewModel: DoctorSearchViewModel by viewModels { DoctorSearchViewModelFactory(doctorRepository) }
    private val doctorProfileViewModel: DoctorProfileViewModel by viewModels {
        DoctorProfileViewModelFactory(doctorProfileRepository, usuariosRepository, historialRepository, citasRepository)
    }
    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels { BookAppointmentViewModelFactory(doctorRepository, citasRepository, userPreferences, usuariosRepository) }
    private val insuranceViewModel: InsuranceViewModel by viewModels { InsuranceViewModelFactory(segurosRepository, userPreferences) }
    private val doctorSearchPatientViewModel: DoctorSearchPatientViewModel by viewModels { DoctorSearchPatientViewModelFactory(usuariosRepository) }
    private val doctorScheduleViewModel: DoctorScheduleViewModel by viewModels { DoctorScheduleViewModelFactory(citasRepository, usuariosRepository) }
    private val adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel by viewModels { AdminManageSpecialtiesViewModelFactory(specialtyRepository) }
    private val adminAddDoctorViewModel: AdminAddDoctorViewModel by viewModels { AdminAddDoctorViewModelFactory(usuariosRepository, specialtyRepository) }
    private val adminViewDoctorsViewModel: AdminViewDoctorsViewModel by viewModels { AdminViewDoctorsViewModelFactory(usuariosRepository) }
    private val doctorPatientProfileViewModel: DoctorPatientProfileViewModel by viewModels {
        DoctorPatientProfileViewModelFactory(usuariosRepository, citasRepository, segurosRepository, historialRepository)
    }
    private val themeViewModel: ThemeViewModel by viewModels { ThemeViewModelFactory(userPreferences) }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val themePreference by themeViewModel.themeFlow.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val isDark = when (themePreference) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            App_clinica_atlTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val userRole by authViewModel.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)
                    val userId by userPreferences.userIdFlow.collectAsStateWithLifecycle(initialValue = null)

                    // --- ¡¡LÓGICA DE REINICIO (SOLUCIÓN NUCLEAR)!! ---
                    LaunchedEffect(currentRoute) {
                        if (currentRoute == Route.Restart.path) {
                            // Cierra la sesión en el ViewModel (resetea su estado)
                            authViewModel.logout()
                            // Crea el intent para reiniciar la app
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            val componentName = intent?.component
                            val mainIntent = Intent.makeRestartActivityTask(componentName)
                            // Lanza la nueva actividad y cierra el proceso actual
                            startActivity(mainIntent)
                            Runtime.getRuntime().exit(0)
                        }
                    }
                    // --- FIN DE LA LÓGICA DE REINICIO ---

                    val isPatient = userRole == "paciente"
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { isPatient })

                    val topBarVisible = when {
                        currentRoute == Route.Login.path || currentRoute == Route.Register.path -> false
                        currentRoute == Route.AdminMenu.path || currentRoute == Route.AdminAddSpecialty.path ||
                                currentRoute == Route.AdminAddDoctor.path || currentRoute == Route.AdminViewDoctors.path -> false
                        currentRoute == Route.DoctorMenu.path || currentRoute == Route.DoctorSchedule.path ||
                                currentRoute == Route.DoctorSearchPatient.path || currentRoute == Route.DoctorProfile.path ||
                                currentRoute == Route.DoctorPreview.path ||
                                currentRoute?.startsWith("doctor_patient_profile") == true -> false
                        currentRoute == Route.LogoutConfirmation.path -> false
                        currentRoute == Route.Restart.path -> false // Oculta en la ruta de reinicio
                        currentRoute == null -> false
                        else -> true
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = topBarVisible, // Usamos la misma lógica
                        drawerContent = {
                            AppDrawerVm(
                                vm = authViewModel, currentRoute = currentRoute,
                                onGoToPatientProfile = { navController.navigate(Route.PatientProfile.path); scope.launch { drawerState.close() } },
                                onGoToDoctorProfile = {
                                    userId?.let {
                                        navController.navigate(Route.DoctorProfile.createRoute(it))
                                        scope.launch { drawerState.close() }
                                    }
                                },
                                onHome = { navController.navigate(Route.Home.path); scope.launch { drawerState.close() } },
                                onInsurance = { navController.navigate(Route.Seguros.path); scope.launch { drawerState.close() } },
                                onBookAppointment = { navController.navigate(Route.BookAppointment.path); scope.launch { drawerState.close() } },
                                onLogout = {
                                    navController.navigate(Route.LogoutConfirmation.path)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                if (topBarVisible) {
                                    AppTopBar(
                                        onMenuClick = { scope.launch { drawerState.open() } },
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
                                doctorProfileViewModel = doctorProfileViewModel, doctorScheduleViewModel = doctorScheduleViewModel, bookAppointmentViewModel = bookAppointmentViewModel,
                                adminManageSpecialtiesViewModel = adminManageSpecialtiesViewModel,
                                adminAddDoctorViewModel = adminAddDoctorViewModel,
                                insuranceViewModel = insuranceViewModel,
                                doctorSearchPatientViewModel = doctorSearchPatientViewModel,
                                doctorPatientProfileViewModel = doctorPatientProfileViewModel,
                                adminViewDoctorsViewModel = adminViewDoctorsViewModel,
                                currentDoctorId = userId
                            )
                        }
                    }
                }
            }
        }
    }
}

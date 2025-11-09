package com.example.app_clinica_atl.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app_clinica_atl.ui.components.AppDrawer
import com.example.app_clinica_atl.ui.components.AppTopBar
import com.example.app_clinica_atl.ui.components.defaultDrawerItems
import com.example.app_clinica_atl.ui.screen.AdminAddDoctorScreen
import com.example.app_clinica_atl.ui.screen.AdminDoctorScheduleScreen
import com.example.app_clinica_atl.ui.screen.AdminDoctorSearchScreenVm
import com.example.app_clinica_atl.ui.screen.AdminManageDoctorScreenVm
import com.example.app_clinica_atl.ui.screen.AdminMenuScreen
import com.example.app_clinica_atl.ui.screen.AdminUserHistoriesScreen
import com.example.app_clinica_atl.ui.screen.BookAppointmentScreenVm
// --- 1. IMPORTAR DoctorScheduleScreenVm ---
import com.example.app_clinica_atl.ui.screen.DoctorScheduleScreenVm
import com.example.app_clinica_atl.ui.screen.DoctorMenuScreen
import com.example.app_clinica_atl.ui.screen.DoctorProfileScreenVm
import com.example.app_clinica_atl.ui.screen.FormularioSeguroScreen
import com.example.app_clinica_atl.ui.screen.HomeScreenVm
import com.example.app_clinica_atl.ui.screen.LoginScreenVm
import com.example.app_clinica_atl.ui.screen.PatientProfileScreenVm
import com.example.app_clinica_atl.ui.screen.PatientSearchScreenVm
import com.example.app_clinica_atl.ui.screen.RegisterScreenVm
import com.example.app_clinica_atl.ui.screen.SegurosScreen
import com.example.app_clinica_atl.ui.viewmodel.AdminManageDoctorViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    patientViewModel: PatientViewModel,
    bookAppointmentViewModel: BookAppointmentViewModel,
    doctorSearchViewModel: DoctorSearchViewModel,
    adminManageDoctorViewModel: AdminManageDoctorViewModel
) {
    val context = LocalContext.current.applicationContext
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Lógica para ocultar la barra superior y el menú en Login/Register
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBarAndDrawer = when (currentRoute) {
        Route.Login.path -> false
        Route.Register.path -> false
        else -> true
    }


    // Rutas Paciente
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { launchSingleTop = true } }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) { launchSingleTop = true } }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) { launchSingleTop = true } }
    val goBookAppointment: () -> Unit = { navController.navigate(Route.BookAppointment.path) { launchSingleTop = true } }
    val goInsurance: () -> Unit = { navController.navigate(Route.Insurance.path) { launchSingleTop = true } }
    val goInsuranceForm: () -> Unit = { navController.navigate(Route.InsuranceForm.path) { launchSingleTop = true } }
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) { launchSingleTop = true } }


    // Rutas Doctor
    val goPatientSearch: () -> Unit = { navController.navigate(Route.PatientSearch.path) { launchSingleTop = true } }
    val goDoctorMenu: () -> Unit = { navController.navigate(Route.DoctorMenu.path) { launchSingleTop = true } }
    val goDoctorAppointments: () -> Unit = { navController.navigate(Route.DoctorAppointments.path) { launchSingleTop = true } }
    val goDoctorProfile: () -> Unit = { navController.navigate(Route.DoctorProfile.path) { launchSingleTop = true } }


    //Rutas Administrador
    val goAdminMenu: () -> Unit = { navController.navigate(Route.AdminMenu.path) { launchSingleTop = true } }
    val goAdminDoctorSchedule: () -> Unit = { navController.navigate(Route.AdminDoctorSchedule.path) { launchSingleTop = true } }
    val goAdminUserHistories: () -> Unit = { navController.navigate(Route.AdminUserHistories.path) { launchSingleTop = true } }
    val goAdminDoctorSearch: () -> Unit = { navController.navigate(Route.AdminDoctorSearch.path) { launchSingleTop = true } }
    val goAdminManageDoctor: () -> Unit = { navController.navigate(Route.AdminManageDoctor.path) { launchSingleTop = true } }
    val goAdminAddDoctor: () -> Unit = { navController.navigate(Route.AdminAddDoctor.path) { launchSingleTop = true } }


    val logoutAndNavigate: () -> Unit = {
        scope.launch {
            authViewModel.logout()
            scope.launch { drawerState.close() }
            navController.navigate(Route.Login.path) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Desactiva el gesto de deslizar si no mostramos el drawer
        gesturesEnabled = showTopBarAndDrawer,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                items = defaultDrawerItems(
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onInsurance = { scope.launch { drawerState.close() }; goInsurance() },
                    onBookAppointment = { scope.launch { drawerState.close() }; goBookAppointment() },
                    onProfile = { scope.launch { drawerState.close() }; goProfile() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                // Solo muestra la barra superior si showTopBarAndDrawer es true
                if (showTopBarAndDrawer) {
                    AppTopBar(onOpenDrawer = { scope.launch { drawerState.open() } })
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Login.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    HomeScreenVm(
                        vm = authViewModel,
                        onBookAppointment = goBookAppointment,
                        onInsuranceSelected = goInsuranceForm
                    )
                }
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onNavigateAfterLogin = { role ->

                            // (Este es el Toast de login que pediste)
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                            val targetRoute = when (role) {
                                1L -> Route.Home.path
                                2L -> Route.DoctorMenu.path
                                3L -> Route.AdminMenu.path
                                else -> Route.Home.path
                            }
                            navController.navigate(targetRoute) {
                                popUpTo(Route.Login.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }
                composable(Route.BookAppointment.path) {
                    BookAppointmentScreenVm(vm = bookAppointmentViewModel)
                }
                composable(Route.Insurance.path) {
                    SegurosScreen(navController = navController)
                }
                composable(Route.InsuranceForm.path) {
                    FormularioSeguroScreen(navController = navController)
                }
                composable(Route.Profile.path) {
                    PatientProfileScreenVm(
                        vm = authViewModel,
                        onLogout = logoutAndNavigate
                    )
                }
                composable(Route.PatientSearch.path) {
                    PatientSearchScreenVm(vm = patientViewModel)
                }
                composable(Route.DoctorMenu.path) {
                    DoctorMenuScreen(
                        onGoAppointments = goDoctorAppointments,
                        onGoHistories = goPatientSearch,
                        onGoProfile = goDoctorProfile,
                        onLogout = logoutAndNavigate
                    )
                }

                // --- 2. ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
                // (Llamamos a la pantalla "inteligente" y le pasamos el VM)
                composable(Route.DoctorAppointments.path) {
                    DoctorScheduleScreenVm(vm = authViewModel)
                }
                // --- FIN 2 ---

                composable(Route.DoctorProfile.path) {
                    DoctorProfileScreenVm(
                        vm = authViewModel,
                        onLogout = logoutAndNavigate
                    )
                }
                composable(Route.AdminMenu.path) {
                    AdminMenuScreen(
                        onViewDoctorSchedules = goAdminDoctorSchedule,
                        onViewUserHistories = goAdminUserHistories,
                        onViewDoctorSearch = goAdminDoctorSearch,
                        onManageDoctors = goAdminManageDoctor,
                        onAddDoctor = goAdminAddDoctor,
                        onLogout = logoutAndNavigate
                    )
                }
                composable(Route.AdminDoctorSchedule.path) {
                    AdminDoctorScheduleScreen()
                }
                composable(Route.AdminUserHistories.path) {
                    AdminUserHistoriesScreen()
                }
                composable(Route.AdminDoctorSearch.path) { AdminDoctorSearchScreenVm(vm = doctorSearchViewModel) }
                composable(Route.AdminManageDoctor.path) {
                    AdminManageDoctorScreenVm(vm = adminManageDoctorViewModel)
                }
                composable(Route.AdminAddDoctor.path) {
                    AdminAddDoctorScreen(onCreateDoctor = goAdminMenu)
                }
            }
        }
    }
}
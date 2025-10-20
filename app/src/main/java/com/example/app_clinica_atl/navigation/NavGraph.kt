package com.example.app_clinica_atl.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app_clinica_atl.ui.components.AppDrawer
import com.example.app_clinica_atl.ui.components.AppTopBar
import com.example.app_clinica_atl.ui.components.defaultDrawerItems
import com.example.app_clinica_atl.ui.screen.* // Importa todas tus pantallas
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    patientViewModel: PatientViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- Acciones de Navegación ---
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { launchSingleTop = true } }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) { launchSingleTop = true } }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) { launchSingleTop = true } }
    val goBookAppointment: () -> Unit = { navController.navigate(Route.BookAppointment.path) { launchSingleTop = true } }
    val goInsurance: () -> Unit = { navController.navigate(Route.Insurance.path) { launchSingleTop = true } }
    val goInsuranceForm: () -> Unit = { navController.navigate(Route.InsuranceForm.path) { launchSingleTop = true } }
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) { launchSingleTop = true } }
    val goDoctorMenu: () -> Unit = { navController.navigate(Route.DoctorMenu.path) { launchSingleTop = true } }
    val goDoctorAppointments: () -> Unit = { navController.navigate(Route.DoctorAppointments.path) { launchSingleTop = true } }
    val goDoctorProfile: () -> Unit = { navController.navigate(Route.DoctorProfile.path) { launchSingleTop = true } }

    val goPatientSearch: () -> Unit = {
        navController.navigate(Route.PatientSearch.path) {
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onInsurance = { scope.launch { drawerState.close() }; goInsurance() },
                    onBookAppointment = { scope.launch { drawerState.close() }; goBookAppointment() },
                    onProfile = { scope.launch { drawerState.close() }; goProfile() },
                    onDoctorMenu = { scope.launch { drawerState.close() }; goDoctorMenu() },
                    onGoToPatientSearch = {
                        scope.launch { drawerState.close() }
                        goPatientSearch()
                    },
                    onLogin = { scope.launch { drawerState.close() }; goLogin() },
                    onRegister = { scope.launch { drawerState.close() }; goRegister() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    HomeScreen(
                        onBookAppointment = goBookAppointment,
                        onInsuranceSelected = goInsuranceForm
                    )
                }
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = goHome,
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
                    BookAppointmentScreen()
                }
                composable(Route.Insurance.path) {
                    SegurosScreen(navController = navController)
                }
                composable(Route.InsuranceForm.path) {
                    FormularioSeguroScreen(navController = navController)
                }
                composable(Route.Profile.path) {
                    PatientProfileScreen()
                }
                composable(Route.PatientSearch.path) {
                    PatientSearchScreenVm(
                        vm = patientViewModel
                    )
                }
                composable(Route.DoctorMenu.path) {
                    DoctorMenuScreen(
                        onGoAppointments = goDoctorAppointments,
                        onGoHistories = goPatientSearch,
                        onGoProfile = goDoctorProfile
                    )
                }
                composable(Route.DoctorAppointments.path) {
                    DoctorAppointmentsScreen()
                }
                composable(Route.DoctorProfile.path) {
                    DoctorProfileScreen()
                }
            }
        }
    }
}

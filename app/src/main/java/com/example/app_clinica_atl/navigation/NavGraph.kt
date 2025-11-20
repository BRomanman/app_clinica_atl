package com.example.app_clinica_atl.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Imports de Pantallas
import com.example.app_clinica_atl.ui.screen.*
// Imports de ViewModels
import com.example.app_clinica_atl.ui.viewmodel.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    patientViewModel: PatientViewModel,
    doctorSearchViewModel: DoctorSearchViewModel,
    doctorProfileViewModel: DoctorProfileViewModel,
    bookAppointmentViewModel: BookAppointmentViewModel,
    adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel,
    adminAddDoctorViewModel: AdminAddDoctorViewModel,
    insuranceViewModel: InsuranceViewModel,
    doctorSearchPatientViewModel: DoctorSearchPatientViewModel,
    doctorPatientProfileViewModel: DoctorPatientProfileViewModel,
    adminViewDoctorsViewModel: AdminViewDoctorsViewModel,
    currentDoctorId: Long?
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Route.Login.path,
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- Rutas de Login/Registro ---
        composable(Route.Login.path) {
            LoginScreenVm(
                authViewModel = authViewModel,
                onLoginSuccessNavigate = { role ->
                    val destination = when (role) {
                        "admin" -> Route.AdminMenu.path
                        "doctor" -> Route.DoctorMenu.path
                        else -> Route.Home.path
                    }
                    navController.navigate(destination) { popUpTo(Route.Login.path) { inclusive = true } }
                },
                onGoRegister = { navController.navigate(Route.Register.path) }
            )
        }
        composable(Route.Register.path) {
            RegisterScreenVm(
                authViewModel = authViewModel,
                onRegisterSuccessNavigate = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                }
            )
        }

        // --- Rutas de Paciente ---
        composable(Route.Home.path) {
            HomeScreen(
                viewModel = homeViewModel,
                onBookAppointmentClick = { navController.navigate(Route.BookAppointment.path) },
                onInsuranceClick = { navController.navigate(Route.Seguros.path) },
                onProfileClick = { navController.navigate(Route.PatientProfile.path) }
            )
        }
        composable(Route.PatientProfile.path) {
            PatientProfileScreen(
                viewModel = patientViewModel,
                onGoToSeguros = { navController.navigate(Route.Seguros.path) },
                onLogout = { navController.navigate(Route.LogoutConfirmation.path) }
            )
        }
        composable(Route.Seguros.path) {
            SegurosScreen(viewModel = insuranceViewModel)
        }
        composable(Route.BookAppointment.path) {
            BookAppointmentScreen(
                viewModel = bookAppointmentViewModel,
                onViewProfile = { doctorId -> navController.navigate(Route.DoctorProfile.createRoute(doctorId)) },
                onBookingSuccess = {
                    navController.navigate(Route.PatientProfile.path) {
                        popUpTo(Route.Home.path)
                    }
                }
            )
        }

        // --- Rutas de Doctor ---
        composable(Route.DoctorMenu.path) {
            DoctorMenuScreen(
                onProfileClick = {
                    currentDoctorId?.let { doctorId ->
                        navController.navigate(Route.DoctorProfile.createRoute(doctorId))
                    }
                },
                onScheduleClick = { navController.navigate(Route.DoctorSchedule.path) },
                onSearchPatient = { navController.navigate(Route.DoctorSearchPatient.path) },
                onLogout = { navController.navigate(Route.LogoutConfirmation.path) }
            )
        }
        composable(Route.DoctorSchedule.path) {
            DoctorScheduleScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Route.DoctorSearchPatient.path) {
            DoctorSearchPatientScreen(
                viewModel = doctorSearchPatientViewModel,
                onBackClick = { navController.popBackStack() },
                onPatientClick = { patientId ->
                    navController.navigate(Route.DoctorPatientProfile.createRoute(patientId))
                }
            )
        }
        composable(
            route = Route.DoctorPatientProfile.path,
            arguments = listOf(navArgument("patientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getLong("patientId") ?: return@composable
            DoctorPatientProfileScreen(
                patientId = patientId,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorPatientProfileViewModel
            )
        }

        // --- Rutas de Admin ---
        composable(Route.AdminMenu.path) {
            AdminMenuScreen(
                onAddSpecialty = { navController.navigate(Route.AdminAddSpecialty.path) },
                onAddDoctor = { navController.navigate(Route.AdminAddDoctor.path) },
                onViewDoctors = { navController.navigate(Route.AdminViewDoctors.path) },
                onLogout = { navController.navigate(Route.LogoutConfirmation.path) }
            )
        }
        composable(Route.AdminAddSpecialty.path) {
            AdminManageSpecialtiesScreen(
                viewModel = adminManageSpecialtiesViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Route.AdminAddDoctor.path) {
            AdminAddDoctorScreen(
                viewModel = adminAddDoctorViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Route.AdminViewDoctors.path) {
            AdminViewDoctorsScreen(
                viewModel = adminViewDoctorsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Ruta Común (DoctorProfile) ---
        composable(
            route = Route.DoctorProfile.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorProfileViewModel,
                modifier = Modifier.padding(PaddingValues(0.dp))
            )
        }

        // --- ¡¡LÓGICA DE LOGOUT ACTUALIZADA (SOLUCIÓN NUCLEAR)!! ---
        composable(Route.LogoutConfirmation.path) {
            LogoutConfirmationScreen(
                onGoToLogin = {
                    // ¡¡CAMBIO!! No llamamos a logout()
                    // Simplemente navegamos a la nueva ruta de reinicio
                    navController.navigate(Route.Restart.path) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onExitApp = { /* Lógica de 'finish' en la pantalla */ }
            )
        }

        // --- ¡¡NUEVA RUTA DE REINICIO!! ---
        // Esta ruta no muestra UI. Su único propósito es
        // ser detectada por MainActivity para reiniciar la app.
        composable(Route.Restart.path) {
            // No se muestra nada
        }
    }
}

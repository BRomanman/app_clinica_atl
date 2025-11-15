package com.example.app_clinica_atl.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app_clinica_atl.ui.screen.AdminAddDoctorScreen
import com.example.app_clinica_atl.ui.screen.AdminManageSpecialtiesScreen
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModel
import com.example.app_clinica_atl.ui.viewmodel.AdminManageSpecialtiesViewModel
import com.example.app_clinica_atl.ui.screen.AdminMenuScreen
import com.example.app_clinica_atl.ui.screen.BookAppointmentScreen
import com.example.app_clinica_atl.ui.screen.DoctorMenuScreen
import com.example.app_clinica_atl.ui.screen.DoctorProfileScreen
import com.example.app_clinica_atl.ui.screen.DoctorScheduleScreen
// --- ¡¡IMPORTS AÑADIDOS!! ---
import com.example.app_clinica_atl.ui.screen.DoctorSearchPatientScreen
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModel
// --- FIN IMPORTS ---
import com.example.app_clinica_atl.ui.screen.HomeScreen
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import com.example.app_clinica_atl.ui.screen.LoginScreenVm
import com.example.app_clinica_atl.ui.screen.PatientProfileScreen
import com.example.app_clinica_atl.ui.screen.RegisterScreenVm
import com.example.app_clinica_atl.ui.screen.SegurosScreen
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorProfileViewModel
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchViewModel
import com.example.app_clinica_atl.ui.viewmodel.HomeViewModel
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    patientViewModel: PatientViewModel,
    doctorSearchViewModel: DoctorSearchViewModel,
    doctorProfileViewModel: DoctorProfileViewModel,
    bookAppointmentViewModel: BookAppointmentViewModel,
    adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel,
    adminAddDoctorViewModel: AdminAddDoctorViewModel,
    insuranceViewModel: InsuranceViewModel,
    // --- ¡¡NUEVO VIEWMODEL RECIBIDO!! ---
    doctorSearchPatientViewModel: DoctorSearchPatientViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.path
    ) {
        composable(Route.Login.path) {
            LoginScreenVm(
                authViewModel = authViewModel,
                onLoginSuccessNavigate = { role ->
                    val destination = when (role) {
                        "admin" -> Route.AdminMenu.path
                        "doctor" -> Route.DoctorMenu.path
                        else -> Route.Home.path
                    }
                    navController.navigate(destination) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Route.Register.path) }
            )
        }
        composable(Route.Register.path) {
            RegisterScreenVm(
                authViewModel = authViewModel,
                onRegisterSuccessNavigate = {
                    navController.navigate(Route.Login.path)
                }
            )
        }
        composable(Route.Home.path) {
            HomeScreen(
                viewModel = homeViewModel,
                onProfileClick = { navController.navigate(Route.PatientProfile.path) },
                onBookAppointmentClick = { navController.navigate(Route.BookAppointment.path) },
                onMyDatesClick = { /* TODO */ },
                onInsuranceClick = { navController.navigate(Route.Seguros.path) }
            )
        }
        composable(Route.PatientProfile.path) {
            PatientProfileScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = patientViewModel,
                onGoToSeguros = { navController.navigate(Route.Seguros.path) }
            )
        }
        composable(Route.Seguros.path) {
            SegurosScreen(
                viewModel = insuranceViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Route.BookAppointment.path) {
            BookAppointmentScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = bookAppointmentViewModel,
                onViewProfile = { doctorId ->
                    navController.navigate(Route.DoctorProfile.createRoute(doctorId))
                }
            )
        }

        // --- RUTAS DE DOCTOR ---
        composable(Route.DoctorMenu.path) {
            DoctorMenuScreen(
                onProfileClick = { /* TODO: Navegar a DoctorProfile con el ID del doctor logueado */ },
                onScheduleClick = { navController.navigate(Route.DoctorSchedule.path) },
                // --- ¡¡ACCIÓN CONECTADA!! ---
                onSearchPatient = { navController.navigate(Route.DoctorSearchPatient.path) }
            )
        }
        composable(Route.DoctorSchedule.path) {
            DoctorScheduleScreen(onBackClick = { navController.popBackStack() })
        }

        // --- ¡¡NUEVA PANTALLA CONECTADA!! ---
        composable(Route.DoctorSearchPatient.path) {
            DoctorSearchPatientScreen(
                viewModel = doctorSearchPatientViewModel,
                onBackClick = { navController.popBackStack() },
                onPatientClick = { patientId ->
                    // El doctor navega al perfil del paciente
                    // (En el futuro, podríamos crear un 'DoctorViewPatientProfileScreen')
                    // Por ahora, lo mandamos al perfil normal.
                    navController.navigate(Route.PatientProfile.path)
                }
            )
        }
        // --- FIN ---

        // --- RUTAS DE ADMIN ---
        composable(Route.AdminMenu.path) {
            AdminMenuScreen(
                onAddSpecialty = { navController.navigate(Route.AdminAddSpecialty.path) },
                onAddDoctor = { navController.navigate(Route.AdminAddDoctor.path) },
                onLogout = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.AdminMenu.path) { inclusive = true }
                    }
                }
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

        composable(
            route = Route.DoctorProfile.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorProfileViewModel
            )
        }
    }
}
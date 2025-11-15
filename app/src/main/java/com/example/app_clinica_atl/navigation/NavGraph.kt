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
import com.example.app_clinica_atl.ui.screen.AdminAddDoctorScreen
import com.example.app_clinica_atl.ui.screen.AdminManageSpecialtiesScreen
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModel
import com.example.app_clinica_atl.ui.viewmodel.AdminManageSpecialtiesViewModel
import com.example.app_clinica_atl.ui.screen.AdminMenuScreen
import com.example.app_clinica_atl.ui.screen.BookAppointmentScreen
import com.example.app_clinica_atl.ui.screen.DoctorMenuScreen
import com.example.app_clinica_atl.ui.screen.DoctorProfileScreen
import com.example.app_clinica_atl.ui.screen.DoctorScheduleScreen
import com.example.app_clinica_atl.ui.screen.DoctorSearchPatientScreen
import com.example.app_clinica_atl.ui.viewmodel.DoctorSearchPatientViewModel
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
    doctorSearchPatientViewModel: DoctorSearchPatientViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.path,
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- Rutas sin padding ---
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

        // --- Rutas de Paciente (Con TopBar/Drawer) ---
        composable(Route.Home.path) {
            HomeScreen(
                viewModel = homeViewModel,
                onBookAppointmentClick = { navController.navigate(Route.BookAppointment.path) },
                // onMyDatesClick = { /* ¡¡ELIMINADO!! */ },
                onInsuranceClick = { navController.navigate(Route.Seguros.path) },
                onProfileClick = { navController.navigate(Route.PatientProfile.path) }
            )
        }
        composable(Route.PatientProfile.path) {
            PatientProfileScreen(
                viewModel = patientViewModel,
                onGoToSeguros = { navController.navigate(Route.Seguros.path) }
            )
        }
        composable(Route.Seguros.path) {
            SegurosScreen(
                viewModel = insuranceViewModel
            )
        }
        composable(Route.BookAppointment.path) {
            BookAppointmentScreen(
                viewModel = bookAppointmentViewModel,
                onViewProfile = { doctorId ->
                    navController.navigate(Route.DoctorProfile.createRoute(doctorId))
                }
            )
        }

        // --- Rutas de Doctor (Sin TopBar/Drawer) ---
        composable(Route.DoctorMenu.path) {
            DoctorMenuScreen(
                onProfileClick = { /* TODO */ },
                onScheduleClick = { navController.navigate(Route.DoctorSchedule.path) },
                onSearchPatient = { navController.navigate(Route.DoctorSearchPatient.path) }
            )
        }
        composable(Route.DoctorSchedule.path) {
            DoctorScheduleScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Route.DoctorSearchPatient.path) {
            DoctorSearchPatientScreen(
                viewModel = doctorSearchPatientViewModel,
                onBackClick = { navController.popBackStack() },
                onPatientClick = { /* TODO */ }
            )
        }

        // --- Rutas de Admin (Sin TopBar/Drawer) ---
        composable(Route.AdminMenu.path) {
            val scope = rememberCoroutineScope()
            AdminMenuScreen(
                onAddSpecialty = { navController.navigate(Route.AdminAddSpecialty.path) },
                onAddDoctor = { navController.navigate(Route.AdminAddDoctor.path) },
                onLogout = {
                    scope.launch { authViewModel.logout() }
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
    }
}
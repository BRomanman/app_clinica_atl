package com.example.app_clinica_atl.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

// DATA
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.data.remote.dto.normalizeRole

// SCREENS + VIEWMODELS
import com.example.app_clinica_atl.ui.screen.*
import com.example.app_clinica_atl.ui.viewmodel.*

// FACTORIES
import com.example.app_clinica_atl.ui.viewmodel.AdminProfileViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.AdminViewDoctorsViewModelFactory
import com.example.app_clinica_atl.ui.viewmodel.AdminEditDoctorViewModelFactory

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
    doctorScheduleViewModel: DoctorScheduleViewModel,
    bookAppointmentViewModel: BookAppointmentViewModel,
    adminManageSpecialtiesViewModel: AdminManageSpecialtiesViewModel,
    adminAddDoctorViewModel: AdminAddDoctorViewModel,
    insuranceViewModel: InsuranceViewModel,
    doctorSearchPatientViewModel: DoctorSearchPatientViewModel,
    doctorPatientProfileViewModel: DoctorPatientProfileViewModel,
    adminViewDoctorsViewModel: AdminViewDoctorsViewModel?,
    currentDoctorId: Long?
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Route.Login.path,
        modifier = Modifier.padding(paddingValues)
    ) {

        // ====================================================
        //                   LOGIN / REGISTRO
        // ====================================================
        composable(Route.Login.path) {
            LoginScreenVm(
                authViewModel = authViewModel,
                onLoginSuccessNavigate = { role ->
                    val destination = when (normalizeRole(role)) {
                        "administrador" -> Route.AdminMenu.path
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
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.LogoutConfirmation.path) {
            LogoutConfirmationScreen(
                onConfirm = {
                    scope.launch {
                        authViewModel.logout()
                        navController.navigate(Route.Login.path) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // ====================================================
        //                     PACIENTE
        // ====================================================
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

        // =======================
        //      SEGUROS
        // =======================
        composable(Route.Seguros.path) {
            SegurosScreen(
                viewModel = insuranceViewModel,
                onSeguroSeleccionado = { seguro ->
                    navController.navigate(Route.ContratarSeguro.create(seguro.id))
                }
            )
        }

        // =======================
        // CONTRATAR SEGURO
        // =======================
        composable(
            route = Route.ContratarSeguro.path,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val seguroId = backStackEntry.arguments?.getLong("id") ?: return@composable

            val allSeguros = insuranceViewModel.uiState.value.healthInsurances +
                    insuranceViewModel.uiState.value.lifeInsurances

            val seguro = allSeguros.firstOrNull { it.id == seguroId }

            if (seguro != null) {
                ContratarSeguroScreen(
                    seguro = seguro,
                    viewModel = insuranceViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Route.BookAppointment.path) {
            BookAppointmentScreen(
                viewModel = bookAppointmentViewModel,
                onViewProfile = { doctorId ->
                    navController.navigate(Route.DoctorPreview.createRoute(doctorId))
                },
                onBookingSuccess = {
                    navController.navigate(Route.PatientProfile.path) {
                        popUpTo(Route.Home.path)
                    }
                }
            )
        }

        // ====================================================
        //                     DOCTOR
        // ====================================================
        composable(Route.DoctorMenu.path) {
            DoctorMenuScreen(
                onProfileClick = {
                    currentDoctorId?.let { id ->
                        navController.navigate(Route.DoctorProfile.createRoute(id))
                    }
                },
                onScheduleClick = { navController.navigate(Route.DoctorSchedule.path) },
                onSearchPatient = { navController.navigate(Route.DoctorSearchPatient.path) },
                onLogout = { navController.navigate(Route.LogoutConfirmation.path) }
            )
        }

        composable(Route.DoctorSchedule.path) {
            DoctorScheduleScreen(
                doctorId = currentDoctorId,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorScheduleViewModel
            )
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

        // ====================================================
        //                   ADMINISTRADOR
        // ====================================================
        composable(Route.AdminMenu.path) {
            AdminMenuScreen(
                onAddSpecialty = { navController.navigate(Route.AdminAddSpecialty.path) },
                onAddDoctor = { navController.navigate(Route.AdminAddDoctor.path) },
                onViewDoctors = { navController.navigate(Route.AdminViewDoctors.path) },
                onProfileClick = { navController.navigate(Route.AdminProfile.path) },
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

        // --- LISTA DE DOCTORES ---
        composable(Route.AdminViewDoctors.path) {
            val repo = UsuariosRepository()
            val factory = AdminViewDoctorsViewModelFactory(repo)
            val viewModel: AdminViewDoctorsViewModel = viewModel(factory = factory)

            AdminViewDoctorsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onDoctorClick = { id ->
                    navController.navigate(Route.AdminEditDoctor.createRoute(id))
                }
            )
        }

        // --- PERFIL ADMIN ---
        composable(Route.AdminProfile.path) {
            val context = LocalContext.current
            val userPrefs = UserPreferences(context)
            val repo = UsuariosRepository()

            val factory = AdminProfileViewModelFactory(repo, userPrefs)
            val viewModel: AdminProfileViewModel = viewModel(factory = factory)

            AdminProfileScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- EDITAR DOCTOR ---
        composable(
            route = Route.AdminEditDoctor.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getLong("doctorId") ?: return@composable
            val repo = UsuariosRepository()

            val factory = AdminEditDoctorViewModelFactory(repo, doctorId)
            val viewModel: AdminEditDoctorViewModel = viewModel(factory = factory)

            AdminEditDoctorScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ====================================================
        //                    PREVIEW PÚBLICO
        // ====================================================
        composable(
            route = Route.DoctorProfile.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = id,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorProfileViewModel,
                modifier = Modifier.padding(PaddingValues(0.dp)),
                isPublicView = false
            )
        }

        composable(
            route = Route.DoctorPreview.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = id,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorProfileViewModel,
                modifier = Modifier.padding(PaddingValues(0.dp)),
                isPublicView = true
            )
        }

        composable(Route.Restart.path) {}
    }
}

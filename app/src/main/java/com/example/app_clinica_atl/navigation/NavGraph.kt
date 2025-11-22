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

// --- Imports de Datos ---
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.data.remote.dto.normalizeRole

// --- Imports de Pantallas y ViewModels ---
import com.example.app_clinica_atl.ui.screen.*
import com.example.app_clinica_atl.ui.viewmodel.*

// --- Imports de Factories (asegúrate de que estas clases existan en sus respectivos archivos VM) ---
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
    // adminViewDoctorsViewModel lo creamos dentro, no hace falta pasarlo
    adminViewDoctorsViewModel: AdminViewDoctorsViewModel?,
    currentDoctorId: Long?
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Route.Login.path,
        modifier = Modifier.padding(paddingValues)
    ) {

        // ==========================================
        // 1. AUTENTICACIÓN (Login/Registro/Logout)
        // ==========================================
        composable(Route.Login.path) {
            LoginScreenVm(
                authViewModel = authViewModel,
                onLoginSuccessNavigate = { role ->
                    val destination = when (normalizeRole(role)) {
                        "administrador" -> Route.AdminMenu.path
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

        // ==========================================
        // 2. FLUJO PACIENTE (Sin cambios)
        // ==========================================
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
                onViewProfile = { doctorId -> navController.navigate(Route.DoctorPreview.createRoute(doctorId)) },
                onBookingSuccess = {
                    navController.navigate(Route.PatientProfile.path) {
                        popUpTo(Route.Home.path)
                    }
                }
            )
        }

        // ==========================================
        // 3. FLUJO DOCTOR (Sin cambios)
        // ==========================================
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

        // ==========================================
        // 4. FLUJO ADMINISTRADOR (Restaurado y Limpio)
        // ==========================================
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
                onDoctorClick = { doctorId ->
                    // Navegación a la pantalla de edición
                    navController.navigate(Route.AdminEditDoctor.createRoute(doctorId))
                }
            )
        }

        // --- PERFIL DEL ADMIN (MIS DATOS) - VERSIÓN BASE ---
        composable(Route.AdminProfile.path) {
            val context = LocalContext.current
            val userPrefs = UserPreferences(context)
            val repo = UsuariosRepository()

            // Factory limpia: Solo Repositorio y Preferencias (sin Context extra para cámara)
            val factory = AdminProfileViewModelFactory(repo, userPrefs)
            val viewModel: AdminProfileViewModel = viewModel(factory = factory)

            AdminProfileScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- EDITAR DOCTOR (Desde la lista) ---
        composable(
            route = Route.AdminEditDoctor.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getLong("doctorId") ?: return@composable
            val repo = UsuariosRepository()

            // Asegúrate de tener AdminEditDoctorViewModel y su Factory creados como vimos antes
            val factory = AdminEditDoctorViewModelFactory(repo, doctorId)
            val viewModel: AdminEditDoctorViewModel = viewModel(factory = factory)

            // Asegúrate de tener AdminEditDoctorScreen creada
            AdminEditDoctorScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 5. RUTAS COMUNES (Perfiles públicos)
        // ==========================================
        composable(
            route = Route.DoctorProfile.path,
            arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = doctorId,
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
            val doctorId = backStackEntry.arguments?.getLong("doctorId")
            DoctorProfileScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                viewModel = doctorProfileViewModel,
                modifier = Modifier.padding(PaddingValues(0.dp)),
                isPublicView = true
            )
        }

        composable(Route.Restart.path) {
            // Placeholder para reinicio
        }
    }
}
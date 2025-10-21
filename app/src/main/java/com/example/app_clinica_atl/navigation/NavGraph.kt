package com.example.app_clinica_atl.navigation

import android.os.Build // Necesario para RequiresApi
import androidx.annotation.RequiresApi // Necesario para la anotación
import androidx.compose.foundation.layout.padding // Para el padding del Scaffold
import androidx.compose.material3.DrawerValue // Para el estado inicial del Drawer
import androidx.compose.material3.ModalNavigationDrawer // El componente Drawer
import androidx.compose.material3.Scaffold // La estructura base de la pantalla
import androidx.compose.material3.rememberDrawerState // Para recordar el estado del Drawer
import androidx.compose.runtime.Composable // Para marcar la función como Composable
import androidx.compose.runtime.rememberCoroutineScope // Para abrir/cerrar el Drawer
import androidx.compose.ui.Modifier // Para modificadores de UI
import androidx.navigation.NavHostController // El controlador de navegación
import androidx.navigation.compose.NavHost // El contenedor de destinos
import androidx.navigation.compose.composable // Para definir cada destino (pantalla)
import com.example.app_clinica_atl.ui.components.AppDrawer // Tu componente Drawer
import com.example.app_clinica_atl.ui.components.AppTopBar // Tu componente TopBar
import com.example.app_clinica_atl.ui.components.defaultDrawerItems // Tus items del Drawer
import com.example.app_clinica_atl.ui.screen.* // Importa TODAS tus pantallas
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel // ViewModel de Autenticación
import com.example.app_clinica_atl.ui.viewmodel.BookAppointmentViewModel // ViewModel de Reserva
import com.example.app_clinica_atl.ui.viewmodel.PatientViewModel // ViewModel de Pacientes
import kotlinx.coroutines.launch // Para lanzar corutinas (abrir/cerrar drawer)

// Anotación necesaria porque BookAppointmentScreen usa APIs de Android Oreo+
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController, // Recibe el controlador
    // Recibe los ViewModels creados en MainActivity
    authViewModel: AuthViewModel,
    patientViewModel: PatientViewModel,
    bookAppointmentViewModel: BookAppointmentViewModel
) {
    // Estado para controlar si el menú lateral (Drawer) está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Scope para poder usar corutinas (necesario para abrir/cerrar el drawer suavemente)
    val scope = rememberCoroutineScope()

    // --- Acciones de Navegación ---
    // Funciones lambda para navegar a cada pantalla.
    // launchSingleTop = true evita crear múltiples copias de la misma pantalla si ya está abierta.
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { launchSingleTop = true } }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) { launchSingleTop = true } }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) { launchSingleTop = true } }
    val goBookAppointment: () -> Unit = { navController.navigate(Route.BookAppointment.path) { launchSingleTop = true } }
    val goInsurance: () -> Unit = { navController.navigate(Route.Insurance.path) { launchSingleTop = true } }
    val goInsuranceForm: () -> Unit = { navController.navigate(Route.InsuranceForm.path) { launchSingleTop = true } }
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) { launchSingleTop = true } }
    val goPatientSearch: () -> Unit = { navController.navigate(Route.PatientSearch.path) { launchSingleTop = true } }
    // Rutas del menú Doctor (definidas pero sin pantalla "Add Doctor" por ahora)
    val goDoctorMenu: () -> Unit = { navController.navigate(Route.DoctorMenu.path) { launchSingleTop = true } }
    val goDoctorAppointments: () -> Unit = { navController.navigate(Route.DoctorAppointments.path) { launchSingleTop = true } }
    val goDoctorProfile: () -> Unit = { navController.navigate(Route.DoctorProfile.path) { launchSingleTop = true } }

    // Componente principal que permite el menú lateral deslizable
    ModalNavigationDrawer(
        drawerState = drawerState, // Conecta el estado
        drawerContent = { // Define qué mostrar dentro del menú lateral
            AppDrawer( // Usa tu componente AppDrawer
                currentRoute = null, // Podrías usar navController.currentBackStackEntryAsState() para marcar la ruta actual
                items = defaultDrawerItems( // Usa tu función que crea los items
                    // Pasa todas las acciones de navegación a los items del menú
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onInsurance = { scope.launch { drawerState.close() }; goInsurance() },
                    onBookAppointment = { scope.launch { drawerState.close() }; goBookAppointment() },
                    onProfile = { scope.launch { drawerState.close() }; goProfile() },
                    onDoctorMenu = { scope.launch { drawerState.close() }; goDoctorMenu() }, // Acción para Menú Doctor
                    onGoToPatientSearch = { scope.launch { drawerState.close() }; goPatientSearch() },
                    onLogin = { scope.launch { drawerState.close() }; goLogin() },
                    onRegister = { scope.launch { drawerState.close() }; goRegister() }
                    // No pasamos onGoToAddDoctor porque no existe aún
                )
            )
        }
    ) {
        // Scaffold define la estructura básica (barra superior, contenido principal)
        Scaffold(
            topBar = { // Define la barra superior
                AppTopBar( // Usa tu componente AppTopBar
                    // Acción para abrir el menú lateral (icono de hamburguesa)
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                    // Pasamos las acciones a la TopBar (si tiene botones directos, los elimina si no los pasas)
                    // NOTA: Tu AppTopBar actual solo tiene onOpenDrawer, así que esto no añade botones
                    // Si modificas AppTopBar para tener botones, tendrías que pasar las acciones aquí.
                    // Ejemplo: onHome = goHome, onGoToPatientSearch = goPatientSearch, etc.
                )
            }
        ) { innerPadding -> // Provee padding para que el contenido no quede debajo de la TopBar
            // NavHost es el contenedor donde se mostrarán las diferentes pantallas
            NavHost(
                navController = navController, // Usa el controlador
                startDestination = Route.Home.path, // Pantalla inicial
                modifier = Modifier.padding(innerPadding) // Aplica el padding del Scaffold
            ) {
                // Define cada pantalla (destino) de tu aplicación
                composable(Route.Home.path) {
                    HomeScreen(
                        onBookAppointment = goBookAppointment,
                        onInsuranceSelected = goInsuranceForm // Conecta el click en tarjeta de seguro al formulario
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
                    // --- USA LA VERSIÓN MVVM ---
                    BookAppointmentScreenVm(
                        vm = bookAppointmentViewModel
                    )
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
                // Composables para el flujo del Doctor
                composable(Route.DoctorMenu.path) {
                    DoctorMenuScreen(
                        onGoAppointments = goDoctorAppointments,
                        onGoHistories = goPatientSearch, // Reutiliza la búsqueda de pacientes
                        onGoProfile = goDoctorProfile
                    )
                }
                composable(Route.DoctorAppointments.path) {
                    DoctorAppointmentsScreen() // Pantalla placeholder
                }
                composable(Route.DoctorProfile.path) {
                    DoctorProfileScreen() // Pantalla placeholder
                }
                // No añadimos el composable para AddDoctor
            }
        }
    }
}
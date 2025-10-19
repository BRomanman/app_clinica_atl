package com.example.app_clinica_atl.navigation

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
import com.example.app_clinica_atl.ui.screen.BookAppointmentScreen
import com.example.app_clinica_atl.ui.screen.HomeScreen
import com.example.app_clinica_atl.ui.screen.LoginScreenVm
import com.example.app_clinica_atl.ui.screen.RegisterScreenVm
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helpers de navegación
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    // --- Acción nueva ---
    val goBookAppointment: () -> Unit = { navController.navigate(Route.BookAppointment.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    },
                    // --- Pasar la acción nueva ---
                    onBookAppointment = {
                        scope.launch { drawerState.close() }
                        goBookAppointment()
                    }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
                    // --- Pasar la acción nueva ---
                    onBookAppointment = goBookAppointment
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // --- Pantalla Home actualizada ---
                composable(Route.Home.path) {
                    HomeScreen(
                        onBookAppointment = goBookAppointment
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
            }
        }
    }
}

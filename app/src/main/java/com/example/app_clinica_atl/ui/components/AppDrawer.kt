package com.example.app_clinica_atl.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_clinica_atl.R
// --- 1. IMPORTAR EL AUTHVIEWMODEL ---
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.navigation.Route

// --- 2. MODELO DE DATOS PARA LOS ÍTEMS (Sin cambios) ---
data class DrawerItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val action: () -> Unit
)

// --- 3. COMPOSABLE "INTELIGENTE" (CORREGIDO) ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppDrawerVm(
    vm: AuthViewModel,
    currentRoute: String?,
    // Acepta ambas rutas de perfil
    onGoToPatientProfile: () -> Unit,
    onGoToDoctorProfile: () -> Unit,
    // Acepta otras rutas
    onHome: () -> Unit,
    onInsurance: () -> Unit,
    onBookAppointment: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ¡SOLUCIÓN 1, 2 y 3! ---
    // Observa el rol del usuario directamente desde el userRoleFlow
    val userRole by vm.userRoleFlow.collectAsStateWithLifecycle(initialValue = "paciente")

    // Decide dinámicamente a qué perfil ir
    val onGoToProfile: () -> Unit = when (userRole) {
        "doctor" -> onGoToDoctorProfile // Rol "doctor" -> Perfil de Doctor
        else -> onGoToPatientProfile // Rol "paciente" o "admin" -> Perfil de Paciente
    }

    // Genera la lista de ítems dinámicamente
    val items = defaultDrawerItems(
        onHome = onHome,
        onInsurance = onInsurance,
        onBookAppointment = onBookAppointment,
        onProfile = onGoToProfile // ¡Usa la acción de perfil correcta!
    )

    // Llama a la UI "tonta"
    AppDrawer(
        currentRoute = currentRoute,
        items = items,
        modifier = modifier
    )
}


// --- 4. COMPOSABLE "TONTO" (UI - Sin cambios) ---
@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        // Encabezado con el Logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_clean), // Usa logo_clean
                contentDescription = "Logo",
                modifier = Modifier
                    .height(60.dp)
                    .padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "Clínica",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ATL DUOC",
                    fontWeight = FontWeight.Light,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Cuerpo con los ítems de navegación
        Column(modifier = Modifier.padding(12.dp)) {
            items.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label, fontWeight = FontWeight.SemiBold) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    selected = item.route == currentRoute,
                    onClick = item.action, // Usamos la acción definida
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// --- 5. LISTA DE ÍTEMS (CORREGIDA) ---
@Composable
private fun defaultDrawerItems(
    onHome: () -> Unit,
    onInsurance: () -> Unit,
    onBookAppointment: () -> Unit,
    onProfile: () -> Unit
): List<DrawerItem> {
    return listOf(
        DrawerItem(
            route = Route.Home.path,
            icon = Icons.Default.Home,
            label = stringResource(id = R.string.drawer_home),
            action = onHome
        ),
        DrawerItem(
            // --- ¡SOLUCIÓN 4! ---
            route = Route.Seguros.path,
            icon = Icons.Default.FavoriteBorder,
            label = stringResource(id = R.string.drawer_insurance),
            action = onInsurance
        ),
        DrawerItem(
            route = Route.BookAppointment.path,
            icon = Icons.Default.CalendarToday,
            label = stringResource(id = R.string.drawer_book_appointment),
            action = onBookAppointment
        ),
        // --- 6. ÍTEM DE PERFIL (CORREGIDO) ---
        DrawerItem(
            // --- ¡SOLUCIÓN 5! ---
            route = Route.PatientProfile.path,
            icon = Icons.Default.Person,
            label = stringResource(id = R.string.drawer_profile),
            action = onProfile
        )
    )
}

// (El ítem de Administrador sigue separado, lo cual está bien)
val adminDrawerItems = listOf(
    DrawerItem(
        route = "admin_schedule",
        icon = Icons.Default.CalendarToday,
        label = "Horarios Doctores",
        action = {}
    ),
    DrawerItem(
        route = "admin_history",
        icon = Icons.Default.Assignment,
        label = "Historiales",
        action = {}
    )
)
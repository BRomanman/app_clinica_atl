package com.example.app_clinica_atl.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.app_clinica_atl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onMenuClick: () -> Unit,
    // onLogoutClick: () -> Unit, // <-- ¡¡PARÁMETRO ELIMINADO!!
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        actions = {
            // --- Botón de Tema (Sol/Luna) ---
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkTheme) "Activar modo claro" else "Activar modo oscuro"
                )
            }

            // --- ¡¡BOTÓN DE LOGOUT ELIMINADO!! ---
        }
    )
}
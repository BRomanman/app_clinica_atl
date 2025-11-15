package com.example.app_clinica_atl.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
    onMenuClick: () -> Unit, // <-- 1. Renombrado de 'onOpenDrawer' a 'onMenuClick'
    onLogoutClick: () -> Unit  // <-- 2. ¡Parámetro añadido para el botón de logout!
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) { // <-- 3. Usa 'onMenuClick'
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        actions = {
            // --- ¡¡BOTÓN DE LOGOUT AÑADIDO!! ---
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar Sesión"
                )
            }
        }
    )
}
package com.example.app_clinica_atl.ui.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView // <-- ¡IMPORT CORRECTO!
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LogoutConfirmationScreen(
    onGoToLogin: () -> Unit,
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ¡¡ESTA ES LA VERSIÓN CORRECTA!! ---
    val view = LocalView.current
    val activity = (view.context as? Activity)
    // --- FIN DE LA CORRECCIÓN ---

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Estás seguro?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¿Quieres salir de la app o iniciar sesión en una nueva cuenta?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón 2: Iniciar sesión en nueva cuenta
        Button(
            onClick = onGoToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión en otra cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón 1: Salir de la app
        Button(
            onClick = {
                // Usamos el parámetro Y la lógica de activity
                onExitApp()
                activity?.finish()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Salir de la aplicación")
        }
    }
}
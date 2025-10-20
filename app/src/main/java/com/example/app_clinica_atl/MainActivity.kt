package com.example.app_clinica_atl

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.data.local.database.AppDatabase
import com.example.app_clinica_atl.data.repository.UserRepository
import com.example.app_clinica_atl.navigation.AppNavGraph
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModel
import com.example.app_clinica_atl.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}


/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppRoot() {

    // ====== NUEVO: construcción de dependencias (Composition Root) ======
    val context = LocalContext.current.applicationContext
    // ^ Obtenemos el applicationContext para construir la base de datos de Room.

    val db = AppDatabase.getInstance(context)
    // ^ Singleton de Room. No crea múltiples instancias.

    val userDao = db.userDao()
    // ^ Obtenemos el DAO de usuarios desde la DB.

    val userRepository = UserRepository(userDao)
    // ^ Repositorio que encapsula la lógica de login/registro contra Room.



    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )
    // ^ Creamos el ViewModel con factory para inyectar el repositorio.
    //   Esto reemplaza cualquier uso anterior de listas en memoria (USERS).


    val navController = rememberNavController() // Controlador de navegación (igual que antes)
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )

        }
    }

}
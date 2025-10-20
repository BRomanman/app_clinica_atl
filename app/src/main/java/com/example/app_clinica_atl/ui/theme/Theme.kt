package com.example.app_clinica_atl.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- ¡IMPORTANTE! ---
// Estas son las variables de tu archivo "Color.kt"
// Nos aseguramos de importarlas
import com.example.app_clinica_atl.ui.theme.Primary
import com.example.app_clinica_atl.ui.theme.PrimaryLight
import com.example.app_clinica_atl.ui.theme.PrimaryDark
import com.example.app_clinica_atl.ui.theme.Secondary
import com.example.app_clinica_atl.ui.theme.SecondaryLight
import com.example.app_clinica_atl.ui.theme.SecondaryDark
import com.example.app_clinica_atl.ui.theme.NeutralLight
import com.example.app_clinica_atl.ui.theme.NeutralDark

// --- TU CÓDIGO (Esto ya lo tenías) ---
private val LightColorScheme = lightColorScheme(
    primary = Primary, // Azul 4A90E2
    onPrimary = Color.White, // Texto blanco sobre azul
    primaryContainer = PrimaryLight, // Azul claro B3D4FC
    onPrimaryContainer = PrimaryDark, // Azul oscuro 005BB5

    secondary = Secondary, // Verde menta 50E3C2
    onSecondary = NeutralDark, // Gris oscuro 4A4A4A
    secondaryContainer = SecondaryLight, // Verde suave B2FFF2
    onSecondaryContainer = SecondaryDark, // Verde profundo 00B894

    tertiary = SecondaryDark, // Reusamos el verde profundo
    onTertiary = NeutralLight,

    background = NeutralLight, // Gris muy claro F5F5F5
    onBackground = NeutralDark, // Gris oscuro 4A4A4A

    surface = NeutralLight, // Fondo para 'Cards' (igual al fondo)
    onSurface = NeutralDark, // Texto para 'Cards'
)

// --- TU CÓDIGO (Esto ya lo tenías) ---
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight, // Azul claro B3D4FC
    onPrimary = PrimaryDark, // Azul oscuro 005BB5
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,

    secondary = Secondary, // Verde menta 50E3C2
    onSecondary = SecondaryDark, // Verde profundo 00B894
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = Secondary,

    tertiary = SecondaryLight, // Verde suave B2FFF2
    onTertiary = SecondaryDark,

    background = Color(0xFF1C1B1F), // Un fondo oscuro estándar
    onBackground = NeutralLight, // Texto claro F5F5F5

    surface = Color(0xFF2C2C2E), // Fondo oscuro ligeramente más claro para 'Cards'
    onSurface = NeutralLight, // Texto claro para 'Cards'
)


// --- ¡ESTA ES LA PARTE QUE TE FALTABA! ---
// Esta es la función "AppClinicaATLTheme" que yo mencionaba.
// Es la que "activa" los colores de arriba.
@Composable
fun AppClinicaATLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Pongo 'dynamicColor' en 'false' para que TUS colores azules
    // siempre ganen y no sean reemplazados por el color del fondo de pantalla
    // del teléfono (que es lo que causa el morado).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Si no hay color dinámico, usa nuestros esquemas
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // (Esto usa tu archivo Typography.kt)
        content = content
    )
}
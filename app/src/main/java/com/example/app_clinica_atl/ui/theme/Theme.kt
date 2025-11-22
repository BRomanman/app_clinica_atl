package com.example.app_clinica_atl.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Nota: Ya NO hay imports para Primary, Secondary, etc.
// Esas variables ya viven en el archivo Color.kt,
// y como están en el mismo paquete, Theme.kt ya las puede "ver".

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Primary,
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Secondary,
    onSecondaryContainer = PrimaryLight,
    tertiary = AccentAqua,
    onTertiary = Color.White,
    background = Color(0xFF0F1A26),
    surface = Color(0xFF152232),
    surfaceVariant = Color(0xFF1F2E3F),
    onBackground = Color(0xFFE7ECF4),
    onSurface = Color(0xFFE7ECF4),
    onSurfaceVariant = Color(0xFFBAC7DB),
    error = CalmError,
    onError = Color(0xFF3D1A10),
    errorContainer = CalmErrorContainer,
    onErrorContainer = Color(0xFF3D1A10)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = AccentAqua,
    onTertiary = Color.White,
    tertiaryContainer = AccentAquaLight,
    onTertiaryContainer = Color(0xFF0F2E3B),
    background = NeutralLight,
    surface = Color.White,
    surfaceVariant = PrimaryLight,
    onBackground = NeutralDark,
    onSurface = NeutralDark,
    onSurfaceVariant = Color(0xFF4A5B70),
    error = CalmError,
    onError = Color(0xFF3D1A10),
    errorContainer = CalmErrorContainer,
    onErrorContainer = Color(0xFF3D1A10)
)

@Composable
fun App_clinica_atlTheme( // <-- El nombre de tu tema
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // (Esto usa tu archivo Typography.kt)
        content = content
    )
}

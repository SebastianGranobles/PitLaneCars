package com.example.parcial_sebastiangranoblesardila.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ⭐ ESQUEMA DE COLORES OSCURO CON ESTILO NEÓN / CYBERPUNK ⭐
private val DarkNeonColorScheme = darkColorScheme(
    primary = NeonBlue,              // Color principal para elementos interactivos (botones)
    onPrimary = OnDark,              // Color del texto sobre el color primario
    primaryContainer = NeonDarkPurple, // Contenedores que tienen relación con el primario
    onPrimaryContainer = OnDark,

    secondary = NeonPurple,          // Color secundario para acentos
    onSecondary = OnDark,            // Texto sobre el color secundario
    secondaryContainer = NeonPurple.copy(alpha = 0.3f),
    onSecondaryContainer = OnDark,

    tertiary = NeonGreen,            // Color terciario para acentos menos importantes
    onTertiary = OnDark,

    background = BackgroundDark,     // Color de fondo de toda la app
    onBackground = OnDark,           // Color del texto sobre el fondo

    surface = SurfaceDark,           // Color de superficies como Cards, Menús, etc.
    onSurface = OnDark,              // Color del texto sobre esas superficies

    error = ErrorRed,                // Color para indicar errores
    onError = OnErrorDark,           // Texto sobre el color de error
    errorContainer = ErrorRed.copy(alpha = 0.4f),
    onErrorContainer = OnDark
)

// Dejamos un tema claro por defecto por si se necesita, pero nuestra app será oscura.
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    /* Otros colores por defecto para el tema claro */
)

@Composable
fun Parcial_SebastianGranoblesArdilaTheme(
    darkTheme: Boolean = true, // ⭐ Forzamos a que la app SIEMPRE use el tema oscuro ⭐
    dynamicColor: Boolean = false, // Desactivamos los colores dinámicos de Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Como 'darkTheme' es 'true', siempre entrará aquí
        darkTheme -> DarkNeonColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Hacemos que la barra de estado sea del mismo color que el fondo de la app
            window.statusBarColor = colorScheme.background.toArgb()
            // Le decimos al sistema que los iconos de la barra (reloj, batería) deben ser claros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Tu archivo de tipografía no necesita cambios
        content = content
    )
}

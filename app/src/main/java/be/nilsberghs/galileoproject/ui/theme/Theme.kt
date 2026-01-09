package be.nilsberghs.galileoproject.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = GalileoPink,
    secondary = GalileoPurple,
    tertiary = Color.White,

    background = GalileoDarkBlue,
    surface = GalileoDarkBlue,
    onBackground = Color.White,
    onSurface = Color.White,
    // Add this to fix icons and secondary text in Dark Mode
    onSurfaceVariant = Color.White.copy(alpha = 0.7f), 
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = GalileoDarkBlue
)

private val LightColorScheme = lightColorScheme(
    primary = GalileoDarkBlue,
    onPrimary = Color.White,
    secondary = GalileoPink,
    onSecondary = Color.White,
    tertiary = GalileoPurple,
    onSurface = GalileoDarkBlue,
    onBackground = GalileoDarkBlue,
    onSurfaceVariant = GalileoDarkBlue.copy(alpha = 0.7f)
)

@Composable
fun GalileoProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = SleekBerry,
    onPrimary = Color.White,
    primaryContainer = SleekBerryLight,
    onPrimaryContainer = SleekBerry,
    secondary = SleekBerryVariant,
    onSecondary = TextMuted,
    secondaryContainer = SleekBerryLight,
    onSecondaryContainer = SleekBerry,
    tertiary = InfoBlue,
    background = SleekBackground,
    onBackground = TextPrimary,
    surface = WarmSurface,
    onSurface = TextPrimary,
    surfaceVariant = SleekBerryLight,
    onSurfaceVariant = TextSecondary,
    outline = SleekBerryVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeLight,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF5A2006),
    onPrimaryContainer = Color(0xFFFFECE5),
    secondary = EmeraldTeal,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0)
)

@Composable
fun RecipeSwipeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to keep our signature food theme look
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

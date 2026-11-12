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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A2D5E),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = BrightBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = AccentCyan,
    onTertiary = StudyForgeDarkBg,
    tertiaryContainer = Color(0xFF0E4A5E),
    onTertiaryContainer = Color(0xFFCFFAFE),
    background = StudyForgeDarkBg,
    onBackground = StudyForgeDarkTextPrimary,
    surface = StudyForgeDarkSurface,
    onSurface = StudyForgeDarkTextPrimary,
    surfaceVariant = StudyForgeDarkCard,
    onSurfaceVariant = StudyForgeDarkTextSecondary,
    outline = StudyForgeDarkBorder,
    outlineVariant = Color(0xFF1E293B),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigoDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = BrightBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFF6FF),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = AccentCyanDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFECFEFF),
    onTertiaryContainer = Color(0xFF155E75),
    background = StudyForgeLightBg,
    onBackground = StudyForgeLightTextPrimary,
    surface = StudyForgeLightCard,
    onSurface = StudyForgeLightTextPrimary,
    surfaceVariant = StudyForgeLightSurface,
    onSurfaceVariant = StudyForgeLightTextSecondary,
    outline = StudyForgeLightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun StudyForgeTheme(
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

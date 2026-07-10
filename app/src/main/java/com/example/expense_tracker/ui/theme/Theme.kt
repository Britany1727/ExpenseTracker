package com.example.expense_tracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueCrossSecondary,
    secondary = BlueCrossAccent,
    background = Color(0xFF10192B),
    surface = Color(0xFF17233D)
)

private val LightColorScheme = lightColorScheme(
    primary = BlueCrossPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = ClaySurfaceVariant,
    onPrimaryContainer = BlueCrossPrimaryDark,
    secondary = BlueCrossSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = ClaySurface,
    tertiary = BlueCrossAccent,
    background = ClayBackground,
    onBackground = TextPrimary,
    surface = ClaySurface,
    onSurface = TextPrimary,
    surfaceVariant = ClaySurfaceVariant,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MedRecordatorioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
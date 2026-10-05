package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PastelSagePrimary,
    onPrimary = Color.White,
    primaryContainer = PastelSageOnContainer,
    onPrimaryContainer = PastelSageContainer,
    secondary = PastelDustyBlue,
    onSecondary = Color.White,
    secondaryContainer = PastelDustyBlueOnContainer,
    onSecondaryContainer = PastelDustyBlueContainer,
    tertiary = PastelMutedPeach,
    onTertiary = Color.White,
    background = Color(0xFF181F1C),
    surface = Color(0xFF202925),
    surfaceVariant = Color(0xFF283430),
    onBackground = Color(0xFFE8EFEA),
    onSurface = Color(0xFFE8EFEA),
    outline = Color(0xFF4A5752)
)

private val LightColorScheme = lightColorScheme(
    primary = PastelSagePrimary,
    onPrimary = Color.White,
    primaryContainer = PastelSageContainer,
    onPrimaryContainer = PastelSageOnContainer,
    secondary = PastelDustyBlue,
    onSecondary = Color.White,
    secondaryContainer = PastelDustyBlueContainer,
    onSecondaryContainer = PastelDustyBlueOnContainer,
    tertiary = PastelMutedPeach,
    onTertiary = Color.White,
    background = PastelCreamBackground,
    surface = PastelCardSurface,
    surfaceVariant = PastelBeigeSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = PastelCardBorder
)

@Composable
fun LapoorTheme(
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

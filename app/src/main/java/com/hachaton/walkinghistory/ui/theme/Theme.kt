package com.hachaton.walkinghistory.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    secondary = Bez,
    tertiary = Blue,
    background = Bez,
    surface = Bez,
    onBackground = Brown,
    onSurface = Brown
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    secondary = Bez,
    tertiary = Blue,
    background = Bez,
    surface = Bez,
    onBackground = Brown,
    onSurface = Brown
)

@Composable
fun WalkingHistoryTheme(
    darkTheme: Boolean = false,    // Wymuszamy zawsze paletę kolorów aplikacji
    dynamicColor: Boolean = false, // Wyłączamy dynamiczne podmienianie kolorów przez system Android
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

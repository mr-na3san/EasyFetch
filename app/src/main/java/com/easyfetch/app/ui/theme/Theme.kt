package com.easyfetch.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EasyFetchColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = MidnightDeep,
    secondary = SignalViolet,
    onSecondary = Paper,
    tertiary = FlareCoral,
    onTertiary = MidnightDeep,
    background = Midnight,
    onBackground = Paper,
    surface = MidnightSurface,
    onSurface = Paper,
    surfaceVariant = MidnightDeep,
    onSurfaceVariant = Mist,
    outline = Mist
)

@Composable
fun EasyFetchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EasyFetchColorScheme,
        typography = Typography,
        content = content
    )
}

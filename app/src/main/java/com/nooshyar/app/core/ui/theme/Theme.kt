package com.nooshyar.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nooshyar.app.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    secondary = CoffeeBrown,
    background = CreamBackground,
    surface = CardLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = AlertRed
)

private val DarkColors = darkColorScheme(
    primary = TealLight,
    onPrimary = Color.Black,
    primaryContainer = TealDark,
    secondary = CoffeeBrown,
    background = Color(0xFF0F1419),
    surface = CardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = AlertRed
)

@Composable
fun NooshYarTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NooshYarTypography,
        content = content
    )
}

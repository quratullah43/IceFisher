package com.icefisher.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    secondary = IceBlue,
    tertiary = Golden,
    background = DarkWater,
    surface = IceSurface,
    onPrimary = SnowWhite,
    onSecondary = DarkWater,
    onTertiary = DarkWater,
    onBackground = SnowWhite,
    onSurface = DarkWater
)

@Composable
fun IceFisherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

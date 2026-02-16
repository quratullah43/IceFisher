package com.icefisher.game.utils

import androidx.compose.ui.graphics.Color
import com.icefisher.game.ui.theme.DarkWater
import com.icefisher.game.ui.theme.DeepBlue
import com.icefisher.game.ui.theme.FrozenBlue
import com.icefisher.game.ui.theme.IceBlue

data class Fish(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val direction: Int,
    val color: Color
)

data class GameLevel(
    val level: Int,
    val skyColor: Color,
    val waterColor: Color,
    val iceColor: Color,
    val fishCount: Int,
    val fishSpeed: Float,
    val targetCatch: Int,
    val snowflakes: Int
)

object GameLevels {
    val levels = listOf(
        GameLevel(
            level = 1,
            skyColor = Color(0xFF87CEEB),
            waterColor = DarkWater,
            iceColor = Color(0xFFE8F4F8),
            fishCount = 3,
            fishSpeed = 1.5f,
            targetCatch = 8,
            snowflakes = 20
        ),
        GameLevel(
            level = 2,
            skyColor = Color(0xFF6CB4EE),
            waterColor = Color(0xFF1565C0),
            iceColor = Color(0xFFD4E8EE),
            fishCount = 4,
            fishSpeed = 2f,
            targetCatch = 12,
            snowflakes = 30
        ),
        GameLevel(
            level = 3,
            skyColor = Color(0xFFFF7F50),
            waterColor = DeepBlue,
            iceColor = Color(0xFFFFE4C4),
            fishCount = 5,
            fishSpeed = 2.5f,
            targetCatch = 15,
            snowflakes = 25
        ),
        GameLevel(
            level = 4,
            skyColor = Color(0xFF4B0082),
            waterColor = Color(0xFF0D1B2A),
            iceColor = Color(0xFFE6E6FA),
            fishCount = 6,
            fishSpeed = 3f,
            targetCatch = 18,
            snowflakes = 40
        ),
        GameLevel(
            level = 5,
            skyColor = Color(0xFF1A1A2E),
            waterColor = Color(0xFF0A0A1A),
            iceColor = IceBlue.copy(alpha = 0.3f),
            fishCount = 8,
            fishSpeed = 3.5f,
            targetCatch = 20,
            snowflakes = 50
        )
    )
}

data class Snowflake(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

val fishColors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFF4ECDC4),
    Color(0xFFFFE66D),
    Color(0xFF95E1D3),
    Color(0xFFF38181),
    Color(0xFFAA96DA),
    Color(0xFFFCBAD3)
)

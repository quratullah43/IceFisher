package com.icefisher.game.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icefisher.game.R
import com.icefisher.game.ui.theme.DeepBlue
import com.icefisher.game.ui.theme.Golden
import com.icefisher.game.ui.theme.IceBlue
import com.icefisher.game.ui.theme.SnowWhite
import com.icefisher.game.utils.GameLevels
import kotlin.random.Random

@Composable
fun LevelSelectScreen(
    maxUnlockedLevel: Int,
    bestScores: List<Int>,
    onLevelSelect: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val snowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snowfall"
    )

    val snowflakes = remember {
        List(30) {
            Triple(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 3f + 2f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB),
                        Color(0xFF4FC3F7),
                        DeepBlue
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            snowflakes.forEach { (baseX, baseY, size) ->
                val x = (baseX * this.size.width)
                val y = ((baseY * this.size.height + snowOffset) % this.size.height)
                drawCircle(
                    color = SnowWhite.copy(alpha = 0.7f),
                    radius = size,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = SnowWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Select Level",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnowWhite
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    LevelCard(
                        level = 1,
                        isUnlocked = true,
                        bestScore = bestScores.getOrElse(0) { 0 },
                        onClick = { onLevelSelect(0) }
                    )
                    LevelCard(
                        level = 2,
                        isUnlocked = maxUnlockedLevel >= 1,
                        bestScore = bestScores.getOrElse(1) { 0 },
                        onClick = { if (maxUnlockedLevel >= 1) onLevelSelect(1) }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    LevelCard(
                        level = 3,
                        isUnlocked = maxUnlockedLevel >= 2,
                        bestScore = bestScores.getOrElse(2) { 0 },
                        onClick = { if (maxUnlockedLevel >= 2) onLevelSelect(2) }
                    )
                    LevelCard(
                        level = 4,
                        isUnlocked = maxUnlockedLevel >= 3,
                        bestScore = bestScores.getOrElse(3) { 0 },
                        onClick = { if (maxUnlockedLevel >= 3) onLevelSelect(3) }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LevelCard(
                        level = 5,
                        isUnlocked = maxUnlockedLevel >= 4,
                        bestScore = bestScores.getOrElse(4) { 0 },
                        onClick = { if (maxUnlockedLevel >= 4) onLevelSelect(4) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tap a level to start fishing!",
                fontSize = 16.sp,
                color = SnowWhite.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LevelCard(
    level: Int,
    isUnlocked: Boolean,
    bestScore: Int,
    onClick: () -> Unit
) {
    val gameLevel = GameLevels.levels[level - 1]

    Card(
        modifier = Modifier
            .size(130.dp)
            .alpha(if (isUnlocked) 1f else 0.5f)
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) gameLevel.skyColor else Color.Gray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Golden),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\uD83D\uDD12",
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Catch ${gameLevel.targetCatch}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SnowWhite
                )

                if (bestScore > 0) {
                    Text(
                        text = "Best: $bestScore",
                        fontSize = 11.sp,
                        color = Golden
                    )
                }
            }
        }
    }
}

package com.icefisher.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
fun StatsScreen(
    bestScores: List<Int>,
    maxUnlockedLevel: Int,
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
        List(25) {
            Triple(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 3f + 2f
            )
        }
    }

    val totalFish = bestScores.sum()
    val completedLevels = bestScores.indices.count { i ->
        bestScores[i] >= GameLevels.levels[i].targetCatch
    }

    BackHandler { onBackClick() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF0D47A1),
                        Color(0xFF1565C0),
                        DeepBlue
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            snowflakes.forEach { (baseX, baseY, sfSize) ->
                val x = (baseX * this.size.width)
                val y = ((baseY * this.size.height + snowOffset) % this.size.height)
                drawCircle(
                    color = SnowWhite.copy(alpha = 0.5f),
                    radius = sfSize,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
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
                    text = "Statistics",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnowWhite
                )

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = totalFish.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Golden
                        )
                        Text(
                            text = "Total Fish",
                            fontSize = 14.sp,
                            color = SnowWhite.copy(alpha = 0.7f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completedLevels / 5",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Completed",
                            fontSize = 14.sp,
                            color = SnowWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameLevels.levels.forEachIndexed { index, level ->
                    val best = bestScores.getOrElse(index) { 0 }
                    val isCompleted = best >= level.targetCatch
                    val isUnlocked = index <= maxUnlockedLevel

                    LevelStatsCard(
                        levelNumber = index + 1,
                        bestScore = best,
                        targetCatch = level.targetCatch,
                        fishCount = level.fishCount,
                        fishSpeed = level.fishSpeed,
                        isCompleted = isCompleted,
                        isUnlocked = isUnlocked,
                        skyColor = level.skyColor
                    )
                }
            }
        }
    }
}

@Composable
fun LevelStatsCard(
    levelNumber: Int,
    bestScore: Int,
    targetCatch: Int,
    fishCount: Int,
    fishSpeed: Float,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    skyColor: Color
) {
    val progress = if (targetCatch > 0) (bestScore.toFloat() / targetCatch).coerceAtMost(1f) else 0f
    val statusText = when {
        !isUnlocked -> "Locked"
        isCompleted -> "Completed"
        bestScore > 0 -> "In Progress"
        else -> "Not Started"
    }
    val statusColor = when {
        !isUnlocked -> Color.Gray
        isCompleted -> Color(0xFF4CAF50)
        bestScore > 0 -> Golden
        else -> SnowWhite.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isUnlocked) skyColor else Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = levelNumber.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnowWhite
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Level $levelNumber",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnowWhite
                    )
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }

                if (isCompleted) {
                    Text(
                        text = "\u2705",
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Best", value = bestScore.toString(), color = Golden)
                StatItem(label = "Target", value = targetCatch.toString(), color = IceBlue)
                StatItem(label = "Fish", value = fishCount.toString(), color = SnowWhite)
                StatItem(label = "Speed", value = String.format("%.1fx", fishSpeed), color = SnowWhite)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    fontSize = 12.sp,
                    color = SnowWhite.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isCompleted) Color(0xFF4CAF50) else Golden,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color(0xFF4CAF50) else Golden
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = SnowWhite.copy(alpha = 0.5f)
        )
    }
}

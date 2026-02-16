package com.icefisher.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icefisher.game.R
import com.icefisher.game.ui.theme.DeepBlue
import com.icefisher.game.ui.theme.Golden
import com.icefisher.game.ui.theme.IceBlue
import com.icefisher.game.ui.theme.SnowWhite
import com.icefisher.game.utils.Fish
import com.icefisher.game.utils.GameLevels
import com.icefisher.game.utils.Snowflake
import com.icefisher.game.utils.fishColors
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen(
    levelIndex: Int,
    onBackClick: (caughtFish: Int, passed: Boolean) -> Unit,
    onLevelComplete: (caughtFish: Int, passed: Boolean) -> Unit
) {
    val level = GameLevels.levels[levelIndex]
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var caughtFish by remember { mutableIntStateOf(0) }
    var hookX by remember { mutableFloatStateOf(screenWidthPx / 2) }
    var hookY by remember { mutableFloatStateOf(0f) }
    var isHookDropping by remember { mutableStateOf(false) }
    var isHookReturning by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var targetHookY by remember { mutableFloatStateOf(0f) }
    var timeLeftSeconds by remember { mutableIntStateOf(90) }
    var levelPassed by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    val iceHeight = screenHeightPx * 0.35f
    val waterStartY = iceHeight
    val holeX = screenWidthPx / 2
    val holeWidth = 80f

    BackHandler {
        if (isPaused) {
            isPaused = false
        } else if (!showResult) {
            isPaused = true
        }
    }

    val animatedHookY by animateFloatAsState(
        targetValue = if (isHookDropping) targetHookY else if (isHookReturning) waterStartY else hookY,
        animationSpec = tween(
            durationMillis = if (isHookDropping) 500 else 300,
            easing = LinearEasing
        ),
        finishedListener = {
            if (isHookDropping) {
                isHookDropping = false
                isHookReturning = true
            } else if (isHookReturning) {
                isHookReturning = false
                hookY = waterStartY
            }
        },
        label = "hook"
    )

    val fishes = remember { mutableStateListOf<Fish>() }
    val snowflakes = remember {
        mutableStateListOf<Snowflake>().apply {
            repeat(level.snowflakes) {
                add(
                    Snowflake(
                        x = Random.nextFloat() * screenWidthPx,
                        y = Random.nextFloat() * iceHeight,
                        size = Random.nextFloat() * 4f + 2f,
                        speed = Random.nextFloat() * 2f + 1f,
                        alpha = Random.nextFloat() * 0.5f + 0.3f
                    )
                )
            }
        }
    }

    LaunchedEffect(level) {
        repeat(level.fishCount) {
            val direction = if (Random.nextBoolean()) 1 else -1
            val startX = if (direction == 1) -100f else screenWidthPx + 100f
            fishes.add(
                Fish(
                    x = startX,
                    y = waterStartY + Random.nextFloat() * (screenHeightPx - waterStartY - 100f) + 50f,
                    speed = level.fishSpeed * (Random.nextFloat() * 0.5f + 0.75f),
                    size = Random.nextFloat() * 30f + 40f,
                    direction = direction,
                    color = fishColors[Random.nextInt(fishColors.size)]
                )
            )
        }
    }

    LaunchedEffect(isPaused, showResult) {
        while (timeLeftSeconds > 0 && !showResult) {
            if (!isPaused) {
                delay(1000)
                if (!isPaused) {
                    timeLeftSeconds--
                }
            } else {
                delay(100)
            }
        }
        if (!showResult && timeLeftSeconds <= 0) {
            levelPassed = caughtFish >= level.targetCatch
            showResult = true
        }
    }

    LaunchedEffect(isPaused) {
        while (true) {
            if (isPaused || showResult) {
                delay(100)
                continue
            }
            delay(16)

            for (i in snowflakes.indices) {
                val sf = snowflakes[i]
                var newY = sf.y + sf.speed
                if (newY > iceHeight) {
                    newY = 0f
                }
                snowflakes[i] = sf.copy(y = newY)
            }

            for (i in fishes.indices) {
                val fish = fishes[i]
                val newX = fish.x + fish.speed * fish.direction

                if (newX > screenWidthPx + 150f) {
                    fishes[i] = fish.copy(x = -100f)
                } else if (newX < -150f) {
                    fishes[i] = fish.copy(x = screenWidthPx + 100f)
                } else {
                    fishes[i] = fish.copy(x = newX)
                }
            }

            if (isHookDropping || isHookReturning) {
                val hookBounds = 40f
                for (i in fishes.indices) {
                    val fish = fishes[i]
                    if (kotlin.math.abs(hookX - fish.x) < hookBounds + fish.size / 2 &&
                        kotlin.math.abs(animatedHookY - fish.y) < hookBounds
                    ) {
                        caughtFish++
                        val direction = if (Random.nextBoolean()) 1 else -1
                        val startX = if (direction == 1) -100f else screenWidthPx + 100f
                        fishes[i] = Fish(
                            x = startX,
                            y = waterStartY + Random.nextFloat() * (screenHeightPx - waterStartY - 100f) + 50f,
                            speed = level.fishSpeed * (Random.nextFloat() * 0.5f + 0.75f),
                            size = Random.nextFloat() * 30f + 40f,
                            direction = direction,
                            color = fishColors[Random.nextInt(fishColors.size)]
                        )

                        if (caughtFish >= level.targetCatch) {
                            levelPassed = true
                        }
                        break
                    }
                }
            }
        }
    }

    if (showResult) {
        ResultScreen(
            level = levelIndex + 1,
            caughtFish = caughtFish,
            targetCatch = level.targetCatch,
            passed = levelPassed,
            onNextLevel = {
                onLevelComplete(caughtFish, levelPassed)
            },
            onBackToMenu = { onBackClick(caughtFish, levelPassed) },
            isLastLevel = levelIndex >= GameLevels.levels.size - 1
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isPaused) {
                detectTapGestures { offset ->
                    if (!isPaused && !isHookDropping && !isHookReturning && offset.y > iceHeight) {
                        hookX = holeX
                        targetHookY = offset.y
                        isHookDropping = true
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(level.skyColor, level.skyColor.copy(alpha = 0.8f)),
                    startY = 0f,
                    endY = iceHeight
                )
            )

            snowflakes.forEach { sf ->
                drawCircle(
                    color = SnowWhite.copy(alpha = sf.alpha),
                    radius = sf.size,
                    center = Offset(sf.x, sf.y)
                )
            }

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(level.waterColor, level.waterColor.copy(alpha = 0.7f)),
                    startY = iceHeight,
                    endY = size.height
                ),
                topLeft = Offset(0f, iceHeight),
                size = Size(size.width, size.height - iceHeight)
            )

            drawRect(
                color = level.iceColor,
                topLeft = Offset(0f, iceHeight - 30f),
                size = Size(holeX - holeWidth / 2, 30f)
            )
            drawRect(
                color = level.iceColor,
                topLeft = Offset(holeX + holeWidth / 2, iceHeight - 30f),
                size = Size(size.width - holeX - holeWidth / 2, 30f)
            )

            drawOval(
                color = level.waterColor.copy(alpha = 0.8f),
                topLeft = Offset(holeX - holeWidth / 2, iceHeight - 20f),
                size = Size(holeWidth, 25f)
            )

            fishes.forEach { fish ->
                drawFish(fish)
            }

            if (isHookDropping || isHookReturning || animatedHookY > waterStartY) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(holeX, iceHeight - 15f),
                    end = Offset(hookX, animatedHookY),
                    strokeWidth = 2f
                )
                drawHook(hookX, animatedHookY)
            }

            drawFisherman(holeX, iceHeight - 30f)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isPaused = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = "Pause",
                    tint = SnowWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Level ${levelIndex + 1}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnowWhite
                )
                Text(
                    text = "Fish: $caughtFish / ${level.targetCatch}",
                    fontSize = 16.sp,
                    color = if (levelPassed) Color(0xFF4CAF50) else Golden
                )
            }

            val minutes = timeLeftSeconds / 60
            val seconds = timeLeftSeconds % 60
            val timerColor = if (timeLeftSeconds <= 10) Color(0xFFFF5252) else SnowWhite
            Text(
                text = String.format("%d:%02d", minutes, seconds),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = timerColor
            )
        }

        Text(
            text = "Tap in water to cast!",
            fontSize = 14.sp,
            color = SnowWhite.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )

        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PAUSED",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnowWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fish: $caughtFish / ${level.targetCatch}",
                        fontSize = 18.sp,
                        color = Golden
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { isPaused = false },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Golden
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = "Resume",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
                        onClick = {
                            isPaused = false
                            onBackClick(caughtFish, levelPassed)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SnowWhite.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Exit to Menu",
                            fontSize = 18.sp,
                            color = SnowWhite
                        )
                    }
                }
            }
        }
    }
}

fun DrawScope.drawFish(fish: Fish) {
    val bodyPath = Path().apply {
        moveTo(fish.x - fish.size / 2 * fish.direction, fish.y)
        quadraticBezierTo(
            fish.x, fish.y - fish.size / 3,
            fish.x + fish.size / 2 * fish.direction, fish.y
        )
        quadraticBezierTo(
            fish.x, fish.y + fish.size / 3,
            fish.x - fish.size / 2 * fish.direction, fish.y
        )
        close()
    }
    drawPath(bodyPath, fish.color)

    val tailPath = Path().apply {
        moveTo(fish.x - fish.size / 2 * fish.direction, fish.y)
        lineTo(fish.x - fish.size * 0.8f * fish.direction, fish.y - fish.size / 4)
        lineTo(fish.x - fish.size * 0.8f * fish.direction, fish.y + fish.size / 4)
        close()
    }
    drawPath(tailPath, fish.color.copy(alpha = 0.8f))

    drawCircle(
        color = Color.White,
        radius = fish.size / 10,
        center = Offset(fish.x + fish.size / 4 * fish.direction, fish.y - fish.size / 8)
    )
    drawCircle(
        color = Color.Black,
        radius = fish.size / 15,
        center = Offset(fish.x + fish.size / 4 * fish.direction, fish.y - fish.size / 8)
    )
}

fun DrawScope.drawHook(x: Float, y: Float) {
    val hookPath = Path().apply {
        moveTo(x, y - 15f)
        lineTo(x, y + 10f)
        quadraticBezierTo(x, y + 25f, x - 10f, y + 25f)
        quadraticBezierTo(x - 20f, y + 25f, x - 15f, y + 15f)
    }
    drawPath(
        hookPath,
        Color.Gray,
        style = Stroke(width = 3f)
    )
}

fun DrawScope.drawFisherman(x: Float, y: Float) {
    drawCircle(
        color = Color(0xFFFFDBB4),
        radius = 20f,
        center = Offset(x, y - 60f)
    )

    drawRect(
        color = Color(0xFF1565C0),
        topLeft = Offset(x - 15f, y - 40f),
        size = Size(30f, 40f)
    )

    drawOval(
        color = Color(0xFF4A148C),
        topLeft = Offset(x - 25f, y - 85f),
        size = Size(50f, 30f)
    )

    drawLine(
        color = Color(0xFF795548),
        start = Offset(x + 15f, y - 30f),
        end = Offset(x + 50f, y - 80f),
        strokeWidth = 4f
    )
    drawLine(
        color = Color(0xFF795548),
        start = Offset(x + 50f, y - 80f),
        end = Offset(x, y + 10f),
        strokeWidth = 3f
    )
}

@Composable
fun ResultScreen(
    level: Int,
    caughtFish: Int,
    targetCatch: Int,
    passed: Boolean,
    onNextLevel: () -> Unit,
    onBackToMenu: () -> Unit,
    isLastLevel: Boolean
) {
    BackHandler {
        onBackToMenu()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF0D47A1)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (!passed) "TIME'S UP!" else if (isLastLevel) "CONGRATULATIONS!" else "LEVEL COMPLETE!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (passed) Golden else Color(0xFFFF5252)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Level $level",
                fontSize = 24.sp,
                color = SnowWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Fish caught: $caughtFish / $targetCatch",
                fontSize = 20.sp,
                color = SnowWhite.copy(alpha = 0.8f)
            )

            if (passed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Level passed!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (passed && !isLastLevel) {
                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Golden
                    ),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Text(
                        text = "Next Level",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))
            }

            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnowWhite.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Back to Menu",
                    fontSize = 18.sp,
                    color = SnowWhite
                )
            }
        }
    }
}

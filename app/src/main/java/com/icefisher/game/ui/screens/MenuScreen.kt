package com.icefisher.game.ui.screens

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icefisher.game.ui.theme.DeepBlue
import com.icefisher.game.ui.theme.Golden
import com.icefisher.game.ui.theme.IceBlue
import com.icefisher.game.ui.theme.SnowWhite
import kotlin.random.Random

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onStatsClick: () -> Unit,
    onPolicyClick: () -> Unit
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
        List(50) {
            Triple(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 4f + 2f
            )
        }
    }

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
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB).copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = this.size.height * 0.4f
                )
            )

            snowflakes.forEach { (baseX, baseY, sfSize) ->
                val x = (baseX * this.size.width)
                val y = ((baseY * this.size.height + snowOffset) % this.size.height)
                drawCircle(
                    color = SnowWhite.copy(alpha = 0.8f),
                    radius = sfSize,
                    center = Offset(x, y)
                )
            }

            val iceY = this.size.height * 0.75f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB3E5FC).copy(alpha = 0.6f),
                        Color(0xFF81D4FA).copy(alpha = 0.4f)
                    ),
                    startY = iceY,
                    endY = iceY + 40f
                ),
                topLeft = Offset(0f, iceY),
                size = Size(this.size.width, 40f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1).copy(alpha = 0.8f),
                        Color(0xFF1A237E)
                    ),
                    startY = iceY + 40f,
                    endY = this.size.height
                ),
                topLeft = Offset(0f, iceY + 40f),
                size = Size(this.size.width, this.size.height - iceY - 40f)
            )

            val wavePath = Path().apply {
                moveTo(0f, iceY + 40f)
                var wx = 0f
                while (wx < size.width) {
                    quadraticBezierTo(
                        wx + 20f, iceY + 32f,
                        wx + 40f, iceY + 40f
                    )
                    wx += 40f
                }
                lineTo(size.width, iceY + 50f)
                lineTo(0f, iceY + 50f)
                close()
            }
            drawPath(wavePath, Color(0xFF1565C0).copy(alpha = 0.5f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ICEFISHER",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = SnowWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Catch fish through the ice!",
                fontSize = 18.sp,
                color = IceBlue.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Golden
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "PLAY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStatsClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IceBlue.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Statistics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SnowWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPolicyClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IceBlue.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Privacy Policy",
                    fontSize = 16.sp,
                    color = SnowWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}

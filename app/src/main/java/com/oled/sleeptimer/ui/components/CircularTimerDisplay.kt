package com.oled.sleeptimer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oled.sleeptimer.model.TimerState
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.AccentIndigo
import com.oled.sleeptimer.ui.theme.OledBlack
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.StatusAmber
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography

@Composable
fun CircularTimerDisplay(
    state: TimerState,
    idleDurationMs: Long,
    modifier: Modifier = Modifier,
    size: Dp = 270.dp,
    strokeWidth: Dp = 6.dp
) {
    val isRunning = state.isRunning
    val isPaused = state.isPaused

    // Tiempo a mostrar
    val displayRemainingMs = if (isRunning) state.remainingMs else idleDurationMs
    val formattedTime = TimerState(remainingMs = displayRemainingMs).formattedTime()

    // Progreso del arco (1.0f -> 0.0f cuando corre, o 1.0f si está listo con tiempo seleccionado)
    val targetProgress = when {
        isRunning && state.totalDurationMs > 0L -> (state.remainingMs.toFloat() / state.totalDurationMs.toFloat()).coerceIn(0f, 1f)
        !isRunning && idleDurationMs > 0L -> 1f
        else -> 0f
    }

    val animatedProgress = remember { Animatable(targetProgress) }
    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
    }

    // Efecto de pulso suave cuando está corriendo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .background(OledBlack),
        contentAlignment = Alignment.Center
    ) {
        // Anillo visual Canvas
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Track de fondo tenue
            drawArc(
                color = Color(0xFF141418),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Arco de progreso con degradado
            if (animatedProgress.value > 0f) {
                val sweepAngle = 360f * animatedProgress.value
                val gradientBrush = Brush.sweepGradient(
                    0.0f to AccentCyan,
                    0.75f to AccentIndigo,
                    1.0f to AccentCyan
                )

                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Contenido central: Tiempo y estado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Chip de estado minimalista
            val (statusText, statusColor) = when {
                isRunning && !isPaused -> "ACTIVO" to AccentCyan
                isRunning && isPaused -> "PAUSADO" to StatusAmber
                idleDurationMs > 0L -> "LISTO" to AccentIndigo
                else -> "CONFIGURAR" to TextMuted
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0F12))
                    .border(1.dp, OledBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning && !isPaused) statusColor.copy(alpha = pulseAlpha) else statusColor
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusText,
                    style = Typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    ),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dígitos del tiempo con tamaño grande y tabular figures
            Text(
                text = formattedTime,
                style = Typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1.0).sp
                ),
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtítulo tenue
            Text(
                text = if (isRunning) {
                    if (isPaused) "Toca Reanudar para continuar" else "Apagado al llegar a 00:00"
                } else {
                    if (idleDurationMs > 0L) "Pulsa Iniciar para comenzar" else "Añade minutos abajo"
                },
                style = Typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

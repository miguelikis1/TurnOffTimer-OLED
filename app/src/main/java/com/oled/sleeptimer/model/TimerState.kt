package com.oled.sleeptimer.model

import java.util.Locale

/**
 * Representa el estado inmutable del temporizador de apagado.
 */
data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val totalDurationMs: Long = 0L,
    val remainingMs: Long = 0L,
    val keepScreenOn: Boolean = false
) {
    /**
     * Progreso normalizado de 1.0f (inicio) a 0.0f (finalizado).
     */
    val progress: Float
        get() = if (totalDurationMs > 0L) {
            (remainingMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val hours: Long
        get() = (remainingMs / 1000) / 3600

    val minutes: Long
        get() = ((remainingMs / 1000) % 3600) / 60

    val seconds: Long
        get() = (remainingMs / 1000) % 60

    /**
     * Formato limpio de tiempo para display OLED y notificaciones.
     * Muestra HH:mm:ss si hay horas, o mm:ss si es menor a 1 hora.
     */
    fun formattedTime(alwaysShowHours: Boolean = false): String {
        return if (hours > 0 || alwaysShowHours) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}

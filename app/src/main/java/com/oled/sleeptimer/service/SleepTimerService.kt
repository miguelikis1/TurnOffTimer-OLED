package com.oled.sleeptimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.oled.sleeptimer.MainActivity
import com.oled.sleeptimer.R
import com.oled.sleeptimer.model.TimerState
import com.oled.sleeptimer.receiver.SleepDeviceAdminReceiver
import com.oled.sleeptimer.util.MediaControllerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepTimerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var targetEndTimeElapsed: Long = 0L
    private var currentTotalDurationMs: Long = 0L
    private var pausedRemainingMs: Long = 0L
    private var keepScreenOnPref: Boolean = false

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMs = intent.getLongExtra(EXTRA_DURATION_MS, 0L)
                val keepScreenOn = intent.getBooleanExtra(EXTRA_KEEP_SCREEN_ON, false)
                if (durationMs > 0L) {
                    startTimer(durationMs, keepScreenOn)
                }
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_CANCEL -> cancelTimer()
            ACTION_ADD_TIME -> {
                val addMs = intent.getLongExtra(EXTRA_ADD_MS, 0L)
                if (addMs > 0L) {
                    addTime(addMs)
                }
            }
            ACTION_SET_KEEP_SCREEN_ON -> {
                val keepScreenOn = intent.getBooleanExtra(EXTRA_KEEP_SCREEN_ON, false)
                keepScreenOnPref = keepScreenOn
                _timerState.value = _timerState.value.copy(keepScreenOn = keepScreenOn)
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMs: Long, keepScreenOn: Boolean) {
        timerJob?.cancel()
        currentTotalDurationMs = durationMs
        targetEndTimeElapsed = SystemClock.elapsedRealtime() + durationMs
        pausedRemainingMs = 0L
        keepScreenOnPref = keepScreenOn

        acquireWakeLock(durationMs + 60_000L)

        _timerState.value = TimerState(
            isRunning = true,
            isPaused = false,
            totalDurationMs = durationMs,
            remainingMs = durationMs,
            keepScreenOn = keepScreenOn
        )

        // Iniciar en primer plano de forma inmediata
        startInForeground(durationMs)

        startTimerCountdownLoop()
    }

    private fun startTimerCountdownLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                val nowElapsed = SystemClock.elapsedRealtime()
                val remaining = (targetEndTimeElapsed - nowElapsed).coerceAtLeast(0L)

                _timerState.value = _timerState.value.copy(
                    isRunning = true,
                    isPaused = false,
                    remainingMs = remaining
                )

                // Actualizar notificación silenciosa
                updateNotification(remaining)

                if (remaining <= 0L) {
                    onTimerFinished()
                    break
                }

                // Cálculo para alinearse con el siguiente segundo exacto
                val delayTime = (remaining % 1000L).let { if (it == 0L) 1000L else it }
                delay(delayTime.coerceAtLeast(100L))
            }
        }
    }

    private fun pauseTimer() {
        if (!_timerState.value.isRunning || _timerState.value.isPaused) return

        timerJob?.cancel()
        val remaining = (targetEndTimeElapsed - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        pausedRemainingMs = remaining

        _timerState.value = _timerState.value.copy(
            isPaused = true,
            remainingMs = remaining
        )

        updateNotification(remaining, isPaused = true)
        releaseWakeLock()
    }

    private fun resumeTimer() {
        if (!_timerState.value.isRunning || !_timerState.value.isPaused) return

        val remaining = if (pausedRemainingMs > 0L) pausedRemainingMs else _timerState.value.remainingMs
        if (remaining <= 0L) {
            cancelTimer()
            return
        }

        targetEndTimeElapsed = SystemClock.elapsedRealtime() + remaining
        acquireWakeLock(remaining + 60_000L)

        _timerState.value = _timerState.value.copy(
            isPaused = false,
            remainingMs = remaining
        )

        startTimerCountdownLoop()
    }

    private fun addTime(addMs: Long) {
        if (!_timerState.value.isRunning) return

        currentTotalDurationMs += addMs
        if (_timerState.value.isPaused) {
            pausedRemainingMs += addMs
            _timerState.value = _timerState.value.copy(
                totalDurationMs = currentTotalDurationMs,
                remainingMs = pausedRemainingMs
            )
            updateNotification(pausedRemainingMs, isPaused = true)
        } else {
            targetEndTimeElapsed += addMs
            val remaining = (targetEndTimeElapsed - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            _timerState.value = _timerState.value.copy(
                totalDurationMs = currentTotalDurationMs,
                remainingMs = remaining
            )
            updateNotification(remaining, isPaused = false)
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        releaseWakeLock()

        _timerState.value = TimerState(
            isRunning = false,
            isPaused = false,
            totalDurationMs = 0L,
            remainingMs = 0L,
            keepScreenOn = keepScreenOnPref
        )

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun onTimerFinished() {
        releaseWakeLock()

        // 1. Restaurar el estado de pantalla (apagar bandera de pantalla encendida)
        _timerState.value = TimerState(
            isRunning = false,
            isPaused = false,
            totalDurationMs = 0L,
            remainingMs = 0L,
            keepScreenOn = false
        )

        // 2. Pausar cualquier reproducción de audio/multimedia activa en el sistema
        MediaControllerHelper.pauseActiveMedia(applicationContext)

        // 3. Bloquear / apagar la pantalla automáticamente si tiene el permiso de Administrador
        SleepDeviceAdminReceiver.lockScreen(applicationContext)

        // 4. Detener el servicio en segundo plano
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startInForeground(remainingMs: Long) {
        val notification = buildNotification(remainingMs, isPaused = false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(remainingMs: Long, isPaused: Boolean = false) {
        val notification = buildNotification(remainingMs, isPaused)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(remainingMs: Long, isPaused: Boolean): Notification {
        val remainingState = TimerState(remainingMs = remainingMs)
        val formattedTime = remainingState.formattedTime()

        // Intent para abrir MainActivity al pulsar sobre la notificación
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para el botón "Cancelar"
        val cancelIntent = Intent(this, SleepTimerService::class.java).apply {
            action = ACTION_CANCEL
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            1,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isPaused) {
            "Temporizador pausado"
        } else {
            getString(R.string.notification_title)
        }

        val text = getString(R.string.notification_remaining, formattedTime)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(!isPaused)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(contentPendingIntent)
            .addAction(
                0,
                getString(R.string.notification_action_cancel),
                cancelPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun acquireWakeLock(timeoutMs: Long) {
        if (wakeLock == null) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepTimer:WakeLockTag")
        }
        wakeLock?.let {
            if (!it.isHeld) {
                it.acquire(timeoutMs)
            }
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        releaseWakeLock()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "sleep_timer_silent_channel"
        const val NOTIFICATION_ID = 2026

        const val ACTION_START = "com.oled.sleeptimer.ACTION_START"
        const val ACTION_PAUSE = "com.oled.sleeptimer.ACTION_PAUSE"
        const val ACTION_RESUME = "com.oled.sleeptimer.ACTION_RESUME"
        const val ACTION_CANCEL = "com.oled.sleeptimer.ACTION_CANCEL"
        const val ACTION_ADD_TIME = "com.oled.sleeptimer.ACTION_ADD_TIME"
        const val ACTION_SET_KEEP_SCREEN_ON = "com.oled.sleeptimer.ACTION_SET_KEEP_SCREEN_ON"

        const val EXTRA_DURATION_MS = "extra_duration_ms"
        const val EXTRA_ADD_MS = "extra_add_ms"
        const val EXTRA_KEEP_SCREEN_ON = "extra_keep_screen_on"

        private val _timerState = MutableStateFlow(TimerState())
        val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

        fun start(context: Context, durationMs: Long, keepScreenOn: Boolean) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_MS, durationMs)
                putExtra(EXTRA_KEEP_SCREEN_ON, keepScreenOn)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pause(context: Context) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun resume(context: Context) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_RESUME
            }
            context.startService(intent)
        }

        fun cancel(context: Context) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_CANCEL
            }
            context.startService(intent)
        }

        fun addTime(context: Context, addMs: Long) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_ADD_TIME
                putExtra(EXTRA_ADD_MS, addMs)
            }
            context.startService(intent)
        }

        fun updateKeepScreenOn(context: Context, keepScreenOn: Boolean) {
            val intent = Intent(context, SleepTimerService::class.java).apply {
                action = ACTION_SET_KEEP_SCREEN_ON
                putExtra(EXTRA_KEEP_SCREEN_ON, keepScreenOn)
            }
            context.startService(intent)
        }
    }
}

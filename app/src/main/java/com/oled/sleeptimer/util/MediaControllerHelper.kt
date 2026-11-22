package com.oled.sleeptimer.util

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Utilidad para pausar y detener de forma garantizada y limpia cualquier reproducción
 * multimedia activa en el sistema Android (Spotify, YouTube, Netflix, podcasts, etc.)
 */
object MediaControllerHelper {

    /**
     * Pausa cualquier reproducción activa:
     * 1. Solicitando AudioFocus con AUDIOFOCUS_GAIN (obliga a reproductores de audio/vídeo a ceder y pausar).
     * 2. Despachando eventos de hardware KeyEvent.KEYCODE_MEDIA_PAUSE (Action DOWN y UP) como respaldo directo.
     * 3. Liberando el AudioFocus tras una breve ventana para no dejar el audio bloqueado en el sistema.
     */
    fun pauseActiveMedia(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return

        // 1. Solicitud prioritaria de AudioFocus con AUDIOFOCUS_GAIN
        var focusRequest: AudioFocusRequest? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener { /* No-op: sólo forzamos la pausa a terceros */ }
                .build()

            focusRequest = request
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        // 2. Despacho explícito de KeyEvent KEYCODE_MEDIA_PAUSE (DOWN + UP)
        val downTime = SystemClock.uptimeMillis()
        val eventDown = KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)
        val eventUp = KeyEvent(downTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0)

        audioManager.dispatchMediaKeyEvent(eventDown)
        audioManager.dispatchMediaKeyEvent(eventUp)

        // 3. Respaldo adicional vía broadcast genérico de botón multimedia
        try {
            val mediaIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
                putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
            }
            context.sendOrderedBroadcast(mediaIntent, null)
        } catch (_: Exception) {
            // Ignorado de forma segura si la versión del SO restringe el broadcast explícito
        }

        // 4. Liberar el foco de audio tras 350ms para que el usuario pueda usar el teléfono normalmente después
        CoroutineScope(Dispatchers.IO).launch {
            delay(350)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
                audioManager.abandonAudioFocusRequest(focusRequest)
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
        }
    }
}

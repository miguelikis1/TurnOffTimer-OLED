package com.oled.sleeptimer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oled.sleeptimer.service.SleepTimerService
import com.oled.sleeptimer.ui.MainScreen
import com.oled.sleeptimer.ui.theme.OledSleepTimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OledSleepTimerTheme {
                val timerState by SleepTimerService.timerState.collectAsStateWithLifecycle()
                val keepScreenOnActive = timerState.isRunning && timerState.keepScreenOn

                // Control reactivo de la bandera FLAG_KEEP_SCREEN_ON en la ventana
                DisposableEffect(keepScreenOnActive) {
                    if (keepScreenOnActive) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    onDispose {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        // Asegurar limpieza de flags de pantalla
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }
}

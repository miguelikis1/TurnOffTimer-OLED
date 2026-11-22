# Sleep Timer OLED (Android Nativo - Kotlin + Jetpack Compose)

[![Release](https://img.shields.io/badge/Release-v1.0.0-00F0FF?style=for-the-badge&logo=android)](release/SleepTimer-v1.0.0.apk)
[![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026--35)-black?style=for-the-badge&logo=android)](https://developer.android.com)
[![Size](https://img.shields.io/badge/APK%20Size-~931%20KB-success?style=for-the-badge)](release/SleepTimer-v1.0.0.apk)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

Aplicación nativa para Android desarrollada en **Kotlin** y **Jetpack Compose** con diseño minimalista **OLED Dark Mode puro** (`#000000`), sin dependencias superfluas, sin anuncios y sin telemetría. Diseñada para escuchar música, podcasts o vídeos por la noche y pausar la reproducción y apagar la pantalla automáticamente al finalizar el tiempo establecido.

---

## 📥 Descarga Directa del APK

Puedes descargar el instalador APK precompilado y firmado directamente:
- **[Descargar SleepTimer-v1.0.0.apk](release/SleepTimer-v1.0.0.apk)** *(Solo ~931 KB, compilación Release optimizada)*

---

## 📱 Características Principales

1. **Diseño OLED Puro (`#000000`) & Tipografía Limpia**:
   - Fondo negro absoluto para ahorro energético en pantallas AMOLED / OLED.
   - Tipografía geométrica sans-serif con dígitos tabulares (`fontFeatureSettings = "tnum"`) que impiden fluctuaciones horizontales en el contador.
   - Anillo visual Canvas con arco de progreso degradado en cian/índigo y pulso sutil de actividad.

2. **Selector de Tiempo Rápido & Ajuste Fino**:
   - Botones rápidos de acceso directo: `+5 min`, `+15 min`, `+30 min`, `+60 min`.
   - Botones de ajuste fino: `-5m`, `-1m`, `+1m`, `+5m` y botón de reinicio `C`.
   - Adición de tiempo al vuelo durante la cuenta atrás activa (`+5m`, `+10m`, `+15m`, `+30m`).

3. **Interruptor "Mantener pantalla encendida"**:
   - Integrado de forma fluida en la interfaz.
   - Aplica dinámicamente `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` en la ventana mientras el temporizador se encuentra activo.
   - Al apagarse o cancelarse, limpia el flag automáticamente para restaurar el comportamiento normal del sistema.

4. **Foreground Service Fiable con Notificación Silenciosa**:
   - Totalmente adaptado a Android 14+ y 15 (`foregroundServiceType="specialUse"`).
   - Canal de notificación silencioso (`IMPORTANCE_LOW`, sin sonido ni vibración) para no interrumpir el sueño.
   - Muestra el tiempo restante actualizado cada segundo.
   - Botón directo de acción **"Cancelar"** en la notificación para detener el servicio inmediatamente.
   - Temporizador de alta precisión basado en `SystemClock.elapsedRealtime()` y `PowerManager.PARTIAL_WAKE_LOCK` para evitar deriva por doze mode.

5. **Acciones Automáticas al Llegar a 00:00**:
   - **Pausa de Multimedia del Sistema**:
     - Solicita `AudioManager.AUDIOFOCUS_GAIN` (obliga a Spotify, YouTube, Netflix, Audible, etc. a detener su reproducción).
     - Envía eventos `KeyEvent.KEYCODE_MEDIA_PAUSE` (Action DOWN y UP) vía `dispatchMediaKeyEvent` y broadcast `ACTION_MEDIA_BUTTON` como respaldo.
     - Libera el foco de audio tras 350 ms para no bloquear el sistema.
   - **Bloqueo y Apagado Automático de Pantalla**:
     - Usa `DevicePolicyManager.lockNow()`.
     - Flujo integrado para activar el permiso de Administrador de Dispositivos con un solo toque.
   - **Cierre Limpio del Servicio**:
     - Detiene el Foreground Service y restaura la pantalla a su estado habitual.

6. **Desinstalación Sencilla**:
   - Botón discreto integrado en la tarjeta de administrador: *"Desactivar permiso (para desinstalar app)"*.
   - Ejecuta `DevicePolicyManager.removeActiveAdmin()`, permitiendo desinstalar la app al instante sin navegar por los menús profundos del sistema.

---

## 🛠️ Requisitos Técnicos

- **Lenguaje**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose con Material 3 (BOM 2024.10.01)
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35 (Android 15)
- **Compilador JVM**: Java 17
- **Permisos de red**: Ninguno (`android.permission.INTERNET` no requerido ni declarado).
- **Telemetría / SDKs externos**: Cero.
- **Tamaño del binario**: ~931 KB.

---

## 🚀 Compilación Local

Para compilar el APK en tu máquina:
```bash
# Versión Debug
./gradlew assembleDebug

# Versión Release
./gradlew assembleRelease
```
El archivo resultante se encuentra en `app/build/outputs/apk/release/app-release.apk`.

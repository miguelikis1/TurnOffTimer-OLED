# Keep Device Admin Receiver
-keep class com.oled.sleeptimer.receiver.SleepDeviceAdminReceiver { *; }

# Keep Foreground Service
-keep class com.oled.sleeptimer.service.SleepTimerService { *; }

# Compose rules
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

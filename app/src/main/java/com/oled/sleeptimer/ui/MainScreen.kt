package com.oled.sleeptimer.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oled.sleeptimer.R
import com.oled.sleeptimer.model.TimerState
import com.oled.sleeptimer.receiver.SleepDeviceAdminReceiver
import com.oled.sleeptimer.service.SleepTimerService
import com.oled.sleeptimer.ui.components.AdminPermissionCard
import com.oled.sleeptimer.ui.components.CircularTimerDisplay
import com.oled.sleeptimer.ui.components.CustomTimePickerDialog
import com.oled.sleeptimer.ui.components.KeepScreenOnToggle
import com.oled.sleeptimer.ui.components.QuickTimeChips
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.OledBlack
import com.oled.sleeptimer.ui.theme.OledBorder
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.OledSurface
import com.oled.sleeptimer.ui.theme.StatusAmber
import com.oled.sleeptimer.ui.theme.StatusRed
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val timerState by SleepTimerService.timerState.collectAsStateWithLifecycle()

    // Minutos seleccionados cuando el temporizador está inactivo (por defecto 30 minutos)
    var selectedMinutes by remember { mutableIntStateOf(30) }
    var keepScreenOn by remember { mutableStateOf(false) }
    var isAdminActive by remember { mutableStateOf(SleepDeviceAdminReceiver.isAdminActive(context)) }
    var showCustomTimeDialog by remember { mutableStateOf(false) }

    // Comprobar estado de admin al recomponer
    LaunchedEffect(Unit) {
        isAdminActive = SleepDeviceAdminReceiver.isAdminActive(context)
    }

    // Launcher para conceder el permiso de Administrador de Dispositivo
    val adminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        isAdminActive = SleepDeviceAdminReceiver.isAdminActive(context)
        if (isAdminActive) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("✓ Permiso de administrador concedido con éxito.")
            }
        }
    }

    // Launcher para permisos de Notificación en Android 13+
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OledBlack,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera Minimalista OLED
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(OledSurface)
                            .border(1.dp, OledBorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bedtime,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SLEEP TIMER",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = TextWhite
                    )
                }

                // Indicador de modo OLED puro
                Text(
                    text = "OLED DARK",
                    style = Typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Anillo central y contador visual (pulsable para ajustar tiempo exacto al estar inactivo)
            CircularTimerDisplay(
                state = timerState,
                idleDurationMs = selectedMinutes * 60 * 1000L,
                size = 280.dp,
                onClick = {
                    if (!timerState.isRunning) {
                        showCustomTimeDialog = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Selector de tiempo con accesos rápidos, ajuste fino y botón de tiempo exacto
            QuickTimeChips(
                isRunning = timerState.isRunning,
                onAddMinutes = { minutesToAdd ->
                    if (timerState.isRunning) {
                        SleepTimerService.addTime(context, minutesToAdd * 60 * 1000L)
                    } else {
                        selectedMinutes = (selectedMinutes + minutesToAdd).coerceIn(1, 1440)
                    }
                },
                onAdjustMinutes = { delta ->
                    selectedMinutes = (selectedMinutes + delta).coerceIn(1, 1440)
                },
                onReset = {
                    selectedMinutes = 0
                },
                onOpenCustomTimeDialog = {
                    showCustomTimeDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción principales (Iniciar / Pausar / Cancelar)
            if (!timerState.isRunning) {
                Button(
                    onClick = {
                        if (selectedMinutes <= 0) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Selecciona al menos 1 minuto para iniciar.")
                            }
                            return@Button
                        }

                        // Solicitar permiso de notificación en Android 13+ si falta
                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        // Si no tiene permiso de admin, recordárselo amablemente
                        if (!isAdminActive) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Aviso: Concede el permiso de bloqueo para que la pantalla se apague al terminar.")
                            }
                        }

                        SleepTimerService.start(
                            context = context,
                            durationMs = selectedMinutes * 60 * 1000L,
                            keepScreenOn = keepScreenOn
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan,
                        contentColor = OledBlack
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INICIAR TEMPORIZADOR",
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Pausar / Reanudar
                    Button(
                        onClick = {
                            if (timerState.isPaused) {
                                SleepTimerService.resume(context)
                            } else {
                                SleepTimerService.pause(context)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (timerState.isPaused) AccentCyan else OledSurface,
                            contentColor = if (timerState.isPaused) OledBlack else TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OledBorderSubtle)
                    ) {
                        Icon(
                            imageVector = if (timerState.isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (timerState.isPaused) "Reanudar" else "Pausar",
                            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    // Botón Cancelar
                    Button(
                        onClick = {
                            SleepTimerService.cancel(context)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E1113),
                            contentColor = StatusRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.35f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = StatusRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancelar",
                            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = StatusRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Interruptor para mantener pantalla encendida
            KeepScreenOnToggle(
                enabled = if (timerState.isRunning) timerState.keepScreenOn else keepScreenOn,
                onToggle = { newChecked ->
                    keepScreenOn = newChecked
                    if (timerState.isRunning) {
                        SleepTimerService.updateKeepScreenOn(context, newChecked)
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tarjeta de Permiso de Administrador y Desactivación
            AdminPermissionCard(
                isAdminActive = isAdminActive,
                onRequestPermission = {
                    val intent = SleepDeviceAdminReceiver.getAddAdminIntent(
                        context,
                        context.getString(R.string.device_admin_explanation)
                    )
                    adminLauncher.launch(intent)
                },
                onRevokePermission = {
                    val removed = SleepDeviceAdminReceiver.removeAdmin(context)
                    isAdminActive = SleepDeviceAdminReceiver.isAdminActive(context)
                    coroutineScope.launch {
                        if (removed || !isAdminActive) {
                            snackbarHostState.showSnackbar("Permiso de administrador desactivado. Ya puedes desinstalar la app libremente.")
                        } else {
                            snackbarHostState.showSnackbar("No se pudo desactivar el administrador.")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de aviso de notificaciones en Android 13+ si están pendientes
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OledSurface)
                        .border(1.dp, OledBorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = StatusAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Permiso de notificaciones necesario para ver la cuenta atrás en segundo plano.",
                                style = Typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Activar", color = AccentCyan)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Pie de página minimalista
            Text(
                text = "Sin telemetría • Sin anuncios • 100% nativo",
                style = Typography.bodyMedium.copy(fontSize = 11.sp),
                color = TextMuted,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }

    // Diálogo modal OLED para selección de tiempo exacto (Horas : Minutos)
    if (showCustomTimeDialog) {
        CustomTimePickerDialog(
            initialTotalMinutes = selectedMinutes,
            onConfirm = { customMins ->
                selectedMinutes = customMins
                showCustomTimeDialog = false
            },
            onDismiss = {
                showCustomTimeDialog = false
            }
        )
    }
}

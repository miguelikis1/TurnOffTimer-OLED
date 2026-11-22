package com.oled.sleeptimer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.OledBlack
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.OledSurface
import com.oled.sleeptimer.ui.theme.OledSurfaceElevated
import com.oled.sleeptimer.ui.theme.StatusAmber
import com.oled.sleeptimer.ui.theme.StatusRed
import com.oled.sleeptimer.ui.theme.StatusSuccess
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography

@Composable
fun AdminPermissionCard(
    isAdminActive: Boolean,
    onRequestPermission: () -> Unit,
    onRevokePermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRevokeDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (!isAdminActive) {
            // Tarjeta de aviso para conceder el permiso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(OledSurface)
                    .border(1.dp, StatusAmber.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StatusAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = StatusAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Permiso de bloqueo pendiente",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextWhite
                            )
                            Text(
                                text = "Requerido para apagar la pantalla al llegar a 00:00",
                                style = Typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan,
                            contentColor = OledBlack
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Activar bloqueo automático",
                            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        } else {
            // Tarjeta minimalista cuando el permiso está concedido
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(OledSurface)
                    .border(1.dp, OledBorderSubtle, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StatusSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bloqueo de pantalla habilitado",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                color = TextWhite
                            )
                            Text(
                                text = "El dispositivo se apagará automáticamente",
                                style = Typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón discreto para desactivar el permiso de administrador para desinstalar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Desactivar permiso (para desinstalar app)",
                            style = Typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = TextMuted
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showRevokeDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para revocar el permiso de administrador
    if (showRevokeDialog) {
        AlertDialog(
            onDismissRequest = { showRevokeDialog = false },
            containerColor = OledSurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "¿Desactivar Administrador?",
                    style = Typography.headlineMedium.copy(fontSize = 18.sp),
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "Android no permite desinstalar aplicaciones que tengan permisos de Administrador de Dispositivo activos.\n\nAl desactivarlo, podrás desinstalar la app normalmente desde los Ajustes o Google Play.",
                    style = Typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRevokeDialog = false
                        onRevokePermission()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRed,
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Desactivar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRevokeDialog = false }
                ) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

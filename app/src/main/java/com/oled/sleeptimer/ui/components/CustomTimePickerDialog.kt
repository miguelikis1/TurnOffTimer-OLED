package com.oled.sleeptimer.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.OledBlack
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.OledSurface
import com.oled.sleeptimer.ui.theme.OledSurfaceElevated
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography

@Composable
fun CustomTimePickerDialog(
    initialTotalSeconds: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var hours by remember { mutableIntStateOf((initialTotalSeconds / 3600).toInt()) }
    var minutes by remember { mutableIntStateOf(((initialTotalSeconds % 3600) / 60).toInt()) }
    var seconds by remember { mutableIntStateOf((initialTotalSeconds % 60).toInt()) }

    var hoursText by remember { mutableStateOf(hours.toString().padStart(2, '0')) }
    var minutesText by remember { mutableStateOf(minutes.toString().padStart(2, '0')) }
    var secondsText by remember { mutableStateOf(seconds.toString().padStart(2, '0')) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OledSurfaceElevated,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AccentCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tiempo Exacto",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Ajusta horas, minutos y segundos al milímetro",
                        style = Typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Fila con los 3 campos: Horas, Minutos y Segundos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna de Horas
                    TimeFieldStepper(
                        label = "HORAS",
                        value = hours,
                        textValue = hoursText,
                        onValueChange = { newVal ->
                            val clamped = newVal.coerceIn(0, 23)
                            hours = clamped
                            hoursText = clamped.toString().padStart(2, '0')
                        },
                        onTextChange = { raw ->
                            val filtered = raw.filter { it.isDigit() }.take(2)
                            hoursText = filtered
                            hours = (filtered.toIntOrNull() ?: 0).coerceIn(0, 23)
                        }
                    )

                    Text(
                        text = ":",
                        style = Typography.displayMedium.copy(fontSize = 28.sp),
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )

                    // Columna de Minutos
                    TimeFieldStepper(
                        label = "MINUTOS",
                        value = minutes,
                        textValue = minutesText,
                        onValueChange = { newVal ->
                            val clamped = newVal.coerceIn(0, 59)
                            minutes = clamped
                            minutesText = clamped.toString().padStart(2, '0')
                        },
                        onTextChange = { raw ->
                            val filtered = raw.filter { it.isDigit() }.take(2)
                            minutesText = filtered
                            minutes = (filtered.toIntOrNull() ?: 0).coerceIn(0, 59)
                        }
                    )

                    Text(
                        text = ":",
                        style = Typography.displayMedium.copy(fontSize = 28.sp),
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )

                    // Columna de Segundos
                    TimeFieldStepper(
                        label = "SEGUNDOS",
                        value = seconds,
                        textValue = secondsText,
                        onValueChange = { newVal ->
                            val clamped = newVal.coerceIn(0, 59)
                            seconds = clamped
                            secondsText = clamped.toString().padStart(2, '0')
                        },
                        onTextChange = { raw ->
                            val filtered = raw.filter { it.isDigit() }.take(2)
                            secondsText = filtered
                            seconds = (filtered.toIntOrNull() ?: 0).coerceIn(0, 59)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Accesos rápidos comunes
                Text(
                    text = "PREAJUSTES",
                    style = Typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                ) {
                    PresetDurationChip("30s") {
                        hours = 0; minutes = 0; seconds = 30
                        hoursText = "00"; minutesText = "00"; secondsText = "30"
                    }
                    PresetDurationChip("5m") {
                        hours = 0; minutes = 5; seconds = 0
                        hoursText = "00"; minutesText = "05"; secondsText = "00"
                    }
                    PresetDurationChip("15m") {
                        hours = 0; minutes = 15; seconds = 0
                        hoursText = "00"; minutesText = "15"; secondsText = "00"
                    }
                    PresetDurationChip("30m") {
                        hours = 0; minutes = 30; seconds = 0
                        hoursText = "00"; minutesText = "30"; secondsText = "00"
                    }
                    PresetDurationChip("1h") {
                        hours = 1; minutes = 0; seconds = 0
                        hoursText = "01"; minutesText = "00"; secondsText = "00"
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val totalSecs = (hours.toLong() * 3600L) + (minutes.toLong() * 60L) + seconds.toLong()
                    onConfirm(totalSecs.coerceAtLeast(1L))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = OledBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aplicar tiempo",
                    style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun TimeFieldStepper(
    label: String,
    value: Int,
    textValue: String,
    onValueChange: (Int) -> Unit,
    onTextChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            ),
            color = TextMuted,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Botón incremento (+)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(OledSurface)
                .border(1.dp, OledBorderSubtle, CircleShape)
                .clickable { onValueChange(value + 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Aumentar $label",
                tint = TextWhite,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Campo numérico directo
        OutlinedTextField(
            value = textValue,
            onValueChange = onTextChange,
            textStyle = Typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFeatureSettings = "tnum"
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = OledBorderSubtle,
                focusedContainerColor = OledBlack,
                unfocusedContainerColor = OledBlack
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Botón decremento (-)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(OledSurface)
                .border(1.dp, OledBorderSubtle, CircleShape)
                .clickable { onValueChange(value - 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = "Reducir $label",
                tint = TextWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PresetDurationChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(OledSurface)
            .border(1.dp, OledBorderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TextSecondary
        )
    }
}

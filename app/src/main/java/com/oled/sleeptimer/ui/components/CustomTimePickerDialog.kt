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
import androidx.compose.ui.graphics.Color
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
    initialTotalMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hours by remember { mutableIntStateOf(initialTotalMinutes / 60) }
    var minutes by remember { mutableIntStateOf(initialTotalMinutes % 60) }

    // Textos para entrada directa por teclado
    var hoursText by remember { mutableStateOf(hours.toString().padStart(2, '0')) }
    var minutesText by remember { mutableStateOf(minutes.toString().padStart(2, '0')) }

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
                        text = "Establece horas y minutos libremente",
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
                Spacer(modifier = Modifier.height(8.dp))

                // Selectores de Horas y Minutos
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
                        onTextChange = { rawText ->
                            val filtered = rawText.filter { it.isDigit() }.take(2)
                            hoursText = filtered
                            val parsed = filtered.toIntOrNull() ?: 0
                            hours = parsed.coerceIn(0, 23)
                        }
                    )

                    // Dos puntos separadores
                    Text(
                        text = ":",
                        style = Typography.displayMedium.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 36.sp
                        ),
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
                        onTextChange = { rawText ->
                            val filtered = rawText.filter { it.isDigit() }.take(2)
                            minutesText = filtered
                            val parsed = filtered.toIntOrNull() ?: 0
                            minutes = parsed.coerceIn(0, 59)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Accesos rápidos predefinidos dentro del diálogo
                Text(
                    text = "PREAJUSTES COMUNES",
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
                    PresetDurationChip("15m") {
                        hours = 0
                        minutes = 15
                        hoursText = "00"
                        minutesText = "15"
                    }
                    PresetDurationChip("30m") {
                        hours = 0
                        minutes = 30
                        hoursText = "00"
                        minutesText = "30"
                    }
                    PresetDurationChip("45m") {
                        hours = 0
                        minutes = 45
                        hoursText = "00"
                        minutesText = "45"
                    }
                    PresetDurationChip("1h") {
                        hours = 1
                        minutes = 0
                        hoursText = "01"
                        minutesText = "00"
                    }
                    PresetDurationChip("2h") {
                        hours = 2
                        minutes = 0
                        hoursText = "02"
                        minutesText = "00"
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val totalMinutes = (hours * 60) + minutes
                    onConfirm(totalMinutes.coerceAtLeast(1))
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
        modifier = Modifier.width(90.dp)
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Botón incremento (+)
        Box(
            modifier = Modifier
                .size(40.dp)
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
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Campo de texto editable directamente con teclado numérico
        OutlinedTextField(
            value = textValue,
            onValueChange = onTextChange,
            textStyle = Typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
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
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Botón decremento (-)
        Box(
            modifier = Modifier
                .size(40.dp)
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
                modifier = Modifier.size(20.dp)
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
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TextSecondary
        )
    }
}

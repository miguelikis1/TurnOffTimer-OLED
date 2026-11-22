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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.OledBorder
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.OledSurface
import com.oled.sleeptimer.ui.theme.StatusAmber
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography

@Composable
fun QuickTimeChips(
    isRunning: Boolean,
    onAddMinutes: (Int) -> Unit,
    onAdjustMinutes: (Int) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isRunning) {
            // Fila 1: Botones rápidos principales (+5, +15, +30, +60 min)
            Text(
                text = "ACCESO RÁPIDO",
                style = Typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                ),
                color = TextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                QuickPresetChip(label = "+5 min") { onAddMinutes(5) }
                QuickPresetChip(label = "+15 min") { onAddMinutes(15) }
                QuickPresetChip(label = "+30 min") { onAddMinutes(30) }
                QuickPresetChip(label = "+60 min") { onAddMinutes(60) }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fila 2: Ajuste fino (-5m, -1m, +1m, +5m) y Reset
            Text(
                text = "AJUSTE FINO",
                style = Typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                ),
                color = TextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Limpiar (Reset)
                FineAdjustmentChip(
                    onClick = onReset,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Limpiar tiempo",
                            tint = StatusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                FineAdjustmentChip(
                    text = "-5m",
                    onClick = { onAdjustMinutes(-5) }
                )

                FineAdjustmentChip(
                    text = "-1m",
                    onClick = { onAdjustMinutes(-1) }
                )

                FineAdjustmentChip(
                    text = "+1m",
                    onClick = { onAdjustMinutes(1) },
                    isAccent = true
                )

                FineAdjustmentChip(
                    text = "+5m",
                    onClick = { onAdjustMinutes(5) },
                    isAccent = true
                )
            }
        } else {
            // Cuando está corriendo: Botones para añadir tiempo directamente al vuelo
            Text(
                text = "AÑADIR TIEMPO AL VUELO",
                style = Typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                ),
                color = TextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                QuickPresetChip(label = "+5 min") { onAddMinutes(5) }
                QuickPresetChip(label = "+10 min") { onAddMinutes(10) }
                QuickPresetChip(label = "+15 min") { onAddMinutes(15) }
                QuickPresetChip(label = "+30 min") { onAddMinutes(30) }
            }
        }
    }
}

@Composable
private fun QuickPresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OledSurface)
            .border(1.dp, OledBorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = TextWhite
        )
    }
}

@Composable
private fun FineAdjustmentChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: (@Composable () -> Unit)? = null,
    isAccent: Boolean = false
) {
    val borderColor = if (isAccent) AccentCyan.copy(alpha = 0.4f) else OledBorderSubtle
    val textColor = if (isAccent) AccentCyan else TextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OledSurface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            icon()
        } else if (text != null) {
            Text(
                text = text,
                style = Typography.bodyMedium.copy(
                    fontWeight = if (isAccent) FontWeight.SemiBold else FontWeight.Normal,
                    fontFeatureSettings = "tnum"
                ),
                color = textColor
            )
        }
    }
}

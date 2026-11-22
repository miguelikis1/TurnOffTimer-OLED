package com.oled.sleeptimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oled.sleeptimer.ui.theme.AccentCyan
import com.oled.sleeptimer.ui.theme.OledBlack
import com.oled.sleeptimer.ui.theme.OledBorderSubtle
import com.oled.sleeptimer.ui.theme.OledSurface
import com.oled.sleeptimer.ui.theme.TextMuted
import com.oled.sleeptimer.ui.theme.TextSecondary
import com.oled.sleeptimer.ui.theme.TextWhite
import com.oled.sleeptimer.ui.theme.Typography

@Composable
fun KeepScreenOnToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (enabled) AccentCyan.copy(alpha = 0.35f) else OledBorderSubtle

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OledSurface)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onToggle(!enabled) }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.LightMode,
                contentDescription = null,
                tint = if (enabled) AccentCyan else TextMuted,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Mantener pantalla encendida",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = TextWhite
                )
                Text(
                    text = "Evita el bloqueo hasta que el temporizador llegue a 00:00",
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OledBlack,
                    checkedTrackColor = AccentCyan,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = Color(0xFF1F1F24),
                    uncheckedBorderColor = OledBorderSubtle
                )
            )
        }
    }
}

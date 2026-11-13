package com.agar.commander.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agar.commander.model.AlertSeverity
import com.agar.commander.model.RobotAlert
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun WarningsDialog(
    alerts: List<RobotAlert>,
    onDismiss: () -> Unit,
    onOpenDiagnostics: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (alerts.isEmpty()) Icons.Filled.Info else Icons.Filled.Warning,
                contentDescription = null,
                tint = if (alerts.isEmpty()) AgarGreen else AgarAmber,
            )
        },
        title = {
            Text(
                text = if (alerts.isEmpty()) "No active warnings" else "Active warnings (${alerts.size})",
            )
        },
        text = {
            if (alerts.isEmpty()) {
                Text(
                    text = "The robot has not reported any active alerts.",
                    color = AgarTextMuted,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 310.dp),
                ) {
                    itemsIndexed(alerts) { index, alert ->
                        WarningRow(alert)
                        if (index < alerts.lastIndex) {
                            HorizontalDivider(color = AgarBorder)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenDiagnostics) {
                Icon(
                    imageVector = Icons.Filled.MonitorHeart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text("Open diagnostics")
            }
        },
    )
}

@Composable
private fun WarningRow(alert: RobotAlert) {
    val style = alert.severity.warningStyle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = alert.severity.label,
            tint = style.color,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = alert.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = alert.severity.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = style.color,
                )
            }
            Text(
                text = alert.code,
                style = MaterialTheme.typography.labelMedium,
                color = AgarTextMuted,
            )
            Text(
                text = alert.message,
                modifier = Modifier.padding(top = 3.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "${alert.source}  •  ${alert.occurredAt}",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal,
                color = AgarTextMuted,
            )
        }
    }
}

private data class WarningStyle(
    val icon: ImageVector,
    val color: Color,
)

private fun AlertSeverity.warningStyle(): WarningStyle = when (this) {
    AlertSeverity.Info -> WarningStyle(Icons.Filled.Info, AgarGreen)
    AlertSeverity.Warning -> WarningStyle(Icons.Filled.Warning, AgarAmber)
    AlertSeverity.Error -> WarningStyle(Icons.Filled.Error, AgarRed)
}

package com.agar.commander.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agar.commander.model.AlertSeverity
import com.agar.commander.model.RobotAlert
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

private data class DiagnosticPreview(
    val name: String,
    val detail: String,
    val healthy: Boolean,
)

private val PreviewDiagnostics = listOf(
    DiagnosticPreview("Raspberry Pi control", "ROS1 and motor control available", true),
    DiagnosticPreview("Jetson compute", "System ready • 41 nodes", true),
    DiagnosticPreview("GPS / RTK", "RTK fix • age 0.2 s", true),
    DiagnosticPreview("ZED X odometry", "30.0 Hz • tracking OK", true),
    DiagnosticPreview("Velodyne LiDAR", "9.9 Hz • scan input OK", true),
    DiagnosticPreview("SLAM engine", "Inactive in GPS mode", true),
    DiagnosticPreview("PTO telemetry", "Good • age 0.4 s", true),
    DiagnosticPreview("Localization guard", "Not evaluated in GPS mode", false),
)

@Composable
fun DiagnosticsScreen(
    alerts: List<RobotAlert>,
    onAction: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        SectionHeader(
            title = "Diagnostics",
            subtitle = "Robot health, SLAM monitoring and Medkit faults",
            action = {
                SecondaryAction(
                    label = "Refresh",
                    icon = Icons.Filled.Refresh,
                    onClick = { onAction("Diagnostics refresh preview") },
                )
            },
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("SYSTEM HEALTH", style = MaterialTheme.typography.labelLarge, color = AgarTextMuted)
                        Spacer(Modifier.weight(1f))
                        Text("7 healthy  •  1 note", style = MaterialTheme.typography.labelLarge, color = AgarGreen)
                    }
                    HorizontalDivider(color = AgarBorder)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(PreviewDiagnostics) { diagnostic ->
                            DiagnosticRow(diagnostic)
                            HorizontalDivider(color = AgarBorder.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Panel(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        SectionHeader(
                            title = "Active faults",
                            subtitle = "${alerts.size} active from Medkit and system health",
                            action = {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = if (alerts.any { it.severity == AlertSeverity.Error }) {
                                        AgarRed
                                    } else {
                                        AgarAmber
                                    },
                                )
                            },
                        )
                        Spacer(Modifier.height(6.dp))
                        if (alerts.isEmpty()) {
                            Text(
                                "No active faults",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgarGreen,
                            )
                        } else {
                            alerts.forEach { alert ->
                                FaultRow(
                                    label = alert.code,
                                    state = alert.severity.label,
                                    color = alert.severity.diagnosticColor(),
                                )
                            }
                        }
                    }
                }
                Panel(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        SectionHeader(
                            title = "Recent events",
                            subtitle = "Latest robot notifications",
                        )
                        Spacer(Modifier.height(4.dp))
                        EventRow("09:48:12", "Robot connection unavailable in preview")
                        EventRow("09:47:58", "UI workspace initialized")
                        EventRow("09:47:57", "Last connection profile loaded")
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Detailed logs and timestamps will come from the robot and AgarViz gateway.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgarTextMuted,
                        )
                    }
                }
            }
        }
    }
}

private fun AlertSeverity.diagnosticColor(): Color = when (this) {
    AlertSeverity.Info -> AgarGreen
    AlertSeverity.Warning -> AgarAmber
    AlertSeverity.Error -> AgarRed
}

@Composable
private fun DiagnosticRow(item: DiagnosticPreview) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (item.healthy) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (item.healthy) AgarGreen else AgarAmber,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.detail, style = MaterialTheme.typography.bodyMedium, color = AgarTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(
            if (item.healthy) "OK" else "INFO",
            style = MaterialTheme.typography.labelLarge,
            color = if (item.healthy) AgarGreen else AgarAmber,
        )
    }
}

@Composable
private fun FaultRow(label: String, state: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(9.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(state, color = color, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun EventRow(time: String, message: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(time, style = MaterialTheme.typography.labelMedium, color = AgarTextMuted)
        Spacer(Modifier.width(10.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

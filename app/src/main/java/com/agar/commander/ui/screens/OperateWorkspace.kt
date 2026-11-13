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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.agar.commander.ui.components.FieldMap
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun OperateScreen(onAction: (String) -> Unit) {
    var missionState by remember { mutableStateOf(MissionRunState.Ready) }
    var showManualDrive by remember { mutableStateOf(false) }

    if (showManualDrive) {
        ManualDriveDialog(
            onDismiss = { showManualDrive = false },
            onAction = onAction,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(
                modifier = Modifier
                    .weight(0.78f)
                    .fillMaxHeight(),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    SectionHeader(
                        title = "Mission control",
                        subtitle = missionState.label,
                        action = {
                            MissionStateBadge(
                                label = missionState.badge,
                                color = missionState.color,
                            )
                        },
                    )

                    Text("North orchard 04", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { 18f / 64f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = AgarGreen,
                        trackColor = AgarSurfaceRaised,
                    )
                    LabeledValueRow("Mission progress", "Waypoint 18 of 64")
                    LabeledValueRow("Remaining route", "3.2 km")
                    LabeledValueRow("Position source", "RTK FIX", valueColor = AgarGreen)

                    HorizontalDivider(color = AgarBorder)

                    PrimaryAction(
                        label = when (missionState) {
                            MissionRunState.Running -> "Mission running"
                            MissionRunState.Paused -> "Resume mission"
                            MissionRunState.Ready -> "Start mission"
                        },
                        icon = Icons.Filled.PlayArrow,
                        onClick = {
                            missionState = MissionRunState.Running
                            onAction("Mission start preview")
                        },
                        enabled = missionState != MissionRunState.Running,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        SecondaryAction(
                            label = "Pause",
                            icon = Icons.Filled.Pause,
                            onClick = {
                                missionState = MissionRunState.Paused
                                onAction("Mission pause preview")
                            },
                            enabled = missionState == MissionRunState.Running,
                            modifier = Modifier.weight(1f),
                            contentColor = AgarAmber,
                        )
                        SecondaryAction(
                            label = "Stop",
                            icon = Icons.Filled.Stop,
                            onClick = {
                                missionState = MissionRunState.Ready
                                onAction("Mission stop preview")
                            },
                            enabled = missionState != MissionRunState.Ready,
                            modifier = Modifier.weight(1f),
                            contentColor = AgarRed,
                        )
                    }

                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(color = AgarBorder)
                    SecondaryAction(
                        label = "Open manual drive",
                        icon = Icons.Filled.SportsEsports,
                        onClick = { showManualDrive = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Panel(
                modifier = Modifier
                    .weight(1.22f)
                    .fillMaxHeight(),
            ) {
                FieldMap(
                    modifier = Modifier.fillMaxSize(),
                    label = "North orchard 04 • live route",
                )
            }
        }
    }
}

@Composable
private fun MissionStateBadge(label: String, color: Color) {
    Row(
        modifier = Modifier
            .background(AgarSurfaceRaised, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, MaterialTheme.shapes.small),
        )
        Spacer(Modifier.size(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
private fun ManualDriveDialog(
    onDismiss: () -> Unit,
    onAction: (String) -> Unit,
) {
    var joystickSensitivity by remember { mutableFloatStateOf(60f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manual drive") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "App controls use robot-confirmed limits. The physical MK32 controller remains available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgarTextMuted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("Drive", style = MaterialTheme.typography.labelLarge)
                        SecondaryAction(
                            "Forward",
                            Icons.Filled.ArrowUpward,
                            { onAction("Manual forward preview") },
                            Modifier.fillMaxWidth(),
                        )
                        SecondaryAction(
                            "Reverse",
                            Icons.Filled.ArrowDownward,
                            { onAction("Manual reverse preview") },
                            Modifier.fillMaxWidth(),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("Steer", style = MaterialTheme.typography.labelLarge)
                        SecondaryAction(
                            "Left",
                            Icons.AutoMirrored.Filled.ArrowBack,
                            { onAction("Manual turn left preview") },
                            Modifier.fillMaxWidth(),
                        )
                        SecondaryAction(
                            "Right",
                            Icons.AutoMirrored.Filled.ArrowForward,
                            { onAction("Manual turn right preview") },
                            Modifier.fillMaxWidth(),
                        )
                    }
                }
                HorizontalDivider(color = AgarBorder)
                LabeledValueRow("Maximum speed", "45%")
                LabeledValueRow("Maximum turn rate", "55%")
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Joystick sensitivity", modifier = Modifier.weight(1f))
                    Text("${joystickSensitivity.roundToInt()}%", color = AgarGreen)
                }
                Slider(
                    value = joystickSensitivity,
                    onValueChange = { joystickSensitivity = it },
                    onValueChangeFinished = {
                        onAction("Joystick sensitivity ${joystickSensitivity.roundToInt()}% preview")
                    },
                    valueRange = 10f..100f,
                    steps = 17,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

private enum class MissionRunState(
    val label: String,
    val badge: String,
    val color: Color,
) {
    Ready("Ready to start", "READY", AgarGreen),
    Running("Mission is running", "RUNNING", AgarGreen),
    Paused("Mission is paused", "PAUSED", AgarAmber),
}

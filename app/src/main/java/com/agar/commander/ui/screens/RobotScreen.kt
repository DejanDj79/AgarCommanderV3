package com.agar.commander.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agar.commander.model.ActuatorReading
import com.agar.commander.model.PreviewActuators
import com.agar.commander.model.RobotWorkspace
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun RobotScreen(onAction: (String) -> Unit) {
    var workspace by remember { mutableStateOf(RobotWorkspace.Actuators) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeader(
                title = "Robot",
                subtitle = "Chassis height, attachment mode and safety interlocks",
                modifier = Modifier.weight(1f),
            )
            SegmentedSelector(
                items = RobotWorkspace.entries,
                selected = workspace,
                label = { it.label },
                onSelected = { workspace = it },
                modifier = Modifier.width(230.dp),
            )
        }
        Spacer(Modifier.height(6.dp))

        when (workspace) {
            RobotWorkspace.Actuators -> ActuatorsWorkspace(onAction)
            RobotWorkspace.Safety -> SafetyWorkspace(onAction)
        }
    }
}

@Composable
private fun ActuatorsWorkspace(onAction: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Panel(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Shield, contentDescription = null, tint = AgarGreen)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Safe to drive", style = MaterialTheme.typography.titleMedium, color = AgarGreen)
                    Text(
                        "Height lock enabled • all actuators below 248 mm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgarTextMuted,
                    )
                }
                Text("Override off", style = MaterialTheme.typography.labelLarge, color = AgarTextMuted)
            }
        }

        Row(
            modifier = Modifier.height(88.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PreviewActuators.forEach { reading ->
                ActuatorTile(reading, Modifier.weight(1f), onAction)
            }
        }

        Panel(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                SectionHeader(
                    title = "Height controls",
                    subtitle = "Preset positions apply to all four actuators",
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SecondaryAction("Drive", Icons.Filled.DirectionsCar, { onAction("Drive height preview") }, Modifier.weight(1f))
                    SecondaryAction("Center", Icons.Filled.CenterFocusStrong, { onAction("Center height preview") }, Modifier.weight(1f))
                    SecondaryAction("Up", Icons.Filled.ArrowUpward, { onAction("Robot up preview") }, Modifier.weight(1f))
                    SecondaryAction("Down", Icons.Filled.ArrowDownward, { onAction("Robot down preview") }, Modifier.weight(1f))
                    PrimaryAction("Send", Icons.Filled.PowerSettingsNew, { onAction("Send actuator targets preview") }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SecondaryAction("Hold up", Icons.Filled.ArrowUpward, { onAction("Hold actuator up preview") }, Modifier.weight(1f))
                    SecondaryAction("Hold down", Icons.Filled.ArrowDownward, { onAction("Hold actuator down preview") }, Modifier.weight(1f))
                    SecondaryAction("Initialize", Icons.Filled.RestartAlt, { onAction("Actuator initialization preview") }, Modifier.weight(1f), contentColor = AgarAmber)
                    SecondaryAction("Attachment", Icons.Filled.LockOpen, { onAction("Attachment mode preview") }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ActuatorTile(
    reading: ActuatorReading,
    modifier: Modifier,
    onAction: (String) -> Unit,
) {
    Panel(modifier = modifier.fillMaxHeight().clickable { onAction("Edit ${reading.name} target preview") }) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(reading.name, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.weight(1f))
            Text("${reading.currentMm} mm", style = MaterialTheme.typography.titleLarge, color = AgarGreen)
            Text("Target ${reading.targetMm} mm", style = MaterialTheme.typography.labelMedium, color = AgarTextMuted)
        }
    }
}

@Composable
private fun SafetyWorkspace(onAction: (String) -> Unit) {
    var heightLockEnabled by remember { mutableStateOf(true) }
    var showOverrideDialog by remember { mutableStateOf(false) }

    if (showOverrideDialog) {
        AlertDialog(
            onDismissRequest = { showOverrideDialog = false },
            title = { Text("Request height-lock override?") },
            text = {
                Text(
                    "Driving above the configured actuator height is a safety bypass and must be explicitly confirmed.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOverrideDialog = false
                        onAction("Height-lock override preview")
                    },
                ) { Text("Request override", color = AgarRed) }
            },
            dismissButton = {
                TextButton(onClick = { showOverrideDialog = false }) { Text("Cancel") }
            },
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Panel(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(modifier = Modifier.padding(10.dp)) {
                SectionHeader(
                    title = "Motion interlocks",
                    subtitle = "Robot-side safety state",
                    action = {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = AgarGreen)
                    },
                )
                Spacer(Modifier.height(6.dp))
                SafetyStatus("Brake", "Active", AgarRed, Icons.Filled.Lock)
                SafetyStatus("Actuator height lock", "Ready", AgarGreen, Icons.Filled.Shield)
                SafetyStatus("PTO speed protection", "Active", AgarGreen, Icons.Filled.Shield)
                SafetyStatus("Localization guard", "GPS mode", AgarAmber, Icons.Filled.Shield)
                SafetyStatus("Command source", "MK32", AgarGreen, Icons.Filled.Shield)
                Spacer(Modifier.weight(1f))
                Text(
                    "BRAKE remains available from the global status bar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgarTextMuted,
                )
            }
        }

        Panel(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(modifier = Modifier.padding(10.dp)) {
                SectionHeader(
                    title = "Actuator height lock",
                    subtitle = if (heightLockEnabled) "Drive interlock enabled" else "Drive interlock disabled",
                    action = {
                        Switch(
                            checked = heightLockEnabled,
                            onCheckedChange = {
                                heightLockEnabled = it
                                onAction("Height lock ${if (it) "enabled" else "disabled"} preview")
                            },
                        )
                    },
                )
                Spacer(Modifier.height(6.dp))
                LabeledValueRow("Maximum drive height", "248 mm")
                LabeledValueRow("Highest actuator", "176 mm", valueColor = AgarGreen)
                LabeledValueRow("Current drive state", "Allowed", valueColor = AgarGreen)
                LabeledValueRow("Override", "Off", valueColor = AgarGreen)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = AgarBorder)
                Spacer(Modifier.height(8.dp))
                Text(
                    "When any actuator rises above the configured limit, manual and autonomous movement are blocked on the robot.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgarTextMuted,
                )
                Spacer(Modifier.weight(1f))
                SecondaryAction(
                    label = "Request override",
                    icon = Icons.Filled.LockOpen,
                    onClick = { showOverrideDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    contentColor = AgarAmber,
                )
            }
        }
    }
}

@Composable
private fun SafetyStatus(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(AgarSurfaceRaised, MaterialTheme.shapes.small)
                .padding(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(Modifier.width(10.dp))
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(value, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

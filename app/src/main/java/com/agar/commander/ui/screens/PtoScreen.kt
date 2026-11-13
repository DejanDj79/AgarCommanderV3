package com.agar.commander.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agar.commander.ui.components.EqualMetric
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun PtoScreen(onAction: (String) -> Unit) {
    var ptoRunning by remember { mutableStateOf(false) }
    var protectionEnabled by remember { mutableStateOf(true) }
    var showDisableDialog by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf("Medium") }

    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            title = { Text("Disable PTO speed protection?") },
            text = { Text("This safety bypass allows PTO load to stop reducing robot speed automatically.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        protectionEnabled = false
                        showDisableDialog = false
                        onAction("PTO protection bypass preview")
                    },
                ) { Text("Disable", color = AgarRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDisableDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        SectionHeader(
            title = "PTO",
            subtitle = "Attachment control, live telemetry and load protection",
        )
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight(),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SectionHeader(
                        title = "PTO live data",
                        subtitle = "Telemetry updated 0.4 s ago",
                        action = {
                            Text(
                                if (ptoRunning) "RUNNING" else "STOPPED",
                                color = if (ptoRunning) AgarGreen else AgarTextMuted,
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EqualMetric("TEMP", "41 °C", AgarGreen)
                        EqualMetric("RPM", if (ptoRunning) "268" else "0")
                        EqualMetric("CURRENT", if (ptoRunning) "42 A" else "0 A")
                        EqualMetric("SIGNAL", "Good", AgarGreen)
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = AgarBorder)
                    Spacer(Modifier.height(8.dp))
                    Text("PTO speed")
                    Spacer(Modifier.height(5.dp))
                    SegmentedSelector(
                        items = listOf("Low", "Medium", "High"),
                        selected = speed,
                        label = { it },
                        onSelected = {
                            speed = it
                            onAction("PTO $it speed preview")
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryAction(
                            "Start PTO",
                            Icons.Filled.PowerSettingsNew,
                            {
                                ptoRunning = true
                                onAction("Start PTO preview")
                            },
                            Modifier.weight(1f),
                            enabled = !ptoRunning,
                        )
                        SecondaryAction(
                            "Stop PTO",
                            Icons.Filled.PowerSettingsNew,
                            {
                                ptoRunning = false
                                onAction("Stop PTO preview")
                            },
                            Modifier.weight(1f),
                            enabled = ptoRunning,
                            contentColor = AgarRed,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Low 200 rpm  •  Medium 270 rpm  •  High 540 rpm",
                        color = AgarTextMuted,
                    )
                }
            }

            Panel(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight(),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SectionHeader(
                        title = "PTO speed protection",
                        subtitle = if (protectionEnabled) {
                            "Automatic load protection active"
                        } else {
                            "Safety bypass active"
                        },
                        action = {
                            Switch(
                                checked = protectionEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        protectionEnabled = true
                                        onAction("PTO protection enabled preview")
                                    } else {
                                        showDisableDialog = true
                                    }
                                },
                            )
                        },
                    )
                    Spacer(Modifier.height(6.dp))
                    LabeledValueRow(
                        "Protection state",
                        if (protectionEnabled) "Active" else "Bypassed",
                        valueColor = if (protectionEnabled) AgarGreen else AgarRed,
                    )
                    LabeledValueRow("Current speed factor", "92%")
                    LabeledValueRow("Derate reason", "PTO current load")
                    LabeledValueRow("Telemetry timeout", "No", valueColor = AgarGreen)
                    LabeledValueRow("Hard stop", "No", valueColor = AgarGreen)
                    Spacer(Modifier.weight(1f))
                    Text(
                        if (protectionEnabled) {
                            "The shared drive limit is reduced automatically when PTO load increases."
                        } else {
                            "Protection bypass remains visible until protection is enabled again."
                        },
                        color = if (protectionEnabled) AgarTextMuted else AgarRed,
                    )
                }
            }
        }
    }
}

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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.agar.commander.model.BuzzerMode
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun CommandsScreen(onAction: (String) -> Unit) {
    var maximumSpeed by remember { mutableFloatStateOf(45f) }
    var maximumTurnRate by remember { mutableFloatStateOf(55f) }
    var lightsEnabled by remember { mutableStateOf(false) }
    var buzzerMode by remember { mutableStateOf(BuzzerMode.Reverse) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        SectionHeader(
            title = "Commands",
            subtitle = "Shared drive limits and direct robot commands",
        )
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(
                modifier = Modifier
                    .weight(1.08f)
                    .fillMaxHeight(),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SectionHeader(
                        title = "Drive limits",
                        subtitle = "Applied to manual and autonomous driving",
                        action = {
                            Icon(Icons.Filled.Speed, contentDescription = null, tint = AgarGreen)
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    LimitSlider(
                        label = "Maximum speed",
                        value = maximumSpeed,
                        onValueChange = { maximumSpeed = it },
                        onValueChangeFinished = {
                            onAction("Maximum speed ${maximumSpeed.roundToInt()}% preview")
                        },
                    )
                    LimitSlider(
                        label = "Maximum turn rate",
                        value = maximumTurnRate,
                        onValueChange = { maximumTurnRate = it },
                        onValueChangeFinished = {
                            onAction("Maximum turn rate ${maximumTurnRate.roundToInt()}% preview")
                        },
                    )
                    HorizontalDivider(color = AgarBorder)
                    LabeledValueRow("Active input", "MK32", valueColor = AgarGreen)
                    LabeledValueRow("Physical speed buttons", "Synchronized", valueColor = AgarGreen)
                    LabeledValueRow("Autonomous speed cap", "Enabled", valueColor = AgarGreen)
                    LabeledValueRow("Robot confirmation", "Required", valueColor = AgarAmber)
                }
            }

            Panel(
                modifier = Modifier
                    .weight(0.92f)
                    .fillMaxHeight(),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SectionHeader(
                        title = "Robot commands",
                        subtitle = "Auxiliary controls",
                    )
                    Spacer(Modifier.height(7.dp))
                    CommandSwitch(
                        title = "Work lights",
                        subtitle = if (lightsEnabled) "Lights are on" else "Lights are off",
                        checked = lightsEnabled,
                        icon = { Icon(Icons.Filled.Lightbulb, null, tint = if (lightsEnabled) AgarAmber else AgarTextMuted) },
                        onCheckedChange = {
                            lightsEnabled = it
                            onAction("Lights ${if (it) "on" else "off"} preview")
                        },
                    )
                    HorizontalDivider(color = AgarBorder)
                    SectionHeader(
                        title = "Movement buzzer",
                        subtitle = "Audible warning by driving direction",
                        action = {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = AgarGreen)
                        },
                    )
                    Spacer(Modifier.height(6.dp))
                    SegmentedSelector(
                        items = BuzzerMode.entries,
                        selected = buzzerMode,
                        label = { it.label },
                        onSelected = {
                            buzzerMode = it
                            onAction("Buzzer ${it.label} preview")
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    LabeledValueRow("Buzzer server", "Available", valueColor = AgarGreen)
                    LabeledValueRow("Applied mode", buzzerMode.label, valueColor = AgarGreen)
                }
            }
        }
    }
}

@Composable
private fun LimitSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Text("${value.roundToInt()}%", color = AgarGreen, style = MaterialTheme.typography.titleMedium)
    }
    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = 10f..100f,
        steps = 17,
    )
}

@Composable
private fun CommandSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    icon: @Composable () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
    ) {
        icon()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 9.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = AgarTextMuted)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

package com.agar.commander.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agar.commander.model.SlamMode
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

private data class SlamMapPreview(
    val name: String,
    val profile: String,
    val complete: Boolean,
)

private val PreviewMaps = listOf(
    SlamMapPreview("orchard_north_v3", "zed_constrained", true),
    SlamMapPreview("field_test_01", "zed_constrained", true),
    SlamMapPreview("yard_route", "standard", false),
)

@Composable
fun MapsSlamScreen(onAction: (String) -> Unit) {
    var selectedMode by remember { mutableStateOf(SlamMode.Gps) }
    var selectedMap by remember { mutableStateOf(PreviewMaps.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeader(
                title = "Maps & SLAM",
                subtitle = "Mapping, localization and autonomy readiness",
                modifier = Modifier.weight(1f),
            )
            SegmentedSelector(
                items = SlamMode.entries,
                selected = selectedMode,
                label = { it.label },
                onSelected = {
                    selectedMode = it
                    onAction("SLAM ${it.label} preview only")
                },
                enabled = { it != SlamMode.Autonomous },
                modifier = Modifier.weight(2.35f),
            )
        }
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight(),
            ) {
                Column {
                    SectionHeader(
                        title = "Robot maps",
                        subtitle = "Active: orchard_north_v3",
                        action = {
                            SecondaryAction(
                                label = "Refresh",
                                icon = Icons.Filled.Refresh,
                                onClick = { onAction("Map refresh preview") },
                            )
                        },
                        modifier = Modifier.padding(10.dp),
                    )
                    HorizontalDivider(color = AgarBorder)
                    PreviewMaps.forEach { map ->
                        val selected = map == selectedMap
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selected) AgarSurfaceRaised else MaterialTheme.colorScheme.surface)
                                .clickable { selectedMap = map }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = null,
                                tint = if (selected) AgarGreen else AgarTextMuted,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    map.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) AgarGreen else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(map.profile, style = MaterialTheme.typography.labelMedium, color = AgarTextMuted)
                            }
                            Icon(
                                imageVector = if (map.complete) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                contentDescription = if (map.complete) "Complete map" else "Incomplete map",
                                tint = if (map.complete) AgarGreen else AgarAmber,
                            )
                        }
                        HorizontalDivider(color = AgarBorder.copy(alpha = 0.6f))
                    }
                    Spacer(Modifier.weight(1f))
                    Column(
                        modifier = Modifier.padding(10.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PrimaryAction(
                                label = "New map",
                                icon = Icons.Filled.Add,
                                onClick = { onAction("New mapping preview") },
                                modifier = Modifier.weight(1f),
                            )
                            SecondaryAction(
                                label = "Save",
                                icon = Icons.Filled.Save,
                                onClick = { onAction("Save map preview") },
                                modifier = Modifier.weight(1f),
                            )
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
                            title = selectedMap.name,
                            subtitle = "Selected map bundle",
                            action = {
                                PrimaryAction(
                                    label = "Activate map",
                                    icon = Icons.Filled.PlayArrow,
                                    onClick = { onAction("Activate ${selectedMap.name} preview") },
                                    enabled = selectedMap.complete,
                                )
                            },
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                LabeledValueRow(
                                    "Bundle",
                                    if (selectedMap.complete) "Complete" else "Incomplete",
                                    valueColor = if (selectedMap.complete) AgarGreen else AgarAmber,
                                )
                                LabeledValueRow("Mapping profile", selectedMap.profile)
                                LabeledValueRow("Resolution", "0.05 m")
                            }
                            Column(Modifier.weight(1f)) {
                                LabeledValueRow("Occupancy map", "Available", valueColor = AgarGreen)
                                LabeledValueRow(
                                    "Pose graph",
                                    if (selectedMap.complete) "Available" else "Missing",
                                    valueColor = if (selectedMap.complete) AgarGreen else AgarAmber,
                                )
                                LabeledValueRow("Created", "21 Oct 2026, 12:40")
                            }
                        }
                    }
                }

                Panel(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        SectionHeader(
                            title = "Localization readiness",
                            subtitle = "Runtime health gates",
                        )
                        Spacer(Modifier.height(7.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            HealthBlock("LIDAR", "Ready", true, Modifier.weight(1f))
                            HealthBlock("ZED", "Ready", true, Modifier.weight(1f))
                            HealthBlock("IMU", "Ready", true, Modifier.weight(1f))
                            HealthBlock("TF", "Ready", true, Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            HealthBlock("MAP", "Active", true, Modifier.weight(1f))
                            HealthBlock("LOCAL", "Idle", false, Modifier.weight(1f))
                            HealthBlock("COSTMAP", "Off", false, Modifier.weight(1f))
                            HealthBlock("AUTONOMY", "Blocked", false, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthBlock(
    label: String,
    value: String,
    healthy: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(AgarSurfaceRaised, MaterialTheme.shapes.small)
            .padding(7.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = AgarTextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            style = MaterialTheme.typography.labelLarge,
            color = if (healthy) AgarGreen else AgarAmber,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

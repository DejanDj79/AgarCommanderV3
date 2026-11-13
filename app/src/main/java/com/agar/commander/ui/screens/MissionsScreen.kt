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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agar.commander.model.MissionWorkspace
import com.agar.commander.ui.components.FieldMap
import com.agar.commander.ui.components.EqualMetric
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SecondaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

private data class MissionPreview(
    val name: String,
    val waypoints: Int,
    val visited: Int,
    val map: String,
)

private val PreviewMissions = listOf(
    MissionPreview("North orchard 04", 64, 18, "orchard_north_v3"),
    MissionPreview("Garage to field", 19, 19, "yard_route"),
    MissionPreview("Vineyard row test", 42, 0, "field_test_01"),
    MissionPreview("South boundary", 31, 0, "No SLAM map"),
)

@Composable
fun MissionsScreen(onAction: (String) -> Unit) {
    var workspace by remember { mutableStateOf(MissionWorkspace.Library) }
    var selectedMission by remember { mutableStateOf(PreviewMissions.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeader(
                title = "Missions",
                subtitle = "Load, review and edit robot routes",
                modifier = Modifier.weight(1f),
            )
            SegmentedSelector(
                items = MissionWorkspace.entries,
                selected = workspace,
                label = { it.label },
                onSelected = { workspace = it },
                modifier = Modifier.width(240.dp),
            )
        }
        Spacer(Modifier.height(6.dp))

        when (workspace) {
            MissionWorkspace.Library -> MissionLibrary(
                selectedMission = selectedMission,
                onSelected = { selectedMission = it },
                onAction = onAction,
            )
            MissionWorkspace.Editor -> MissionEditor(onAction)
        }
    }
}

@Composable
private fun MissionLibrary(
    selectedMission: MissionPreview,
    onSelected: (MissionPreview) -> Unit,
    onAction: (String) -> Unit,
) {
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
                    title = "Mission library",
                    subtitle = "${PreviewMissions.size} missions on robot",
                    action = {
                        SecondaryAction(
                            label = "Refresh",
                            icon = Icons.Filled.Refresh,
                            onClick = { onAction("Mission refresh preview") },
                        )
                    },
                    modifier = Modifier.padding(10.dp),
                )
                HorizontalDivider(color = AgarBorder)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(PreviewMissions) { index, mission ->
                        val selected = mission == selectedMission
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selected) AgarSurfaceRaised else MaterialTheme.colorScheme.surface)
                                .clickable { onSelected(mission) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                        ) {
                            Text(
                                mission.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selected) AgarGreen else MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "${mission.waypoints} waypoints  •  ${mission.visited} visited",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgarTextMuted,
                            )
                        }
                        if (index < PreviewMissions.lastIndex) {
                            HorizontalDivider(color = AgarBorder.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }

        Panel(modifier = Modifier.weight(1.05f).fillMaxHeight()) {
            Column(modifier = Modifier.padding(10.dp)) {
                SectionHeader(
                    title = selectedMission.name,
                    subtitle = "Mission details",
                )
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    FieldMap(modifier = Modifier.fillMaxSize(), label = "Mission route preview")
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EqualMetric("TOTAL", selectedMission.waypoints.toString())
                    EqualMetric("VISITED", selectedMission.visited.toString())
                    EqualMetric("SLAM MAP", selectedMission.map)
                    EqualMetric("MODIFIED", "Today 08:42")
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryAction(
                        label = "Load mission",
                        icon = Icons.Filled.FileOpen,
                        onClick = { onAction("Load ${selectedMission.name} preview") },
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryAction(
                        label = "Reset visited",
                        icon = Icons.Filled.RestartAlt,
                        onClick = { onAction("Reset visited preview") },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionEditor(onAction: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Panel(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                SectionHeader(
                    title = "Route editor",
                    subtitle = "6 unsaved waypoints",
                )
                Spacer(Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed((1..6).toList()) { index, waypoint ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (index == 2) AgarSurfaceRaised else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(4.dp),
                                )
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Waypoint ${waypoint.toString().padStart(2, '0')}")
                            Text("44.82${waypoint}°, 20.41${waypoint}°", color = AgarTextMuted)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    SecondaryAction(
                        label = "Add",
                        icon = Icons.Filled.AddLocationAlt,
                        onClick = { onAction("Add waypoint preview") },
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryAction(
                        label = "Reverse",
                        icon = Icons.Filled.SwapVert,
                        onClick = { onAction("Reverse route preview") },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    SecondaryAction(
                        label = "Load",
                        icon = Icons.Filled.FileOpen,
                        onClick = { onAction("Editor load preview") },
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryAction(
                        label = "Save",
                        icon = Icons.Filled.Save,
                        onClick = { onAction("Mission save preview") },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Panel(modifier = Modifier.weight(1.05f).fillMaxHeight()) {
            FieldMap(modifier = Modifier.fillMaxSize(), label = "New mission • unsaved")
        }
    }
}

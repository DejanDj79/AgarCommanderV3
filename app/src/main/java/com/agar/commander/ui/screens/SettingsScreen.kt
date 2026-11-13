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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agar.commander.BuildConfig
import com.agar.commander.model.ConnectionProfile
import com.agar.commander.model.ConnectionState
import com.agar.commander.ui.components.LabeledValueRow
import com.agar.commander.ui.components.Panel
import com.agar.commander.ui.components.PrimaryAction
import com.agar.commander.ui.components.SectionHeader
import com.agar.commander.ui.components.SegmentedSelector
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun SettingsScreen(
    profile: ConnectionProfile,
    connectionState: ConnectionState,
    onConnectionStateChange: (ConnectionState) -> Unit,
    onSaveProfile: (ConnectionProfile) -> Unit,
    onAction: (String) -> Unit,
) {
    var host by rememberSaveable(profile.host) { mutableStateOf(profile.host) }
    var port by rememberSaveable(profile.port) { mutableStateOf(profile.port.toString()) }
    var autoConnect by rememberSaveable(profile.autoConnect) {
        mutableStateOf(profile.autoConnect)
    }
    val draftProfile = ConnectionProfile(
        host = host,
        port = port.toIntOrNull() ?: 0,
        autoConnect = autoConnect,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        SectionHeader(
            title = "Settings",
            subtitle = "Connection profiles and application preferences",
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Panel(modifier = Modifier.weight(1.08f).fillMaxHeight()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    SectionHeader(
                        title = "Local control",
                        subtitle = "Direct ROSBridge connection on robot Wi-Fi",
                        action = {
                            androidx.compose.material3.Icon(
                                Icons.Filled.SettingsEthernet,
                                contentDescription = null,
                                tint = AgarGreen,
                            )
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = host,
                            onValueChange = { host = it },
                            label = { Text("Robot host") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = port,
                            onValueChange = { port = it.filter(Char::isDigit) },
                            label = { Text("Port") },
                            singleLine = true,
                            modifier = Modifier.width(86.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Connect on startup", style = MaterialTheme.typography.titleMedium)
                            Text("Continue into the app if the robot is unavailable", style = MaterialTheme.typography.bodyMedium, color = AgarTextMuted)
                        }
                        Switch(checked = autoConnect, onCheckedChange = { autoConnect = it })
                    }
                    Spacer(Modifier.height(8.dp))
                    PrimaryAction(
                        label = "Save connection profile",
                        icon = Icons.Filled.Save,
                        onClick = {
                            onSaveProfile(draftProfile)
                            onAction("Connection profile saved locally")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = draftProfile.isValid,
                    )
                    Spacer(Modifier.height(8.dp))
                    LabeledValueRow("Endpoint", "ws://${host.trim()}:$port")
                    LabeledValueRow("Profile", "Local Control")
                    LabeledValueRow("Remote driving", "Blocked")
                }
            }

            Column(
                modifier = Modifier.weight(0.92f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (BuildConfig.DEBUG) {
                    Panel(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            SectionHeader(
                                title = "UI preview state",
                                subtitle = "Debug build only • no robot commands",
                            )
                            Spacer(Modifier.height(6.dp))
                            SegmentedSelector(
                                items = ConnectionState.entries,
                                selected = connectionState,
                                label = {
                                    when (it) {
                                        ConnectionState.Connecting -> "Linking"
                                        ConnectionState.Warning -> "Warn"
                                        else -> it.label
                                    }
                                },
                                onSelected = onConnectionStateChange,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                Panel(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        SectionHeader(
                            title = "Application",
                            subtitle = "Current build information",
                        )
                        Spacer(Modifier.height(6.dp))
                        LabeledValueRow("Version", "${BuildConfig.VERSION_NAME}-preview")
                        LabeledValueRow("Platform", "Android 9+ / landscape")
                        LabeledValueRow("Transport", "Mock")
                        LabeledValueRow("Robot commands", "Disabled")
                        Spacer(Modifier.weight(1f))
                        Text(
                            "VPN Monitor and Service profiles will be enabled after their robot-side safety contracts are complete.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgarTextMuted,
                        )
                    }
                }
            }
        }
    }
}

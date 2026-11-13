package com.agar.commander.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agar.commander.model.AppDestination
import com.agar.commander.model.ConnectionState
import com.agar.commander.model.GlobalRobotStatus
import com.agar.commander.ui.theme.AgarAmber
import com.agar.commander.ui.theme.AgarBackground
import com.agar.commander.ui.theme.AgarBorder
import com.agar.commander.ui.theme.AgarGreen
import com.agar.commander.ui.theme.AgarRed
import com.agar.commander.ui.theme.AgarSurface
import com.agar.commander.ui.theme.AgarSurfaceRaised
import com.agar.commander.ui.theme.AgarTextMuted

@Composable
fun AppShell(
    destination: AppDestination,
    status: GlobalRobotStatus,
    onDestinationSelected: (AppDestination) -> Unit,
    onBrakeClick: () -> Unit,
    onWarningsClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(AgarBackground),
    ) {
        AppNavigationRail(
            destination = destination,
            onDestinationSelected = onDestinationSelected,
        )
        VerticalDivider(color = AgarBorder)
        Column(modifier = Modifier.weight(1f)) {
            GlobalStatusBar(
                status = status,
                onBrakeClick = onBrakeClick,
                onWarningsClick = onWarningsClick,
            )
            HorizontalDivider(color = AgarBorder)
            if (destination != AppDestination.Operate) {
                PreviewModeBanner(status.connection)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AgarBackground),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AppNavigationRail(
    destination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
) {
    NavigationRail(
        modifier = Modifier
            .width(86.dp)
            .fillMaxHeight(),
        containerColor = AgarSurface,
    ) {
        Spacer(Modifier.height(5.dp))
        val mainDestinations = listOf(
            AppDestination.Operate,
            AppDestination.Missions,
            AppDestination.Maps,
            AppDestination.Pto,
            AppDestination.Robot,
            AppDestination.Commands,
            AppDestination.Diagnostics,
        )

        mainDestinations.forEach { item ->
            RailDestination(
                destination = item,
                selected = destination == item,
                onClick = { onDestinationSelected(item) },
            )
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 18.dp),
            color = AgarBorder,
        )
        Spacer(Modifier.height(5.dp))
        RailDestination(
            destination = AppDestination.Settings,
            selected = destination == AppDestination.Settings,
            onClick = { onDestinationSelected(AppDestination.Settings) },
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RailDestination(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) AgarGreen else AgarTextMuted
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.small,
        color = if (selected) AgarSurfaceRaised else Color.Transparent,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = destinationIcon(destination),
                contentDescription = destination.label,
                modifier = Modifier.size(18.dp),
                tint = contentColor,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = destination.label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

private fun destinationIcon(destination: AppDestination): ImageVector = when (destination) {
    AppDestination.Operate -> Icons.Outlined.Route
    AppDestination.Missions -> Icons.AutoMirrored.Outlined.ListAlt
    AppDestination.Maps -> Icons.Outlined.Map
    AppDestination.Pto -> Icons.Filled.PowerSettingsNew
    AppDestination.Robot -> Icons.Outlined.Build
    AppDestination.Commands -> Icons.Outlined.Tune
    AppDestination.Diagnostics -> Icons.Outlined.MonitorHeart
    AppDestination.Settings -> Icons.Filled.Settings
}

@Composable
private fun GlobalStatusBar(
    status: GlobalRobotStatus,
    onBrakeClick: () -> Unit,
    onWarningsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(AgarSurface)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        StatusPill(
            label = "ROBOT",
            value = status.connection.label,
            color = connectionColor(status.connection),
            modifier = Modifier.width(72.dp),
            icon = Icons.Filled.Wifi,
        )
        StatusPill(
            label = "POSITION",
            value = status.gpsStatus,
            color = AgarGreen,
            modifier = Modifier.width(86.dp),
            icon = Icons.Filled.GpsFixed,
        )
        StatusPill(
            label = "MODE",
            value = status.operatingMode,
            color = AgarGreen,
            modifier = Modifier.width(58.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp),
        ) {
            Text(
                text = "ACTIVE MISSION",
                style = MaterialTheme.typography.labelMedium,
                color = AgarTextMuted,
            )
            Text(
                text = status.missionName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        StatusPill(
            label = "BATT",
            value = "${status.batteryPercent}%",
            color = AgarGreen,
            modifier = Modifier.width(66.dp),
            icon = Icons.Filled.BatteryFull,
        )
        StatusPill(
            label = "WARN",
            value = status.alertCount.toString(),
            color = if (status.alertCount > 0) AgarAmber else AgarGreen,
            modifier = Modifier.width(58.dp),
            icon = Icons.Filled.Warning,
            onClick = onWarningsClick,
        )
        Surface(
            onClick = onBrakeClick,
            modifier = Modifier
                .width(80.dp)
                .height(44.dp),
            color = if (status.brakeActive) AgarRed.copy(alpha = 0.2f) else AgarSurfaceRaised,
            shape = MaterialTheme.shapes.small,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Brake",
                    tint = if (status.brakeActive) AgarRed else AgarTextMuted,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(4.dp))
                Column {
                    Text("BRAKE", style = MaterialTheme.typography.labelMedium, color = AgarTextMuted)
                    Text(
                        if (status.brakeActive) "ON" else "OFF",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (status.brakeActive) AgarRed else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewModeBanner(connectionState: ConnectionState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(AgarSurfaceRaised)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(7.dp)
                .background(AgarAmber, MaterialTheme.shapes.small),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "UI PREVIEW  •  Robot commands are not sent",
            style = MaterialTheme.typography.labelMedium,
            color = AgarAmber,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "Connection: ${connectionState.label}",
            style = MaterialTheme.typography.labelMedium,
            color = AgarTextMuted,
        )
    }
}

private fun connectionColor(connectionState: ConnectionState): Color = when (connectionState) {
    ConnectionState.Online -> AgarGreen
    ConnectionState.Connecting, ConnectionState.Warning -> AgarAmber
    ConnectionState.Offline -> AgarTextMuted
    ConnectionState.Error -> AgarRed
}

package com.agar.commander.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agar.commander.model.AppDestination
import com.agar.commander.ui.components.AppShell
import com.agar.commander.ui.components.StartupScreen
import com.agar.commander.ui.components.WarningsDialog
import com.agar.commander.ui.screens.CommandsScreen
import com.agar.commander.ui.screens.DiagnosticsScreen
import com.agar.commander.ui.screens.MapsSlamScreen
import com.agar.commander.ui.screens.MissionsScreen
import com.agar.commander.ui.screens.OperateScreen
import com.agar.commander.ui.screens.PtoScreen
import com.agar.commander.ui.screens.RobotScreen
import com.agar.commander.ui.screens.SettingsScreen
import com.agar.commander.ui.theme.AgarBackground
import kotlinx.coroutines.launch

@Composable
fun AgarCommanderApp(appViewModel: AppViewModel = viewModel()) {
    val uiState by appViewModel.uiState.collectAsState()
    var destination by rememberSaveable { mutableStateOf(AppDestination.Operate) }
    var showWarnings by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (!uiState.initialized) {
        StartupScreen()
        return
    }

    val showPreviewMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = "$message • no command sent")
        }
    }

    Scaffold(
        containerColor = AgarBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        AppShell(
            destination = destination,
            status = uiState.robotStatus,
            onDestinationSelected = { destination = it },
            onBrakeClick = {
                appViewModel.toggleBrakePreview()
                showPreviewMessage("Brake preview changed")
            },
            onWarningsClick = { showWarnings = true },
            content = {
                when (destination) {
                    AppDestination.Operate -> OperateScreen(showPreviewMessage)
                    AppDestination.Missions -> MissionsScreen(showPreviewMessage)
                    AppDestination.Maps -> MapsSlamScreen(showPreviewMessage)
                    AppDestination.Pto -> PtoScreen(showPreviewMessage)
                    AppDestination.Robot -> RobotScreen(showPreviewMessage)
                    AppDestination.Commands -> CommandsScreen(showPreviewMessage)
                    AppDestination.Diagnostics -> DiagnosticsScreen(
                        alerts = uiState.alerts,
                        onAction = showPreviewMessage,
                    )
                    AppDestination.Settings -> SettingsScreen(
                        profile = uiState.connectionProfile,
                        connectionState = uiState.robotStatus.connection,
                        onConnectionStateChange = appViewModel::setConnectionPreview,
                        onSaveProfile = appViewModel::saveConnectionProfile,
                        onAction = showPreviewMessage,
                    )
                }
            },
            modifier = Modifier.padding(paddingValues),
        )
    }

    if (showWarnings) {
        WarningsDialog(
            alerts = uiState.alerts,
            onDismiss = { showWarnings = false },
            onOpenDiagnostics = {
                showWarnings = false
                destination = AppDestination.Diagnostics
            },
        )
    }
}

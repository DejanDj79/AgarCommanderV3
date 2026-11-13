package com.agar.commander.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agar.commander.data.ConnectionProfileRepository
import com.agar.commander.model.ConnectionProfile
import com.agar.commander.model.ConnectionState
import com.agar.commander.model.GlobalRobotStatus
import com.agar.commander.model.PreviewRobotAlerts
import com.agar.commander.model.RobotAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val initialized: Boolean = false,
    val connectionProfile: ConnectionProfile = ConnectionProfile(),
    val robotStatus: GlobalRobotStatus = GlobalRobotStatus(),
    val alerts: List<RobotAlert> = PreviewRobotAlerts,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val connectionProfiles = ConnectionProfileRepository(application)
    private val _uiState = MutableStateFlow(AppUiState())

    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = connectionProfiles.profile.first()
            _uiState.update { state ->
                state.copy(
                    connectionProfile = profile,
                    robotStatus = state.robotStatus.copy(
                        connection = if (profile.autoConnect) {
                            ConnectionState.Connecting
                        } else {
                            ConnectionState.Offline
                        },
                    ),
                )
            }

            delay(1_200)
            _uiState.update { it.copy(initialized = true) }

            if (profile.autoConnect) {
                delay(1_600)
                _uiState.update { state ->
                    if (state.robotStatus.connection == ConnectionState.Connecting) {
                        state.copy(
                            robotStatus = state.robotStatus.copy(
                                connection = ConnectionState.Offline,
                            ),
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun setConnectionPreview(connection: ConnectionState) {
        _uiState.update { state ->
            state.copy(robotStatus = state.robotStatus.copy(connection = connection))
        }
    }

    fun toggleBrakePreview() {
        _uiState.update { state ->
            state.copy(
                robotStatus = state.robotStatus.copy(
                    brakeActive = !state.robotStatus.brakeActive,
                ),
            )
        }
    }

    fun saveConnectionProfile(profile: ConnectionProfile) {
        if (!profile.isValid) return

        val normalized = profile.copy(host = profile.host.trim())
        _uiState.update { it.copy(connectionProfile = normalized) }
        viewModelScope.launch {
            connectionProfiles.save(normalized)
        }
    }
}

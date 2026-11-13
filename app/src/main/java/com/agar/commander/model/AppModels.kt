package com.agar.commander.model

enum class AppDestination(val label: String) {
    Operate("Operate"),
    Missions("Missions"),
    Maps("Maps & SLAM"),
    Pto("PTO"),
    Robot("Robot"),
    Commands("Commands"),
    Diagnostics("Diagnostics"),
    Settings("Settings"),
}

enum class ConnectionState(val label: String) {
    Connecting("Connecting"),
    Online("Online"),
    Offline("Offline"),
    Warning("Warning"),
    Error("Error"),
}

enum class RobotWorkspace(val label: String) {
    Actuators("Actuators"),
    Safety("Safety"),
}

enum class MissionWorkspace(val label: String) {
    Library("Library"),
    Editor("Editor"),
}

enum class SlamMode(val label: String) {
    Gps("GPS"),
    Mapping("Mapping"),
    Localization("Localization"),
    Autonomous("Autonomous"),
}

enum class BuzzerMode(val label: String) {
    Off("Off"),
    Reverse("Reverse"),
    Forward("Forward"),
    Both("Both"),
}

enum class AlertSeverity(val label: String) {
    Info("Info"),
    Warning("Warning"),
    Error("Error"),
}

data class RobotAlert(
    val code: String,
    val title: String,
    val message: String,
    val source: String,
    val occurredAt: String,
    val severity: AlertSeverity,
)

val PreviewRobotAlerts = listOf(
    RobotAlert(
        code = "ROBOT_CONNECTION",
        title = "Robot offline",
        message = "The local ROSBridge connection is unavailable.",
        source = "Connection monitor",
        occurredAt = "Now",
        severity = AlertSeverity.Error,
    ),
    RobotAlert(
        code = "SLAM_MESSAGE_TIMING",
        title = "SLAM timing unavailable",
        message = "Scan and TF timing cannot be evaluated while the robot is offline.",
        source = "SLAM health monitor",
        occurredAt = "Now",
        severity = AlertSeverity.Warning,
    ),
)

data class ConnectionProfile(
    val host: String = "192.168.0.61",
    val port: Int = 9090,
    val autoConnect: Boolean = true,
) {
    val endpoint: String
        get() = "ws://${host.trim()}:$port"

    val isValid: Boolean
        get() = host.trim().isNotEmpty() && port in 1..65_535
}

data class GlobalRobotStatus(
    val connection: ConnectionState = ConnectionState.Connecting,
    val gpsStatus: String = "RTK FIX",
    val operatingMode: String = "GPS",
    val missionName: String = "North orchard 04",
    val batteryPercent: Int = 78,
    val brakeActive: Boolean = true,
    val alertCount: Int = 2,
)

data class ActuatorReading(
    val name: String,
    val currentMm: Int,
    val targetMm: Int,
)

val PreviewActuators = listOf(
    ActuatorReading("Front left", 176, 175),
    ActuatorReading("Front right", 175, 175),
    ActuatorReading("Rear left", 174, 175),
    ActuatorReading("Rear right", 176, 175),
)

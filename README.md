# AgAR Commander V3

Native Android operator application for the AgAR robot.

## Current milestone

- Kotlin and Jetpack Compose foundation
- Android 9 / API 28 minimum
- landscape-only SIYI MK32 layout
- startup loader and ViewModel-owned mock background connection state
- persistent local ROSBridge profile stored with Android DataStore
- validated robot host, port, and connect-on-startup settings
- compact eight-destination operator shell for the SIYI MK32 display
- mission-focused Operate screen with mission controls left and live route map right
- dedicated PTO screen for attachment control, telemetry, and speed protection
- dedicated Commands screen for shared drive limits, lights, and buzzer
- Robot workspaces for actuator control and robot-side safety interlocks
- mock Operate, Missions, Maps & SLAM, PTO, Robot, Commands, Diagnostics, and Settings screens
- no robot commands are sent in this milestone

## Build

```bash
./gradlew assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Product notes

See `docs/APP_INFORMATION_ARCHITECTURE.md` and `docs/SCREEN_INVENTORY.md`.

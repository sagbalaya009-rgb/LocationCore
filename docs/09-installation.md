# Development controller installation

This procedure installs the current non-root LocationCore development controller. It supports STATIC, two-point MOVEMENT and GPX ROUTE modes through Android test providers. Android marks every emitted location as mock. It is not the future privileged integration layer.

Requirements: Windows, Google Android SDK with API 37 and Platform Tools, Java 17, authorized USB debugging, and an Android device matching the fingerprint below. The repository contains a Gradle 9.5.0 wrapper and uses Android Gradle Plugin 9.3.2; first build may download them from their official repositories.

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug
.\scripts\windows\Read-DeviceState.ps1
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell pm grant dev.locationcore android.permission.ACCESS_FINE_LOCATION
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell pm grant dev.locationcore android.permission.ACCESS_COARSE_LOCATION
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n dev.locationcore/.MainActivity
```

Stop if the guard does not show `supported_build_match : True`. The allowlist currently contains the investigated TECNO phone build and the active FOSSiBOT M20 build `FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys`.

On the tablet, open Developer options, choose **Select mock location app**, and select **LocationCore**. The same setting can be applied during development with `adb shell appops set dev.locationcore android:mock_location allow`. This grant is required by Android and does not unlock or root the device.

The controller starts in REAL. Virtual modes run in a foreground service at 1 Hz and never auto-start after reboot. Press **Stop and return to real location** before uninstalling.

For recovery from an app crash or stuck provider, run `scripts/adb/Stop-LocationCore.ps1`. It is exact-build guarded, force-stops the package, removes the GPS/network/fused test overrides under a temporary shell mock app-op, cycles Android location only when it was originally enabled to clear stale last-known mock data, restores the original location-enabled state, and always returns the shell app-op to default. Uninstall with `adb uninstall dev.locationcore` after cleanup. No privileged engine is installed.


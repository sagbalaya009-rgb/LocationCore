# Diagnostic development installation

This procedure installs only the read-only diagnostic client. It is **not** a LocationCore virtualization installer.

Requirements: Windows, Google Android SDK with API 37 and Platform Tools, Java 17, authorized USB debugging, and an Android device matching the fingerprint below. The repository contains a Gradle 9.5.0 wrapper and uses Android Gradle Plugin 9.3.2; first build may download them from their official repositories.

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug
.\scripts\windows\Read-DeviceState.ps1
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n dev.locationcore/.MainActivity
```

Stop if the guard does not show `supported_build_match : True` for `TECNO/KL5-OP/TECNO-KL5:14/UP1A.231005.007/260615V2258:user/release-keys`. Grant precise location permission on the phone or with normal ADB package-manager permissions. The app subscribes only while its screen is open. It does not persist coordinates.

Uninstall the diagnostic client with `adb uninstall dev.locationcore`. This does not remove a privileged engine because none has been installed.


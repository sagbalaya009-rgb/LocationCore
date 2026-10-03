# Device results

## 2026-10-03 reconnaissance

- ADB listed one authorized device with model `TECNO_KL5`, product `KL5-OP`.
- Android property checks showed exact fingerprint in the dossier.
- Boot properties reported locked bootloader, green verified boot, slot B, dynamic partitions and Virtual A/B.
- VINTF files declared GNSS AIDL and HIDL interfaces; runtime services showed AIDL `IGnss/default` and HIDL 2.1 registration.
- `dumpsys location` identified Google Play Services network and fused providers and Android GPS provider.
- A later `adb devices -l` returned an empty device list; a physical USB reconnection restored access.
- USB was reconnected. `Read-DeviceState.ps1` passed its exact-build guard, showing slot B, locked bootloader, green verified boot and enforcing SELinux.
- `ro.soc.model` reports `MT6769` while `ro.board.platform` reports `mt6768`; HiOS property reports `hios14.5.0`. `gps_drv_stp` was loaded.
- The AIDL GNSS implementation imports `hw_get_module`; the legacy `gps.default.so` includes `mnld` interface strings.
- `adb reboot bootloader` succeeded, but `fastboot devices` returned no device. The phone returned to Android without physical intervention. `sys.boot_completed=1`, fingerprint unchanged, verified boot green, and slot B were confirmed afterward. No fastboot variable or partition write succeeded.

No static/movement/route mode has been installed or tested. No claim of real-location restoration is made.

## Diagnostic app, 2026-10-03

- `assembleDebug` succeeded using the installed Android SDK after `ANDROID_HOME` was set explicitly.
- APK installation returned `Success`; exact-build fingerprint was checked before installation.
- `dev.locationcore/.MainActivity` launched and Android reported the app process and provider registrations.
- Network and fused providers supplied live updates with `isMock=false`. Passive subscription supplied updates whose source was fused. GPS remained subscribed without a fresh update during the observed interval.
- The first build misattributed passive callbacks. After separate listeners per subscription were added, the corrected build compiled, installed and visibly showed distinct passive updates.
- Coordinates and screenshots were kept outside Git. The corrected diagnostic APK SHA-256 is `813FA9404C53E137E0B164A661694050C2F42F35AE6D200E393B5BF63CCCA612`.
- App was sent to the background; the foreground-only listener lifecycle was exercised. No full battery/performance measurement or crash fallback test has run.


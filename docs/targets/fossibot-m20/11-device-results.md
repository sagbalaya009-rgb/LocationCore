# FOSSiBOT M20 device results

## Read-only reconnaissance, 2026-10-20

- One authorized Android device was present and identified as FOSSiBOT M20 / DT1.
- The exact-build guard matched fingerprint `FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys`.
- The tablet was fully booted with locked vbmeta, green Verified Boot, slot A, and enforcing SELinux.
- AIDL GNSS, HIDL GNSS 2.1, MediaTek GNSS/LBS processes, vendor libraries and the `gps_drv` kernel module were observed.
- No root executable or known root-management package was found. No bootloader, fastboot, partition write, test provider, or mock-location change was attempted during reconnaissance.

## Diagnostic build and device run, 2026-10-20

- `:engine:test` and `:app:assembleDebug` completed successfully offline. The engine suite ran six tests with zero failures.
- `adb install -r` returned `Success`. Android granted normal fine and coarse location permissions and launched `dev.locationcore/.MainActivity`.
- The app registered one listener each for GPS, network, fused, and passive providers. Crash-buffer inspection found no LocationCore crash.
- Android location was enabled in high-accuracy mode. Over approximately 99 seconds, the framework recorded zero delivered fixes for all four LocationCore subscriptions. This is a baseline result, not evidence that GNSS hardware is broken; indoor signal, network state, or provider freshness may explain it.
- The app was force-stopped after observation so its foreground listeners would not continue consuming power.
- Diagnostic APK SHA-256: `6F28A4E7837D9D6442E48AA66905BE4BA10AB672935355BB2CE43C31A7B82603`.

## Controller integration, 2026-10-20

- Installed development controller `0.2.0-alpha` and selected it through Android's mock-location app-op. This is reversible and did not unlock, root, or flash the tablet.
- STATIC emitted synthetic `0,0` with 5 m accuracy at 1 Hz. GPS, network and fused provider state each showed the same location marked `mock`.
- MOVEMENT from synthetic `0,0` to `0,0.001` at 10 m/s showed longitude `0.000455`, speed 10 m/s and bearing 90° after approximately six seconds across GPS, network and fused.
- The first Android GPX run safely stopped because Android's SAX implementation did not recognize the desktop `disallow-doctype-decl` feature. The parser was changed to enforce a 5 MiB bound, reject DTD/entity declarations in supported UTF-8/ASCII input, refuse NUL/UTF-16 input, and reject entity resolution while tolerating unsupported feature flags. The unit suite still passed.
- GPX replay then passed on device with a two-point synthetic fixture: after approximately six seconds, all three providers showed longitude `0.000457`, interpolated altitude about 14.57 m, speed 10 m/s and bearing 90°, marked mock.
- Normal STOP removed fused, network and GPS overrides, stopped the foreground service, set mode REAL, and the app's public `getLastKnownLocation` check returned no mock sample. `dumpsys location` retained a lower-level provider debug value, but it was not returned through the public cache API.
- An exact-build ADB emergency cleanup was exercised during active STATIC. It force-stopped the package, removed all three overrides under a temporary shell mock app-op, reset that shell app-op to default, cycled Android location off/on because it was initially enabled, restored the enabled state, and relaunch reported REAL with no public mock cache.
- No LocationCore fatal exception was present in the crash buffer after these tests. The app was left installed, selected as the mock-location app, open in REAL mode, with no active foreground service.
- Final `0.2.0-alpha` APK SHA-256: `D234638C671042AA6B99932320698918A162A7A136B41FB12970F13B3B0CD194`.

The controller is an application-layer milestone, not the final privileged system. Android intentionally exposes its virtual output as mock.

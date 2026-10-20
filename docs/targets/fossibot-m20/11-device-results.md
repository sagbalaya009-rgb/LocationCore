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

## Controller usability milestone, 2026-10-20

- Added online place search using the Android geocoder with a user-initiated OpenStreetMap Nominatim fallback. Both reached a clear unavailable state because the tablet had no working network/DNS during testing; no successful search result is claimed.
- Replaced a blank-rendering WebView map experiment with a native offline latitude/longitude grid. Selecting a synthetic point returned `24.258762, 45.000000` to the controller fields.
- Saved the synthetic map point as a favorite and confirmed it appeared in the Favorites dialog. Starting it also placed it in Recents. The test favorite and recent data were deleted afterward.
- Re-ran the emergency cleanup after UI testing. The tablet was left with Android location enabled, shell mock app-op at default, LocationCore selected as the user's mock app, no active service, REAL state and no public mock cache.
- Installed the final `0.3.0-alpha` artifact and completed a normal reboot. Android reported boot complete; the LocationCore process and service did not auto-start; saved state remained REAL, inactive and without a stale mock cache. A subsequent cold launch produced no crash and did not start the virtualization service.
- Final `0.3.0-alpha` APK SHA-256: `56424D9ADD4BB33544DDD566B38D00F6EDE835D2513F190EFF634A8218EDFBC3`.

The controller is an application-layer milestone, not the final privileged system. Android intentionally exposes its virtual output as mock.

## Privileged-path reconnaissance, 2026-10-20

- Android reported a locked green boot chain and `sys.oem_unlock_allowed=0`.
- Entered bootloader fastboot mode after running the guarded LocationCore cleanup. Windows detected MediaTek USB `VID_0E8D&PID_201C` but reported driver problem code 28, so no unlock or flash command was issued and no fastboot variable result is claimed.
- Returned to Android with the power-button restart. The same build booted normally; bootloader lock and green verified-boot state remained intact; LocationCore remained REAL and inactive with no service.
- Identified the bundled `com.incar.update` ADUPS client and FOTA version `M20_WXGA_V1.0_20241212_0110`. This identifies the update channel but does not provide the complete signed factory images required by the recovery gate.

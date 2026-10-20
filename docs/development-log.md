# Development log

## 2026-10-03

- Located installed Google Android SDK Platform Tools; USB ADB authorization succeeded for TECNO-KL5.
- Captured read-only build, kernel, boot, partition, location provider and GNSS evidence. Raw files remain outside Git.
- Found locked/green AVB and dynamic A/B architecture. No root command found.
- Found AIDL GNSS service and legacy HIDL declaration plus MediaTek `mnld`/`mtk_agpsd` processes.
- Found Google Play Services owns network and fused providers. Provider consistency remains an open design problem.
- Exact-build stock firmware and validated recovery are missing; privileged changes remain gated.
- Created private GitHub repository `sagbalaya009-rgb/LocationCore` and pushed two sanitized documentation commits. First push attempt was rejected by GitHub email privacy settings; amended author to the account no-reply address and pushed successfully.
- ADB later stopped listing the phone. Restarting the PC-side ADB server did not restore USB enumeration; Windows only listed Bluetooth interfaces until the cable was reconnected.
- USB reconnection restored authorized ADB. Exact-build guard passed.
- Read-only properties found `ro.soc.model=MT6769`, `ro.board.platform=mt6768`, HiOS 14.5.0 and a loaded `gps_drv_stp` module.
- ELF/init inspection strengthened the AIDL → legacy GPS HAL → `mnld` hypothesis; live IPC linkage remains unproven.
- Bootloader-mode inspection could not obtain a fastboot transport. Android booted again unchanged. Fastboot is not a validated recovery route.
- Built and installed a normal read-only diagnostic app to observe REAL provider delivery. No location injection was added.
- Detected passive-update attribution bug in first on-device view; changed to one listener per subscription, rebuilt and reinstalled. Corrected view showed network, fused and passive updates without exposing coordinates in Git.
- Implemented a platform-independent route and state-machine engine; six unit tests passed. It is not integrated with an Android provider.
- Primary-user package query found Transsion's system updater, but no accessible exact-build stock image. Third-party archive remains unverified.
- Probed Android's test-provider API with a disposable provider. Initial call failed because shell mock app-op was default deny; a temporary grant allowed add/set/remove. Passive last-known cache retained the dummy mock point after removal, then cleared after fresh real network/fused updates. Shell app-op returned to default deny. Simple provider removal is insufficient for immediate REAL restoration.
- Entered userspace fastbootd for read-only transport inspection. Windows detected its USB ID but lacked the driver. Official Google USB driver files were installed into the Android SDK; driver binding failed without administrator elevation. The user selected Reboot now, and Android returned fully booted on the unchanged build, slot B and green verified boot.
- User directed removal of all LocationCore components from the phone and will provide a tablet for continuation. Uninstalled the diagnostic app and verified no package, test provider, or temporary mock app-op remained. No tablet inspection or installation has occurred.

## 2026-10-20

- Connected and identified a new FOSSiBOT M20 / DT1 tablet running Android 13 build `M20_WXGA_V1.0_20241212`.
- Captured a privacy-filtered, read-only tablet dossier. Found a locked green AVB state, slot A, Virtual A/B dynamic partitions, enforcing SELinux, no root, and the MediaTek AIDL/HIDL GNSS stack.
- Added the exact tablet fingerprint to the guarded device-state script. Recovered source files that were missing from the working copy but intact in private Git history, consistent with retaining the computer project for tablet continuation.
- Ran six engine tests and built the diagnostic APK successfully. Installed and launched it on the tablet; all four provider subscriptions registered without a crash, but no fixes arrived during approximately 99 seconds. Force-stopped the diagnostic afterward.
- Official support searching did not locate an exact-build firmware download. Privileged work remains gated by recovery evidence.
- Implemented a non-root controller with explicit REAL/STATIC/MOVEMENT/ROUTE states, a foreground emitter service, GPS/network/fused test-provider consistency, manual coordinates, speed/accuracy controls, GPX import, and visible mock-permission diagnostics.
- STATIC, MOVEMENT and GPX ROUTE passed on the tablet with synthetic equatorial fixtures. Fixed Android SAX feature incompatibility while preserving bounded and entity-refusing GPX parsing.
- Normal STOP passed. Built and exercised an exact-build ADB emergency cleanup against an active session; provider overrides, the temporary shell app-op, location-enabled state, public last-known cache, and REAL relaunch all restored as intended.


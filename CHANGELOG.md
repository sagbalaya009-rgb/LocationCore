# Changelog

## 2026-10-03 — reconnaissance

- Recorded exact KL5 Android build, partition/boot properties and observed GNSS processes.
- Mapped Android GPS, network and fused provider ownership.
- Recorded recovery gate and provisional architecture decision.
- No device modification or virtualization installation.
- Added an installed, read-only diagnostic app for live provider observation; corrected passive provider attribution. No virtualization engine is installed.
- Added an offline, locally tested coordinate/GPX/state-machine engine library. No Android provider injection is connected.

## 2026-10-20 — FOSSiBOT M20 application milestone

- Added a privacy-filtered FOSSiBOT M20 / DT1 device dossier and exact-build guard.
- Connected the offline engine to consistent GPS, network and fused Android test-provider output.
- Added REAL, STATIC, two-point MOVEMENT and GPX ROUTE modes with a foreground service and visible mock status.
- Added manual coordinates, speed/accuracy controls, online place search with OpenStreetMap fallback, an offline native coordinate map, favorites, recents and diagnostics.
- Added and exercised an exact-build emergency ADB cleanup that removes provider overrides, clears stale public mock cache and restores Android location state.
- Verified STATIC, MOVEMENT, GPX replay, normal STOP, emergency cleanup, reboot-safe REAL default, map selection, favorites and recents on the tablet. Online search remains unverified because the tablet had no working network/DNS.


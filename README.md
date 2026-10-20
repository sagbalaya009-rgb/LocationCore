# LocationCore

Device-specific Android location virtualization research. The active target is now the user's FOSSiBOT M20 tablet (`M20`, `DT1`); the earlier TECNO Spark 30C work is retained as a separate historical target.

**Current state: TABLET RECONNAISSANCE AND DIAGNOSTIC DEVELOPMENT.** The phone diagnostic app was removed on 2026-10-03. On 2026-10-20, the connected tablet was identified read-only as a stock, locked FOSSiBOT M20 running Android 13 build `M20_WXGA_V1.0_20241212`. No virtualization component, root, unlock, or flash has been applied to either device.

The tablet fingerprint is `FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys`. Installation tooling must refuse uninvestigated builds.

The tablet investigation found a locked, green AVB boot chain, Virtual A/B dynamic partitions, and MediaTek AIDL/HIDL GNSS services. Google Play Services supplies network and fused providers. An exact-build recovery image has **not** been obtained or validated, so privileged device changes remain paused.

The current non-root controller can emit STATIC, two-point MOVEMENT, and imported GPX ROUTE locations through Android's official test-provider mechanism. Android marks the output as mock. REAL is the default, virtual modes never auto-start after reboot, and STOP removes the GPS/network/fused overrides. See the [tablet dossier](docs/targets/fossibot-m20/01-device-dossier.md), [tablet GNSS stack](docs/targets/fossibot-m20/05-gnss-stack.md), [architecture decision](docs/targets/fossibot-m20/adr/0001-locked-device-integration.md), [tablet recovery gate](docs/targets/fossibot-m20/08-recovery-plan.md), and [build/install steps](docs/09-installation.md). The original phone documents remain under `docs/` as historical evidence.

`engine/` contains the platform-independent coordinate, GPX and state-machine library. The Android foreground service now maps its output to the tablet's GPS, network and fused test providers at 1 Hz.


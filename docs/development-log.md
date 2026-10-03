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


# Development log

## 2026-10-03

- Located installed Google Android SDK Platform Tools; USB ADB authorization succeeded for TECNO-KL5.
- Captured read-only build, kernel, boot, partition, location provider and GNSS evidence. Raw files remain outside Git.
- Found locked/green AVB and dynamic A/B architecture. No root command found.
- Found AIDL GNSS service and legacy HIDL declaration plus MediaTek `mnld`/`mtk_agpsd` processes.
- Found Google Play Services owns network and fused providers. Provider consistency remains an open design problem.
- Exact-build stock firmware and validated recovery are missing; privileged changes remain gated.
- Created private GitHub repository `sagbalaya009-rgb/LocationCore` and pushed two sanitized documentation commits. First push attempt was rejected by GitHub email privacy settings; amended author to the account no-reply address and pushed successfully.
- ADB later stopped listing the phone. Restarting the PC-side ADB server did not restore USB enumeration; Windows only listed Bluetooth interfaces. Awaiting physical USB reconnection.


# Architecture evaluation

Decision status: **provisional**. The bootloader is locked, root is unavailable, and exact-build recovery is not validated. An integration layer cannot responsibly be declared proven until privilege, recovery and provider behavior are tested on this device.

| Candidate | Depth and coverage | Device evidence and risk | Current disposition |
| --- | --- | --- | --- |
| A. Framework/service integration | Can affect Android provider delivery, caches and passive consumers if integrated into the actual service; Google fused provider still needs testing | Framework is read-only and verified on this user build; needs a trusted installation/hook mechanism | Strong candidate, privilege gated |
| B. Root framework hooks | Can intercept framework decisions with fine control | Root absent; Android 14/HiOS version sensitivity, SELinux and crash risk | Candidate after recovery and unlock decision |
| C. Privileged daemon with narrow IPC | Good state, telemetry and health control; needs another layer to deliver locations | Does not itself replace framework or HAL data | Likely component, not standalone interception |
| D. GNSS AIDL/HIDL HAL proxy | Deep GNSS control, below Android GPS provider | Both interface families declared; active AIDL service observed. Vendor service replacement has SELinux/AVB/boot risk; network/fused paths remain separate | Candidate only after live call-path proof and recovery |
| E. MediaTek vendor-stack interception | Deepest vendor GNSS control | `mnld`, `mtk_agpsd`, and `/dev/gps_emi` observed, but undocumented IPC and firmware coupling; greater recovery risk | Poor initial choice without further tracing |
| F. Kernel module | Lowest device layer | `CONFIG_GNSS=y` and `/dev/gps_emi` do not show a kernel-level location-object interface; Android still handles fused/network location | No evidence this is a correct control point |
| G. Hybrid | Daemon/state machine plus framework/provider control, possibly GNSS integration for specific paths | Potentially best consistency, but requires validation across Android and Google providers | Provisional preferred direction |

## Selection criteria

The deepest justified layer is the one that actually controls the location objects delivered to ordinary consumers and returns cleanly to REAL. The observed GNSS HAL is deep for satellite fixes but does not own the Google network/fused provider services. Kernel access to a GNSS device node is deeper physically but does not imply useful control over Android `Location` objects. A framework-oriented hybrid currently looks more promising for consistency, with a privileged daemon limited to policy/state and no arbitrary shell command endpoint. This is **an inference**, not a device-verified final architecture.

## Framework test-provider probe

On 2026-10-03, a disposable provider named `locationcore_probe` was added through Android's location shell interface after a temporary shell mock app-op grant. It emitted only the dummy coordinate `0,0`, then was removed in a `finally` cleanup path and the app-op returned to default deny. The provider itself was gone, but the passive provider's last-known cache still showed a mock location from `locationcore_probe`. Foreground real network/fused requests later replaced that cached value. This demonstrates a restoration gap for this higher-level mechanism on the actual firmware; a simple add/remove test-provider scheme is not sufficient for immediate REAL restoration.

Before choosing a final layer, trace framework-to-AIDL/HIDL usage, establish exact firmware recovery, measure provider delivery through a diagnostic app, and test REAL restoration. No boot image, vendor binary, system partition, or SELinux policy has been changed.


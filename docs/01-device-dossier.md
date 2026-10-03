# Read-only device dossier

Collected 2026-10-03 using Google's installed Android SDK Platform Tools over an authorized USB ADB session. No device write command was used. Raw capture remains outside Git because device output can contain identifiers and location history.

| Area | Observed value | Evidence command |
| --- | --- | --- |
| Commercial name | TECNO SPARK 30C | `getprop ro.product.tran.device.name.default` |
| Manufacturer / model | TECNO / TECNO KL5 | `getprop ro.product.manufacturer`, `getprop ro.product.model` |
| Product / device | KL5-OP / TECNO-KL5 | `getprop ro.product.name`, `getprop ro.product.device` |
| Board platform | mt6768 | `getprop ro.board.platform` |
| Hardware | mt6768 | `getprop ro.hardware` |
| SoC marketing designation | **Unverified**; platform identifier alone is insufficient | — |
| Android | 14, API 34 | `getprop ro.build.version.release`, `.sdk` |
| Build display ID | KL5-XK678ABCDEFGHIJKLMNAc-U-OP-260615V2258 | `getprop ro.build.display.id` |
| Fingerprint | TECNO/KL5-OP/TECNO-KL5:14/UP1A.231005.007/260615V2258:user/release-keys | `getprop ro.build.fingerprint` |
| Android security patch | 2026-07-01 | `getprop ro.build.version.security_patch` |
| Vendor security patch | 2025-04-05 | `getprop ro.vendor.build.security_patch` |
| Treble / VNDK | true / 31 | `getprop ro.treble.enabled`, `ro.vndk.version` |
| ABI | arm64-v8a primary, 32-bit ARM supported | `getprop ro.product.cpu.abilist` |
| Kernel | Linux 5.10.237-android12-9, aarch64, Clang 12.0.5 | `uname -a`, `/proc/version` |
| Kernel modules / GNSS config | `CONFIG_MODULES=y`, `CONFIG_GNSS=y` | `/proc/config.gz` |
| SELinux | Enforcing | `getenforce` |
| Root | `su`, `magisk`, `ksud`, `apd` absent from shell PATH; root not demonstrated | `command -v` |
| Boot slot | `_b` | `getprop ro.boot.slot_suffix` |
| Bootloader / verified boot | locked / green | `getprop ro.boot.vbmeta.device_state`, `ro.boot.verifiedbootstate` |
| Dynamic partitions / Virtual A/B | true / true | `getprop ro.boot.dynamic_partitions`, `ro.virtual_ab.enabled` |

`/proc/cmdline` is permission denied to the shell user. CPU topology and exact HiOS version need further confirmation. Sensorservice reports accelerometer, magnetometer, gyroscope, orientation, step counter/detector; no barometer appears in the visible sensor list.

## Observed access limits

Unrooted shell cannot read raw partition contents or many process details. Bootloader and firmware recovery properties remain to be verified without changing device state.


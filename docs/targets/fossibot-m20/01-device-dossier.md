# FOSSiBOT M20 device dossier

Captured read-only from the connected tablet on 2026-10-20. No serial number, account identifier, radio identifier, or precise location is recorded here.

## Identity and software

| Field | Observed value |
| --- | --- |
| Manufacturer / brand | FOSSiBOT |
| Model | M20 |
| Product / device | DT1 / DT1 |
| Build display | `M20_WXGA_V1.0_20241212` |
| Fingerprint | `FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys` |
| Android / API | 13 / 33 |
| First API level | 33 |
| Security patch | 2023-01-05 for system and vendor |
| Build type | user, non-debuggable |
| Treble / VNDK | enabled / 31 |

## SoC, kernel and memory

- MediaTek reports `MT8766B`; board platform reports `mt6761`.
- Four AArch64 CPU cores were present, each advertising a 2,001,000 kHz maximum frequency.
- Kernel: Linux 4.19.191, AArch64, build timestamp 2024-12-12.
- Total RAM reported approximately 2.73 GiB.
- Kernel configuration exposed module support, module unloading, dm-verity, Binder/BinderFS and SELinux. The `gps_drv` module was loaded together with the MediaTek connectivity stack.
- SELinux was enforcing. No `su`, Magisk, KernelSU, APatch, or known root-management package was found by the read-only checks.

## Boot and storage

- Bootloader/vbmeta state: locked; Verified Boot: green; AVB 1.2; verity mode `eio`.
- Active slot: A. Dynamic partitions and compressed userspace Virtual A/B snapshots are enabled.
- File-based encryption is active.
- `boot`, `init_boot`, `vendor_boot`, `dtbo`, `vbmeta`, `vbmeta_system`, `vbmeta_vendor`, and MediaTek boot-chain partitions exist for both slots. `super`, `metadata`, and `userdata` are present.
- Android mounts verified logical `system`, `vendor`, and `product` partitions read-only through device mapper.
- `sys.oem_unlock_allowed=0` at the time of inspection. No unlock was requested or attempted.

## Sensors

The accessible sensor service output confirmed an MTK accelerometer. Gyroscope, magnetometer, pressure, and step sensors were not shown by the filtered read-only capture and remain unverified.

## Current conclusion

This tablet is a distinct target from the TECNO phone even though both use MediaTek GNSS components. No phone-specific boot image, partition assumption, policy, or binary may be reused. The device is suitable for reversible application diagnostics now; privileged integration remains gated by exact-build recovery and a separate architecture decision.

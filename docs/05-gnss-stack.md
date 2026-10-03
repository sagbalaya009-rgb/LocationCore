# GNSS stack on this build

The device VINTF manifests declare both `android.hardware.gnss.IGnss/default` (AIDL) and legacy HIDL `android.hardware.gnss@2.1::IGnss/default`. Runtime `service list` shows the AIDL instance. `lshal` shows the HIDL 2.1 instance owned by the same GNSS service process. Therefore both declarations exist, but the exact framework-to-HAL selection and implementation sharing require tracing before interception design.

Observed processes:

| Process | UID | SELinux domain |
| --- | --- | --- |
| `android.hardware.gnss-service.mediatek` | system | `u:r:mtk_hal_gnss:s0` |
| `mnld` | gps | `u:r:mnld:s0` |
| `mtk_agpsd` | gps | `u:r:mtk_agpsd:s0` |

Observed vendor files include `/vendor/bin/hw/android.hardware.gnss-service.mediatek`, `/vendor/lib64/hw/android.hardware.gnss-impl-mediatek.so`, `/vendor/lib64/hw/android.hardware.gnss@2.1-impl-mediatek.so`, and `/vendor/lib64/hw/gps.default.so`. A `/dev/gps_emi` device node exists with `gps:gps` ownership. These are evidence of layers, **not** proof of the complete internal call chain.

```text
Android GnssService
   -> AIDL IGnss/default (observed service)
      -> MediaTek GNSS service process (observed owner)
         -> MediaTek implementation libraries (present, linkage unverified)
         -> mnld / mtk_agpsd (running, IPC unverified)
         -> GNSS hardware interface (not yet traced)
```

No vendor binary has been modified or copied into the repository.


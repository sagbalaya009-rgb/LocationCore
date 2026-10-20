# FOSSiBOT M20 location and GNSS stack

## Observed framework providers

- Android `LocationManagerService` exposes passive, network, fused and GPS providers.
- Google Play Services owns the network and fused provider services.
- Android's `GnssService` owns the GPS-facing framework path.
- No LocationCore package or test provider was present before tablet development began.

## Observed vendor path

```text
Android LocationManager / GnssService
  -> android.hardware.gnss.IGnss/default (AIDL)
  -> MediaTek GNSS service (SELinux domain mtk_hal_gnss)
  -> HIDL GNSS 2.1 compatibility implementation
  -> gps.default.so / libmnl.so
  -> mnld and mtk_agpsd
  -> gps_drv kernel module / GNSS hardware
```

Evidence:

- Runtime Binder service: `android.hardware.gnss.IGnss/default`.
- VINTF declares an AIDL default GNSS service and HIDL GNSS 2.1/1.1.
- Runtime processes include `android.hardware.gnss-service.mediatek`, `mnld`, and `mtk_agpsd` in separate SELinux domains.
- Vendor files include `gps.default.so`, `libmnl.so`, GNSS service init definitions, and MediaTek LBS HIDL declarations.
- Kernel module list includes `gps_drv` connected to the MediaTek WMT stack.

The diagram is supported by registration, files, process identities, and module presence. The exact live call sequence between every vendor component has not been traced.

## Architectural implication

Interception only below the GNSS HAL would control satellite-derived fixes but would not automatically control Google network and fused provider output. A device-consistent design must account for all framework-visible providers and must prove immediate restoration of real location. The final integration layer remains undecided until reversible diagnostics and recovery prerequisites are complete.

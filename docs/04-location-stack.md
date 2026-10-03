# Android location stack

The running `location` Binder service is Android's `ILocationManager`. `dumpsys location` reports passive, network, fused and GPS providers. The network and fused provider services belong to `com.google.android.gms`; GPS belongs to Android `GnssService`. Location master switch was enabled at capture time.

```text
Applications
   | LocationManager / Google Play location APIs
   v
Android LocationManagerService
   | GPS provider -> GnssService -> GNSS HAL
   | Network provider -> Google Play Services
   | Fused provider -> Google Play Services
   ` Passive provider -> framework observations
```

This is based on the provider identities and target services reported by `dumpsys location`. The data path and cache behavior of Google Play Services still require device testing. It is therefore not yet justified to claim that GNSS-only interception would control fused/network/last-known results.

The raw `dumpsys` result was filtered to avoid collecting historical coordinates. No historical location was committed.


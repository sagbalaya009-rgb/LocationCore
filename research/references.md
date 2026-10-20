# References

- Android Open Source Project, [Android 14 release notes](https://source.android.com/docs/whatsnew/android-14-release) — GNSS AIDL requirements and HIDL support context.
- Android Developers, [LocationManager API](https://developer.android.com/reference/android/location/LocationManager) — test provider behavior and mock-location permission.
- TECNO, [official support](https://www.tecno-mobile.com/support/) — vendor contact route; no matching recovery package found during this pass.
- FRTech, [KL5 firmware listing](https://support.frtech.com/index.php?a=downloads&b=folder&id=1527) — third-party lead for older firmware only; **not** accepted as exact-build recovery evidence.
- HalabTech, [exact-name KL5 firmware listing](https://support.halabtech.com/index.php?a=downloads&b=file&id=1125619) — third-party listing matches this display build, but its archive has not been obtained or validated.
- Android Open Source Project, [Virtual A/B implementation](https://source.android.com/docs/core/ota/virtual_ab/implement) and [dynamic partitions](https://source.android.com/docs/core/ota/dynamic_partitions/implement) — partition and fastbootd context; device behavior still requires direct verification.
- Android Developers, [Location test-provider API](https://developer.android.com/reference/android/location/LocationManager) — official interface used for the disposable framework probe.
- Android Developers, [Get the Google USB Driver](https://developer.android.com/studio/run/win-usb) — official driver package used for the Windows fastbootd transport investigation; the driver was not bound because administrator access was unavailable.
- AOSP, [LocationProviderManager source](https://android.googlesource.com/platform/frameworks/base/+/d398fb78a98240905fbe7c52a77d74eaf1041788/services/core/java/com/android/server/location/provider/LocationProviderManager.java) — mock provider and mock last-location clearing behavior for comparison with the observed KL5 cache result.

Primary source for the device findings is read-only output from the connected phone on 2026-10-03. Raw output is private and excluded from Git.

## FOSSiBOT M20 tablet

- FOSSiBOT US, [official support page](https://us.fossibot.com/pages/support) — official support contact route; no exact M20 build package was exposed during the 2026-10-20 search.
- AOSP, [LocationProviderManager](https://android.googlesource.com/platform/frameworks/base/+/master/services/core/java/com/android/server/location/provider/LocationProviderManager.java) and [mock-removal test](https://android.googlesource.com/platform/frameworks/base/+/master/services/tests/mockingservicestests/src/com/android/server/location/provider/LocationProviderManagerTest.java) — reference behavior for clearing mock last-known locations when removing a test-provider override; tablet behavior was also checked through the public API.

Primary source for the tablet findings is privacy-filtered, read-only output from the connected tablet on 2026-10-20. Raw output and any live coordinates remain private and excluded from Git.


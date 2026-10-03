# Device results

## 2026-10-03 reconnaissance

- ADB listed one authorized device with model `TECNO_KL5`, product `KL5-OP`.
- Android property checks showed exact fingerprint in the dossier.
- Boot properties reported locked bootloader, green verified boot, slot B, dynamic partitions and Virtual A/B.
- VINTF files declared GNSS AIDL and HIDL interfaces; runtime services showed AIDL `IGnss/default` and HIDL 2.1 registration.
- `dumpsys location` identified Google Play Services network and fused providers and Android GPS provider.
- A later `adb devices -l` returned an empty device list; subsequent device tests await reconnection.

No static/movement/route mode has been installed or tested. No claim of real-location restoration is made.


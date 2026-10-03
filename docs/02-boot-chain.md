# Boot chain

The device reports a locked bootloader (`ro.boot.vbmeta.device_state=locked`), green verified boot, AVB 1.2, active slot B, A/B updates and Virtual A/B. SELinux is enforcing. This is a user/release-keys build.

`boot`, `init_boot`, `vendor_boot`, `dtbo`, and `vbmeta` have A/B partition entries. `super` holds dynamic partitions. Do not infer that generic Spark 30C images match this build.

No boot-chain changes have been made. Fastboot/fastbootd commands, unlock behavior, anti-rollback status and a complete stock package have not been validated. Any unlock that erases user data requires explicit user approval after an exact recovery route is established.


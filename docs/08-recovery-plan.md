# Recovery gate and plan

**Status: recovery path not validated. Do not unlock, root, flash, or replace GNSS services.**

Device-specific facts: Android 14 build `260615V2258`; bootloader locked; green AVB; active slot B; dynamic `super`; Virtual A/B. A third-party listing was found for an older `251209V2246` KL5 firmware. It does not match this build and must not be used as a recovery image. TECNO's official support page exists, but an exact-build stock image has not been obtained there.

Before any boot-chain change, obtain the exact signed stock package from TECNO/Carlcare or a provenance-verified source; confirm its product, region, revision and hashes; retain local copies of the exact required images; verify a Windows flashing path and bootloader/fastbootd capabilities; assess unlock data wipe and anti-rollback; preserve user data through an appropriate backup. Never flash preloader or another variant's images by inference.

ADB-only recovery for an eventual non-privileged app would be app uninstall. This does not constitute recovery from a failed boot-chain modification.

No backup image exists yet. No data-loss action has been proposed or performed.


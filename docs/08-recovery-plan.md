# Recovery gate and plan

**Status: recovery path not validated. Do not unlock, root, flash, or replace GNSS services.**

Device-specific facts: Android 14 build `260615V2258`; bootloader locked; green AVB; active slot B; dynamic `super`; Virtual A/B. A third-party [listing for a 6.12 GB archive](https://support.halabtech.com/index.php?a=downloads&b=file&id=1125619) names the exact display build. Its contents, signing, integrity, product match and recovery usability have **not** been checked. A separate listing for older `251209V2246` KL5 firmware does not match this build and must not be used. TECNO's official support page exists, but an exact-build stock image has not been obtained there.

Before any boot-chain change, obtain the exact signed stock package from TECNO/Carlcare or a provenance-verified source; confirm its product, region, revision and hashes; retain local copies of the exact required images; verify a Windows flashing path and bootloader/fastbootd capabilities; assess unlock data wipe and anti-rollback; preserve user data through an appropriate backup. Never flash preloader or another variant's images by inference.

ADB-only recovery for an eventual non-privileged app would be app uninstall. This does not constitute recovery from a failed boot-chain modification.

No backup image exists yet. No data-loss action has been proposed or performed. The USB connection was restored. A read-only bootloader-mode check failed to enumerate fastboot in Windows; the phone returned to Android on the same slot and build with green verified boot. Windows fastboot driver/transport needs diagnosis before it can be counted as a recovery path.

The phone has a `com.transsion.systemupdate` package and reports `persist.sys.ota_version` matching this build. No signed full package or readable on-device image was exposed by these read-only checks. The third-party download link was not accessible through the research browser; this is still a lead, not a backup.


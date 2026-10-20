# FOSSiBOT M20 recovery gate

**Status: recovery path not validated. Do not unlock, root, flash, or modify verified partitions.**

Exact target:

- Model/product: FOSSiBOT M20 / DT1
- Build: `M20_WXGA_V1.0_20241212`
- Fingerprint: `FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys`
- Locked, green AVB; active slot A; dynamic partitions and Virtual A/B

No exact-build signed factory package, image hashes, or validated Windows recovery route has been obtained. Public searching on 2026-10-20 found official FOSSiBOT support contact information but no matching M20 firmware download. Third-party images must not be accepted solely because their model name looks similar.

Before any privileged change:

1. Obtain an exact-build package from FOSSiBOT or another provenance-verifiable source.
2. Validate product/build metadata and cryptographic hashes; identify `boot`, `init_boot`, `vendor_boot`, vbmeta and recovery dependencies.
3. Validate the tablet's fastboot/fastbootd transport with read-only queries and establish the correct MediaTek recovery mechanism.
4. Determine whether unlocking is supported and whether it erases user data; back up user data before requesting explicit approval.
5. Prepare and test Windows recovery commands without flashing the connected tablet.

Application install/uninstall through ADB is currently the only validated reversible development path. It is not recovery from a failed boot-chain modification.

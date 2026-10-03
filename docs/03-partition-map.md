# Partition map

Read-only `/dev/block/by-name` inspection shows physical A/B entries for `boot`, `init_boot`, `vendor_boot`, `dtbo`, `vbmeta`, `vbmeta_system`, `vbmeta_vendor`, and MediaTek firmware partitions. `super` is present, with logical device-mapper partitions for Android system components. `metadata` and `userdata` are separate physical entries.

Relevant observed entries: `boot_a/b`, `init_boot_a/b`, `vendor_boot_a/b`, `dtbo_a/b`, `vbmeta_a/b`, `vbmeta_system_a/b`, `vbmeta_vendor_a/b`, `super`, `metadata`, `userdata`. `recovery` was not listed as a separate by-name partition. This does **not** prove that recovery is absent; its hosting mechanism needs confirmation.

Active slot is B. No image or partition content has been read or copied. No flash address or offset is approved for use.


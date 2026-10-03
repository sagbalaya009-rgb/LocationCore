# Known limitations

- No exact-build firmware archive has been downloaded, hashed or validated.
- Bootloader unlock behavior is untested. The fastbootd menu was observed, but its Windows driver/transport is unvalidated; no fastboot variable was read.
- No privileged engine, Android controller app or virtual-location injection has been built or installed.
- GNSS process/library call relationships are inferred from names and registration, not fully traced.
- Google Play Services fused/network behavior under virtualization is untested.
- Android's passive last-known cache retained a disposable mock sample after test-provider removal until fresh real updates arrived. A future STOP path must explicitly verify cache restoration.


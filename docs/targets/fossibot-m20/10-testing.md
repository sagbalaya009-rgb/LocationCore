# FOSSiBOT M20 test matrix

| Test | Result on 2026-10-20 |
| --- | --- |
| Exact-build guard | PASSED |
| Offline engine unit suite | PASSED: 6 tests, 0 failures |
| APK build/install/launch | PASSED |
| REAL default | PASSED after normal launch and emergency recovery relaunch |
| STATIC at 0,0 / 5 m | PASSED across GPS, network and fused; Android marked all mock |
| MOVEMENT 0,0 → 0,0.001 at 10 m/s | PASSED: observed longitude 0.000455, speed 10 m/s, bearing 90° after about 6 s |
| GPX replay with altitude | PASSED after Android parser compatibility fix: observed longitude 0.000457, altitude 14.57 m, speed 10 m/s, bearing 90° |
| Normal STOP | PASSED: provider overrides removed, foreground service stopped, public last-known API returned no mock |
| Emergency ADB cleanup during active STATIC | PASSED: package stopped, three overrides removed, shell app-op restored to default, original location-enabled state restored, relaunch reported REAL and no mock cache |
| Crash buffer after mode tests | PASSED: no LocationCore fatal exception found |
| Reboot default REAL | NOT RUN |
| Repeated transition endurance | NOT RUN |
| Real outdoor GNSS restoration/fix | NOT RUN; baseline delivered no fix indoors |
| File-picker GPX workflow | PARTIAL: parser and on-device route service passed with a private fixture; human picker flow not yet exercised |
| Map/search/favorites/recents | NOT IMPLEMENTED |
| Battery/performance measurement | NOT RUN |

Coordinates used by the automated integration fixtures were synthetic equatorial values. No personal or historical location is stored in Git.

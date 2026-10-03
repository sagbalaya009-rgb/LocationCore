# Test plan and execution rule

Status labels distinguish observed results from intended acceptance. Do not mark a case passed without the command output or application observation recorded in `11-device-results.md`.

| Test | Current status |
| --- | --- |
| ADB identity and authorization | VERIFIED on 2026-10-03, before later USB disconnect |
| Read-only build/boot/partition dossier | TESTED; shell access limits recorded |
| Offline engine unit tests | 6 passed, 0 failed on local JDK 17 / Gradle 9.5.0 |
| Real provider baseline | PARTIAL: network, fused, passive live non-mock updates; no fresh GPS fix observed |
| Disposable framework test provider | TESTED: add/set/remove worked with temporary app-op; passive cache retained dummy until fresh real update |
| Userspace fastbootd transport | PARTIAL: menu observed on phone; Windows driver absent; no fastboot variable read; Android returned unchanged |
| STATIC delivery and provider consistency | NOT RUN |
| MOVEMENT telemetry | NOT RUN |
| GPX replay | NOT RUN |
| STOP and real restoration | NOT RUN |
| Repeated transitions | NOT RUN |
| Reboot default REAL | NOT RUN |
| Crash fallback | NOT RUN |
| ADB kill switch | NOT RUN |

The read-only `scripts/windows/Read-DeviceState.ps1` checks a single authorized device and exact investigated build. It does not enable location virtualization or modify the phone.

The normal Android diagnostic app was built with Gradle 9.5.0 / Android Gradle Plugin 9.3.2, installed on the exact build, and opened. A first screenshot revealed a passive-provider attribution error because callbacks shared a listener. Separate listeners corrected it; a second private screenshot showed distinct network, fused and passive counters. The screenshot contains live coordinates and remains outside Git.

The engine tests cover coordinate validation, antimeridian and duplicate-waypoint interpolation, altitude, bearing, GPX parsing with DTD refusal, REAL default/STOP, movement progress and pause/resume. The engine has not yet emitted an Android `Location` on the phone.


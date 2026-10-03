# Test plan and execution rule

Status labels distinguish observed results from intended acceptance. Do not mark a case passed without the command output or application observation recorded in `11-device-results.md`.

| Test | Current status |
| --- | --- |
| ADB identity and authorization | VERIFIED on 2026-10-03, before later USB disconnect |
| Read-only build/boot/partition dossier | TESTED; shell access limits recorded |
| Real provider baseline | NOT RUN with controlled diagnostic client |
| STATIC delivery and provider consistency | NOT RUN |
| MOVEMENT telemetry | NOT RUN |
| GPX replay | NOT RUN |
| STOP and real restoration | NOT RUN |
| Repeated transitions | NOT RUN |
| Reboot default REAL | NOT RUN |
| Crash fallback | NOT RUN |
| ADB kill switch | NOT RUN |

The read-only `scripts/windows/Read-DeviceState.ps1` checks a single authorized device and exact investigated build. It does not enable location virtualization or modify the phone.


# Threat model

Assets: integrity of real location mode, bootability, user data, device identifiers, and the privileged command surface.

Primary failure modes: stale virtual location after STOP or reboot; inconsistent GNSS/network/fused data; unauthorized local app command; malformed GPX or IPC input; daemon crash; SELinux denial; OTA version drift; boot-loop after service replacement.

Required controls for a future implementation: boot defaults REAL; bounded and authenticated IPC; coordinates and GPX validation; no arbitrary command execution; build fingerprint guard; short-lived engine state; independent ADB disable path; fail-safe crash handling; exact-image recovery; no permanent permissive SELinux or disabled verified boot.

No third-party anti-abuse bypass is in scope.


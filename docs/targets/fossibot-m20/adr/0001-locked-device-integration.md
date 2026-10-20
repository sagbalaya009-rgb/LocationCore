# ADR 0001: locked-device integration milestone

Status: accepted for the reversible application milestone; privileged final architecture remains open.

## Context

The FOSSiBOT M20 is locked, unrooted, non-debuggable, enforcing SELinux, and lacks a validated exact-build recovery package. Its framework combines Android GPS with Google network/fused providers. Modifying verified partitions now would violate the recovery gate.

## Options

| Option | Control and consistency | Risk / reversibility | Current decision |
| --- | --- | --- | --- |
| Normal Android test providers | Framework-visible, officially supported, always marked mock; fused behavior must be measured | Low; app uninstall and provider removal are available | Use for STATIC/MOVEMENT/ROUTE application milestone |
| Root framework hook | Potentially broad provider consistency | Requires root and boot changes; recovery missing | Defer |
| Privileged daemon + framework bridge | Strong policy separation | Requires system privilege, SELinux work and installation path | Defer |
| GNSS HAL proxy | Deep satellite control; does not automatically own Google network/fused output | High firmware dependence and boot risk | Reject as sole integration layer |
| MediaTek vendor interception | Deep vendor control | Very high firmware dependence and restoration risk | Defer |
| Kernel implementation | Controls a lower hardware path, not Android `Location` objects directly | Highest complexity and poor provider consistency | Reject for this evidence |
| Hybrid privileged framework + daemon | Best prospective full-system consistency | Needs validated root/recovery | Preferred research direction after recovery gate |

## Decision

Build and test a non-root controller around Android test providers first. It must start in REAL, use a foreground service only while virtual mode is active, never auto-start after reboot, remove every test provider on STOP/task removal/service destruction, provide an ADB recovery procedure, and display that Android marks output as mock.

This milestone is useful and reversible but is not represented as the final deep system implementation. A privileged hybrid remains the leading candidate only after exact firmware and recovery are validated.

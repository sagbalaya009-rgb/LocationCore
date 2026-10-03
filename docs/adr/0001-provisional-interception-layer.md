# ADR 0001: Defer privileged interception selection

Status: PROVISIONAL, 2026-10-03.

## Context

The connected TECNO KL5 has a locked, green AVB boot chain and no demonstrated root access. Android `LocationManagerService` sees a MediaTek GNSS service plus Google-owned network and fused providers. A GNSS-only proxy cannot be assumed to cover the latter. Exact-build recovery is not validated.

## Decision

Continue read-only tracing and reversible local development. Do not select a final privileged integration or modify the device boot chain until an exact-build recovery route and provider-level acceptance measurements exist. Favor a narrow daemon plus framework/provider integration for design exploration; retain HAL proxying as a candidate if tests show it improves consistency. Do not use kernel interception solely for depth.

## Consequences

Device-level virtualization acceptance remains BLOCKED. This preserves the working phone and avoids presenting a higher-level mock provider as the finished system. Revisit this ADR with measured results and a recovery package hash before installation.


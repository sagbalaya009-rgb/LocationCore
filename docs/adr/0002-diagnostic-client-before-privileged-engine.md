# ADR 0002: Install a read-only provider diagnostic client

Status: ACCEPTED for diagnostic development, 2026-10-03.

## Context

The phone is locked and unrooted. Fastboot was not available in a read-only bootloader-mode attempt, and exact-build stock recovery has not been validated. Privileged interception remains unsafe to install. The project still needs measured real-provider behavior to choose and test any later integration.

## Decision

Build and install a normal Android app that observes `LocationManager` providers while in the foreground. It requests ordinary runtime location permission, does not inject or persist locations, and can be removed with `adb uninstall`. The app is a diagnostic component, not the finished LocationCore controller or engine.

## Consequences

This gives a reversible real-provider baseline without changing the boot chain. Live coordinates may appear on the phone screen but are not written to Git or diagnostic logs. Privileged virtualization and final architecture selection remain gated.


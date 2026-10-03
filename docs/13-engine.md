# Offline engine foundation

`engine/` is pure Java 17. It contains:

- `GeoPoint`: finite WGS84 latitude/longitude validation and optional altitude.
- `Route`: haversine distance, great-circle interpolation, bearing, altitude interpolation, clamped progress and duplicate-waypoint handling. It uses binary search over cumulative distance for per-update lookup.
- `GpxParser`: bounded SAX parsing of GPX track or route points, maximum 5 MiB / 10,000 points, with DTD and external entities refused.
- `LocationEngine`: volatile REAL-on-construction state machine with STATIC, MOVEMENT and ROUTE modes, pause/resume, STOP, speed/accuracy validation, elapsed realtime progress and wall/elapsed sample timestamps.
- `LocationSample`: an integration-neutral coordinate and telemetry record.

`LocationEngine` deliberately has no device privilege and does not write Android provider state. It is a foundation for the eventual integration layer, which remains undecided until exact-build recovery is credible. No route, movement or static virtual coordinate has been sent to the phone.

Run `gradlew.bat :engine:test`. On 2026-10-03 this ran 6 tests with 0 failures and 0 errors. GPX timestamp-based replay, speed and bearing uncertainty metadata, IPC, and on-device provider delivery are not implemented yet.


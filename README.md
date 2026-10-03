# LocationCore

Device-specific Android location virtualization research for the user's TECNO Spark 30C (`TECNO-KL5`, `KL5-OP`).

**Current state: DEVELOPMENT PAUSED ON PHONE; TABLET TARGET PENDING.** The read-only diagnostic app was uninstalled from the phone at the user's request on 2026-10-03. No virtualization component was installed. The phone has not been rooted, unlocked, or flashed. Retain the engine and research as provisional work; the tablet requires a fresh device dossier and architecture decision before any installation.

The device is on Android 14 build `KL5-XK678ABCDEFGHIJKLMNAc-U-OP-260615V2258`, fingerprint `TECNO/KL5-OP/TECNO-KL5:14/UP1A.231005.007/260615V2258:user/release-keys`. Any future installation must refuse other builds until validated.

The first read-only investigation found a locked AVB boot chain and MediaTek AIDL GNSS service. The network and fused providers are supplied by Google Play Services. A recovery image matching this exact build has **not** been obtained or validated. Privileged device changes are therefore paused.

The diagnostic app displays live GPS, network, fused and passive provider metadata while open. It does not inject or save location. It is not the finished controller. See [device dossier](docs/01-device-dossier.md), [location architecture](docs/04-location-stack.md), [GNSS architecture](docs/05-gnss-stack.md), [recovery gate](docs/08-recovery-plan.md), and [diagnostic build/install steps](docs/09-installation.md).

`engine/` contains a platform-independent coordinate, GPX and state-machine library. Its tests run locally; it is not connected to the Android provider stack yet.


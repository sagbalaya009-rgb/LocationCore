# LocationCore

Device-specific Android location virtualization research for the user's TECNO Spark 30C (`TECNO-KL5`, `KL5-OP`).

**Current state: RECONNAISSANCE. No virtualization component is installed. The phone has not been rooted, unlocked, flashed, or otherwise modified.**

The device is on Android 14 build `KL5-XK678ABCDEFGHIJKLMNAc-U-OP-260615V2258`, fingerprint `TECNO/KL5-OP/TECNO-KL5:14/UP1A.231005.007/260615V2258:user/release-keys`. Any future installation must refuse other builds until validated.

The first read-only investigation found a locked AVB boot chain and MediaTek AIDL GNSS service. The network and fused providers are supplied by Google Play Services. A recovery image matching this exact build has **not** been obtained or validated. Privileged device changes are therefore paused.

See [device dossier](docs/01-device-dossier.md), [location architecture](docs/04-location-stack.md), [GNSS architecture](docs/05-gnss-stack.md), and [recovery gate](docs/08-recovery-plan.md).


# Security and privacy

This private project is limited to controlled location virtualization on the owner's phone. Do not add functionality to defeat banking, identity, game, access-control, or safety systems.

Do not commit raw device logs, serials, account identifiers, precise location history, firmware blobs, signing keys, or tokens. Capture raw diagnostic output privately, sanitize before publication, and review `git diff --cached` before every push.

Future privileged components must have narrow authenticated IPC, least privilege, enforcing SELinux, a REAL-on-boot default, and independent disable/recovery paths.


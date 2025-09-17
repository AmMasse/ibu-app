# Build Dependency & Build Info Note

## App Version Information
- **Version code:** 2
- **Version name:** 1.1.0.0
- **Application ID:** app.vercel.local_genius_guide.twa
- **Minimum SDK:** 23
- **Target SDK:** 35
- **Compile SDK:** 36

## Build Dependencies
- The file `source/cmdline-tools.zip` was removed from version control because it exceeds GitHub's 100 MB file size limit. This file is required for building the Android app and must be manually downloaded or restored before running builds.
- **Action Required:** Download the Android SDK command line tools and place them at `source/cmdline-tools.zip` before building or updating the app.

## Signing Information (for release builds)
- **Keystore file:** `signing.keystore`
- **Key alias:** `ibu-key`
- **Keystore password:** _see secure storage or previous build notes_
- **Key password:** _see secure storage or previous build notes_

## Other Notes
- If you update the app version, change `versionCode` and `versionName` in `source/app/build.gradle` under `defaultConfig`.
- Make sure all required dependencies are present in `source/app/build.gradle`.
- For Play Store release, use the signed and aligned APK/AAB from `source/app/build/outputs/apk/release/` and `source/app/build/outputs/bundle/release/`.

---

_Last updated: 2025-09-17_

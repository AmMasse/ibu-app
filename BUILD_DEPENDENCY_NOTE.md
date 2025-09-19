# Build Dependency & Build Info Note

## App Version Information
- **Version code:** 4
- **Version name:** 1.1.0.1
- **Application ID:** app.vercel.local_genius_guide.twa
- **Minimum SDK:** 23
- **Target SDK:** 35
- **Compile SDK:** 36


## Build Dependencies
- **Android SDK:**
	- Download and extract the Android SDK (command line tools, build-tools, platform-tools, platforms, licenses) to a known location (e.g., `/home/codespace/android-sdk`).
	- Ensure the following subfolders exist inside your SDK directory:
		- `cmdline-tools/`
		- `build-tools/`
		- `platform-tools/`
		- `platforms/`
		- `licenses/`
	- You can use the `sdkmanager` tool to install missing components:
		- Example: `sdkmanager "build-tools;36.0.0" "platforms;android-36" "platform-tools"`
	- Accept all licenses: `yes | sdkmanager --licenses`

- **local.properties:**
	- The file `source/local.properties` must contain the absolute path to your Android SDK:
		- Example: `sdk.dir=/home/codespace/android-sdk`
	- Do NOT use environment variables like `$HOME` in this file; use the full path.

- **Java Version:**
	- Java 17 is required. Set `JAVA_HOME` to the Java 17 installation directory before building:
		- Example: `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`
		- Add to `PATH`: `export PATH=$JAVA_HOME/bin:$PATH`

- **Other:**
	- The file `source/cmdline-tools.zip` was removed from version control because it exceeds GitHub's 100 MB file size limit. If you need to re-bootstrap the SDK, download this file and extract it as needed.

**Action Required Before Building:**
1. Ensure the Android SDK is present and all required components are installed.
2. Ensure `local.properties` points to the correct absolute SDK path.
3. Ensure Java 17 is installed and `JAVA_HOME` is set.
4. (Optional) Download and extract `cmdline-tools.zip` if re-initializing the SDK.

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

# IBU Android App Bundle Build Guide

This documents how to build a **signed Android App Bundle (`.aab`)** for the IBU Android project from a fresh Google Cloud Shell environment.

## 1. Open the project

```bash
cd ~/ibu-app/source
```

The project structure should contain:

```text
app/
gradle/
gradlew
build.gradle
settings.gradle
local.properties
signingKey.keystore
```

---

# 2. Check Java

The project requires Java 17.

```bash
java -version
```

Expected:

```text
openjdk version "17..."
```

Also check Gradle:

```bash
./gradlew --version
```

Expected:

```text
Gradle 8.11.1
Launcher JVM: 17
```

---

# 3. Install Android SDK in a fresh Cloud Shell

A fresh Cloud Shell may not have the Android SDK installed.

Create the SDK directory:

```bash
mkdir -p $HOME/android-sdk/cmdline-tools
cd /tmp
```

Download Google's Android command-line tools:

```bash
wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
```

Extract:

```bash
unzip -q commandlinetools-linux-11076708_latest.zip -d $HOME/android-sdk/cmdline-tools
```

Rename the directory:

```bash
mv $HOME/android-sdk/cmdline-tools/cmdline-tools $HOME/android-sdk/cmdline-tools/latest
```

---

# 4. Configure the Android SDK

Run:

```bash
export ANDROID_HOME=$HOME/android-sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
```

Check:

```bash
sdkmanager --version
```

Expected:

```text
12.0
```

> These `export` commands apply to the current Cloud Shell session. If Cloud Shell starts a completely new session later, run them again.

---

# 5. Check the Android SDK requirements

From the project:

```bash
cd ~/ibu-app/source
```

Check the Android versions:

```bash
grep -E "compileSdk|buildToolsVersion|minSdk|targetSdk" app/build.gradle
```

The project currently uses:

```text
compileSdkVersion 36
minSdkVersion 24
targetSdkVersion 36
```

**Important:** Google Play currently requires this project to target API 36, so make sure `targetSdkVersion` is `36`.

---

# 6. Install the required Android SDK components

Run:

```bash
sdkmanager "platform-tools" "platforms;android-36" "build-tools;36.0.0"
```

Accept the licenses when prompted:

```text
y
```

---

# 7. Configure `local.properties`

Set the Android SDK path:

```bash
cd ~/ibu-app/source
echo "sdk.dir=$HOME/android-sdk" > local.properties
```

Check:

```bash
cat local.properties
```

Expected:

```text
sdk.dir=/home/amanyire_daniel1/android-sdk
```

The username will naturally be different if using another Google account.

---

# 8. Check the signing keystore

This project already has:

```text
source/signingKey.keystore
```

Check that it exists:

```bash
ls -l signingKey.keystore
```

Check the keystore:

```bash
keytool -list -keystore signingKey.keystore
```

Enter the keystore password when prompted.

The project's key alias is:

```text
ibu-key
```

The keystore password and key password are the same for this project.

**Never put the password into Git or commit it to the repository.**

---

# 9. Configure signing in `app/build.gradle`

The `signingConfigs` section should point to the actual keystore filename:

```groovy
signingConfigs {
    release {
        def ksPath = findProperty("RELEASE_STORE_FILE") ?: System.getenv("KEYSTORE_PATH")
        if (ksPath) {
            storeFile file(ksPath)
        } else if (file("signingKey.keystore").exists()) {
            storeFile file("signingKey.keystore")
        } else {
            storeFile file("../signingKey.keystore")
        }
        storePassword findProperty("RELEASE_STORE_PASSWORD") ?: System.getenv("KEYSTORE_PASSWORD") ?: ""
        keyAlias findProperty("RELEASE_KEY_ALIAS") ?: System.getenv("KEY_ALIAS") ?: "ibu-key"
        keyPassword findProperty("RELEASE_KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD") ?: ""
    }
}
```

And the release build type must contain:

```groovy
buildTypes {
    release {
        minifyEnabled true
        signingConfig signingConfigs.release
    }
}
```

---

# 10. Set the signing credentials

From:

```bash
cd ~/ibu-app/source
```

Run:

```bash
export KEYSTORE_PATH="$HOME/ibu-app/source/signingKey.keystore"
export KEYSTORE_PASSWORD='YOUR_PASSWORD'
export KEY_ALIAS='ibu-key'
export KEY_PASSWORD='YOUR_PASSWORD'
```

Replace `YOUR_PASSWORD` with the actual password.

These variables exist only in the current shell session.

Do **not** commit them to the repository.

---

# 11. Build the signed AAB

Run:

```bash
./gradlew bundleRelease
```

A successful build should end with something similar to:

```text
BUILD SUCCESSFUL
```

The signed bundle will be located at:

```text
app/build/outputs/bundle/release/app-release.aab
```

You can check:

```bash
ls -lh app/build/outputs/bundle/release/app-release.aab
```

---

# 12. Verify the AAB signature

Run:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

A successful verification should indicate that the JAR/bundle has been verified.

---

# 13. Git configuration

If Git says:

```text
Please tell me who you are
```

configure your Git identity:

```bash
git config --global user.name "Amanyire Daniel"
git config --global user.email "YOUR_GITHUB_EMAIL"
```

Check:

```bash
git config --global user.name
git config --global user.email
```

---

# Quick rebuild checklist

For future builds, the normal flow is:

```bash
cd ~/ibu-app/source

export ANDROID_HOME=$HOME/android-sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

export KEYSTORE_PATH="$HOME/ibu-app/source/signingKey.keystore"
export KEYSTORE_PASSWORD='YOUR_PASSWORD'
export KEY_ALIAS='ibu-key'
export KEY_PASSWORD='YOUR_PASSWORD'

./gradlew bundleRelease
```

Then get the bundle from:

```text
app/build/outputs/bundle/release/app-release.aab
```

## If starting a completely fresh Cloud Shell

Do the SDK installation steps first:

1. Install command-line tools.
2. Configure `ANDROID_HOME`.
3. Install Android 36 platform/build tools.
4. Set `local.properties`.
5. Set signing credentials.
6. Run `./gradlew bundleRelease`.

## Important files

| File                  | Purpose                                 |
| --------------------- | --------------------------------------- |
| `app/build.gradle`    | Android build and signing configuration |
| `local.properties`    | Local Android SDK location              |
| `signingKey.keystore` | Release signing key                     |
| `app-release.aab`     | Final Play Store bundle                 |

## Important security note

The **keystore is extremely important**. Keep a secure backup of:

```text
signingKey.keystore
```

and its passwords.

Do not commit the keystore or passwords to a public Git repository.

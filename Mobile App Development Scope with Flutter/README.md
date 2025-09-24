# secure_mobile_app

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://docs.flutter.dev/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://docs.flutter.dev/cookbook)

For help getting started with Flutter development, view the
[online documentation](https://docs.flutter.dev/), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Security, Obfuscation, and Signing

- Android code obfuscation (R8):
  - Debug run: `flutter run`
  - Release with obfuscation and split-debug-info:
    ```bash
    flutter build apk --release \
      --obfuscate \
      --split-debug-info=build/debug-info
    ```
  - Keep the symbol files under `build/debug-info` securely for stack trace deobfuscation.

- iOS release build:
  - Open `ios/Runner.xcworkspace` in Xcode
  - Set a valid signing team and provisioning profile
  - Archive and distribute via Xcode Organizer

- Android signing (keystore):
  1. Generate a keystore:
     ```bash
     keytool -genkey -v -keystore ~/.keystores/secure_app.keystore -alias secure_key -keyalg RSA -keysize 2048 -validity 10000
     ```
  2. Create `android/key.properties` and reference it in `build.gradle` signingConfigs.
  3. Build:
     ```bash
     flutter build apk --release
     ```

- HTTPS: Ensure all API endpoints use HTTPS. For development, consider pinning or certificate validation on the backend.

- Secure storage: Tokens/password hashes are stored with `flutter_secure_storage` (encrypted on device).

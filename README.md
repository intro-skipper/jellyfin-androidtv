<h1 align="center">Jellyfin Android TV with skip button</h1>

## Build Process

### Dependencies

- Android Studio

### Build

1. Clone or download this repository

   ```sh
   git clone https://github.com/intro-skipper/jellyfin-androidtv.git
   cd jellyfin-androidtv
   ```

2. Open the project in Android Studio and run it from there or build an APK directly through Gradle:

   ```sh
   ./gradlew assembleDebug
   ```
   
   Add the Android SDK to your PATH environment variable or create the ANDROID_SDK_ROOT variable for
   this to work.

### Deploy to device/emulator

   ```sh
   ./gradlew installDebug
   ```

*You can also replace the "Debug" with "Release" to get an optimized release binary.*

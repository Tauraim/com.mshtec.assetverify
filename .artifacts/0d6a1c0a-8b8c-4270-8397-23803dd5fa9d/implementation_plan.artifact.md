# Fix Build Errors and Missing Symbols

The project is failing to compile due to missing imports, missing activity declarations in the manifest, and unresolved class references. The initial error `cannot find symbol class AppCompatActivity` was likely due to a missing import in some files and a dependency synchronization issue which has been addressed.

## User Review Required

> [!IMPORTANT]
> I noticed that `DashboardActivity` is referenced in `LoginActivity` but does not exist. I will rename this reference to `MainActivity`, which already exists in the project.
> I will also add `SplashActivity` and `LoginActivity` to the `AndroidManifest.xml` as they are currently missing.

## Proposed Changes

### Java Source Code

#### [MODIFY] [LoginActivity.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/LoginActivity.java)
- Add missing imports: `android.content.Intent`, `android.os.Bundle`, `android.widget.EditText`.
- Change `DashboardActivity.class` to `MainActivity.class`.

#### [MODIFY] [SplashActivity.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/SplashActivity.java)
- Add missing imports: `androidx.appcompat.app.AppCompatActivity`, `android.content.Intent`, `android.os.Handler`.

### Android Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/AndroidManifest.xml)
- Register `SplashActivity` and `LoginActivity`.
- Set `SplashActivity` as the launcher activity (since it's a splash screen).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project compiles and builds successfully.

### Manual Verification
- Deploy the app to a device to verify the splash screen flows to the login screen and then to the main activity.

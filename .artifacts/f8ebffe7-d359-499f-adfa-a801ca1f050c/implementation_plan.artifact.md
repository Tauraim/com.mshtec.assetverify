# Fix "cannot find symbol variable activity_dashboard" error

The build error in [Dashboard.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/Dashboard.java) occurs because the layout file `activity_dashboard.xml` is missing from the `res/layout` directory. Additionally, the `Dashboard` activity is not declared in the `AndroidManifest.xml`.

## Proposed Changes

### [Component Name] res/layout

#### [NEW] [activity_dashboard.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/layout/activity_dashboard.xml)
- Create a basic layout file for the Dashboard activity.

### [Component Name] app/src/main

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/AndroidManifest.xml)
- Declare the `Dashboard` activity so it can be used in the application.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to verify that the compilation error is resolved.

### Manual Verification
- Verify that the project builds successfully in Android Studio.

# Fix Android Resource Linking Error

The build is failing because `AndroidManifest.xml` references non-existent resources `@xml/activity_login` and `@xml/activity_splash` in the `android:dataExtractionRules` and `android:fullBackupContent` attributes.

Investigation shows that `activity_login.xml` and `activity_splash.xml` exist in `res/layout`, not `res/xml`. These attributes are for backup and data extraction rules, which require specific XML schemas and should reside in `res/xml`. It appears these layout names were mistakenly used in these attributes.

## Proposed Changes

### Android Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/AndroidManifest.xml)
- Remove `android:dataExtractionRules="@xml/activity_login"`
- Remove `android:fullBackupContent="@xml/activity_splash"`

## Verification Plan

### Automated Tests
- Run `./gradlew :app:processDebugResources` to verify that resource linking succeeds.
- Run a full build `./gradlew assembleDebug` to ensure no other issues exist.

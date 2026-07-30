# Implementation Plan - Enhance Dashboard Icons and Notifications

The user wants to adjust the appearance of the dashboard icons (color, size) and add a notification count badge to the bell icon.

## Proposed Changes

### Icon Appearance

#### [MODIFY] [ic_notifications.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_notifications.xml)
- Change size from `24dp` to `28dp`.
- Update `android:tint` to `@color/onPrimary` (white) for better visibility on the blue toolbar.

#### [MODIFY] [ic_account_circle.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_account_circle.xml)
- Change size from `24dp` to `28dp`.
- Update `android:tint` to `@color/onPrimary`.

#### [MODIFY] [ic_menu.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_menu.xml)
- Change size from `24dp` to `28dp`.
- Update `android:tint` to `@color/onPrimary`.

### Notification Badge

#### [MODIFY] [Dashboard.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/Dashboard.java)
- Use `BadgeDrawable` and `BadgeUtils` to attach a notification count (e.g., "5") to the notification menu item.
- Note: `BadgeUtils` may require an `@UnsafeOptInUsageError` or similar annotation depending on the library version.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug`.

### Manual Verification
- Deploy the app.
- Confirm the top bar icons are white, larger, and the bell icon displays a red badge with the number 5.

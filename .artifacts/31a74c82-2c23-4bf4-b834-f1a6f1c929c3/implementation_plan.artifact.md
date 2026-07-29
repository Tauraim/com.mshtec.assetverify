# Implementation Plan - Fix Resource Linking Errors

The project is failing to build due to missing resources and a misplaced menu resource file.

## Proposed Changes

### [Resources]

#### [NEW] [dashboard_menu.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/menu/dashboard_menu.xml)
- Create the `res/menu` directory and move (copy) the `dashboard_menu.xml` content here. It is currently incorrectly located in `res/layout`.

#### [NEW] [ic_menu.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_menu.xml)
- Add missing menu icon.

#### [NEW] [ic_notifications.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_notifications.xml)
- Add missing notifications icon.

#### [NEW] [ic_account_circle.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/drawable/ic_account_circle.xml)
- Add missing profile icon.

#### [DELETE] dashboard_menu.xml (from layout)
- The file at `app/src/main/res/layout/dashboard_menu.xml` should be removed as it is a menu resource, not a layout. (I will use a shell command to remove it as it's a cleanup step).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:processDebugResources` or `./gradlew assembleDebug` to verify that resource linking succeeds.

### Manual Verification
- Check the layout preview of `activity_dashboard.xml` to ensure icons and menus are correctly resolved.

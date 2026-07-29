# Implementation Plan - Fix Dashboard Visibility and Navigation

The user is not seeing the Dashboard screen when running the app. Research shows that `SplashActivity` navigates to `LoginActivity`, which then navigates to `MainActivity` instead of the `Dashboard` activity. Additionally, the dashboard layout needs some adjustments to match the desired visual structure.

## Proposed Changes

### Navigation Fix

#### [MODIFY] [LoginActivity.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/LoginActivity.java)
Update the login button's click listener to navigate to `Dashboard` instead of `MainActivity`.

### Dashboard Layout Improvements

#### [MODIFY] [activity_dashboard.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/res/layout/activity_dashboard.xml)
- Remove `app:titleCentered="true"` from the `MaterialToolbar` to ensure the title is correctly aligned (standard left-alignment after the navigation icon).
- Replace `android:layout_marginTop="?attr/actionBarSize"` in `NestedScrollView` with `app:layout_behavior="@string/appbar_scrolling_view_behavior"` for better integration with `CoordinatorLayout`.
- Ensure the FAB is correctly positioned and visible.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project still builds.

### Manual Verification
1. Deploy the app.
2. Wait for `SplashActivity` to transition to `LoginActivity`.
3. Click the Login button.
4. Verify that the `Dashboard` screen is displayed with the expected top bar (hamburger, title, icons) and FAB.

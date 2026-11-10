# Room Barcode Lookup and Asset Verification Implementation Plan

Implement room barcode scanning/typing lookup, database check, room status display, asset verification workflow, and room creation prompt for unknown barcodes.

## User Review Required

- **Database:** Using SQLite (`SQLiteOpenHelper`) for reliable local data persistence of rooms and assets.
- **Workflow:** Clicking the verification FAB on the Dashboard opens `RoomLookupActivity`. Users can scan/type a barcode. If found, it displays room status and leads to `AssetVerificationActivity`. If not found, it prompts the user to create a new room.

## Proposed Changes

### Database & Models
#### [NEW] [DatabaseHelper.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/DatabaseHelper.java)
- Manages SQLite database for `rooms` (barcode, name, status, floor) and `assets` (barcode, name, room_barcode, status). Pre-populated with sample data.

### Activities & Layouts
#### [NEW] [RoomLookupActivity.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/RoomLookupActivity.java)
- Input field for room barcode, scan button simulation, lookup logic, status display, and room creation dialog trigger.
#### [NEW] [AssetVerificationActivity.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/AssetVerificationActivity.java)
- Displays assets in the verified room and allows checking them off / verifying them.
#### [MODIFY] [Dashboard.java](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/java/com/example/msh_verify/Dashboard.java)
- Update `fabVerify` click listener to start `RoomLookupActivity`.
#### [NEW] Layout XML files (`activity_room_lookup.xml`, `activity_asset_verification.xml`)
- Material 3 styled UI components.
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/tmuze/AndroidStudioProjects/MSH_VERIFY/app/src/main/AndroidManifest.xml)
- Register `RoomLookupActivity` and `AssetVerificationActivity`.

## Verification Plan

### Automated Tests
- Run `./gradlew test` and `./gradlew assembleDebug` to ensure successful compilation.
- Verify build success.

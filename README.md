That is a very practical project. An Asset Verification System for municipalities is much more than a simple inventory app. It should work similarly to Dynamic Verify, 
allowing field inspectors to verify assets using an Android device while synchronizing with a central server.
                  Android App (Java)     or  
                           | 
                    (Field Officers)
                           |
                    REST API (HTTPS)
                           |
               Spring Boot Java Backend
                           |
                  PostgreSQL / MySQL
                           |
                  File Storage (Photos)
Modules
1. Login
- Username
- Password
- Remember Login
- Municipality Logo
 -------------------------
| Municipality Logo       |
|                         |
| Username                |
| _____________________   |
| Password                |
| _____________________   |
|                         |
| [ Login ]               |
 -------------------------
                                    
Camera & Hardware Laser Barcode/QR Scanning Walkthrough
Added support for camera-based QR/barcode scanning and external hardware laser barcode scanners.
Changes Made
Permissions & Manifest
•
Added <uses-permission android:name="android.permission.CAMERA" /> and camera feature declarations to  AndroidManifest.xml.
Hardware Laser Scanner & Camera Integration
•
Updated  RoomLookupActivity.java:
◦
Hardware Laser Scanners (Keyboard Wedge / HID): Added setOnEditorActionListener on the barcode input field to automatically trigger lookup upon pressing Enter or completing input from external handheld laser barcode scanners.
◦
Camera QR/Barcode Scanning: Added runtime camera permission handling (ActivityResultContracts.RequestPermission) and scanner intent launcher (barcodeScannerLauncher) with fallback test simulator options.
Validation Results

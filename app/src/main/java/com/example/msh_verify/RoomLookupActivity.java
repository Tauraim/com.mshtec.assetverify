package com.example.msh_verify;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class RoomLookupActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private EditText edtRoomBarcode;
    private MaterialButton btnScanBarcode, btnLookupRoom, btnVerifyAssets;
    private MaterialCardView cardRoomDetails;
    private TextView txtRoomName, txtRoomFloor, txtRoomStatus;

    private DatabaseHelper dbHelper;
    private DatabaseHelper.RoomModel currentRoom;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Intent> barcodeScannerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_lookup);

        dbHelper = new DatabaseHelper(this);

        topAppBar = findViewById(R.id.topAppBar);
        edtRoomBarcode = findViewById(R.id.edtRoomBarcode);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        btnLookupRoom = findViewById(R.id.btnLookupRoom);
        cardRoomDetails = findViewById(R.id.cardRoomDetails);
        txtRoomName = findViewById(R.id.txtRoomName);
        txtRoomFloor = findViewById(R.id.txtRoomFloor);
        txtRoomStatus = findViewById(R.id.txtRoomStatus);
        btnVerifyAssets = findViewById(R.id.btnVerifyAssets);

        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        // Initialize Camera Permission & Scanner Launchers
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCameraScanner();
                    } else {
                        Toast.makeText(this, "Camera permission is required to scan QR/Barcodes", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        barcodeScannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String scannedBarcode = result.getData().getStringExtra("SCAN_RESULT");
                        if (scannedBarcode == null) {
                            scannedBarcode = result.getData().getStringExtra("qr_code");
                        }
                        if (scannedBarcode != null && !scannedBarcode.isEmpty()) {
                            edtRoomBarcode.setText(scannedBarcode);
                            performLookup(scannedBarcode.trim().toUpperCase());
                        }
                    }
                }
        );

        // Hardware Laser Barcode Scanner Support (Keyboard Wedge / HID / Enter Key detection)
        edtRoomBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String barcode = edtRoomBarcode.getText().toString().trim().toUpperCase();
                if (!TextUtils.isEmpty(barcode)) {
                    performLookup(barcode);
                }
                return true;
            }
            return false;
        });

        btnScanBarcode.setOnClickListener(v -> {
            checkAndRequestCameraPermission();
        });

        btnLookupRoom.setOnClickListener(v -> {
            String barcode = edtRoomBarcode.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(barcode)) {
                edtRoomBarcode.setError("Please enter or scan a room barcode");
                return;
            }
            performLookup(barcode);
        });

        btnVerifyAssets.setOnClickListener(v -> {
            if (currentRoom != null) {
                Intent intent = new Intent(RoomLookupActivity.this, AssetVerificationActivity.class);
                intent.putExtra("ROOM_BARCODE", currentRoom.getBarcode());
                intent.putExtra("ROOM_NAME", currentRoom.getName());
                startActivity(intent);
            }
        });
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraScanner();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCameraScanner() {
        try {
            // Attempt to launch standard ZXing / Barcode Scanner Intent
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
            barcodeScannerLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            // If no external scanner app is installed, fall back to simulated scanner / test picker with camera message
            String[] sampleBarcodes = {"RM-101", "RM-102", "RM-999"};
            new AlertDialog.Builder(this)
                    .setTitle("Camera Scanner / Laser Ready")
                    .setMessage("Camera scanner app not detected. You can use your hardware laser scanner, type the barcode, or select a sample room:")
                    .setItems(sampleBarcodes, (dialog, which) -> {
                        edtRoomBarcode.setText(sampleBarcodes[which]);
                        performLookup(sampleBarcodes[which]);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void performLookup(String barcode) {
        currentRoom = dbHelper.getRoom(barcode);
        if (currentRoom != null) {
            cardRoomDetails.setVisibility(View.VISIBLE);
            txtRoomName.setText(currentRoom.getName());
            txtRoomFloor.setText(currentRoom.getFloor());
            txtRoomStatus.setText(currentRoom.getStatus());
            if ("Verified".equalsIgnoreCase(currentRoom.getStatus())) {
                txtRoomStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                txtRoomStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            Toast.makeText(this, "Room found!", Toast.LENGTH_SHORT).show();
        } else {
            cardRoomDetails.setVisibility(View.GONE);
            showCreateRoomDialog(barcode);
        }
    }

    private void showCreateRoomDialog(String barcode) {
        new AlertDialog.Builder(this)
                .setTitle("Room Not Found")
                .setMessage("The barcode '" + barcode + "' does not exist in the database. Would you like to create a new room for this barcode?")
                .setPositiveButton("Create Room", (dialog, which) -> {
                    final EditText inputName = new EditText(this);
                    inputName.setHint("Room Name (e.g. Conference Room B)");

                    new AlertDialog.Builder(this)
                            .setTitle("New Room Details")
                            .setView(inputName)
                            .setPositiveButton("Save & Verify", (d, w) -> {
                                String roomName = inputName.getText().toString().trim();
                                if (TextUtils.isEmpty(roomName)) {
                                    roomName = "Room " + barcode;
                                }
                                boolean success = dbHelper.addRoom(barcode, roomName, "Pending Verification", "Floor 1");
                                if (success) {
                                    Toast.makeText(this, "Room created successfully!", Toast.LENGTH_SHORT).show();
                                    performLookup(barcode);
                                } else {
                                    Toast.makeText(this, "Failed to create room.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

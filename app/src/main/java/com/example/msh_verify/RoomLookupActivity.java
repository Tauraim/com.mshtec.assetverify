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
import android.widget.AutoCompleteTextView;
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

import java.util.List;

public class RoomLookupActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private EditText edtRoomBarcode;
    private MaterialButton btnScanBarcode, btnLookupRoom, btnVerifyAssets, btnEditRoom;
    private MaterialCardView cardRoomDetails;
    private TextView txtRoomName, txtRoomFloor, txtRoomStatus, txtRoomBuilding, txtRoomResponsible;

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
        txtRoomBuilding = findViewById(R.id.txtRoomBuilding);
        txtRoomFloor = findViewById(R.id.txtRoomFloor);
        txtRoomResponsible = findViewById(R.id.txtRoomResponsible);
        txtRoomStatus = findViewById(R.id.txtRoomStatus);
        btnVerifyAssets = findViewById(R.id.btnVerifyAssets);
        btnEditRoom = findViewById(R.id.btnEditRoom);

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

        btnEditRoom.setOnClickListener(v -> {
            if (currentRoom != null) {
                showEditRoomDialog();
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
            String[] sampleBarcodes = {"RM-101", "RM-102", "RM-103", "RM-999"};
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
            txtRoomBuilding.setText(currentRoom.getBuilding() != null ? currentRoom.getBuilding() : "N/A");
            txtRoomFloor.setText(currentRoom.getFloor() != null ? currentRoom.getFloor() : "N/A");
            txtRoomResponsible.setText(currentRoom.getPersonResponsible() != null ? currentRoom.getPersonResponsible() : "None");
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
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_room, null);
                    TextView txtDialogTitle = dialogView.findViewById(R.id.txtDialogTitle);
                    if (txtDialogTitle != null) {
                        txtDialogTitle.setText("Create New Room");
                    }
                    EditText edtRoomBarcodeDialog = dialogView.findViewById(R.id.edtRoomBarcodeDialog);
                    AutoCompleteTextView edtBuilding = dialogView.findViewById(R.id.edtBuilding);
                    AutoCompleteTextView edtFloor = dialogView.findViewById(R.id.edtFloor);
                    AutoCompleteTextView edtRoomName = dialogView.findViewById(R.id.edtRoomName);
                    EditText edtPersonsResponsible = dialogView.findViewById(R.id.edtPersonsResponsible);
                    MaterialButton btnSelectPersons = dialogView.findViewById(R.id.btnSelectPersons);

                    if (edtRoomBarcodeDialog != null) {
                        edtRoomBarcodeDialog.setText(barcode);
                    }

                    // Populate AutoComplete adapters from database
                    List<String> buildings = dbHelper.getDistinctBuildings();
                    edtBuilding.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, buildings));

                    List<String> floors = dbHelper.getDistinctFloors();
                    edtFloor.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, floors));

                    List<String> roomNames = dbHelper.getDistinctRoomNames();
                    edtRoomName.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roomNames));

                    // Multi-select existing persons responsible from database
                    btnSelectPersons.setOnClickListener(v -> {
                        List<String> existingPersons = dbHelper.getDistinctPersonsResponsible();
                        if (existingPersons.isEmpty()) {
                            Toast.makeText(this, "No previous persons responsible found in database", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String[] personsArray = existingPersons.toArray(new String[0]);
                        boolean[] checkedItems = new boolean[personsArray.length];

                        String currentText = edtPersonsResponsible.getText().toString();
                        List<String> selectedPersonsList = new java.util.ArrayList<>();
                        if (!currentText.isEmpty()) {
                            for (String p : currentText.split(",")) {
                                String trimmed = p.trim();
                                if (!trimmed.isEmpty() && !selectedPersonsList.contains(trimmed)) {
                                    selectedPersonsList.add(trimmed);
                                }
                            }
                        }

                        for (int i = 0; i < personsArray.length; i++) {
                            if (selectedPersonsList.contains(personsArray[i])) {
                                checkedItems[i] = true;
                            }
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("Select Persons Responsible")
                                .setMultiChoiceItems(personsArray, checkedItems, (d, which1, isChecked) -> {
                                    String person = personsArray[which1];
                                    if (isChecked) {
                                        if (!selectedPersonsList.contains(person)) {
                                            selectedPersonsList.add(person);
                                        }
                                    } else {
                                        selectedPersonsList.remove(person);
                                    }
                                })
                                .setPositiveButton("OK", (d, w) -> {
                                    edtPersonsResponsible.setText(TextUtils.join(", ", selectedPersonsList));
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });

                    new AlertDialog.Builder(this)
                            .setTitle("New Room Details")
                            .setView(dialogView)
                            .setPositiveButton("Save & Verify", (d, w) -> {
                                String newBarcode = edtRoomBarcodeDialog != null ? edtRoomBarcodeDialog.getText().toString().trim().toUpperCase() : barcode;
                                String building = edtBuilding.getText().toString().trim();
                                String floor = edtFloor.getText().toString().trim();
                                String roomName = edtRoomName.getText().toString().trim();
                                String persons = edtPersonsResponsible.getText().toString().trim();

                                if (TextUtils.isEmpty(newBarcode)) {
                                    Toast.makeText(this, "Room barcode cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (TextUtils.isEmpty(building)) {
                                    building = "Main Building";
                                }
                                if (TextUtils.isEmpty(floor)) {
                                    floor = "Floor 1";
                                }
                                if (TextUtils.isEmpty(roomName)) {
                                    roomName = "Room " + newBarcode;
                                }
                                if (TextUtils.isEmpty(persons)) {
                                    persons = "Unassigned";
                                }

                                boolean success = dbHelper.addRoom(newBarcode, building, floor, roomName, "Pending Verification", persons);
                                if (success) {
                                    Toast.makeText(this, "Room created successfully!", Toast.LENGTH_SHORT).show();
                                    edtRoomBarcode.setText(newBarcode);
                                    performLookup(newBarcode);
                                } else {
                                    Toast.makeText(this, "Failed to create room (Barcode may already exist).", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditRoomDialog() {
        if (currentRoom == null) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_room, null);
        TextView txtDialogTitle = dialogView.findViewById(R.id.txtDialogTitle);
        if (txtDialogTitle != null) {
            txtDialogTitle.setText("Edit Room Details");
        }
        EditText edtRoomBarcodeDialog = dialogView.findViewById(R.id.edtRoomBarcodeDialog);
        AutoCompleteTextView edtBuilding = dialogView.findViewById(R.id.edtBuilding);
        AutoCompleteTextView edtFloor = dialogView.findViewById(R.id.edtFloor);
        AutoCompleteTextView edtRoomName = dialogView.findViewById(R.id.edtRoomName);
        EditText edtPersonsResponsible = dialogView.findViewById(R.id.edtPersonsResponsible);
        MaterialButton btnSelectPersons = dialogView.findViewById(R.id.btnSelectPersons);

        // Pre-populate with current room values
        if (edtRoomBarcodeDialog != null) {
            edtRoomBarcodeDialog.setText(currentRoom.getBarcode());
        }
        edtBuilding.setText(currentRoom.getBuilding());
        edtFloor.setText(currentRoom.getFloor());
        edtRoomName.setText(currentRoom.getName());
        edtPersonsResponsible.setText(currentRoom.getPersonResponsible());

        // Populate AutoComplete adapters from database
        List<String> buildings = dbHelper.getDistinctBuildings();
        edtBuilding.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, buildings));

        List<String> floors = dbHelper.getDistinctFloors();
        edtFloor.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, floors));

        List<String> roomNames = dbHelper.getDistinctRoomNames();
        edtRoomName.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roomNames));

        // Multi-select existing persons responsible from database
        btnSelectPersons.setOnClickListener(v -> {
            List<String> existingPersons = dbHelper.getDistinctPersonsResponsible();
            if (existingPersons.isEmpty()) {
                Toast.makeText(this, "No previous persons responsible found in database", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] personsArray = existingPersons.toArray(new String[0]);
            boolean[] checkedItems = new boolean[personsArray.length];

            String currentText = edtPersonsResponsible.getText().toString();
            List<String> selectedPersonsList = new java.util.ArrayList<>();
            if (!currentText.isEmpty()) {
                for (String p : currentText.split(",")) {
                    String trimmed = p.trim();
                    if (!trimmed.isEmpty() && !selectedPersonsList.contains(trimmed)) {
                        selectedPersonsList.add(trimmed);
                    }
                }
            }

            for (int i = 0; i < personsArray.length; i++) {
                if (selectedPersonsList.contains(personsArray[i])) {
                    checkedItems[i] = true;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select Persons Responsible")
                    .setMultiChoiceItems(personsArray, checkedItems, (d, which1, isChecked) -> {
                        String person = personsArray[which1];
                        if (isChecked) {
                            if (!selectedPersonsList.contains(person)) {
                                selectedPersonsList.add(person);
                            }
                        } else {
                            selectedPersonsList.remove(person);
                        }
                    })
                    .setPositiveButton("OK", (d, w) -> {
                        edtPersonsResponsible.setText(TextUtils.join(", ", selectedPersonsList));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Edit Room")
                .setView(dialogView)
                .setPositiveButton("Save Changes", (d, w) -> {
                    String newBarcode = edtRoomBarcodeDialog != null ? edtRoomBarcodeDialog.getText().toString().trim().toUpperCase() : currentRoom.getBarcode();
                    String building = edtBuilding.getText().toString().trim();
                    String floor = edtFloor.getText().toString().trim();
                    String roomName = edtRoomName.getText().toString().trim();
                    String persons = edtPersonsResponsible.getText().toString().trim();

                    if (TextUtils.isEmpty(newBarcode)) {
                        Toast.makeText(this, "Room barcode cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(building)) building = "Main Building";
                    if (TextUtils.isEmpty(floor)) floor = "Floor 1";
                    if (TextUtils.isEmpty(roomName)) roomName = "Room " + newBarcode;
                    if (TextUtils.isEmpty(persons)) persons = "Unassigned";

                    boolean success = dbHelper.updateRoom(currentRoom.getBarcode(), newBarcode, building, floor, roomName, persons);
                    if (success) {
                        Toast.makeText(this, "Room updated successfully!", Toast.LENGTH_SHORT).show();
                        edtRoomBarcode.setText(newBarcode);
                        performLookup(newBarcode);
                    } else {
                        Toast.makeText(this, "Failed to update room (Barcode may already exist).", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

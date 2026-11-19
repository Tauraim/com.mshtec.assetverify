package com.example.msh_verify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "msh_verify.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ROOMS = "rooms";
    public static final String COLUMN_ROOM_ID = "id";
    public static final String COLUMN_ROOM_BARCODE = "barcode";
    public static final String COLUMN_ROOM_NAME = "name";
    public static final String COLUMN_ROOM_STATUS = "status";
    public static final String COLUMN_ROOM_FLOOR = "floor";
    public static final String COLUMN_ROOM_BUILDING = "building";
    public static final String COLUMN_ROOM_PERSON_RESPONSIBLE = "person_responsible";

    public static final String TABLE_ASSETS = "assets";
    public static final String COLUMN_ASSET_ID = "id";
    public static final String COLUMN_ASSET_BARCODE = "barcode";
    public static final String COLUMN_ASSET_NAME = "name";
    public static final String COLUMN_ASSET_ROOM_BARCODE = "room_barcode";
    public static final String COLUMN_ASSET_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ROOM_BARCODE + " TEXT UNIQUE,"
                + COLUMN_ROOM_NAME + " TEXT,"
                + COLUMN_ROOM_STATUS + " TEXT,"
                + COLUMN_ROOM_FLOOR + " TEXT,"
                + COLUMN_ROOM_BUILDING + " TEXT,"
                + COLUMN_ROOM_PERSON_RESPONSIBLE + " TEXT" + ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        String CREATE_ASSETS_TABLE = "CREATE TABLE " + TABLE_ASSETS + "("
                + COLUMN_ASSET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ASSET_BARCODE + " TEXT UNIQUE,"
                + COLUMN_ASSET_NAME + " TEXT,"
                + COLUMN_ASSET_ROOM_BARCODE + " TEXT,"
                + COLUMN_ASSET_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_ASSETS_TABLE);

        // Insert Sample Data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample Rooms
        addRoomInternal(db, "RM-101", "Main Building", "Floor 1", "Server Room A", "Pending Verification", "Alice Smith, Bob Jones");
        addRoomInternal(db, "RM-102", "Main Building", "Floor 2", "Executive Boardroom", "Verified", "Charlie Brown");
        addRoomInternal(db, "RM-103", "Science Block", "Floor 3", "Lab 301", "Pending Verification", "David Lee, Eva Green");

        // Sample Assets for RM-101
        addAssetInternal(db, "AST-501", "Dell PowerEdge Server", "RM-101", "Pending");
        addAssetInternal(db, "AST-502", "Cisco Catalyst Switch", "RM-101", "Pending");
        addAssetInternal(db, "AST-503", "APC Smart-UPS 3000VA", "RM-101", "Verified");

        // Sample Assets for RM-102
        addAssetInternal(db, "AST-601", "Samsung 85\" 4K Display", "RM-102", "Verified");
        addAssetInternal(db, "AST-602", "Conference Speakerphone", "RM-102", "Verified");

        // Sample Assets for RM-103
        addAssetInternal(db, "AST-701", "Digital Microscope", "RM-103", "Pending");
    }

    private void addRoomInternal(SQLiteDatabase db, String barcode, String building, String floor, String name, String status, String personResponsible) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_BARCODE, barcode);
        values.put(COLUMN_ROOM_BUILDING, building);
        values.put(COLUMN_ROOM_FLOOR, floor);
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_STATUS, status);
        values.put(COLUMN_ROOM_PERSON_RESPONSIBLE, personResponsible);
        db.insert(TABLE_ROOMS, null, values);
    }

    private void addAssetInternal(SQLiteDatabase db, String barcode, String name, String roomBarcode, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASSET_BARCODE, barcode);
        values.put(COLUMN_ASSET_NAME, name);
        values.put(COLUMN_ASSET_ROOM_BARCODE, roomBarcode);
        values.put(COLUMN_ASSET_STATUS, status);
        db.insert(TABLE_ASSETS, null, values);
    }

    public boolean addRoom(String barcode, String building, String floor, String name, String status, String personResponsible) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_BARCODE, barcode);
        values.put(COLUMN_ROOM_BUILDING, building);
        values.put(COLUMN_ROOM_FLOOR, floor);
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_STATUS, status);
        values.put(COLUMN_ROOM_PERSON_RESPONSIBLE, personResponsible);
        long result = db.insert(TABLE_ROOMS, null, values);
        return result != -1;
    }

    public boolean updateRoom(String oldBarcode, String newBarcode, String building, String floor, String name, String personResponsible) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_BARCODE, newBarcode);
        values.put(COLUMN_ROOM_BUILDING, building);
        values.put(COLUMN_ROOM_FLOOR, floor);
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_PERSON_RESPONSIBLE, personResponsible);
        int rows = db.update(TABLE_ROOMS, values, COLUMN_ROOM_BARCODE + "=?", new String[]{oldBarcode});
        return rows > 0;
    }

    public RoomModel getRoom(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOMS,
                new String[]{COLUMN_ROOM_ID, COLUMN_ROOM_BARCODE, COLUMN_ROOM_NAME, COLUMN_ROOM_STATUS, COLUMN_ROOM_FLOOR, COLUMN_ROOM_BUILDING, COLUMN_ROOM_PERSON_RESPONSIBLE},
                COLUMN_ROOM_BARCODE + "=?",
                new String[]{barcode}, null, null, null);

        RoomModel room = null;
        if (cursor != null && cursor.moveToFirst()) {
            room = new RoomModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            cursor.close();
        }
        return room;
    }

    public List<String> getDistinctBuildings() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ROOMS, new String[]{COLUMN_ROOM_BUILDING},
                COLUMN_ROOM_BUILDING + " IS NOT NULL AND " + COLUMN_ROOM_BUILDING + " != ''",
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                if (val != null && !val.isEmpty() && !list.contains(val)) {
                    list.add(val);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<String> getDistinctFloors() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ROOMS, new String[]{COLUMN_ROOM_FLOOR},
                COLUMN_ROOM_FLOOR + " IS NOT NULL AND " + COLUMN_ROOM_FLOOR + " != ''",
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                if (val != null && !val.isEmpty() && !list.contains(val)) {
                    list.add(val);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<String> getDistinctRoomNames() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ROOMS, new String[]{COLUMN_ROOM_NAME},
                COLUMN_ROOM_NAME + " IS NOT NULL AND " + COLUMN_ROOM_NAME + " != ''",
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                if (val != null && !val.isEmpty() && !list.contains(val)) {
                    list.add(val);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<String> getDistinctPersonsResponsible() {
        List<String> personList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ROOMS, new String[]{COLUMN_ROOM_PERSON_RESPONSIBLE},
                COLUMN_ROOM_PERSON_RESPONSIBLE + " IS NOT NULL AND " + COLUMN_ROOM_PERSON_RESPONSIBLE + " != ''",
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                if (val != null && !val.isEmpty()) {
                    String[] parts = val.split(",");
                    for (String p : parts) {
                        String trimmed = p.trim();
                        if (!trimmed.isEmpty() && !personList.contains(trimmed)) {
                            personList.add(trimmed);
                        }
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return personList;
    }

    public List<AssetModel> getAssetsForRoom(String roomBarcode) {
        List<AssetModel> assetList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSETS,
                new String[]{COLUMN_ASSET_ID, COLUMN_ASSET_BARCODE, COLUMN_ASSET_NAME, COLUMN_ASSET_ROOM_BARCODE, COLUMN_ASSET_STATUS},
                COLUMN_ASSET_ROOM_BARCODE + "=?",
                new String[]{roomBarcode}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                AssetModel asset = new AssetModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                assetList.add(asset);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return assetList;
    }

    public void updateAssetStatus(String assetBarcode, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASSET_STATUS, status);
        db.update(TABLE_ASSETS, values, COLUMN_ASSET_BARCODE + "=?", new String[]{assetBarcode});
    }

    public void updateRoomStatus(String roomBarcode, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_STATUS, status);
        db.update(TABLE_ROOMS, values, COLUMN_ROOM_BARCODE + "=?", new String[]{roomBarcode});
    }

    // Models
    public static class RoomModel {
        private int id;
        private String barcode;
        private String name;
        private String status;
        private String floor;
        private String building;
        private String personResponsible;

        public RoomModel(int id, String barcode, String name, String status, String floor, String building, String personResponsible) {
            this.id = id;
            this.barcode = barcode;
            this.name = name;
            this.status = status;
            this.floor = floor;
            this.building = building;
            this.personResponsible = personResponsible;
        }

        public int getId() { return id; }
        public String getBarcode() { return barcode; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public String getFloor() { return floor; }
        public String getBuilding() { return building; }
        public String getPersonResponsible() { return personResponsible; }
    }

    public static class AssetModel {
        private int id;
        private String barcode;
        private String name;
        private String roomBarcode;
        private String status;

        public AssetModel(int id, String barcode, String name, String roomBarcode, String status) {
            this.id = id;
            this.barcode = barcode;
            this.name = name;
            this.roomBarcode = roomBarcode;
            this.status = status;
        }

        public int getId() { return id; }
        public String getBarcode() { return barcode; }
        public String getName() { return name; }
        public String getRoomBarcode() { return roomBarcode; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}

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
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ROOMS = "rooms";
    public static final String COLUMN_ROOM_ID = "id";
    public static final String COLUMN_ROOM_BARCODE = "barcode";
    public static final String COLUMN_ROOM_NAME = "name";
    public static final String COLUMN_ROOM_STATUS = "status";
    public static final String COLUMN_ROOM_FLOOR = "floor";

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
                + COLUMN_ROOM_FLOOR + " TEXT" + ")";
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
        addRoomInternal(db, "RM-101", "Server Room A", "Pending Verification", "Floor 1");
        addRoomInternal(db, "RM-102", "Executive Boardroom", "Verified", "Floor 2");

        // Sample Assets for RM-101
        addAssetInternal(db, "AST-501", "Dell PowerEdge Server", "RM-101", "Pending");
        addAssetInternal(db, "AST-502", "Cisco Catalyst Switch", "RM-101", "Pending");
        addAssetInternal(db, "AST-503", "APC Smart-UPS 3000VA", "RM-101", "Verified");

        // Sample Assets for RM-102
        addAssetInternal(db, "AST-601", "Samsung 85\" 4K Display", "RM-102", "Verified");
        addAssetInternal(db, "AST-602", "Conference Speakerphone", "RM-102", "Verified");
    }

    private void addRoomInternal(SQLiteDatabase db, String barcode, String name, String status, String floor) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_BARCODE, barcode);
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_STATUS, status);
        values.put(COLUMN_ROOM_FLOOR, floor);
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

    public boolean addRoom(String barcode, String name, String status, String floor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_BARCODE, barcode);
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_STATUS, status);
        values.put(COLUMN_ROOM_FLOOR, floor);
        long result = db.insert(TABLE_ROOMS, null, values);
        return result != -1;
    }

    public RoomModel getRoom(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROOMS,
                new String[]{COLUMN_ROOM_ID, COLUMN_ROOM_BARCODE, COLUMN_ROOM_NAME, COLUMN_ROOM_STATUS, COLUMN_ROOM_FLOOR},
                COLUMN_ROOM_BARCODE + "=?",
                new String[]{barcode}, null, null, null);

        RoomModel room = null;
        if (cursor != null && cursor.moveToFirst()) {
            room = new RoomModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
        }
        return room;
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

        public RoomModel(int id, String barcode, String name, String status, String floor) {
            this.id = id;
            this.barcode = barcode;
            this.name = name;
            this.status = status;
            this.floor = floor;
        }

        public int getId() { return id; }
        public String getBarcode() { return barcode; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public String getFloor() { return floor; }
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

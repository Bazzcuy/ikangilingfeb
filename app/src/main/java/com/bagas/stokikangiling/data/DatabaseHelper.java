package com.bagas.stokikangiling.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "stok_ikan_giling.db";
    public static final int DATABASE_VERSION = 3;

    public static final String TABLE_PRODUK = "produk";
    public static final String TABLE_STOCK_LOG = "stock_log";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProduk = "CREATE TABLE " + TABLE_PRODUK + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "kode_produk TEXT NOT NULL UNIQUE, " +
                "nama_produk TEXT NOT NULL, " +
                "jenis_ikan TEXT NOT NULL, " +
                "harga_per_kg REAL NOT NULL, " +
                "stok_kg REAL NOT NULL DEFAULT 0, " +
                "gambar_uri TEXT NOT NULL, " +
                "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String createStockLog = "CREATE TABLE " + TABLE_STOCK_LOG + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "produk_id INTEGER NOT NULL, " +
                "jenis_transaksi TEXT NOT NULL, " +
                "jumlah_kg REAL NOT NULL, " +
                "catatan TEXT, " +
                "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(produk_id) REFERENCES " + TABLE_PRODUK + "(id) ON DELETE CASCADE" +
                ")";

        db.execSQL(createProduk);
        db.execSQL(createStockLog);
        isiDataAwal(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUK + " ADD COLUMN gambar_uri TEXT NOT NULL DEFAULT ''");
        }
        if (oldVersion < 3) isiDataAwalJikaKosong(db);
    }

    private void isiDataAwalJikaKosong(SQLiteDatabase db) {
        android.database.Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUK, null);
        try {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) isiDataAwal(db);
        } finally {
            cursor.close();
        }
    }

    private void isiDataAwal(SQLiteDatabase db) {
        Object[][] data = {
                {"IG-001", "Ikan Giling Tenggiri", "Tenggiri (Scomberomorus commerson)", 115000d, 18d, "res:ikan_tenggiri"},
                {"IG-002", "Ikan Giling Gabus", "Gabus (Channa striata)", 95000d, 12d, "res:ikan_gabus"},
                {"IG-003", "Ikan Giling Belida", "Belida (Chitala lopis)", 140000d, 6d, "res:ikan_belida"},
                {"IG-004", "Ikan Giling Kakap Merah", "Kakap merah (Lutjanus campechanus)", 105000d, 14d, "res:ikan_kakap_merah"},
                {"IG-005", "Ikan Giling Tuna", "Tuna sirip kuning (Thunnus albacares)", 90000d, 20d, "res:ikan_tuna"},
                {"IG-006", "Ikan Giling Tongkol", "Tongkol komo (Euthynnus affinis)", 65000d, 16d, "res:ikan_tongkol"},
                {"IG-007", "Ikan Giling Patin", "Patin siam (Pangasianodon hypophthalmus)", 55000d, 22d, "res:ikan_patin"},
                {"IG-008", "Ikan Giling Lele", "Lele lokal (Clarias batrachus)", 48000d, 10d, "res:ikan_lele"},
                {"IG-009", "Ikan Giling Nila", "Nila (Oreochromis niloticus)", 58000d, 15d, "res:ikan_nila"},
                {"IG-010", "Ikan Giling Bandeng", "Bandeng (Chanos chanos)", 62000d, 17d, "res:ikan_bandeng"}
        };
        db.beginTransaction();
        try {
            for (Object[] item : data) {
                android.content.ContentValues values = new android.content.ContentValues();
                values.put("kode_produk", (String) item[0]);
                values.put("nama_produk", (String) item[1]);
                values.put("jenis_ikan", (String) item[2]);
                values.put("harga_per_kg", (Double) item[3]);
                values.put("stok_kg", (Double) item[4]);
                values.put("gambar_uri", (String) item[5]);
                long produkId = db.insertOrThrow(TABLE_PRODUK, null, values);
                android.content.ContentValues log = new android.content.ContentValues();
                log.put("produk_id", produkId);
                log.put("jenis_transaksi", "MASUK");
                log.put("jumlah_kg", (Double) item[4]);
                log.put("catatan", "Stok awal data contoh");
                db.insertOrThrow(TABLE_STOCK_LOG, null, log);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

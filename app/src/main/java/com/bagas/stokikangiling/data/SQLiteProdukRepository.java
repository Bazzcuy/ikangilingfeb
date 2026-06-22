package com.bagas.stokikangiling.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bagas.stokikangiling.model.DashboardStats;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.model.StockLog;

import java.util.ArrayList;
import java.util.List;

public class SQLiteProdukRepository implements ProdukRepository {
    private final DatabaseHelper databaseHelper;

    public SQLiteProdukRepository(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    @Override
    public long insertProduk(IkanGiling produk) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = isiNilaiProduk(produk);
        return db.insert(DatabaseHelper.TABLE_PRODUK, null, values);
    }

    @Override
    public boolean updateProduk(IkanGiling produk) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = isiNilaiProduk(produk);
        values.remove("kode_produk");
        values.remove("stok_kg");
        int rows = db.update(DatabaseHelper.TABLE_PRODUK, values, "id = ?",
                new String[]{String.valueOf(produk.getId())});
        return rows > 0;
    }

    @Override
    public boolean deleteProduk(long produkId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_PRODUK, "id = ?",
                new String[]{String.valueOf(produkId)});
        return rows > 0;
    }

    @Override
    public IkanGiling findProdukById(long produkId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUK, null, "id = ?",
                new String[]{String.valueOf(produkId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return mapProduk(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    @Override
    public IkanGiling findProdukByKode(String kodeProduk) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUK, null, "LOWER(kode_produk) = LOWER(?)",
                new String[]{kodeProduk}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return mapProduk(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<IkanGiling> findAllProduk() {
        List<IkanGiling> result = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUK, null, null,
                null, null, null, "nama_produk ASC");
        try {
            while (cursor.moveToNext()) {
                result.add(mapProduk(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<IkanGiling> searchProduk(String keyword) {
        List<IkanGiling> result = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String like = "%" + keyword + "%";
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUK, null,
                "kode_produk LIKE ? OR nama_produk LIKE ? OR jenis_ikan LIKE ?",
                new String[]{like, like, like}, null, null, "nama_produk ASC");
        try {
            while (cursor.moveToNext()) {
                result.add(mapProduk(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean catatPerubahanStok(long produkId, String jenisTransaksi, double jumlahKg, String catatan) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        boolean masuk = "MASUK".equals(jenisTransaksi);
        db.beginTransaction();
        try {
            String sql = masuk
                    ? "UPDATE " + DatabaseHelper.TABLE_PRODUK + " SET stok_kg = stok_kg + ? WHERE id = ?"
                    : "UPDATE " + DatabaseHelper.TABLE_PRODUK + " SET stok_kg = stok_kg - ? WHERE id = ? AND stok_kg >= ?";
            android.database.sqlite.SQLiteStatement statement = db.compileStatement(sql);
            statement.bindDouble(1, jumlahKg);
            statement.bindLong(2, produkId);
            if (!masuk) statement.bindDouble(3, jumlahKg);
            if (statement.executeUpdateDelete() != 1) return false;
            ContentValues log = new ContentValues();
            log.put("produk_id", produkId);
            log.put("jenis_transaksi", jenisTransaksi);
            log.put("jumlah_kg", jumlahKg);
            log.put("catatan", catatan);
            db.insertOrThrow(DatabaseHelper.TABLE_STOCK_LOG, null, log);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<StockLog> findRecentStockLogs(int limit) {
        List<StockLog> result = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sql = "SELECT l.id, l.produk_id, p.kode_produk, p.nama_produk, " +
                "l.jenis_transaksi, l.jumlah_kg, l.catatan, l.created_at " +
                "FROM " + DatabaseHelper.TABLE_STOCK_LOG + " l " +
                "INNER JOIN " + DatabaseHelper.TABLE_PRODUK + " p ON p.id = l.produk_id " +
                "ORDER BY l.id DESC LIMIT ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(limit)});
        try {
            while (cursor.moveToNext()) {
                result.add(new StockLog(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public DashboardStats getDashboardStats(double batasStokRendah) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT " +
                "COUNT(*) AS total_produk, " +
                "COALESCE(SUM(stok_kg), 0) AS total_stok, " +
                "COALESCE(SUM(CASE WHEN stok_kg <= ? THEN 1 ELSE 0 END), 0) AS stok_rendah, " +
                "COALESCE(SUM(stok_kg * harga_per_kg), 0) AS nilai_persediaan " +
                "FROM " + DatabaseHelper.TABLE_PRODUK;

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(batasStokRendah)});
        try {
            if (cursor.moveToFirst()) {
                return new DashboardStats(
                        cursor.getInt(0),
                        cursor.getDouble(1),
                        cursor.getInt(2),
                        cursor.getDouble(3)
                );
            }
            return new DashboardStats(0, 0, 0, 0);
        } finally {
            cursor.close();
        }
    }

    private ContentValues isiNilaiProduk(IkanGiling produk) {
        ContentValues values = new ContentValues();
        values.put("kode_produk", produk.getKodeProduk());
        values.put("nama_produk", produk.getNamaProduk());
        values.put("jenis_ikan", produk.getJenisIkan());
        values.put("harga_per_kg", produk.getHargaPerKg());
        values.put("stok_kg", produk.getStokKg());
        values.put("gambar_uri", produk.getGambarUri());
        return values;
    }

    private IkanGiling mapProduk(Cursor cursor) {
        return new IkanGiling(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("kode_produk")),
                cursor.getString(cursor.getColumnIndexOrThrow("nama_produk")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("harga_per_kg")),
                cursor.getString(cursor.getColumnIndexOrThrow("gambar_uri")),
                cursor.getString(cursor.getColumnIndexOrThrow("jenis_ikan")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("stok_kg"))
        );
    }
}

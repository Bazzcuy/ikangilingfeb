package com.bagas.stokikangiling.febri.model;

public class StockLog {
    private long id;
    private long produkId;
    private String kodeProduk;
    private String namaProduk;
    private String jenisTransaksi;
    private double jumlahKg;
    private String catatan;
    private String createdAt;

    public StockLog() {
    }

    public StockLog(long id, long produkId, String kodeProduk, String namaProduk,
                    String jenisTransaksi, double jumlahKg, String catatan, String createdAt) {
        this.id = id;
        this.produkId = produkId;
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.jenisTransaksi = jenisTransaksi;
        this.jumlahKg = jumlahKg;
        this.catatan = catatan;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getProdukId() {
        return produkId;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    public double getJumlahKg() {
        return jumlahKg;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

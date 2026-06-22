package com.bagas.stokikangiling.febri.model;

/**
 * Class turunan dari Produk untuk menampilkan inheritance dan overriding.
 */
public class IkanGiling extends Produk {
    private String jenisIkan;
    private double stokKg;

    public IkanGiling() {
        super();
    }

    public IkanGiling(long id, String kodeProduk, String namaProduk, double hargaPerKg,
                      String gambarUri, String jenisIkan, double stokKg) {
        super(id, kodeProduk, namaProduk, hargaPerKg, gambarUri);
        this.jenisIkan = jenisIkan;
        this.stokKg = stokKg;
    }

    public IkanGiling(String kodeProduk, String namaProduk, double hargaPerKg,
                      String gambarUri, String jenisIkan, double stokKg) {
        this(0, kodeProduk, namaProduk, hargaPerKg, gambarUri, jenisIkan, stokKg);
    }

    public String getJenisIkan() {
        return jenisIkan;
    }

    public double getStokKg() {
        return stokKg;
    }

    public void setJenisIkan(String jenisIkan) {
        this.jenisIkan = jenisIkan;
    }

    public void setStokKg(double stokKg) {
        this.stokKg = stokKg;
    }

    public void tambahStok(double jumlahKg) {
        this.stokKg += jumlahKg;
    }

    public boolean kurangiStok(double jumlahKg) {
        if (jumlahKg <= 0 || jumlahKg > stokKg) {
            return false;
        }
        this.stokKg -= jumlahKg;
        return true;
    }

    public double hitungNilaiPersediaan() {
        return getHargaPerKg() * stokKg;
    }

    @Override
    public String getRingkasan() {
        return getKodeProduk() + " - " + getNamaProduk() + " (" + jenisIkan + ")";
    }
}

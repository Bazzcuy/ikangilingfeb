package com.bagas.stokikangiling.febri.model;

/**
 * Class induk untuk menunjukkan konsep OOP:
 * encapsulation, constructor, getter-setter, dan polymorphism.
 */
public class Produk {
    private long id;
    private String kodeProduk;
    private String namaProduk;
    private double hargaPerKg;
    private String gambarUri;

    public Produk() {
    }

    public Produk(long id, String kodeProduk, String namaProduk, double hargaPerKg, String gambarUri) {
        this.id = id;
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.hargaPerKg = hargaPerKg;
        this.gambarUri = gambarUri;
    }

    public Produk(String kodeProduk, String namaProduk, double hargaPerKg, String gambarUri) {
        this(0, kodeProduk, namaProduk, hargaPerKg, gambarUri);
    }

    public long getId() {
        return id;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public double getHargaPerKg() {
        return hargaPerKg;
    }

    public String getGambarUri() {
        return gambarUri;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setKodeProduk(String kodeProduk) {
        this.kodeProduk = kodeProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public void setHargaPerKg(double hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    public void setGambarUri(String gambarUri) {
        this.gambarUri = gambarUri;
    }

    public String getRingkasan() {
        return kodeProduk + " - " + namaProduk;
    }
}

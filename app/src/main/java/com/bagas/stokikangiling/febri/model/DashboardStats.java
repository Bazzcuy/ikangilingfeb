package com.bagas.stokikangiling.febri.model;

public class DashboardStats {
    private final int totalProduk;
    private final double totalStokKg;
    private final int stokRendah;
    private final double nilaiPersediaan;

    public DashboardStats(int totalProduk, double totalStokKg, int stokRendah, double nilaiPersediaan) {
        this.totalProduk = totalProduk;
        this.totalStokKg = totalStokKg;
        this.stokRendah = stokRendah;
        this.nilaiPersediaan = nilaiPersediaan;
    }

    public int getTotalProduk() {
        return totalProduk;
    }

    public double getTotalStokKg() {
        return totalStokKg;
    }

    public int getStokRendah() {
        return stokRendah;
    }

    public double getNilaiPersediaan() {
        return nilaiPersediaan;
    }
}

package com.bagas.stokikangiling.data;

import com.bagas.stokikangiling.model.DashboardStats;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.model.StockLog;

import java.util.List;

public interface ProdukRepository {
    long insertProduk(IkanGiling produk);
    boolean updateProduk(IkanGiling produk);
    boolean deleteProduk(long produkId);
    IkanGiling findProdukById(long produkId);
    IkanGiling findProdukByKode(String kodeProduk);
    List<IkanGiling> findAllProduk();
    List<IkanGiling> searchProduk(String keyword);
    boolean catatPerubahanStok(long produkId, String jenisTransaksi, double jumlahKg, String catatan);
    List<StockLog> findRecentStockLogs(int limit);
    DashboardStats getDashboardStats(double batasStokRendah);
}

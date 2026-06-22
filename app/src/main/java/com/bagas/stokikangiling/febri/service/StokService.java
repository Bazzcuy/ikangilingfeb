package com.bagas.stokikangiling.febri.service;

import com.bagas.stokikangiling.febri.data.ProdukRepository;
import com.bagas.stokikangiling.febri.model.IkanGiling;
import com.bagas.stokikangiling.febri.model.OperationResult;

public class StokService {
    public static final String TRANSAKSI_MASUK = "MASUK";
    public static final String TRANSAKSI_KELUAR = "KELUAR";

    private final ProdukRepository repository;

    public StokService(ProdukRepository repository) {
        this.repository = repository;
    }

    public OperationResult tambahProduk(IkanGiling produk) {
        String validasi = validasiProduk(produk, true);
        if (validasi != null) {
            return OperationResult.error(validasi);
        }

        if (repository.findProdukByKode(produk.getKodeProduk().trim()) != null) {
            return OperationResult.error("Kode produk sudah digunakan.");
        }

        rapikanProduk(produk);
        long id = repository.insertProduk(produk);
        if (id <= 0) {
            return OperationResult.error("Produk gagal disimpan.");
        }
        return OperationResult.success("Produk berhasil ditambahkan.");
    }

    public OperationResult ubahProduk(IkanGiling produk) {
        String validasi = validasiProduk(produk, false);
        if (validasi != null) {
            return OperationResult.error(validasi);
        }

        rapikanProduk(produk);
        boolean updated = repository.updateProduk(produk);
        return updated
                ? OperationResult.success("Produk berhasil diperbarui.")
                : OperationResult.error("Produk gagal diperbarui.");
    }

    public OperationResult hapusProduk(long produkId) {
        if (produkId <= 0) {
            return OperationResult.error("Produk tidak valid.");
        }
        boolean deleted = repository.deleteProduk(produkId);
        return deleted
                ? OperationResult.success("Produk berhasil dihapus.")
                : OperationResult.error("Produk gagal dihapus.");
    }

    public OperationResult tambahStok(long produkId, double jumlahKg, String catatan) {
        IkanGiling produk = repository.findProdukById(produkId);
        if (produk == null) {
            return OperationResult.error("Produk tidak ditemukan.");
        }
        if (jumlahKg <= 0) {
            return OperationResult.error("Jumlah stok masuk harus lebih dari 0.");
        }

        boolean berhasil = repository.catatPerubahanStok(produkId, TRANSAKSI_MASUK,
                jumlahKg, rapikanCatatan(catatan));
        return berhasil ? OperationResult.success("Stok masuk berhasil dicatat.")
                : OperationResult.error("Stok gagal diperbarui.");
    }

    public OperationResult kurangiStok(long produkId, double jumlahKg, String catatan) {
        IkanGiling produk = repository.findProdukById(produkId);
        if (produk == null) {
            return OperationResult.error("Produk tidak ditemukan.");
        }
        if (jumlahKg <= 0) {
            return OperationResult.error("Jumlah stok keluar harus lebih dari 0.");
        }
        if (jumlahKg > produk.getStokKg()) {
            return OperationResult.error("Stok tidak mencukupi. Sisa stok saat ini: " + produk.getStokKg() + " kg.");
        }

        boolean berhasil = repository.catatPerubahanStok(produkId, TRANSAKSI_KELUAR,
                jumlahKg, rapikanCatatan(catatan));
        return berhasil ? OperationResult.success("Stok keluar berhasil dicatat.")
                : OperationResult.error("Stok tidak mencukupi atau sudah berubah. Muat ulang lalu coba lagi.");
    }

    private void rapikanProduk(IkanGiling produk) {
        if (produk.getKodeProduk() != null) {
            produk.setKodeProduk(produk.getKodeProduk().trim().toUpperCase());
        }
        produk.setNamaProduk(produk.getNamaProduk().trim());
        produk.setJenisIkan(produk.getJenisIkan().trim());
        produk.setGambarUri(produk.getGambarUri().trim());
    }

    private String validasiProduk(IkanGiling produk, boolean validasiKode) {
        if (produk == null) {
            return "Data produk tidak boleh kosong.";
        }
        if (validasiKode && kosong(produk.getKodeProduk())) {
            return "Kode produk wajib diisi.";
        }
        if (kosong(produk.getNamaProduk())) {
            return "Nama produk wajib diisi.";
        }
        if (kosong(produk.getJenisIkan())) {
            return "Jenis ikan wajib diisi.";
        }
        if (kosong(produk.getGambarUri())) {
            return "Gambar produk wajib dipilih.";
        }
        if (!Double.isFinite(produk.getHargaPerKg()) || produk.getHargaPerKg() <= 0) {
            return "Harga per kilogram harus lebih dari 0.";
        }
        if (!Double.isFinite(produk.getStokKg()) || produk.getStokKg() < 0) {
            return "Stok awal tidak boleh negatif.";
        }
        return null;
    }

    private boolean kosong(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String rapikanCatatan(String catatan) {
        return catatan == null ? "" : catatan.trim();
    }
}

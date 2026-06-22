package com.bagas.stokikangiling.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.data.ProdukRepository;
import com.bagas.stokikangiling.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.model.DashboardStats;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.model.StockLog;
import com.bagas.stokikangiling.ui.adapter.StockLogAdapter;
import com.bagas.stokikangiling.util.FormatUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanActivity extends AppCompatActivity {
    private static final String PREF_LAPORAN = "laporan_prefs";
    private static final String KEY_CATATAN_IMPORT = "catatan_import";
    private static final String KEY_CATATAN_SUMBER = "catatan_import_sumber";
    private static final String KEY_CATATAN_WAKTU = "catatan_import_waktu";

    private ProdukRepository repository;
    private StockLogAdapter adapter;
    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> exportCsvLauncher;
    private ActivityResultLauncher<Intent> importCatatanLauncher;

    private View emptyState;
    private View layoutCatatanImport;
    private RecyclerView rvRiwayat;

    private TextView tvLaporanProduk;
    private TextView tvLaporanTotalStok;
    private TextView tvLaporanNilai;
    private TextView tvLaporanStokRendah;
    private TextView tvCatatanImportInfo;
    private TextView tvCatatanImportIsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        repository = new SQLiteProdukRepository(this);
        preferences = getSharedPreferences(PREF_LAPORAN, Context.MODE_PRIVATE);
        siapkanFilePicker();

        MaterialToolbar toolbar = findViewById(R.id.toolbarLaporan);
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton btnExportCsv = findViewById(R.id.btnExportCsv);
        MaterialButton btnImportCatatan = findViewById(R.id.btnImportCatatan);
        btnExportCsv.setOnClickListener(v -> bukaTujuanExport());
        btnImportCatatan.setOnClickListener(v -> bukaSumberCatatan());

        tvLaporanProduk = findViewById(R.id.tvLaporanProduk);
        tvLaporanTotalStok = findViewById(R.id.tvLaporanTotalStok);
        tvLaporanNilai = findViewById(R.id.tvLaporanNilai);
        tvLaporanStokRendah = findViewById(R.id.tvLaporanStokRendah);
        layoutCatatanImport = findViewById(R.id.layoutCatatanImport);
        tvCatatanImportInfo = findViewById(R.id.tvCatatanImportInfo);
        tvCatatanImportIsi = findViewById(R.id.tvCatatanImportIsi);

        emptyState = findViewById(R.id.layoutRiwayatKosong);
        rvRiwayat = findViewById(R.id.rvRiwayatStok);

        adapter = new StockLogAdapter();
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));
        rvRiwayat.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        muatLaporan();
    }

    private void muatLaporan() {
        DashboardStats stats = repository.getDashboardStats(5);
        tvLaporanProduk.setText(String.valueOf(stats.getTotalProduk()));
        tvLaporanTotalStok.setText(FormatUtils.kg(stats.getTotalStokKg()));
        tvLaporanNilai.setText(FormatUtils.rupiah(stats.getNilaiPersediaan()));
        tvLaporanStokRendah.setText(String.valueOf(stats.getStokRendah()));

        List<StockLog> logs = repository.findRecentStockLogs(30);
        adapter.submitList(logs);

        boolean kosong = logs.isEmpty();
        emptyState.setVisibility(kosong ? View.VISIBLE : View.GONE);
        rvRiwayat.setVisibility(kosong ? View.GONE : View.VISIBLE);

        tampilkanCatatanImport();
    }

    private void siapkanFilePicker() {
        exportCsvLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) exportCsv(uri);
                    }
                });

        importCatatanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) importCatatan(uri);
                    }
                });
    }

    private void bukaTujuanExport() {
        String tanggal = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "laporan_stok_ikan_" + tanggal + ".csv");
        exportCsvLauncher.launch(intent);
    }

    private void bukaSumberCatatan() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        importCatatanLauncher.launch(intent);
    }

    private void exportCsv(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                Toast.makeText(this, "File export tidak bisa dibuka", Toast.LENGTH_SHORT).show();
                return;
            }
            outputStream.write(("\uFEFF" + susunCsvLaporan()).getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "Laporan CSV berhasil diexport", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal export CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void importCatatan(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Toast.makeText(this, "File catatan tidak bisa dibuka", Toast.LENGTH_SHORT).show();
                return;
            }
            String isiCatatan = bacaText(inputStream).trim();
            if (isiCatatan.isEmpty()) {
                Toast.makeText(this, "File catatan kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            String waktu = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID")).format(new Date());
            preferences.edit()
                    .putString(KEY_CATATAN_IMPORT, isiCatatan)
                    .putString(KEY_CATATAN_SUMBER, namaFile(uri))
                    .putString(KEY_CATATAN_WAKTU, waktu)
                    .apply();
            tampilkanCatatanImport();
            Toast.makeText(this, "Catatan berhasil diimport", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal import catatan: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String susunCsvLaporan() {
        DashboardStats stats = repository.getDashboardStats(5);
        List<IkanGiling> produkList = repository.findAllProduk();
        List<StockLog> logs = repository.findRecentStockLogs(1000);
        StringBuilder csv = new StringBuilder();

        csv.append("LAPORAN STOK IKAN GILING\n");
        csv.append("Dibuat pada,").append(csvCell(tanggalSekarang())).append('\n');
        csv.append("Total produk,").append(stats.getTotalProduk()).append('\n');
        csv.append("Total stok kg,").append(stats.getTotalStokKg()).append('\n');
        csv.append("Nilai persediaan,").append(stats.getNilaiPersediaan()).append('\n');
        csv.append("Stok rendah,").append(stats.getStokRendah()).append("\n\n");

        csv.append("DAFTAR PRODUK\n");
        csv.append("Kode,Nama,Jenis Ikan,Harga per Kg,Stok Kg,Nilai Persediaan\n");
        for (IkanGiling produk : produkList) {
            csv.append(csvCell(produk.getKodeProduk())).append(',')
                    .append(csvCell(produk.getNamaProduk())).append(',')
                    .append(csvCell(produk.getJenisIkan())).append(',')
                    .append(produk.getHargaPerKg()).append(',')
                    .append(produk.getStokKg()).append(',')
                    .append(produk.hitungNilaiPersediaan()).append('\n');
        }

        csv.append("\nRIWAYAT STOK\n");
        csv.append("Tanggal,Kode Produk,Nama Produk,Jenis Transaksi,Jumlah Kg,Catatan\n");
        for (StockLog log : logs) {
            csv.append(csvCell(log.getCreatedAt())).append(',')
                    .append(csvCell(log.getKodeProduk())).append(',')
                    .append(csvCell(log.getNamaProduk())).append(',')
                    .append(csvCell(labelTransaksi(log.getJenisTransaksi()))).append(',')
                    .append(log.getJumlahKg()).append(',')
                    .append(csvCell(log.getCatatan())).append('\n');
        }

        String catatan = preferences.getString(KEY_CATATAN_IMPORT, "");
        if (!catatan.isEmpty()) {
            csv.append("\nCATATAN IMPORT\n");
            csv.append("Sumber,").append(csvCell(preferences.getString(KEY_CATATAN_SUMBER, "-"))).append('\n');
            csv.append("Waktu import,").append(csvCell(preferences.getString(KEY_CATATAN_WAKTU, "-"))).append('\n');
            csv.append("Isi,").append(csvCell(catatan)).append('\n');
        }
        return csv.toString();
    }

    private String bacaText(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append('\n');
            if (builder.length() > 20000) break;
        }
        return builder.toString();
    }

    private void tampilkanCatatanImport() {
        String catatan = preferences.getString(KEY_CATATAN_IMPORT, "");
        if (catatan.isEmpty()) {
            layoutCatatanImport.setVisibility(View.GONE);
            return;
        }
        layoutCatatanImport.setVisibility(View.VISIBLE);
        String sumber = preferences.getString(KEY_CATATAN_SUMBER, "File catatan");
        String waktu = preferences.getString(KEY_CATATAN_WAKTU, "-");
        tvCatatanImportInfo.setText("Sumber: " + sumber + " - Import: " + waktu);
        tvCatatanImportIsi.setText(catatan);
    }

    private String namaFile(Uri uri) {
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) return cursor.getString(index);
                }
            }
        }
        String path = uri.getLastPathSegment();
        return path == null ? "File catatan" : path;
    }

    private String tanggalSekarang() {
        return new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID")).format(new Date());
    }

    private String labelTransaksi(String jenisTransaksi) {
        return "MASUK".equals(jenisTransaksi) ? "Stok Masuk" : "Stok Keluar";
    }

    private String csvCell(String value) {
        String aman = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + aman + "\"";
    }
}

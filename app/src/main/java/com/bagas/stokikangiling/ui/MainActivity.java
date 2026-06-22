package com.bagas.stokikangiling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.data.ProdukRepository;
import com.bagas.stokikangiling.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.model.DashboardStats;
import com.bagas.stokikangiling.util.FormatUtils;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private ProdukRepository repository;

    private TextView tvTotalProduk;
    private TextView tvTotalStok;
    private TextView tvStokRendah;
    private TextView tvNilaiPersediaan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new SQLiteProdukRepository(this);

        tvTotalProduk = findViewById(R.id.tvTotalProduk);
        tvTotalStok = findViewById(R.id.tvTotalStok);
        tvStokRendah = findViewById(R.id.tvStokRendah);
        tvNilaiPersediaan = findViewById(R.id.tvNilaiPersediaan);

        MaterialButton btnProduk = findViewById(R.id.btnProduk);
        MaterialButton btnTambahProduk = findViewById(R.id.btnTambahProduk);
        MaterialButton btnStokMasuk = findViewById(R.id.btnStokMasuk);
        MaterialButton btnStokKeluar = findViewById(R.id.btnStokKeluar);
        MaterialButton btnLaporan = findViewById(R.id.btnLaporan);

        btnProduk.setOnClickListener(v -> buka(ProdukListActivity.class));
        btnTambahProduk.setOnClickListener(v -> buka(ProdukFormActivity.class));
        btnStokMasuk.setOnClickListener(v -> buka(StokMasukActivity.class));
        btnStokKeluar.setOnClickListener(v -> buka(StokKeluarActivity.class));
        btnLaporan.setOnClickListener(v -> buka(LaporanActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {
        DashboardStats stats = repository.getDashboardStats(5);
        tvTotalProduk.setText(String.valueOf(stats.getTotalProduk()));
        tvTotalStok.setText(FormatUtils.kg(stats.getTotalStokKg()));
        tvStokRendah.setText(String.valueOf(stats.getStokRendah()));
        tvNilaiPersediaan.setText(FormatUtils.rupiah(stats.getNilaiPersediaan()));
    }

    private void buka(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}

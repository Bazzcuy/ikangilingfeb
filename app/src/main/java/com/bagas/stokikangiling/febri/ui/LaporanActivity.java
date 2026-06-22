package com.bagas.stokikangiling.febri.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.stokikangiling.febri.R;
import com.bagas.stokikangiling.febri.data.ProdukRepository;
import com.bagas.stokikangiling.febri.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.febri.model.DashboardStats;
import com.bagas.stokikangiling.febri.model.StockLog;
import com.bagas.stokikangiling.febri.ui.adapter.StockLogAdapter;
import com.bagas.stokikangiling.febri.util.FormatUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class LaporanActivity extends AppCompatActivity {
    private ProdukRepository repository;
    private StockLogAdapter adapter;
    private View emptyState;
    private RecyclerView rvRiwayat;

    private TextView tvLaporanProduk;
    private TextView tvLaporanTotalStok;
    private TextView tvLaporanNilai;
    private TextView tvLaporanStokRendah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        repository = new SQLiteProdukRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarLaporan);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvLaporanProduk = findViewById(R.id.tvLaporanProduk);
        tvLaporanTotalStok = findViewById(R.id.tvLaporanTotalStok);
        tvLaporanNilai = findViewById(R.id.tvLaporanNilai);
        tvLaporanStokRendah = findViewById(R.id.tvLaporanStokRendah);

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
    }
}

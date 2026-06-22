package com.bagas.stokikangiling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.data.ProdukRepository;
import com.bagas.stokikangiling.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.model.OperationResult;
import com.bagas.stokikangiling.service.StokService;
import com.bagas.stokikangiling.ui.adapter.ProdukAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ProdukListActivity extends AppCompatActivity implements ProdukAdapter.ProdukActionListener {
    public static final String EXTRA_PRODUK_ID = "extra_produk_id";

    private ProdukRepository repository;
    private StokService stokService;
    private ProdukAdapter adapter;
    private TextInputEditText edtCari;
    private View emptyState;
    private RecyclerView rvProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_list);

        repository = new SQLiteProdukRepository(this);
        stokService = new StokService(repository);

        MaterialToolbar toolbar = findViewById(R.id.toolbarProduk);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvProduk = findViewById(R.id.rvProduk);
        emptyState = findViewById(R.id.layoutProdukKosong);
        edtCari = findViewById(R.id.edtCariProduk);
        FloatingActionButton fabTambah = findViewById(R.id.fabTambahProduk);

        adapter = new ProdukAdapter(this);
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        rvProduk.setAdapter(adapter);

        fabTambah.setOnClickListener(v -> startActivity(new Intent(this, ProdukFormActivity.class)));

        edtCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                muatProduk(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        muatProduk(edtCari.getText() == null ? "" : edtCari.getText().toString());
    }

    private void muatProduk(String keyword) {
        List<IkanGiling> produk = keyword.trim().isEmpty()
                ? repository.findAllProduk()
                : repository.searchProduk(keyword.trim());
        adapter.submitList(produk);
        boolean kosong = produk.isEmpty();
        emptyState.setVisibility(kosong ? View.VISIBLE : View.GONE);
        rvProduk.setVisibility(kosong ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEdit(IkanGiling produk) {
        Intent intent = new Intent(this, ProdukFormActivity.class);
        intent.putExtra(EXTRA_PRODUK_ID, produk.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(IkanGiling produk) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hapus produk?")
                .setMessage("Produk " + produk.getNamaProduk() + " akan dihapus beserta riwayat stoknya.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (dialog, which) -> {
                    OperationResult result = stokService.hapusProduk(produk.getId());
                    Snackbar.make(rvProduk, result.getMessage(), Snackbar.LENGTH_LONG).show();
                    muatProduk(edtCari.getText() == null ? "" : edtCari.getText().toString());
                })
                .show();
    }
}

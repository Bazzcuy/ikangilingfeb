package com.bagas.stokikangiling.ui;

import android.os.Bundle;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.model.OperationResult;
import com.bagas.stokikangiling.service.StokService;
import com.bagas.stokikangiling.util.FormatUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class StokKeluarActivity extends BaseStokActivity {
    private StokService stokService;
    private TextInputEditText edtJumlah, edtCatatan;
    private MaterialButton btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_keluar);

        repository = new SQLiteProdukRepository(this);
        stokService = new StokService(repository);

        MaterialToolbar toolbar = findViewById(R.id.toolbarStokKeluar);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtJumlah = findViewById(R.id.edtJumlahStok);
        edtCatatan = findViewById(R.id.edtCatatanStok);
        btnSimpan = findViewById(R.id.btnSimpanStokKeluar);

        siapkanDropdownProduk();
        btnSimpan.setOnClickListener(v -> simpanStokKeluar());
    }

    private void simpanStokKeluar() {
        IkanGiling produk = produkTerpilih();
        if (produk == null) {
            Snackbar.make(btnSimpan, "Pilih produk terlebih dahulu.", Snackbar.LENGTH_LONG).show();
            return;
        }

        double jumlah;
        try {
            jumlah = FormatUtils.parseDoubleFlexible(teks(edtJumlah));
        } catch (NumberFormatException e) {
            Snackbar.make(btnSimpan, "Jumlah stok harus berupa angka.", Snackbar.LENGTH_LONG).show();
            return;
        }

        OperationResult result = stokService.kurangiStok(produk.getId(), jumlah, teks(edtCatatan));
        Snackbar.make(btnSimpan, result.getMessage(), Snackbar.LENGTH_LONG).show();
        if (result.isSuccess()) {
            btnSimpan.postDelayed(this::finish, 650);
        }
    }

    private String teks(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}

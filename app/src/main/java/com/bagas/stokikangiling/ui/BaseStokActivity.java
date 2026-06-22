package com.bagas.stokikangiling.ui;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.data.ProdukRepository;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.util.FormatUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class bantuan untuk menghindari duplikasi logika dropdown produk
 * pada aktivitas stok masuk dan stok keluar.
 */
public abstract class BaseStokActivity extends AppCompatActivity {
    protected ProdukRepository repository;
    protected MaterialAutoCompleteTextView dropdownProduk;
    protected TextView tvStokSaatIni;
    protected List<IkanGiling> daftarProduk = new ArrayList<>();
    private IkanGiling produkTerpilih;

    protected void siapkanDropdownProduk() {
        dropdownProduk = findViewById(R.id.dropdownProduk);
        tvStokSaatIni = findViewById(R.id.tvStokSaatIni);

        daftarProduk = repository.findAllProduk();
        List<String> labelProduk = new ArrayList<>();
        for (IkanGiling produk : daftarProduk) {
            labelProduk.add(produk.getRingkasan());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                labelProduk
        );
        dropdownProduk.setAdapter(adapter);
        dropdownProduk.setThreshold(0);
        dropdownProduk.setOnClickListener(v -> tampilkanPilihanProduk());
        dropdownProduk.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tampilkanPilihanProduk();
        });

        dropdownProduk.setOnItemClickListener((parent, view, position, id) -> {
            produkTerpilih = daftarProduk.get(position);
            tvStokSaatIni.setText("Stok saat ini: " + FormatUtils.kg(produkTerpilih.getStokKg()));
        });

        if (daftarProduk.isEmpty()) {
            dropdownProduk.setHint("Belum ada produk");
            dropdownProduk.setEnabled(false);
            tvStokSaatIni.setText("Tambahkan produk terlebih dahulu.");
        }
    }

    protected IkanGiling produkTerpilih() {
        String pilihan = dropdownProduk.getText() == null ? "" : dropdownProduk.getText().toString().trim();
        if (produkTerpilih != null && produkTerpilih.getRingkasan().equals(pilihan)) {
            return produkTerpilih;
        }
        return null;
    }

    private void tampilkanPilihanProduk() {
        if (!daftarProduk.isEmpty()) {
            dropdownProduk.showDropDown();
        }
    }
}

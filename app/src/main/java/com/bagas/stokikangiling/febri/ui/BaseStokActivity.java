package com.bagas.stokikangiling.febri.ui;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bagas.stokikangiling.febri.R;
import com.bagas.stokikangiling.febri.data.ProdukRepository;
import com.bagas.stokikangiling.febri.model.IkanGiling;
import com.bagas.stokikangiling.febri.util.FormatUtils;
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

        dropdownProduk.setOnItemClickListener((parent, view, position, id) -> {
            IkanGiling produk = daftarProduk.get(position);
            tvStokSaatIni.setText("Stok saat ini: " + FormatUtils.kg(produk.getStokKg()));
        });

        if (daftarProduk.isEmpty()) {
            dropdownProduk.setHint("Belum ada produk");
            tvStokSaatIni.setText("Tambahkan produk terlebih dahulu.");
        }
    }

    protected IkanGiling produkTerpilih() {
        int posisi = -1;
        String pilihan = dropdownProduk.getText() == null ? "" : dropdownProduk.getText().toString();
        for (int i = 0; i < daftarProduk.size(); i++) {
            if (daftarProduk.get(i).getRingkasan().equals(pilihan)) {
                posisi = i;
                break;
            }
        }
        return posisi >= 0 ? daftarProduk.get(posisi) : null;
    }
}

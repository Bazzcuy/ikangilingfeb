package com.bagas.stokikangiling.febri.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bagas.stokikangiling.febri.R;
import com.bagas.stokikangiling.febri.data.ProdukRepository;
import com.bagas.stokikangiling.febri.data.SQLiteProdukRepository;
import com.bagas.stokikangiling.febri.model.IkanGiling;
import com.bagas.stokikangiling.febri.model.OperationResult;
import com.bagas.stokikangiling.febri.service.StokService;
import com.bagas.stokikangiling.febri.util.FormatUtils;
import com.bagas.stokikangiling.febri.util.ImageUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProdukFormActivity extends AppCompatActivity {
    private ProdukRepository repository;
    private StokService stokService;

    private TextView tvJudulForm;
    private TextInputLayout tilKode;
    private TextInputEditText edtKode, edtNama, edtJenis, edtHarga, edtStok;
    private MaterialButton btnSimpan, btnPilihGambar;
    private ImageView ivPreviewGambar;
    private TextView tvStatusGambar;

    private boolean modeEdit = false;
    private long produkId = 0;
    private IkanGiling produkLama;
    private String gambarUriTerpilih = "";

    private final ActivityResultLauncher<Intent> pemilihGambar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                Uri uri = result.getData().getData();
                if (uri == null) {
                    return;
                }
                try {
                    final int takeFlags = result.getData().getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (SecurityException ignored) {
                    // Sebagian provider tidak memberi izin persistable; URI tetap digunakan selama tersedia.
                }
                gambarUriTerpilih = uri.toString();
                tampilkanPreviewGambar(gambarUriTerpilih);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_form);

        repository = new SQLiteProdukRepository(this);
        stokService = new StokService(repository);

        MaterialToolbar toolbar = findViewById(R.id.toolbarProdukForm);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvJudulForm = findViewById(R.id.tvJudulFormProduk);
        tilKode = findViewById(R.id.tilKodeProduk);
        edtKode = findViewById(R.id.edtKodeProduk);
        edtNama = findViewById(R.id.edtNamaProduk);
        edtJenis = findViewById(R.id.edtJenisIkan);
        edtHarga = findViewById(R.id.edtHargaPerKg);
        edtStok = findViewById(R.id.edtStokAwal);
        btnSimpan = findViewById(R.id.btnSimpanProduk);
        btnPilihGambar = findViewById(R.id.btnPilihGambarProduk);
        ivPreviewGambar = findViewById(R.id.ivPreviewGambarProduk);
        tvStatusGambar = findViewById(R.id.tvStatusGambarProduk);

        produkId = getIntent().getLongExtra(ProdukListActivity.EXTRA_PRODUK_ID, 0);
        if (produkId > 0) {
            modeEdit = true;
            muatDataEdit();
        }

        btnPilihGambar.setOnClickListener(v -> bukaPemilihGambar());
        btnSimpan.setOnClickListener(v -> simpan());
    }

    private void bukaPemilihGambar() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pemilihGambar.launch(intent);
    }

    private void muatDataEdit() {
        produkLama = repository.findProdukById(produkId);
        if (produkLama == null) {
            Snackbar.make(tvJudulForm, "Produk tidak ditemukan.", Snackbar.LENGTH_LONG).show();
            finish();
            return;
        }

        tvJudulForm.setText("Ubah Produk");
        btnSimpan.setText("Simpan Perubahan");
        btnPilihGambar.setText("Ganti Gambar Produk");
        tilKode.setEnabled(false);

        edtKode.setText(produkLama.getKodeProduk());
        edtNama.setText(produkLama.getNamaProduk());
        edtJenis.setText(produkLama.getJenisIkan());
        edtHarga.setText(String.valueOf(produkLama.getHargaPerKg()));
        edtStok.setText(String.valueOf(produkLama.getStokKg()));
        edtStok.setEnabled(false);
        gambarUriTerpilih = produkLama.getGambarUri() == null ? "" : produkLama.getGambarUri();
        tampilkanPreviewGambar(gambarUriTerpilih);
    }

    private void tampilkanPreviewGambar(String uriText) {
        if (TextUtils.isEmpty(uriText)) {
            ivPreviewGambar.setImageResource(R.drawable.ic_image_placeholder);
            tvStatusGambar.setText("Belum ada gambar dipilih. Gambar produk wajib diisi.");
            return;
        }
        ImageUtils.tampilkan(ivPreviewGambar, uriText);
        tvStatusGambar.setText("Gambar tersimpan bersama data produk dan tersedia offline.");
    }

    private void simpan() {
        bersihkanError();

        String kode = teks(edtKode);
        String nama = teks(edtNama);
        String jenis = teks(edtJenis);
        String hargaText = teks(edtHarga);
        String stokText = teks(edtStok);

        if (TextUtils.isEmpty(kode) && !modeEdit) {
            tilKode.setError("Kode produk wajib diisi");
            return;
        }

        double harga;
        double stok;
        try {
            harga = FormatUtils.parseDoubleFlexible(hargaText);
            stok = FormatUtils.parseDoubleFlexible(stokText);
        } catch (NumberFormatException e) {
            Snackbar.make(btnSimpan, "Harga dan stok harus berupa angka.", Snackbar.LENGTH_LONG).show();
            return;
        }

        IkanGiling produk = modeEdit
                ? new IkanGiling(produkId, produkLama.getKodeProduk(), nama, harga, gambarUriTerpilih, jenis, stok)
                : new IkanGiling(kode, nama, harga, gambarUriTerpilih, jenis, stok);

        OperationResult result = modeEdit
                ? stokService.ubahProduk(produk)
                : stokService.tambahProduk(produk);

        Snackbar.make(btnSimpan, result.getMessage(), Snackbar.LENGTH_LONG).show();
        if (result.isSuccess()) {
            btnSimpan.postDelayed(this::finish, 700);
        }
    }

    private void bersihkanError() {
        tilKode.setError(null);
    }

    private String teks(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}

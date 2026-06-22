package com.bagas.stokikangiling.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.stokikangiling.R;
import com.bagas.stokikangiling.model.IkanGiling;
import com.bagas.stokikangiling.util.FormatUtils;
import com.bagas.stokikangiling.util.ImageUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {
    public interface ProdukActionListener {
        void onEdit(IkanGiling produk);
        void onDelete(IkanGiling produk);
    }

    private final List<IkanGiling> items = new ArrayList<>();
    private final ProdukActionListener listener;

    public ProdukAdapter(ProdukActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<IkanGiling> produkList) {
        items.clear();
        if (produkList != null) {
            items.addAll(produkList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        IkanGiling produk = items.get(position);
        holder.tvNamaProduk.setText(produk.getNamaProduk());
        holder.tvKodeJenis.setText(produk.getKodeProduk() + " • " + produk.getJenisIkan());
        holder.tvHarga.setText("Harga: " + FormatUtils.rupiah(produk.getHargaPerKg()) + "/kg");
        holder.tvStok.setText("Stok " + FormatUtils.kg(produk.getStokKg()));
        holder.tampilkanGambar(produk.getGambarUri());

        boolean stokRendah = produk.getStokKg() <= 5;
        holder.tvStok.setBackgroundResource(stokRendah
                ? R.drawable.bg_badge_stock_low
                : R.drawable.bg_badge_stock_normal);
        holder.tvStok.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                stokRendah ? R.color.warning : R.color.success));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(produk));
        holder.btnHapus.setOnClickListener(v -> listener.onDelete(produk));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProdukViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGambarProduk;
        TextView tvNamaProduk, tvKodeJenis, tvHarga, tvStok;
        MaterialButton btnEdit, btnHapus;

        ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGambarProduk = itemView.findViewById(R.id.ivGambarProduk);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvKodeJenis = itemView.findViewById(R.id.tvKodeJenis);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStok = itemView.findViewById(R.id.tvStok);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }

        void tampilkanGambar(String uriText) {
            if (uriText == null || uriText.trim().isEmpty()) {
                ivGambarProduk.setImageResource(R.drawable.ic_image_placeholder);
                return;
            }
            ImageUtils.tampilkan(ivGambarProduk, uriText);
        }
    }
}

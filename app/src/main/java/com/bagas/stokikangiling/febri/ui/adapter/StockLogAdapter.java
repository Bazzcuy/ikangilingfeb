package com.bagas.stokikangiling.febri.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bagas.stokikangiling.febri.R;
import com.bagas.stokikangiling.febri.model.StockLog;
import com.bagas.stokikangiling.febri.service.StokService;
import com.bagas.stokikangiling.febri.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class StockLogAdapter extends RecyclerView.Adapter<StockLogAdapter.StockLogViewHolder> {
    private final List<StockLog> items = new ArrayList<>();

    public void submitList(List<StockLog> logs) {
        items.clear();
        if (logs != null) {
            items.addAll(logs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_log, parent, false);
        return new StockLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockLogViewHolder holder, int position) {
        StockLog log = items.get(position);
        holder.tvProduk.setText(log.getKodeProduk() + " • " + log.getNamaProduk());
        holder.tvJumlah.setText(FormatUtils.kg(log.getJumlahKg()));
        holder.tvTanggal.setText(log.getCreatedAt());

        String catatan = log.getCatatan();
        if (catatan == null || catatan.trim().isEmpty()) {
            holder.tvCatatan.setText("Tanpa catatan");
        } else {
            holder.tvCatatan.setText(catatan);
        }

        boolean masuk = StokService.TRANSAKSI_MASUK.equals(log.getJenisTransaksi());
        holder.tvJenis.setText(masuk ? "Stok Masuk" : "Stok Keluar");
        holder.tvJenis.setBackgroundResource(masuk
                ? R.drawable.bg_badge_masuk
                : R.drawable.bg_badge_keluar);
        holder.tvJenis.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                masuk ? R.color.success : R.color.danger));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StockLogViewHolder extends RecyclerView.ViewHolder {
        TextView tvJenis, tvProduk, tvJumlah, tvCatatan, tvTanggal;

        StockLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJenis = itemView.findViewById(R.id.tvJenisTransaksi);
            tvProduk = itemView.findViewById(R.id.tvLogProduk);
            tvJumlah = itemView.findViewById(R.id.tvLogJumlah);
            tvCatatan = itemView.findViewById(R.id.tvLogCatatan);
            tvTanggal = itemView.findViewById(R.id.tvLogTanggal);
        }
    }
}

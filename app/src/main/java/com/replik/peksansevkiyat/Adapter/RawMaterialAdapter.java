package com.replik.peksansevkiyat.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.RawMaterialItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.LotItem;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RawMaterialAdapter extends RecyclerView.Adapter<RawMaterialAdapter.ViewHolder> {
    private List<RawMaterialItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRawMaterialSelected(RawMaterialItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RawMaterialAdapter() {
        this.items = new ArrayList<>();
    }

    public void addItem(RawMaterialItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public Set<String> getStockCodes() {
        Set<String> stockCodes = new HashSet<>();
        for (RawMaterialItem item : items) {
            stockCodes.add(item.getStockCode());
        }
        return stockCodes;
    }

    public RawMaterialItem findItemByStockCode(String stockCode) {
        if (stockCode == null) return null;
        
        for (RawMaterialItem item : items) {
            if (stockCode.equals(item.getStockCode())) {
                return item;
            }
        }
        return null;
    }

    public List<RawMaterialItem> getItems() {
        return items;
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_raw_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RawMaterialItem item = items.get(position);
        double totalLotAmount = 0;
        for (LotItem lot : item.getLots()) {
            totalLotAmount += lot.getAmount();
        }

        holder.tvStockCode.setText(item.getStockCode());
        holder.tvStockName.setText(item.getStockName());
        holder.tvAmount.setText(String.format("%.2f Kg", item.getAmount()));

        boolean isComplete = Math.abs(totalLotAmount - item.getAmount()) < 0.1;
        
        if (isComplete) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
            holder.tvStockCode.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.tvStockName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.tvStockCode.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
            holder.tvStockName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.replik));
        }

        android.util.Log.d("RawMaterialAdapter", String.format("StockCode: %s, TotalLotAmount: %.2f, ItemAmount: %.2f, Difference: %.2f, IsComplete: %b",
            item.getStockCode(), totalLotAmount, item.getAmount(), Math.abs(totalLotAmount - item.getAmount()), isComplete));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRawMaterialSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvStockCode;
        private final TextView tvStockName;
        private final TextView tvAmount;
        private final MaterialCardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStockCode = itemView.findViewById(R.id.tv_stock_code);
            tvStockName = itemView.findViewById(R.id.tv_stock_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            cardView = (MaterialCardView) itemView.findViewById(R.id.card_view);
        }
    }
} 

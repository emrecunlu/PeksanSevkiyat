package com.replik.peksansevkiyat.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class ConsumableLotAdapter extends RecyclerView.Adapter<ConsumableLotAdapter.ViewHolder> {
    
    public static class LotItem {
        private final String lotNumber;
        private final double quantity;

        public LotItem(String lotNumber, double quantity) {
            this.lotNumber = lotNumber;
            this.quantity = quantity;
        }

        public String getLotNumber() { return lotNumber; }
        public double getQuantity() { return quantity; }
    }

    private final List<LotItem> items = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumable_lot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LotItem item = items.get(position);
        holder.tvLotNumber.setText(item.getLotNumber());
        holder.tvQuantity.setText(String.format("%.2f Kg", item.getQuantity()));

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(String lotNumber, double quantity) {
        items.add(new LotItem(lotNumber, quantity));
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            if (position < items.size()) {
                notifyItemRangeChanged(position, items.size() - position);
            }
        }
    }

    public List<LotItem> getItems() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvLotNumber;
        final TextView tvQuantity;
        final MaterialButton btnDelete;
        final MaterialCardView cardView;

        ViewHolder(View view) {
            super(view);
            cardView = (MaterialCardView) view;
            tvLotNumber = view.findViewById(R.id.tvLotNumber);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
} 
package com.replik.peksansevkiyat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.StockItem;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<StockItem> stocks = new ArrayList<>();
    private int selectedPosition = -1;
    private OnStockSelectedListener listener;
    private boolean selectionEnabled;
    private List<String> existingStockCodes;

    public interface OnStockSelectedListener {
        void onStockSelected(StockItem stock);
    }

    public StockAdapter() {
        this(true);
    }

    public StockAdapter(boolean selectionEnabled) {
        this(selectionEnabled, new ArrayList<>());
    }

    public StockAdapter(List<String> existingStockCodes) {
        this(true, existingStockCodes);
    }

    public StockAdapter(boolean selectionEnabled, List<String> existingStockCodes) {
        this.selectionEnabled = selectionEnabled;
        this.existingStockCodes = existingStockCodes;
    }

    public void setOnStockSelectedListener(OnStockSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        StockItem stock = stocks.get(position);
        boolean isDisabled = existingStockCodes.contains(stock.getStockCode());
        holder.bind(stock, selectionEnabled && position == selectedPosition, isDisabled);
        
        if (selectionEnabled && !isDisabled) {
            holder.cardView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                
                if (listener != null) {
                    listener.onStockSelected(stock);
                }
            });
        } else {
            holder.cardView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public void setStocks(List<StockItem> stocks) {
        this.stocks = stocks;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void setExistingStockCodes(List<String> existingStockCodes) {
        this.existingStockCodes = existingStockCodes;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        notifyItemChanged(oldPosition);
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvStockName;
        private final TextView tvStockCode;
        private final TextView tvAmount;
        private final Context context;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = (MaterialCardView) itemView;
            tvStockName = itemView.findViewById(R.id.tv_stock_name);
            tvStockCode = itemView.findViewById(R.id.tv_stock_code);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }

        public void bind(StockItem stock, boolean isSelected, boolean isDisabled) {
            tvStockName.setText(stock.getStockName());
            tvStockCode.setText(stock.getStockCode());
            tvAmount.setText(String.format("%.2f Kg", stock.getAmount()));

            if (isDisabled) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvStockName.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvStockCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.black));
                cardView.setEnabled(false);
                cardView.setAlpha(0.5f);
            } else if (isSelected) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.replik));
                tvStockName.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                tvStockCode.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                cardView.setEnabled(true);
                cardView.setAlpha(1f);
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvStockName.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvStockCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.replik));
                cardView.setEnabled(true);
                cardView.setAlpha(1f);
            }
        }
    }
} 
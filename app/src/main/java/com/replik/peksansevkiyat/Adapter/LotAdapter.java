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
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.LotItem;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class LotAdapter extends RecyclerView.Adapter<LotAdapter.ViewHolder> {
    private List<LotItem> lots = new ArrayList<>();
    private List<String> existingSerialNumbers = new ArrayList<>();
    private OnLotSelectedListener listener;
    private int selectedPosition = -1;

    public void setLots(List<LotItem> lots) {
        this.lots = lots;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void setExistingSerialNumbers(List<String> serialNumbers) {
        this.existingSerialNumbers = serialNumbers;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        notifyItemChanged(oldPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LotItem lot = lots.get(position);
        boolean isDisabled = existingSerialNumbers.contains(lot.getSerialNumber());
        holder.bind(lot, position == selectedPosition, isDisabled);
        
        if (!isDisabled) {
            holder.cardView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                
                if (listener != null) {
                    listener.onLotSelected(lot);
                }
            });
        } else {
            holder.cardView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return lots.size();
    }

    public void setOnLotSelectedListener(OnLotSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnLotSelectedListener {
        void onLotSelected(LotItem lot);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLotNumber;
        TextView tvSerialNumber;
        TextView tvAmount;
        MaterialCardView cardView;
        Context context;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvLotNumber = itemView.findViewById(R.id.tv_lot_number);
            tvSerialNumber = itemView.findViewById(R.id.tv_serial_number);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            cardView = itemView.findViewById(R.id.card_view);
        }

        public void bind(LotItem lot, boolean isSelected, boolean isDisabled) {
            tvLotNumber.setText(lot.getLotNumber());
            tvSerialNumber.setText(lot.getSerialNumber());
            tvAmount.setText(String.format("%.2f Kg", lot.getAmount()));

            if (isDisabled) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvLotNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvSerialNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.black));
                cardView.setEnabled(false);
                cardView.setAlpha(0.5f);
            } else if (isSelected) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.replik));
                tvLotNumber.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                tvSerialNumber.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.white_light));
                cardView.setEnabled(true);
                cardView.setAlpha(1f);
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvLotNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvSerialNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.replik));
                cardView.setEnabled(true);
                cardView.setAlpha(1f);
            }
        }
    }
} 
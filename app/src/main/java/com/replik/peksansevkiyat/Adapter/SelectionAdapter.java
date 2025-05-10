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
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    public interface OnItemSelectedListener {
        void onItemSelected(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final List<Item> allItems = new ArrayList<>();
    private final OnItemSelectedListener listener;
    private Item selectedItem;

    public SelectionAdapter(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, item.equals(selectedItem));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> newItems) {
        items.clear();
        items.addAll(newItems);
        allItems.clear();
        allItems.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setSelectedCode(String code) {
        for (Item item : items) {
            if (item.code.equals(code)) {
                setSelectedItem(item);
                break;
            }
        }
    }

    public void filter(String query) {
        items.clear();
        if (query.isEmpty()) {
            items.addAll(allItems);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Item item : allItems) {
                if (item.code.toLowerCase().contains(lowerQuery) ||
                    item.name.toLowerCase().contains(lowerQuery)) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedItem(Item item) {
        Item oldSelection = selectedItem;
        selectedItem = item;
        
        if (oldSelection != null) {
            int oldIndex = items.indexOf(oldSelection);
            if (oldIndex != -1) {
                notifyItemChanged(oldIndex);
            }
        }
        
        int newIndex = items.indexOf(item);
        if (newIndex != -1) {
            notifyItemChanged(newIndex);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvCode;
        private final TextView tvName;
        private final Context context;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = (MaterialCardView) itemView;
            tvCode = itemView.findViewById(R.id.tvCode);
            tvName = itemView.findViewById(R.id.tvName);
        }

        void bind(Item item, boolean isSelected) {
            tvCode.setText(item.code);
            tvName.setText(item.name);

            if (isSelected) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.replik));
                tvCode.setTextColor(ContextCompat.getColor(context, R.color.white));
                tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                tvCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                tvName.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemSelected(item);
                }
            });
        }
    }

    public static class Item {
        public final String code;
        public final String name;

        public Item(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return code.equals(item.code);
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }
    }
} 
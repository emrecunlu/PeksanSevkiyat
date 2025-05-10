package com.replik.peksansevkiyat.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.Warehouse;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class WarehouseAdapter extends ArrayAdapter<Warehouse> {
    private List<Warehouse> warehouseList;
    private List<Warehouse> allWarehouses;

    public WarehouseAdapter(@NonNull Context context, @NonNull List<Warehouse> warehouses) {
        super(context, android.R.layout.simple_dropdown_item_1line, warehouses);
        this.warehouseList = new ArrayList<>(warehouses);
        this.allWarehouses = new ArrayList<>(warehouses);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Warehouse warehouse = getItem(position);
        if (warehouse != null) {
            String displayText = warehouse.getWarehouseCode() + " - " + warehouse.getWarehouseName();
            textView.setText(displayText);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textView.setPadding(32, 8, 32, 8);
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Warehouse> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    suggestions.addAll(allWarehouses);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Warehouse warehouse : allWarehouses) {
                        if (warehouse.getWarehouseName().toLowerCase().contains(filterPattern) ||
                            String.valueOf(warehouse.getWarehouseCode()).contains(filterPattern)) {
                            suggestions.add(warehouse);
                        }
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                if (results.values != null) {
                    List<Warehouse> filteredList = (List<Warehouse>) results.values;
                    if (!filteredList.isEmpty()) {
                        addAll(filteredList);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue == null) return "";
                Warehouse warehouse = (Warehouse) resultValue;
                return warehouse.getWarehouseCode() + " - " + warehouse.getWarehouseName();
            }
        };
    }
} 
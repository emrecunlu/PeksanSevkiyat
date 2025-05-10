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

import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.StockItem;
import com.replik.peksansevkiyat.R;

import java.util.ArrayList;
import java.util.List;

public class CountingStockAdapter extends ArrayAdapter<StockItem> {
    private List<StockItem> stockList;
    private List<StockItem> allStocks;

    public CountingStockAdapter(@NonNull Context context, @NonNull List<StockItem> stocks) {
        super(context, android.R.layout.simple_dropdown_item_1line, stocks);
        this.stockList = new ArrayList<>(stocks);
        this.allStocks = new ArrayList<>(stocks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        StockItem stock = getItem(position);
        if (stock != null) {
            String displayText = stock.getStockCode() + " - " + stock.getStockName();
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
                List<StockItem> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    suggestions.addAll(allStocks);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (StockItem stock : allStocks) {
                        if (stock.getStockName().toLowerCase().contains(filterPattern) ||
                            stock.getStockCode().toLowerCase().contains(filterPattern)) {
                            suggestions.add(stock);
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
                    addAll((List<StockItem>) results.values);
                }
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue == null) return "";
                StockItem stock = (StockItem) resultValue;
                return stock.getStockCode() + " - " + stock.getStockName();
            }
        };
    }
} 
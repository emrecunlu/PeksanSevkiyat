package com.replik.peksansevkiyat.DataClass.ModelDto.Stock;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class StockItem {
    @SerializedName("id")
    private int id;
    
    @SerializedName("stokKodu")
    private String stockCode;
    
    @SerializedName("stokAdi")
    private String stockName;
    
    @SerializedName("miktar")
    private double amount;
    
    @SerializedName("createdDate")
    private String createdDate;

    public StockItem(int id, String stockCode, String stockName, double amount, String createdDate) {
        this.id = id;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.amount = amount;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public double getAmount() {
        return amount;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}

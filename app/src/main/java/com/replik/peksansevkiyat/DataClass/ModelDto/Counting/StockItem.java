package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

import com.google.gson.annotations.SerializedName;

public class StockItem {
    @SerializedName("stockCode")
    private String stockCode;

    @SerializedName("stockName")
    private String stockName;

    @SerializedName("totalAmount")
    private double totalAmount;

    // Getter ve Setter metodları
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
} 
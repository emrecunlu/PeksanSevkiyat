package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

public class ConsumableStockItem {
    private String stockCode;
    private String stockName;
    private double quantity;
    private String unit;

    // Getter ve Setter metodları
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    
    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
} 
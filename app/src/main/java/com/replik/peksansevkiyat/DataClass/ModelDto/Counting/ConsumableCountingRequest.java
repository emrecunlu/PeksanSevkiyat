package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

public class ConsumableCountingRequest {
    private String stockCode;
    private double countedQuantity;
    private String countedBy;
    private String countingDate;

    // Getter ve Setter metodları
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    
    public double getCountedQuantity() { return countedQuantity; }
    public void setCountedQuantity(double countedQuantity) { this.countedQuantity = countedQuantity; }
    
    public String getCountedBy() { return countedBy; }
    public void setCountedBy(String countedBy) { this.countedBy = countedBy; }
    
    public String getCountingDate() { return countingDate; }
    public void setCountingDate(String countingDate) { this.countingDate = countingDate; }
} 
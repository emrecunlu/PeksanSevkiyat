package com.replik.peksansevkiyat.DataClass.ModelDto.Stock;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RawMaterialItem implements Serializable {
    @SerializedName("stokKodu")
    private String stockCode;
    
    @SerializedName("stokAdi")
    private String stockName;
    
    @SerializedName("miktar")
    private double amount;

    private List<LotItem> lots;

    public RawMaterialItem(String stockCode, String stockName, double amount) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.amount = amount;
        this.lots = new ArrayList<>();
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

    public List<LotItem> getLots() {
        return lots;
    }

    public void addLot(LotItem lot) {
        if (lots == null) {
            lots = new ArrayList<>();   
        }
        lots.add(lot);
    }

    public double getRemainingAmount() {
        double usedAmount = 0;
        if (lots != null) {
            for (LotItem lot : lots) {
                usedAmount += lot.getAmount();
            }
        }
        return amount - usedAmount;
    }
} 
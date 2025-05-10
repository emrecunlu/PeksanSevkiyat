package com.replik.peksansevkiyat.DataClass.ModelDto.Stock;

import com.google.gson.annotations.SerializedName;

public class StockLot {
    @SerializedName("serino")
    private String serialNo;
    @SerializedName("lot")
    private String lotNo;
    @SerializedName("miktar")
    private double amount;

    public StockLot(String serialNo, String lotNo, double amount) {
        this.serialNo = serialNo;
        this.lotNo = lotNo;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getLotNo() {
        return lotNo;
    }

    public String getSerialNo() {
        return serialNo;
    }
}

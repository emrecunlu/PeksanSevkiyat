package com.replik.peksansevkiyat.DataClass.ModelDto.Stock;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LotItem implements Serializable {
    @SerializedName("serino")
    private String serialNumber;

    @SerializedName("lot")
    private String lotNumber;

    @SerializedName("miktar")
    private double amount;

    public LotItem(String serialNumber, String lotNumber, double amount) {
        this.serialNumber = serialNumber;
        this.lotNumber = lotNumber;
        this.amount = amount;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
} 
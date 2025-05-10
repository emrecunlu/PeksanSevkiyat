package com.replik.peksansevkiyat.DataClass.ModelDto.Transfer;

import com.google.gson.annotations.SerializedName;

public class LineItem {
    @SerializedName("seriNo")
    private String seriNo;

    @SerializedName("amount")
    private double amount;

    public LineItem(String seriNo, double amount) {
        this.seriNo = seriNo;
        this.amount = amount;
    }

    public String getSeriNo() {
        return seriNo;
    }

    public void setSeriNo(String seriNo) {
        this.seriNo = seriNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
} 
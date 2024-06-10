package com.replik.peksansevkiyat.DataClass.ModelDto.Label;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ZarfLabelResult {
    @SerializedName("isSuccessfull")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private ZarfLabel data;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }


    public List<ZarfProducts> getProducts() {
        return data.getProducts();
    }
}

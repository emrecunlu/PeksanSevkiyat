package com.replik.peksansevkiyat.DataClass.ModelDto.Transfer;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TransferRequest {
    @SerializedName("stokKodu")
    private String stokKodu;

    @SerializedName("lineItems")
    private List<LineItem> lineItems;

    public TransferRequest(String stokKodu, List<LineItem> lineItems) {
        this.stokKodu = stokKodu;
        this.lineItems = lineItems;
    }

    public String getStokKodu() {
        return stokKodu;
    }

    public void setStokKodu(String stokKodu) {
        this.stokKodu = stokKodu;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }
} 
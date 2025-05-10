package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

import java.util.List;

public class CreateRecountRequest {
    private String stokKodu;
    private int depoKodu;
    private double miktar;
    private List<RecountLineItem> lineItems;

    public static class RecountLineItem {
        private double miktar;
        private String lot;

        public RecountLineItem(double miktar, String lot) {
            this.miktar = miktar;
            this.lot = lot;
        }

        public double getMiktar() {
            return miktar;
        }

        public void setMiktar(double miktar) {
            this.miktar = miktar;
        }

        public String getLot() {
            return lot;
        }

        public void setLot(String lot) {
            this.lot = lot;
        }
    }

    public String getStokKodu() {
        return stokKodu;
    }

    public void setStokKodu(String stokKodu) {
        this.stokKodu = stokKodu;
    }

    public int getDepoKodu() {
        return depoKodu;
    }

    public void setDepoKodu(int depoKodu) {
        this.depoKodu = depoKodu;
    }

    public double getMiktar() {
        return miktar;
    }

    public void setMiktar(double miktar) {
        this.miktar = miktar;
    }

    public List<RecountLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<RecountLineItem> lineItems) {
        this.lineItems = lineItems;
    }
} 
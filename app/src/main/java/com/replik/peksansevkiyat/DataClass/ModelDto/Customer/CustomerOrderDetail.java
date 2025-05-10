package com.replik.peksansevkiyat.DataClass.ModelDto.Customer;

import java.io.Serializable;

public class CustomerOrderDetail implements Serializable {
    private int id;
    private String sevkNo;
    private String sipNo;
    private String stokKodu;
    private String urunYapkod;
    private int sipMiktar;
    private int sevkMiktar;
    private int gonderilenMiktar;
    private String stokAdi;
    private String color;
    private boolean isDeposit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSevkNo() {
        return sevkNo;
    }

    public void setSevkNo(String sevkNo) {
        this.sevkNo = sevkNo;
    }

    public String getSipNo() {
        return sipNo;
    }

    public void setSipNo(String sipNo) {
        this.sipNo = sipNo;
    }

    public String getStokKodu() {
        return stokKodu;
    }

    public void setStokKodu(String stokKodu) {
        this.stokKodu = stokKodu;
    }

    public String getUrunYapkod() {
        return urunYapkod;
    }

    public void setUrunYapkod(String urunYapkod) {
        this.urunYapkod = urunYapkod;
    }

    public int getSipMiktar() {
        return sipMiktar;
    }

    public void setSipMiktar(int sipMiktar) {
        this.sipMiktar = sipMiktar;
    }

    public int getSevkMiktar() {
        return sevkMiktar;
    }

    public void setSevkMiktar(int sevkMiktar) {
        this.sevkMiktar = sevkMiktar;
    }

    public int getGonderilenMiktar() {
        return gonderilenMiktar;
    }

    public void setGonderilenMiktar(int gonderilenMiktar) {
        this.gonderilenMiktar = gonderilenMiktar;
    }

    public String getStokAdi() {
        return stokAdi;
    }

    public void setStokAdi(String stokAdi) {
        this.stokAdi = stokAdi;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDeposit() {
        return isDeposit;
    }

    public void setDeposit(boolean deposit) {
        isDeposit = deposit;
    }
}

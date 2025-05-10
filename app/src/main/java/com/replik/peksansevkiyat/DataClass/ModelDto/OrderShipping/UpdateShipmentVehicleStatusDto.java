package com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping;

public class UpdateShipmentVehicleStatusDto {
    private String sevkSipNo;
    private int staffNo;
    private int type;
    private Boolean hygiene;
    private Boolean insect;
    private Boolean smell;
    private Boolean leakage;

    public UpdateShipmentVehicleStatusDto(String sevkSipNo, int staffNo, int type, Boolean hygiene, Boolean insect, Boolean smell, Boolean leakage) {
        this.sevkSipNo = sevkSipNo;
        this.staffNo = staffNo;
        this.type = type;
        this.hygiene = hygiene;
        this.insect = insect;
        this.smell = smell;
        this.leakage = leakage;
    }

    public int getType() {
        return type;
    }

    public int getStaffNo() {
        return staffNo;
    }

    public String getSevkSipNo() {
        return sevkSipNo;
    }

    public Boolean getHygiene() {
        return hygiene;
    }

    public Boolean getInsect() {
        return insect;
    }

    public Boolean getSmell() {
        return smell;
    }

    public Boolean getLeakage() {
        return leakage;
    }
}

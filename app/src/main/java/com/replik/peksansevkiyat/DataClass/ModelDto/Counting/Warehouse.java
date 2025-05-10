package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

import com.google.gson.annotations.SerializedName;

public class Warehouse {
    @SerializedName("depoKodu")
    private int warehouseCode;

    @SerializedName("depoIsmi")
    private String warehouseName;

    @SerializedName("depoKilitle")
    private String isLocked;

    @SerializedName("cariKodu")
    private String customerCode;

    @SerializedName("eksibakiye")
    private String negativeBalance;

    @SerializedName("fiatTipi")
    private String priceType;

    @SerializedName("subeKodu")
    private int branchCode;

    @SerializedName("kayityapankul")
    private String createdBy;

    @SerializedName("kayittarihi")
    private String createdDate;

    @SerializedName("duzeltmeyapankul")
    private String modifiedBy;

    @SerializedName("duzeltmetarihi")
    private String modifiedDate;

    // Getter ve Setter metodları
    public int getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(int warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getIsLocked() { return isLocked; }
    public void setIsLocked(String isLocked) { this.isLocked = isLocked; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getNegativeBalance() { return negativeBalance; }
    public void setNegativeBalance(String negativeBalance) { this.negativeBalance = negativeBalance; }

    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }

    public int getBranchCode() { return branchCode; }
    public void setBranchCode(int branchCode) { this.branchCode = branchCode; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(String modifiedDate) { this.modifiedDate = modifiedDate; }
} 
package com.replik.peksansevkiyat.DataClass.ModelDto.Order;

import android.hardware.camera2.CameraExtensionSession;

import com.google.gson.annotations.SerializedName;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShipping;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDtos {

    public static class createOrderByProductsDto {
        String sevkNo;
        String sipno;
        String carikod;
        int staffId;
        boolean isPlt;
        List<OrderProduct> products;

        public createOrderByProductsDto(String sevkNo, String sipno, String carikod, int staffId, boolean isPlt, List<OrderProduct> orderProducts) {
            this.sevkNo = sevkNo;
            this.sipno = sipno;
            this.carikod = carikod;
            this.staffId = staffId;
            this.isPlt = isPlt;
            this.products = orderProducts;
        }
    }

    public static class setPickingItem {
        @SerializedName("sipNo")
        String sipNo;
        @SerializedName("staffId")
        Integer staffId;
        @SerializedName("barcode")
        String barcode;
        @SerializedName("quantity")
        Double quantity;
        @SerializedName("isNumune")
        Boolean isNumune;

        public setPickingItem(String SipNo, Integer StaffId, String Barcode, Double Quantity, Boolean IsNumune) {
            this.sipNo = SipNo;
            this.staffId = StaffId;
            this.barcode = Barcode;
            this.quantity = Quantity;
            this.isNumune = IsNumune;
        }
    }

    public static class delPickingItem {
        @SerializedName("sipNo")
        String sipNo;
        @SerializedName("staffId")
        Integer staffId;
        @SerializedName("barcode")
        String barcode;

        public delPickingItem(String SipNo, Integer StaffId, String Barcode) {
            this.sipNo = SipNo;
            this.staffId = StaffId;
            this.barcode = Barcode;
        }
    }

    public static class setOrderStatus {
        @SerializedName("sipNo")
        String sipNo;
        @SerializedName("orderShipping")
        OrderShipping orderShipping;

        public setOrderStatus(String sipNo, OrderShipping orderShipping) {
            this.sipNo = sipNo;
            this.orderShipping = orderShipping;
        }
    }

    public static class setNetsisShipment {
        String sevkno;
        String carikod;
        int staffId;
        List<OrderShipment> orders;

        public setNetsisShipment(String sevkno, String carikod, int staffId, List<OrderShipment> orders) {
            this.sevkno = sevkno;
            this.carikod = carikod;
            this.staffId = staffId;
            this.orders = orders;
        }
    }
}

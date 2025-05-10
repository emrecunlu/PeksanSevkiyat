package com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail;

public class OrderPrintLabelDto {
    private String orderNo;
    private String customerName;
    private String date;

    public OrderPrintLabelDto(String orderNo, String customerName, String date) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.date = date;
    }
}

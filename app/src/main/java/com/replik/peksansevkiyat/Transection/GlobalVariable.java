package com.replik.peksansevkiyat.Transection;

import android.app.Application;

import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrderDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.Order;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateShipmentVehicleStatusDto;

import java.util.List;

public class GlobalVariable extends Application {
    public static String FileName = "/Replik.txt";

    public static UpdateShipmentVehicleStatusDto shipmentVehicleStatus;

    public static String apiUrl;

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String apiPdfUrl = "http://185.148.84.56:7248/";

    public static List<CustomerOrderDetail> customerOrderDetails;

    public static List<CustomerOrderDetail> getCustomerOrderDetails() {
        return customerOrderDetails;
    }

    public static void setShipmentVehicleStatus(UpdateShipmentVehicleStatusDto updateShipmentVehicleStatusDto) {
        shipmentVehicleStatus = updateShipmentVehicleStatusDto;
    }

    public static UpdateShipmentVehicleStatusDto getShipmentVehicleStatus() {
        return shipmentVehicleStatus;
    }

    public static void setCustomerOrderDetails(List<CustomerOrderDetail> products) {
        customerOrderDetails = products;
    }

    public static void setApiUrl(String url) {
        apiUrl = url;
    }

    public static String apiVersion = "1.0.51";

    public static int userCode;

    public static void setUserCode(int value) {
        userCode = value;
    }

    public static String printerName;

    public static void setPrinter(String value) {
        printerName = value;
    }

    public static int getUserCode() {
        return userCode;
    }

    private static Integer userId;

    public static Integer getUserId() {
        return userId;
    }

    public static void setUserId(Integer value) {
        userId = value;
    }

    private static String userName = "Kullanıcı Girişi Yapılmamış";

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String value) {
        userName = value;
    }

    private static Order selectedOrder;

    public static Order getSelectedOrder() {
        return selectedOrder;
    }

    public static void setSelectedOrder(Order order) {
        selectedOrder = order;
    }

    private static Double manuelQuantityVal;

    public static Double getManuelQuantityVal() {
        return manuelQuantityVal;
    }

    public static void setManuelQuantityVal(Double order) {
        manuelQuantityVal = order;
    }
}

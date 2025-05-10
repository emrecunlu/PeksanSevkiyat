package com.replik.peksansevkiyat.DataClass.ListAdapter;

import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.Customer;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrder;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.Order;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShipping;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingTransport;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.StockItem;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class ListenerInterface {

    public interface UpdateTransportDialogListener {
        void onTransportSelected(OrderShippingTransport transport);

        void onVehicleStatusOpen();
    }

    public interface UpdateVehicleStatusDialogListener {
        void onSubmit(Map<String, Boolean> status);
    }

    public interface ShipmentCustomerOrderListener {
        void onItemClicked(CustomerOrder customerOrder);
    }

    public interface ShipmentCustomerListener {
        void onItemClicked(Customer customer);
    }

    public interface OrderListener {
        void onItemCliked(Order order);
    }

    public interface OrderShippingListener {
        void onItemCliked(OrderShipping orderShipping);//OrderSipping
    }

    public interface RawMaterialListener {
        void onClick(StockItem stockItem, int position);
    }
}

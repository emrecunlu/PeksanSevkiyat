package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.replik.peksansevkiyat.DataClass.ListAdapter.ListAdapter_Customer_Order_Detail;
import com.replik.peksansevkiyat.DataClass.ListAdapter.ListenerInterface;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.Customer;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrder;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrderDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ShippingPrintLabelDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderProduct;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingTransport;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateOrderShippingTransportDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateShipmentVehicleStatusDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;
import com.replik.peksansevkiyat.Transection.PrintBluetooth;
import com.replik.peksansevkiyat.Transection.Voids;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipmentOrderDetailActivity extends AppCompatActivity implements ListenerInterface.UpdateTransportDialogListener, ListenerInterface.UpdateVehicleStatusDialogListener {
    ImageView logoImageView, printImageView;
    Button finishOrderButton;
    EditText barcodeEditText;
    Map<String, Boolean> vehicleStatus = new HashMap<>();
    PrintBluetooth printBluetooth = new PrintBluetooth();
    ProgressDialog loader;
    AlertDialog alert;
    CardView transportCard;
    APIInterface apiInterface;
    ConstraintLayout progressBar;
    TextView staffNameTextView, sipNoTextView, customerTextView, shippingNameTextView, deliveryNameTextView, deliveryAddressTextView, deliveryDate, txtKoliPalet;
    ListAdapter_Customer_Order_Detail listAdapter;
    CheckBox labelToPrint;
    RecyclerView recyclerView;
    Context context = ShipmentOrderDetailActivity.this;
    List<CustomerOrderDetail> customerOrderDetailList = new ArrayList<>();
    Customer customer;
    CustomerOrder order;

    public ShipmentOrderDetailActivity() {
        vehicleStatus.put("hygenie", false);
        vehicleStatus.put("insect", false);
        vehicleStatus.put("smell", false);
        vehicleStatus.put("leakage", false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_order_detail);

        Intent intent = getIntent();

        customer = (Customer) intent.getSerializableExtra("customer");
        order = (CustomerOrder) intent.getSerializableExtra("order");

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);

        loader = Dialog.getDialog(context, getString(R.string.loading));

        labelToPrint = (CheckBox) findViewById(R.id.labelCheckbox);
        txtKoliPalet = (TextView) findViewById(R.id.txtKoliPalet);
        progressBar = (ConstraintLayout) findViewById(R.id.pnlProgressBar);
        barcodeEditText = (EditText) findViewById(R.id.txtBarcode);
        recyclerView = (RecyclerView) findViewById(R.id.customer_order_detail_list_recycler_view);
        logoImageView = (ImageView) findViewById(R.id.imgLogo);
        printImageView = (ImageView) findViewById(R.id.imgPrint);
        staffNameTextView = (TextView) findViewById(R.id.txtUserName);
        sipNoTextView = (TextView) findViewById(R.id.txtSipNo);
        customerTextView = (TextView) findViewById(R.id.txtCari);
        shippingNameTextView = (TextView) findViewById(R.id.txtNakliye);
        deliveryNameTextView = (TextView) findViewById(R.id.txtTeslimAdi);
        deliveryAddressTextView = (TextView) findViewById(R.id.txtTeslimAdresi);
        deliveryDate = (TextView) findViewById(R.id.txtSevkTarih);
        transportCard = (CardView) findViewById(R.id.transportCard);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listAdapter = new ListAdapter_Customer_Order_Detail(customerOrderDetailList);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(layoutManager);

        sipNoTextView.setText(order.getSevkNo());
        staffNameTextView.setText(GlobalVariable.getUserName());

        customerTextView.setText(customer.getFullName());

        shippingNameTextView.setText(order.getNakliyeTipi());

        txtKoliPalet.setText(order.getKoliPalet());

        deliveryDate.setText(Voids.formatDate(order.getShipmentDate()));
        deliveryNameTextView.setText(order.getTeslimAdi());
        deliveryAddressTextView.setText(order.getTeslimAdresi());

        logoImageView.setOnClickListener(v -> {
            finish();
        });

        transportCard.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            new UpdateTransportDialogFragment(order.getNakliyeId(), this).show(fragmentManager, "UPDATE_TRANSPORT_DIALOG");
        });

        finishOrderButton = (Button) findViewById(R.id.btnFinishOrder);
        finishOrderButton.setVisibility(View.GONE);
        finishOrderButton.setOnClickListener(v -> {
            if (vehicleStatus.containsValue(false)) {
                Toast.makeText(context, getString(R.string.check_vehicle_control), Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getSupportFragmentManager();
                new UpdateVehicleStatusDialogFragment(vehicleStatus, ShipmentOrderDetailActivity.this).show(fragmentManager, "UPDATE_VEHICLE_STATUS");

                return;
            }


            UpdateShipmentVehicleStatusDto shipmentVehicleStatusDto =
                    new UpdateShipmentVehicleStatusDto(order.getSevkNo(), GlobalVariable.getUserCode(), 0, vehicleStatus.getOrDefault("hygenie", false), vehicleStatus.getOrDefault("insect", false), vehicleStatus.getOrDefault("smell", false), vehicleStatus.getOrDefault("leakage", false));

            GlobalVariable.setShipmentVehicleStatus(shipmentVehicleStatusDto);
            GlobalVariable.setCustomerOrderDetails(customerOrderDetailList);

            Intent i = new Intent(this, ShipmentOrderFinish.class);

            i.putExtra("customer", customer);
            i.putExtra("order", order);

            startActivity(i);
        });

        barcodeEditText.setInputType(InputType.TYPE_NULL);
        barcodeEditText.requestFocus();
        barcodeEditText.setOnKeyListener(
                (v, keyCode, event) -> {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (barcodeEditText.getText().toString().contains("=")) {
                                String[] value = barcodeEditText.getText().toString().split("=");

                                barcodeEntry(value[1].toUpperCase());
                            } else
                                barcodeEntry(barcodeEditText.getText().toString());

                            return true;
                        }

                        /*if (txtBarcode.getText().toString().contains("=")) {
                            String value = txtBarcode.getText().toString().split("=")[1];

                            fnBarcodeControl(value);
                        } else
                            fnBarcodeControl(txtBarcode.getText().toString());*/
                    }
                    return false;
                }
        );

        fetchOrderDetailList();
    }

    void fetchOrderDetailList() {
        progressBar.setVisibility(View.VISIBLE);

        apiInterface.getOrderDetail(order.getSevkNo()).enqueue(
                new Callback<List<CustomerOrderDetail>>() {
                    @Override
                    public void onResponse(Call<List<CustomerOrderDetail>> call, Response<List<CustomerOrderDetail>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            customerOrderDetailList = response.body();
                            listAdapter.setList(customerOrderDetailList);

                            if (response.body().stream().allMatch(x -> x.getSevkMiktar() == x.getGonderilenMiktar())) {
                                finishOrderButton.setVisibility(View.VISIBLE);
                            }
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<CustomerOrderDetail>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    void barcodeEntry(String barcode) {
        barcodeEditText.setText("");
        loader.show();

        apiInterface.getPalletDetail(barcode.trim()).enqueue(
                new Callback<List<PalletDetail>>() {
                    @Override
                    public void onResponse(Call<List<PalletDetail>> call, Response<List<PalletDetail>> response) {
                        loader.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            List<CustomerOrderDetail> list = customerOrderDetailList.stream().filter(x -> x.getSevkMiktar() != x.getGonderilenMiktar()).collect(Collectors.toList());

                            if (response.body().size() == list.size() || response.body().size() < list.size()) {
                                for (PalletDetail palletDetail : response.body()) {
                                    if (list.stream().noneMatch(x -> Objects.equals(x.getStokKodu(), palletDetail.getStokKod()) && Objects.equals(x.getUrunYapkod(), palletDetail.getYapkod()))) {
                                        alert = Alert.getAlert(context, getString(R.string.error), "Hatalı Palet Serisi!");
                                        alert.show();

                                        return;
                                    }
                                }
                            } else {
                                alert = Alert.getAlert(context, getString(R.string.error), "Hatalı Palet Serisi!");
                                alert.show();

                                return;
                            }

                            for (PalletDetail product : response.body()) {
                                Optional<CustomerOrderDetail> orderDetail = list.stream().filter(x -> Objects.equals(x.getStokKodu(), product.getStokKod()) && Objects.equals(x.getUrunYapkod(), product.getYapkod())).findFirst();

                                if (orderDetail.isPresent()) {
                                    final OrderDtos.createOrderByProductsDto orderByProductsDto =
                                            new OrderDtos.createOrderByProductsDto(
                                                    orderDetail.get().getSevkNo(),
                                                    orderDetail.get().getSipNo(),
                                                    customer.getCode(),
                                                    GlobalVariable.getUserId(),
                                                    barcode.toUpperCase().contains("PLT") && orderDetail.get().isDeposit(),
                                                    product.getProducts().stream().map(x -> new OrderProduct(x, product.getStokKod(), product.getYapkod())).collect(Collectors.toList())
                                            );

                                    loader.show();

                                    apiInterface.createOrderByProducts(orderByProductsDto).enqueue(
                                            new Callback<Result>() {
                                                @Override
                                                public void onResponse(Call<Result> call, Response<Result> response) {
                                                    loader.dismiss();

                                                    if (response.body() != null) {
                                                        if (response.body().getSuccess()) {
                                                            fetchOrderDetailList();

                                                            /*CHECKBOX CHECKED CONTROL*/
                                                            if (labelToPrint.isChecked()) {
                                                                printLabel(new ShippingPrintLabelDto(
                                                                        order.getSevkNo(),
                                                                        order.getTeslimAdi(),
                                                                        order.getTeslimAdresi()
                                                                ));
                                                            }

                                                            Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                                                            alert.show();
                                                        }
                                                    } else {
                                                        alert = Alert.getAlert(context, getString(R.string.error), response.message());
                                                        alert.show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Result> call, Throwable t) {
                                                    loader.dismiss();

                                                    alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                                                    alert.show();
                                                }
                                            }
                                    );
                                }
                            }

                        } else {
                            alert = Alert.getAlert(context, getString(R.string.error), getString(R.string.danger));
                            alert.show();
                        }

                        loader.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<PalletDetail>> call, Throwable t) {
                        loader.dismiss();
                    }
                }
        );
    }

    void printLabel(ShippingPrintLabelDto shippingPrintLabelDto) {
        try {
            PrintBluetooth.printer_id = GlobalVariable.printerName;

            printBluetooth.findBT();
            printBluetooth.openBT();
            printBluetooth.printTestLabel(shippingPrintLabelDto);
            printBluetooth.closeBT();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTransportSelected(OrderShippingTransport transport) {
        loader.show();
        apiInterface.updateTransportType(new UpdateOrderShippingTransportDto(order.getSevkNo(), transport.getId())).enqueue(
                new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        loader.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {
                                order.setNakliyeId(transport.getId());
                                order.setNakliyeTipi(transport.getDesc());

                                shippingNameTextView.setText(transport.getDesc());
                            } else {
                                alert = Alert.getAlert(context, getString(R.string.error), getString(R.string.error));
                                alert.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        loader.dismiss();

                        alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                        alert.show();
                    }
                }
        );
    }

    @Override
    public void onVehicleStatusOpen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new UpdateVehicleStatusDialogFragment(vehicleStatus, this).show(fragmentManager, "UPDATE_VEHICLE_STATUS");
    }

    @Override
    public void onSubmit(Map<String, Boolean> status) {
        this.vehicleStatus = status;
    }
}
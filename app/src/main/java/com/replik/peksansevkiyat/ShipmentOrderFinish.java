package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.replik.peksansevkiyat.DataClass.ListAdapter.ListAdapter_OrderShipping;
import com.replik.peksansevkiyat.DataClass.ListAdapter.ListenerInterface;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.Customer;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrder;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ZarfLabel;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ZarfLabelResult;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderShipment;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShipping;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingList;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateShipmentVehicleStatusDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Interface.APIPdfClient;
import com.replik.peksansevkiyat.Interface.ApiPdfInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;
import com.replik.peksansevkiyat.Transection.PrintBluetooth;
import com.replik.peksansevkiyat.Transection.Voids;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipmentOrderFinish extends AppCompatActivity implements ListenerInterface.OrderShippingListener {
    APIInterface apiInterface;
    ApiPdfInterface apiPdfInterface;
    ProgressDialog nDialog;
    AlertDialog alert;

    Context context = ShipmentOrderFinish.this;

    View pBar;
    ConstraintLayout pnlSearch, pnlAddShipping;
    ScrollView pnlManualEntry;
    Customer customer;
    CustomerOrder order;
    ImageButton imgLogo, imgSettings;
    TextView txtUserName, txtSearch, txtSipNo, txtSipCari, txtSipTarih;
    TextView txtOrderPlaka, txtOrderVergiNo, txtOrderIsim, txtOrderIlce, txtOrderIl, txtOrderUlke, txtOrderPosta, txtOrderSoforAd, txtOrderSoforSoyad, txtOrderSoforTc, txtOrderAciklama, txtOrderDorse;
    Button btnAddShipping, btnOrderFinish;

    RecyclerView lstData;
    List<OrderShipping> lst = new ArrayList<>();
    LinearLayoutManager layoutManager;
    ListAdapter_OrderShipping adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_finish);

        if (GlobalVariable.getCustomerOrderDetails() == null) {
            finish();
        }

        Intent intent = getIntent();

        customer = (Customer) intent.getSerializableExtra("customer");
        order = (CustomerOrder) intent.getSerializableExtra("order");

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        apiPdfInterface = APIPdfClient.getRetrofit().create(ApiPdfInterface.class);
        nDialog = Dialog.getDialog(context, getString(R.string.loading));

        pBar = findViewById(R.id.pnlProgressBar);
        pnlAddShipping = findViewById(R.id.pnlAddShipping);
        pnlSearch = findViewById(R.id.pnlSearch);
        pnlManualEntry = findViewById(R.id.pnlManualEntry);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserName.setText(GlobalVariable.getUserName());

        lstData = findViewById(R.id.lstData);
        layoutManager = new LinearLayoutManager(context);
        lstData.setLayoutManager(layoutManager);
        adapter = new ListAdapter_OrderShipping(lst, this);
        lstData.setAdapter(adapter);

        txtSipNo = findViewById(R.id.txtSipNo);
        txtSipNo.setText(order.getSevkNo());
        txtSipCari = findViewById(R.id.txtSipCari);
        txtSipCari.setText(customer.getFullName());
        txtSipTarih = findViewById(R.id.txtSipTarih);
        txtSipTarih.setText(Voids.formatDate(order.getShipmentDate()));

        txtOrderPlaka = findViewById(R.id.txtOrderPlaka);
        txtOrderVergiNo = findViewById(R.id.txtOrderVergiNo);
        txtOrderIsim = findViewById(R.id.txtOrderIsim);
        txtOrderIlce = findViewById(R.id.txtOrderIlce);
        txtOrderIl = findViewById(R.id.txtOrderIl);
        txtOrderUlke = findViewById(R.id.txtOrderUlke);
        txtOrderPosta = findViewById(R.id.txtOrderPosta);
        txtOrderSoforAd = findViewById(R.id.txtOrderSoforAd);
        txtOrderSoforSoyad = findViewById(R.id.txtOrderSoforSoyad);
        txtOrderSoforTc = findViewById(R.id.txtOrderSoforTc);
        txtOrderAciklama = findViewById(R.id.txtOrderAciklama);
        txtOrderDorse = findViewById(R.id.txtOrderDorse1);

        pnlAddShipping.setVisibility(View.GONE);
        fnGetShippingList("");

        imgSettings = findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(view -> {
            Intent i = new Intent(context, SettingsActivity.class);
            startActivity(i);
        });

        imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(v -> {
            GlobalVariable.setCustomerOrderDetails(null);
            finish();
        });

        txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                lst = new ArrayList<>();
                fnGetShippingList(txtSearch.getText().toString());
                return true;
            }
            return false;
        });

        btnAddShipping = findViewById(R.id.btnAddShipping);
        btnAddShipping.setOnClickListener(view -> {
            nDialog.show();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.sure));
            builder.setMessage(getString(R.string.question_order_shipping_add));
            builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> nDialog.dismiss());
            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                nDialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                pnlManualEntry.setVisibility(View.VISIBLE);
                pnlSearch.setVisibility(View.GONE);
            });
            builder.show();
        });

        btnOrderFinish = findViewById(R.id.btnOrderFinish);
        btnOrderFinish.setOnClickListener(view -> {
            String ErrorMessage = "";
            int renkE = Color.rgb(240, 80, 80);
            if (txtOrderPlaka.getText().toString().isEmpty()) {
                txtOrderPlaka.setHintTextColor(renkE);
                ErrorMessage += getString(R.string.order_plaka);
            }

            if (txtOrderVergiNo.getText().toString().isEmpty()) {
                txtOrderVergiNo.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_vergino);
            }

            if (txtOrderIsim.getText().toString().isEmpty()) {
                txtOrderIsim.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_isim);
            }

            if (txtOrderIlce.getText().toString().isEmpty()) {
                txtOrderIlce.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_ilce);
            }

            if (txtOrderIl.getText().toString().isEmpty()) {
                txtOrderIl.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_il);
            }

            if (txtOrderIl.getText().toString().isEmpty()) {
                txtOrderIl.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_il);
            }

            if (txtOrderUlke.getText().toString().isEmpty()) {
                txtOrderUlke.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_ulke);
            }

            if (txtOrderPosta.getText().toString().isEmpty()) {
                txtOrderPosta.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_posta);
            }

            if (txtOrderSoforAd.getText().toString().isEmpty()) {
                txtOrderSoforAd.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_sofor_ad);
            }

            if (txtOrderSoforSoyad.getText().toString().isEmpty()) {
                txtOrderSoforSoyad.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_sofor_soyad);
            }

            if (txtOrderSoforTc.getText().toString().isEmpty()) {
                txtOrderSoforTc.setHintTextColor(renkE);
                ErrorMessage += (!ErrorMessage.isEmpty() ? "\n" : "") + getString(R.string.order_sofor_tc);
            }

            if (ErrorMessage.isEmpty()) {
                OrderShipping os = new OrderShipping();
                os.setOrderShipping(
                        txtOrderPlaka.getText().toString(),
                        txtOrderVergiNo.getText().toString(),
                        txtOrderIsim.getText().toString(),
                        txtOrderIlce.getText().toString(),
                        txtOrderIl.getText().toString(),
                        txtOrderUlke.getText().toString(),
                        txtOrderPosta.getText().toString(),
                        txtOrderSoforAd.getText().toString(),
                        txtOrderSoforAd.getText().toString(),
                        txtOrderAciklama.getText().toString(),
                        txtOrderSoforTc.getText().toString(),
                        txtOrderDorse.getText().toString());

                final List<OrderShipment> orderShipments = GlobalVariable.getCustomerOrderDetails().stream().map(x -> new OrderShipment(
                        x.getSipNo(),
                        os
                )).collect(Collectors.toList());

                postOrderFinish(new OrderDtos.setNetsisShipment(order.getSevkNo(), customer.getCode(), GlobalVariable.getUserId(), orderShipments));
            } else {
                alert = Alert.getAlert(context, getString(R.string.error), ErrorMessage + "\n" + getString(R.string.not_empty));
                alert.show();
            }
        });
    }

    void postOrderFinish(OrderDtos.setNetsisShipment data) {
        nDialog.show();

        apiInterface.createNetsisShipment(data).enqueue(
                new Callback<ZarfLabelResult>() {
                    @Override
                    public void onResponse(@NonNull Call<ZarfLabelResult> call, @NonNull Response<ZarfLabelResult> response) {
                        nDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {

                                nDialog.show();
                                apiPdfInterface.generatePdf(order.getSevkNo(), GlobalVariable.getUserCode())
                                        .enqueue(
                                                new Callback<Result>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> r) {
                                                        apiInterface.updateVehicleStatus(GlobalVariable.getShipmentVehicleStatus())
                                                                .enqueue(
                                                                        new Callback<Result>() {
                                                                            @Override
                                                                            public void onResponse(Call<Result> call, Response<Result> _) {
                                                                                nDialog.hide();

                                                                                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();

                                                                                GlobalVariable.setShipmentVehicleStatus(null);
                                                                                printLabel(response.body().getData(), order.getSevkNo());

                                                                                Intent i = new Intent(context, MenuActivity.class);
                                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                startActivity(i);

                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Result> call, Throwable t) {
                                                                                nDialog.hide();

                                                                                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                                                                                alert.show();
                                                                            }
                                                                        }
                                                                );


                                                    }

                                                    @Override
                                                    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {

                                                        Log.i("info", call.toString());

                                                        nDialog.dismiss();

                                                        alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                                                        alert.show();
                                                    }
                                                }
                                        );
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
                    public void onFailure(@NonNull Call<ZarfLabelResult> call, @NonNull Throwable t) {
                        nDialog.dismiss();

                        alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                        alert.show();
                    }
                }
        );
    }

    private void fnGetShippingList(String search) {
        pBar.setVisibility(View.VISIBLE);
        apiInterface.getOrderShippingList(GlobalVariable.getUserId(), search).enqueue(new Callback<OrderShippingList>() {
            @Override
            public void onResponse(@NonNull Call<OrderShippingList> call, @NonNull Response<OrderShippingList> response) {
                pBar.setVisibility(View.GONE);
                assert response.body() != null;
                if (response.body().getSuccess()) {
                    adapter.setData(response.body().getOrderShipping());

                    if (response.body().getOrderShipping().isEmpty())
                        pnlAddShipping.setVisibility(View.VISIBLE);
                    else
                        pnlAddShipping.setVisibility(View.GONE);
                } else {
                    alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderShippingList> call, @NonNull Throwable t) {
                pBar.setVisibility(View.GONE);
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }

    @Override
    public void onItemCliked(OrderShipping orderShipping) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_order_finish));

        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {

            final List<OrderShipment> orderShipments = GlobalVariable.getCustomerOrderDetails().stream().map(x -> new OrderShipment(
                    x.getSipNo(),
                    orderShipping
            )).collect(Collectors.toList());

            postOrderFinish(new OrderDtos.setNetsisShipment(order.getSevkNo(), customer.getCode(), GlobalVariable.getUserId(), orderShipments));
        });

        builder.show();
    }

    void printLabel(ZarfLabel label, String sevkNo) {
        try {
            PrintBluetooth printBluetooth = new PrintBluetooth();
            PrintBluetooth.printer_id = GlobalVariable.printerName;

            label.setSevkNo(sevkNo);

            printBluetooth.findBT();
            printBluetooth.openBT();
            printBluetooth.printZarfHeader(label);
            printBluetooth.printZarfTable(label.getProducts());
            printBluetooth.closeBT();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
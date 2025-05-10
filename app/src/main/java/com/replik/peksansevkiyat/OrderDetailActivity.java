package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.replik.peksansevkiyat.DataClass.ListAdapter.ListAdapter_OrderDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail.OrderDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail.OrderDetailList;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail.OrderPrintLabelDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.DataClass.ModelDto.Seritra.spSeritraSingle;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Interface.Interfaces;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;
import com.replik.peksansevkiyat.Transection.PrintBluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements Interfaces.OrderManuelQuantityInterface {
    APIInterface apiInterface;
    ProgressDialog nDialog;
    AlertDialog alert;

    PrintBluetooth printBluetooth = new PrintBluetooth();

    Context context = OrderDetailActivity.this;

    View pBar;

    List<OrderDetail> lst = new ArrayList<>();

    ImageButton imgLogo, imgSettings;
    TextView txtUserName, txtBarcode, txtSipNo, txtSipCari, txtSipTarih;
    Button btnFinishOrder;
    RecyclerView lstData;

    LinearLayoutManager layoutManager;
    ListAdapter_OrderDetail adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //seçili sipariş yok ise listeye yönlendir ki sorun olmasın
        if (GlobalVariable.getSelectedOrder() == null) {
            Intent ii = new Intent(context, OrderActivity.class);
            startActivity(ii);
            finish();
        }

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        nDialog = Dialog.getDialog(context, getString(R.string.loading));
        pBar = findViewById(R.id.pnlProgressBar);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserName.setText(GlobalVariable.getUserName());

        txtSipNo = findViewById(R.id.txtSipNo);
        txtSipNo.setText(GlobalVariable.getSelectedOrder().getSipNo());
        txtSipCari = findViewById(R.id.txtSipCari);
        txtSipCari.setText(GlobalVariable.getSelectedOrder().getCari());
        txtSipTarih = findViewById(R.id.txtSipTarih);
        txtSipTarih.setText(GlobalVariable.getSelectedOrder().getTarih());

        lstData = findViewById(R.id.lstData);
        layoutManager = new LinearLayoutManager(context);
        lstData.setLayoutManager(layoutManager);
        adapter = new ListAdapter_OrderDetail(lst);
        lstData.setAdapter(adapter);

        fnGetOrderDetailList(GlobalVariable.getSelectedOrder().getSipNo());

        imgSettings = findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //etiketi yazıdr
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.sure));
                builder.setMessage(getString(R.string.question_pallet_print));
                builder.setNegativeButton(getString(R.string.no), null);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int ii) {
                        print();
                    }
                });
                builder.show();
            }
        });

        imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnBackList();
            }
        });

        txtBarcode = findViewById(R.id.txtSearch);
        //txtBarcode.setShowSoftInputOnFocus(false);
        txtBarcode.setInputType(InputType.TYPE_NULL);
        txtBarcode.requestFocus();
        txtBarcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    /*if (event.getAction() == KeyEvent.KEYCODE_ENTER) {
                        Toast.makeText(context, "The text is: " + txtBarcode.getText() , Toast.LENGTH_LONG).show();
                        fnSeriControl(txtBarcode.getText().toString());
                        return true;
                    }*/

                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (GlobalVariable.getSelectedOrder().getNumuneSip())
                            if (txtBarcode.getText().toString().contains("=")) {
                                String[] value = txtBarcode.getText().toString().split("=");

                                fnShowManuelQuantity(value[1].toUpperCase());

                            } else
                                fnShowManuelQuantity(txtBarcode.getText().toString());
                        else
                            //buras
                            if (txtBarcode.getText().toString().contains("=")) {
                                String[] value = txtBarcode.getText().toString().split("=");

                                fnOrderPicking(value[1].toUpperCase(), 1.0);

                            } else
                                fnOrderPicking(txtBarcode.getText().toString(), 1.0);

                        return true;
                    }
                }
                return false;
            }
        });

        btnFinishOrder = findViewById(R.id.btnFinishOrder);
        btnFinishOrder.setVisibility(View.GONE);
        btnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nDialog.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.sure));
                builder.setMessage(getString(R.string.question_order_finish));
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //onBackPressed();
                        nDialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent ii = new Intent(context, OrderFinishActivity.class);
                        startActivity(ii);
                        finish();
                        nDialog.hide();
                    }
                });
                builder.show();
            }
        });
    }

    void fnShowManuelQuantity(String barcode) {
        nDialog.show();
        apiInterface.getSeriControl(GlobalVariable.getUserId(), barcode).enqueue(new Callback<spSeritraSingle>() {
            @Override
            public void onResponse(Call<spSeritraSingle> call, Response<spSeritraSingle> response) {
                nDialog.hide();
                if (response.body().getSuccess()) {
                    GlobalVariable.setManuelQuantityVal(response.body().getSpSeritra().getMiktar().doubleValue());
                    OrderManualQuantityFragment of = new OrderManualQuantityFragment();
                    of.show(getFragmentManager(), "modalX");
                } else {
                    nDialog.hide();
                    alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<spSeritraSingle> call, Throwable t) {
                nDialog.hide();
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }

    @Override
    public void modalTick(Boolean statu, Double value) {
        if (statu)
            fnOrderPicking(txtBarcode.getText().toString(), value);
        else
            txtBarcode.setText("");
    }

    void fnOrderPicking(String barcode, Double quantity) {
        txtBarcode.setText("");
        nDialog.show();
        apiInterface.setOrderCollectedByBarcode(
                new OrderDtos.setPickingItem(
                        GlobalVariable.getSelectedOrder().getSipNo(),
                        GlobalVariable.getUserId(),
                        barcode,
                        quantity,
                        GlobalVariable.getSelectedOrder().getNumuneSip())).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                nDialog.hide();
                if (response.body().getSuccess())
                    fnGetOrderDetailList(GlobalVariable.getSelectedOrder().getSipNo());
                else {
                    nDialog.hide();
                    if (response.body().getMessage().equals("DEL_PICKING")) {
                        //silme onayı
                        fnOrderPickingDelete(barcode);
                    } else {
                        alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                        alert.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                nDialog.hide();
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }


    private void fnOrderPickingDelete(String barcode) {
        nDialog.show();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_order_detail_delete));
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nDialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fnIslem(barcode);
            }
        });
        builder.show();
    }

    void fnIslem(String barcode) {
        nDialog.show();
        apiInterface.delOrderPicking(new OrderDtos.delPickingItem(GlobalVariable.getSelectedOrder().getSipNo(), GlobalVariable.getUserId(), barcode))
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if (response.body().getSuccess()) {
                            Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();
                            fnGetOrderDetailList(GlobalVariable.getSelectedOrder().getSipNo());
                        } else {
                            nDialog.hide();
                            alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        nDialog.hide();
                        alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                        alert.show();
                    }
                });
    }

    void fnGetOrderDetailList(String sipNo) {
        pBar.setVisibility(View.VISIBLE);
        apiInterface.getOrderDetailList(GlobalVariable.getUserId(), sipNo).enqueue(new Callback<OrderDetailList>() {
            @Override
            public void onResponse(Call<OrderDetailList> call, Response<OrderDetailList> response) {
                pBar.setVisibility(View.GONE);
                if (response.body().getSuccess()) {
                    if (response.body().getSuccess()) {
                        if (response.body().getOrderDetail().stream().filter(orderDetail -> orderDetail.getToplananMiktar().floatValue() > 0).count() > 0)
                            btnFinishOrder.setVisibility(View.VISIBLE);
                        else
                            btnFinishOrder.setVisibility(View.GONE);

                        adapter.setData(response.body().getOrderDetail());
                    } else {
                        alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                        alert.show();
                    }
                } else {
                    alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailList> call, Throwable t) {
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
                btnFinishOrder.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        fnBackList();
    }

    void fnBackList() {
        nDialog.show();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_order_detail_back));
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nDialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //onBackPressed();
                GlobalVariable.setSelectedOrder(null);
                finish();
                nDialog.dismiss();
            }
        });
        builder.show();
    }

    void print() {
        try {
            Toast.makeText(context, "Etiket", Toast.LENGTH_LONG).show();

           /* PrintBluetooth.printer_id = GlobalVariable.printerName;

            printBluetooth.findBT();
            printBluetooth.openBT();
            printBluetooth.printOrderLabel(new OrderPrintLabelDto(
                    GlobalVariable.getSelectedOrder().getSipNo(),
                    GlobalVariable.getSelectedOrder().getCari(),
                    GlobalVariable.getSelectedOrder().getTarih()
            ));
            printBluetooth.closeBT();*/
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
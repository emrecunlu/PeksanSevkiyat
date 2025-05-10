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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.replik.peksansevkiyat.DataClass.ListAdapter.ListAdapter_PalletDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Dtos.dtoPalletDetailAndSeritra_data;
import com.replik.peksansevkiyat.DataClass.ModelDto.Dtos.getStandartLong;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletSingle;
import com.replik.peksansevkiyat.DataClass.ModelDto.PalletDetail.PalletDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.PalletDetail.PalletDetailDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.PalletDetail.PalletDetailList;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.DataClass.ModelDto.Seritra.spSeritra;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaletteEdit extends AppCompatActivity {
    APIInterface apiInterface;
    ProgressDialog nDialog;
    AlertDialog alert;

    Context context = PaletteEdit.this;

    ImageButton imgLogo, imgSettings;
    TextView txtUserName, txtBarcode, lblBoxCount;
    ListView lstData;
    Button btnPalletDelete;

    Integer palletId = -1; //231114121150756PLT   231114121050960PLT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_edit);

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        nDialog = Dialog.getDialog(context, getString(R.string.loading));
        lstData = findViewById(R.id.lstData);

        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserName.setText(GlobalVariable.getUserName());

        lblBoxCount = (TextView) findViewById(R.id.lblBoxCount);

        imgSettings = findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SettingsActivity.class);
                startActivity(i);
            }
        });

        imgLogo = (ImageButton) findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        txtBarcode = findViewById(R.id.txtSearch);
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
                        if (txtBarcode.getText().toString().contains("=")) {
                            String[] value = txtBarcode.getText().toString().split("=");

                            fnBarcodeControl(value[1].toUpperCase());

                        } else
                            fnBarcodeControl(txtBarcode.getText().toString().toUpperCase());
                        return true;
                    }
                }
                return false;
            }
        });

        btnPalletDelete = findViewById(R.id.btnPalletPrint);
        btnPalletDelete.setVisibility(View.GONE);
        btnPalletDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnDeletePallet();
            }
        });
    }

    void fnBarcodeControl(String barcode) {
        lblBoxCount.setText("0");
        txtBarcode.setText("");
        if (barcode.endsWith("PLT"))
            fnGetPallet(barcode);
        else if (palletId == -1) {
            alert = Alert.getAlert(context, getString(R.string.danger), getString(R.string.error_pallet_barcode));
            alert.show();
        } else
            fnPalletInSeriEdit(barcode);
    }

    void fnPalletInSeriEdit(String barcode) {
        nDialog.show();
        apiInterface.getSeriControlFromPallet(GlobalVariable.getUserId(), barcode).enqueue(new Callback<dtoPalletDetailAndSeritra_data>() {
            @Override
            public void onResponse(Call<dtoPalletDetailAndSeritra_data> call, Response<dtoPalletDetailAndSeritra_data> response) {
                nDialog.hide();

                if (response.body().getSuccess()) {
                    spSeritra seritra = response.body().getDtoPalletDetailAndSeritra().getSeritra();
                    PalletDetail palletDetail = response.body().getDtoPalletDetailAndSeritra().getPalletDetail();
                    if (palletDetail == null) {
                        fnAddPalletDetail(seritra);
                    } else if (!palletDetail.getPalletId().equals(palletId)) {
                        alert = Alert.getAlert(context, getString(R.string.danger), getString(R.string.error_wrong_pallet));
                        alert.show();
                    } else {
                        fnDeleteDetail(palletDetail.getId());
                    }
                } else {
                    nDialog.hide();
                    alert = Alert.getAlert(context, getString(R.string.danger), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<dtoPalletDetailAndSeritra_data> call, Throwable t) {
                alert = Alert.getAlert(context, getString(R.string.danger), t.getMessage());
                alert.show();
            }
        });
    }

    void fnAddPalletDetail(spSeritra seritra) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_pallet_detail_add));
        builder.setNegativeButton(getString(R.string.no), null);

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PalletDetailDtos.setPalletDetailColumn data = new PalletDetailDtos.setPalletDetailColumn(palletId, "", GlobalVariable.getUserId(), seritra.getSeriNo(), seritra.getStokKodu(), seritra.getMiktar(), seritra.getMiktar2());

                nDialog.show();
                apiInterface.setPalletDetail(data).enqueue(new Callback<PalletDetailList>() {
                    @Override
                    public void onResponse(Call<PalletDetailList> call, Response<PalletDetailList> response) {
                        nDialog.hide();
                        if (response.body().getSuccess())
                            setPalletDetailAdapter(response.body());
                        else {
                            nDialog.hide();

                            alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PalletDetailList> call, Throwable t) {
                        alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                        alert.show();
                    }
                });
            }
        });
        builder.show();
    }

    void fnDeleteDetail(Integer palletDetailId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_pallet_detail_delete));
        builder.setNegativeButton(getString(R.string.no), null);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nDialog.show();
                apiInterface.delPalletDetail(new PalletDetailDtos.delPalletDetailColumn(palletDetailId, GlobalVariable.getUserId())).enqueue(new Callback<PalletDetailList>() {
                    @Override
                    public void onResponse(Call<PalletDetailList> call, Response<PalletDetailList> response) {
                        nDialog.hide();

                        if (response.body().getSuccess()) {
                            setPalletDetailAdapter(response.body());
                            Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();
                        } else {
                            nDialog.hide();

                            alert = Alert.getAlert(context, getString(R.string.danger), response.body().getMessage());
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PalletDetailList> call, Throwable t) {
                        alert = Alert.getAlert(context, getString(R.string.danger), t.getMessage());
                        alert.show();
                    }
                });
            }
        });
        builder.show();
    }

    void fnDeletePallet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sure));
        builder.setMessage(getString(R.string.question_pallet_delete));
        builder.setNegativeButton(getString(R.string.no), null);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nDialog.show();

                apiInterface.delPallet(new getStandartLong(GlobalVariable.getUserId(), palletId)).enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        nDialog.hide();

                        if (response.body().getSuccess()) {
                            palletId = -1;
                            setPalletDetailAdapter(null);
                            btnPalletDelete.setVisibility(View.GONE);
                            Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();
                        } else {
                            alert = Alert.getAlert(context, getString(R.string.danger), response.body().getMessage());
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        nDialog.hide();

                        alert = Alert.getAlert(context, getString(R.string.danger), t.getMessage());
                        alert.show();
                    }
                });
            }
        });
        builder.show();
    }

    void fnGetPallet(String barcode) {
        nDialog.show();

        apiInterface.getPalletControl(GlobalVariable.getUserId(), barcode).enqueue(new Callback<PalletSingle>() {
            @Override
            public void onResponse(Call<PalletSingle> call, Response<PalletSingle> response) {
                nDialog.hide();

                if (response.body().getSuccess()) {
                    if (response.body().getPallet() != null) {
                        palletId = response.body().getPallet().getId();
                        fnGetPalletDetailList();
                    } else {
                        alert = Alert.getAlert(context, getString(R.string.error), getString(R.string.no_pallet));
                        alert.show();
                    }
                } else {
                    nDialog.hide();
                    alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<PalletSingle> call, Throwable t) {
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }

    void fnGetPalletDetailList() {
        nDialog.show();
        apiInterface.getPalletDetail(GlobalVariable.getUserId(), palletId).enqueue(new Callback<PalletDetailList>() {
            @Override
            public void onResponse(Call<PalletDetailList> call, Response<PalletDetailList> response) {
                nDialog.hide();

                if (response.body().getSuccess())
                    setPalletDetailAdapter(response.body());
                else {
                    nDialog.hide();

                    alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<PalletDetailList> call, Throwable t) {
                nDialog.hide();

                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }

    void setPalletDetailAdapter(PalletDetailList lstPalletDetail) {
        ArrayList lst = new ArrayList<PalletDetail>();

        if (lstPalletDetail != null)
            for (PalletDetail p : lstPalletDetail.getPalletDetails()) {
                palletId = p.getPalletId();
                btnPalletDelete.setVisibility(View.VISIBLE);
                lst.add(p);
            }

        lblBoxCount.setText(String.valueOf(lst.size()));

        ListAdapter_PalletDetail adapter = new ListAdapter_PalletDetail(context, R.layout.list_adapter_pallet_detail, lst);
        lstData.setAdapter(adapter);
    }
}
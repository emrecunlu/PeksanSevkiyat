package com.replik.peksansevkiyat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.replik.peksansevkiyat.DataClass.ListAdapter.ListAdapter_PalletContent;
import com.replik.peksansevkiyat.DataClass.ModelDto.ErrorResult;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.CreatePalletDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletContent;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletContentDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletContentResponse;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletLabelResponse;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;
import com.replik.peksansevkiyat.Transection.PrintBluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaletteAdd extends AppCompatActivity {

    TextView txtStaffName, txtProductCount;
    ImageButton imgHeaderLogo;
    Button btnPrint;
    EditText inputBarcode;
    APIInterface apiInterface;
    ProgressDialog loader;
    List<PalletContent> products = new ArrayList<>();
    ListView listView;
    PrintBluetooth printBluetooth = new PrintBluetooth();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_palette_add);

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);

        loader = Dialog.getDialog(this, getString(R.string.loading));

        txtStaffName = findViewById(R.id.txt_username);
        txtProductCount = findViewById(R.id.txt_count);
        imgHeaderLogo = findViewById(R.id.img_logo);
        inputBarcode = findViewById(R.id.input_barcode);
        btnPrint = findViewById(R.id.btn_print);
        listView = findViewById(R.id.list_data);

        // set staff title from header
        txtStaffName.setText(GlobalVariable.getUserName());

        // on click back button
        imgHeaderLogo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.danger));
            builder.setMessage(getString(R.string.question_pallet_delete));

            builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel());

            builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> finish());

            builder.show();
        });

        // listen barcode input
        inputBarcode.requestFocus();
        inputBarcode.setInputType(InputType.TYPE_NULL);
        inputBarcode.setOnKeyListener((v, keyCode, event) -> {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                String barcode = inputBarcode.getText().toString().toUpperCase();

                Optional<PalletContent> productQuery = products.stream().filter(x -> x.getSerialNo().equalsIgnoreCase(barcode)).findFirst();

                if (productQuery.isPresent()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle(getString(R.string.info));
                    builder.setMessage(getString(R.string.question_pallet_will_be_added));

                    builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
                    builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        products.add(productQuery.get());
                        setListAdapter(products);
                    });

                    builder.show();
                } else {
                    sendBarcodeRequest(barcode);
                }

                inputBarcode.setText("");
            }


            return false;
        });


        btnPrint.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.print_pallet));
            builder.setMessage(getString(R.string.question_pallet_print));

            builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                dialog.dismiss();
            });

            builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                sendCreatePalletRequest();
                dialog.dismiss();
            });

            builder.show();
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.danger));

            builder.setMessage(getString(R.string.question_pallet_detail_delete));

            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i1) -> {
                products.remove(i);

                setListAdapter(products);
            });
            builder.setNegativeButton(getString(R.string.no), (dialogInterface, i1) -> dialogInterface.dismiss());


            builder.show();
        });
    }

    Map<String, String> getYapAndStockCode() {
        Map<String, String> map = new HashMap<>();

        map.put("stockCode", null);
        map.put("yapKod", null);

        if (!products.isEmpty()) {
            final PalletContent content = products.get(products.size() - 1);

            map.put("stockCode", content.getStockCode());
            map.put("yapKod", content.getYapKod());
        }

        return map;
    }

    void sendBarcodeRequest(String barcode) {
        final List<String> series = products.stream().map(PalletContent::getSerialNo).collect(Collectors.toList());
        final Number staffId = GlobalVariable.getUserId();
        final String stockCode = getYapAndStockCode().get("stockCode");
        final String yapKod = getYapAndStockCode().get("yapKod");

        series.add(barcode);

        final PalletContentDto palletContentDto = new PalletContentDto(series, staffId, stockCode, yapKod);

        loader.show();
        apiInterface.PalletDetailList(palletContentDto).enqueue(new Callback<PalletContentResponse>() {
            @Override
            public void onResponse(Call<PalletContentResponse> call, Response<PalletContentResponse> response) {
                loader.hide();

                if (response.body().getSuccessfull()) {

                    Optional<PalletContent> productQuery = response.body().getData().stream().filter(x -> x.getSerialNo().equalsIgnoreCase(barcode)).findFirst();

                    if (productQuery.isPresent()) {
                        products.add(productQuery.get());
                        setListAdapter(products);
                    }
                } else {
                    Alert.getAlert(PaletteAdd.this, getString(R.string.error), response.body().getMessage()).show();
                }
            }

            @Override
            public void onFailure(Call<PalletContentResponse> call, Throwable t) {
                loader.hide();

                Alert.getAlert(PaletteAdd.this, getString(R.string.error), t.getMessage()).show();
            }
        });
    }

    void sendCreatePalletRequest() {
        final List<String> series = products.stream().map(PalletContent::getSerialNo).collect(Collectors.toList());
        final Number staffId = GlobalVariable.getUserId();

        final CreatePalletDto createPalletDto = new CreatePalletDto(series, staffId);

        loader.show();
        apiInterface.PalletCreate(createPalletDto).enqueue(new Callback<PalletLabelResponse>() {
            @Override
            public void onResponse(Call<PalletLabelResponse> call, Response<PalletLabelResponse> response) {
                loader.hide();

                if (response.code() == 200) {
                    Toast.makeText(PaletteAdd.this, response.body().getData().getBarkod(), Toast.LENGTH_LONG).show();

                    products.clear();
                    setListAdapter(products);

                    printLabel(response.body().getData().getBarkod());
                } else {
                    final ErrorResult errorResult = new Gson().fromJson(response.errorBody().charStream(), ErrorResult.class);

                    Alert.getAlert(PaletteAdd.this, getString(R.string.error), errorResult.getMessage()).show();
                }
            }

            @Override
            public void onFailure(Call<PalletLabelResponse> call, Throwable t) {
                loader.hide();

                Alert.getAlert(PaletteAdd.this, getString(R.string.error), t.getMessage()).show();
            }
        });
    }

    void printLabel(String barcode) {
        try {
            PrintBluetooth.printer_id = GlobalVariable.printerName;

            // send command to blueooth for print label
            printBluetooth.findBT();
            printBluetooth.openBT();
            printBluetooth.printPalletLabel(barcode);
            printBluetooth.closeBT();
        } catch (IOException e) {
            Alert.getAlert(this, getString(R.string.error), e.getMessage()).show();
        }
    }

    void setListAdapter(List<PalletContent> list) {
        btnPrint.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);

        txtProductCount.setText(String.valueOf(products.size()));

        ListAdapter_PalletContent adapter = new ListAdapter_PalletContent(this, R.layout.list_adapter_pallet_detail, list);
        listView.setAdapter(adapter);
    }
}
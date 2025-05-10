package com.replik.peksansevkiyat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.replik.peksansevkiyat.Adapter.RawMaterialAdapter;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.LotItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.RawMaterialItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Transfer.TransferRequest;
import com.replik.peksansevkiyat.DataClass.ModelDto.Transfer.LineItem;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RawMaterialsActivity extends AppCompatActivity implements RawMaterialAdapter.OnItemClickListener {
    private ImageButton imgLogo;
    private TextView txtUserName, txtMenuName;
    private MaterialButton btnAddNew;
    private MaterialButton btnTransfer;
    private RecyclerView rvRawMaterials;
    private RawMaterialAdapter adapter;
    private APIInterface apiInterface;
    private ProgressDialog nDialog;
    private AlertDialog alert;

    private final ActivityResultLauncher<Intent> stockListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    ArrayList<String> stockNames = data.getStringArrayListExtra("stockNames");
                    ArrayList<String> stockCodes = data.getStringArrayListExtra("stockCodes");
                    double[] amounts = data.getDoubleArrayExtra("amounts");

                    if (stockNames != null && stockCodes != null && amounts != null) {
                        for (int i = 0; i < stockCodes.size(); i++) {
                            RawMaterialItem item = new RawMaterialItem(
                                    stockCodes.get(i),
                                    stockNames.get(i),
                                    amounts[i]
                            );
                            adapter.addItem(item);
                        }
                        checkTransferButton();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> lotListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String stockCode = result.getData().getStringExtra("stockCode");
                    ArrayList<LotItem> lots = (ArrayList<LotItem>) result.getData().getSerializableExtra("lots");

                    // Stok koduna göre ilgili RawMaterialItem'ı bul
                    RawMaterialItem item = adapter.findItemByStockCode(stockCode);
                    if (item != null && lots != null) {
                        // Önceki lotları temizle ve yeni lotları ekle
                        item.getLots().clear();
                        for (LotItem lot : lots) {
                            item.addLot(lot);
                        }
                        adapter.notifyDataSetChanged();
                        checkTransferButton();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_materials);

        // API interface'ini başlat
        apiInterface = APIClient.getRetrofit().create(APIInterface.class);

        // Progress dialog'u başlat
        nDialog = Dialog.getDialog(this, getString(R.string.loading));

        initializeViews();
        setupViews();

        // Bildirimden gelen verileri kontrol et
        ArrayList<RawMaterialItem> notificationItems = (ArrayList<RawMaterialItem>) getIntent().getSerializableExtra("notificationRawMaterials");
        if (notificationItems != null && !notificationItems.isEmpty()) {
            for (RawMaterialItem item : notificationItems) {
                adapter.addItem(item);
            }
            checkTransferButton();
        }
    }

    private void initializeViews() {
        imgLogo = findViewById(R.id.iw_logo);
        txtUserName = findViewById(R.id.tw_username);
        txtMenuName = findViewById(R.id.tw_menu);
        btnAddNew = findViewById(R.id.btn_add_new);
        btnTransfer = findViewById(R.id.btn_transfer);
        rvRawMaterials = findViewById(R.id.rv_raw_materials);

        rvRawMaterials.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RawMaterialAdapter();
        adapter.setOnItemClickListener(this);
        rvRawMaterials.setAdapter(adapter);

        nDialog = Dialog.getDialog(this, getString(R.string.loading));
    }

    private void setupViews() {
        txtUserName.setText(GlobalVariable.getUserName());
        imgLogo.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("fromNotification", false)) {
                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                onBackPressed();
            }
        });
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, RawMaterialStockListActivity.class);
            intent.putStringArrayListExtra("existingStockCodes", new ArrayList<>(adapter.getStockCodes()));
            stockListLauncher.launch(intent);
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferMaterials();
            }
        });
    }

    private void checkTransferButton() {
        if (adapter.getItemCount() == 0) {
            btnTransfer.setVisibility(View.GONE);
            return;
        }

        boolean allComplete = true;
        for (RawMaterialItem item : adapter.getItems()) {
            double totalLotAmount = 0;
            for (LotItem lot : item.getLots()) {
                totalLotAmount += lot.getAmount();
            }
            if (Math.abs(totalLotAmount - item.getAmount()) >= 0.001) {
                allComplete = false;
                break;
            }
        }

        btnTransfer.setVisibility(allComplete ? View.VISIBLE : View.GONE);
    }

    private void transferMaterials() {
        nDialog.show();

        List<TransferRequest> transferRequests = adapter.getItems().stream()
                .map(element -> new TransferRequest(
                        element.getStockCode(),
                        element.getLots().stream()
                                .map(lot -> new LineItem(lot.getSerialNumber(), lot.getAmount()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        apiInterface.transferMaterial(transferRequests)
                .enqueue(
                        new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                nDialog.dismiss();


                                if (response.body().getSuccess()) {
                                    Toast.makeText(RawMaterialsActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();

                                    adapter.clearItems();
                                    checkTransferButton();
                                } else {
                                    alert = Alert.getAlert(RawMaterialsActivity.this, getString(R.string.error), response.body().getMessage());
                                    alert.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                nDialog.dismiss();

                                alert = Alert.getAlert(RawMaterialsActivity.this,
                                        getString(R.string.error),
                                        "Bağlantı hatası: " + t.getMessage());
                                alert.show();
                            }
                        }
                );
    }

    @Override
    public void onRawMaterialSelected(RawMaterialItem item) {
        Intent intent = new Intent(this, LotListActivity.class);
        intent.putExtra("stockCode", item.getStockCode());
        intent.putExtra("stockAmount", item.getAmount());
        intent.putExtra("existingLots", new ArrayList<>(item.getLots()));
        lotListLauncher.launch(intent);
    }

    @Override
    public void onBackPressed() {
        if (adapter != null && adapter.getItemCount() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning))
                   .setMessage("Transfer edilmemiş hammaddeler var. Çıkmak istediğinizden emin misiniz?")
                   .setPositiveButton("Evet", (dialog, which) -> {
                        if (getIntent().getBooleanExtra("fromNotification", false)) {
                            Intent intent = new Intent(this, MenuActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            super.onBackPressed();
                        }
                   })
                   .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                   .show();
        } else {
            if (getIntent().getBooleanExtra("fromNotification", false)) {
                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }
} 
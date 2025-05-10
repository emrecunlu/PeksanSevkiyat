package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Toast;
import android.widget.Button;
import android.app.ProgressDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.replik.peksansevkiyat.Adapter.ConsumableLotAdapter;
import com.replik.peksansevkiyat.Adapter.SelectionAdapter;
import com.replik.peksansevkiyat.Transection.GlobalVariable;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.CreateRecountRequest;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.BaseResponse;
import com.replik.peksansevkiyat.Interface.CountingAPIInterface;
import com.replik.peksansevkiyat.Interface.CountingAPIClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class ConsumableCountingActivity extends AppCompatActivity implements SelectionAdapter.OnItemSelectedListener {
    private ImageButton imgLogo;
    private TextView txtUserName;
    private TextInputEditText warehouseInput;
    private TextInputEditText stockInput;
    private TextInputEditText quantityInput;
    private MaterialButton btnAdd;
    private MaterialButton btnConfirm;
    private ActivityResultLauncher<Intent> selectionLauncher;
    private RecyclerView rvLots;
    private ConsumableLotAdapter lotAdapter;
    private double totalStockQuantity = 0;
    private ProgressDialog loader;

    private static final String TYPE_WAREHOUSE = "warehouse";
    private static final String TYPE_STOCK = "stock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumable_counting);

        loader = Dialog.getDialog(this, getString(R.string.loading));
        initializeViews();
        setupViews();
        setupActivityResultLauncher();
    }

    private void initializeViews() {
        imgLogo = findViewById(R.id.imgLogo);
        txtUserName = findViewById(R.id.txtUserName);
        warehouseInput = findViewById(R.id.warehouseInput);
        stockInput = findViewById(R.id.stockInput);
        quantityInput = findViewById(R.id.quantityInput);
        btnAdd = findViewById(R.id.btnAdd);
        btnConfirm = findViewById(R.id.btnConfirm);
        rvLots = findViewById(R.id.rvLots);

        txtUserName.setText(GlobalVariable.getUserName());

        // Ellipsize ayarları
        warehouseInput.setEllipsize(TextUtils.TruncateAt.END);
        stockInput.setEllipsize(TextUtils.TruncateAt.END);

        // Otomatik fokus almayı engelle
        quantityInput.clearFocus();

        // Root view'a fokus ver
        View rootView = findViewById(android.R.id.content);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        // RecyclerView setup
        rvLots.setLayoutManager(new LinearLayoutManager(this));
        lotAdapter = new ConsumableLotAdapter();
        rvLots.setAdapter(lotAdapter);

        // Onaylama butonunu başlangıçta gizle
        btnConfirm.setVisibility(View.GONE);
    }

    private void setupViews() {
        lotAdapter.setOnDeleteClickListener(position -> {
            lotAdapter.removeItem(position);
            updateConfirmButtonVisibility();
            updateInputStates();
        });

        imgLogo.setOnClickListener(v -> finish());

        warehouseInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, WarehouseListActivity.class);
            intent.putExtra("selectedCode", warehouseInput.getText().toString().isEmpty() ? "" : warehouseInput.getText().toString().split(" - ")[0]);
            intent.putExtra("type", "warehouse");
            selectionLauncher.launch(intent);
        });

        stockInput.setOnClickListener(v -> {
            if (warehouseInput.getText().toString().isEmpty()) {
                Toast.makeText(this, "Lütfen önce depo seçiniz", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, StockListActivity.class);
            intent.putExtra("selectedCode", stockInput.getText().toString().isEmpty() ? "" : stockInput.getText().toString().split(" - ")[0]);
            intent.putExtra("warehouseCode", warehouseInput.getText().toString().split(" - ")[0]);
            intent.putExtra("type", "stock");
            selectionLauncher.launch(intent);
        });

        quantityInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                return true;
            }
            return false;
        });

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAddButtonState();
                updateTotalStockQuantity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAdd.setOnClickListener(v -> showLotEntryDialog());

        btnConfirm.setOnClickListener(v -> {
            // Onaylama işlemi için dialog göster
            new AlertDialog.Builder(this)
                .setTitle("Onaylama")
                .setMessage("Sayımı onaylamak istediğinize emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    // CreateRecountRequest nesnesini oluştur
                    CreateRecountRequest request = new CreateRecountRequest();
                    
                    // Depo kodunu ayarla
                    String warehouseCode = warehouseInput.getText().toString().split(" - ")[0];
                    request.setDepoKodu(Integer.parseInt(warehouseCode));
                    
                    // Stok kodunu ayarla
                    String stockCode = stockInput.getText().toString().split(" - ")[0];
                    request.setStokKodu(stockCode);
                    
                    // Toplam miktarı ayarla
                    request.setMiktar(totalStockQuantity);
                    
                    // Line items'ları ayarla
                    List<CreateRecountRequest.RecountLineItem> lineItems = new ArrayList<>();
                    for (ConsumableLotAdapter.LotItem item : lotAdapter.getItems()) {
                        lineItems.add(new CreateRecountRequest.RecountLineItem(item.getQuantity(), item.getLotNumber()));
                    }
                    request.setLineItems(lineItems);
                    
                    // Loading dialog göster
                    loader.show();
                    
                    // API çağrısını yap
                    CountingAPIInterface apiInterface = CountingAPIClient.getRetrofit().create(CountingAPIInterface.class);
                    apiInterface.createRecount(request).enqueue(new Callback<BaseResponse<Void>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                            loader.hide();
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(ConsumableCountingActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                                clearLotList(); // Adapter'ı resetle
                                warehouseInput.setText(""); // Depo seçimini temizle
                                stockInput.setText(""); // Stok seçimini temizle
                                quantityInput.setText(""); // Miktar girişini temizle
                                updateInputStates(); // Input alanlarını aktif et
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        BaseResponse<?> errorResponse = new Gson().fromJson(
                                            response.errorBody().string(),
                                            BaseResponse.class
                                        );
                                        String errorMessage = errorResponse != null && errorResponse.getMessage() != null 
                                            ? errorResponse.getMessage() 
                                            : getString(R.string.error_consumable_counting);
                                        
                                        new AlertDialog.Builder(ConsumableCountingActivity.this)
                                            .setTitle(getString(R.string.error))
                                            .setMessage(errorMessage)
                                            .setPositiveButton(getString(R.string.close), null)
                                            .show();
                                    } else {
                                        new AlertDialog.Builder(ConsumableCountingActivity.this)
                                            .setTitle(getString(R.string.error))
                                            .setMessage(getString(R.string.error_consumable_counting))
                                            .setPositiveButton(getString(R.string.close), null)
                                            .show();
                                    }
                                } catch (Exception e) {
                                    new AlertDialog.Builder(ConsumableCountingActivity.this)
                                        .setTitle(getString(R.string.error))
                                        .setMessage(getString(R.string.error_consumable_counting))
                                        .setPositiveButton(getString(R.string.close), null)
                                        .show();
                                }
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                            loader.hide();
                            Toast.makeText(ConsumableCountingActivity.this, 
                                "Bağlantı hatası: " + t.getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Hayır", null)
                .show();
        });
    }

    private void setupActivityResultLauncher() {
        selectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String code = result.getData().getStringExtra("code");
                        String name = result.getData().getStringExtra("name");
                        String type = result.getData().getStringExtra("type");

                        if (code != null && name != null) {
                            String displayText = code + " - " + name;

                            if (type != null && type.equals("warehouse")) {
                                if (!displayText.equals(warehouseInput.getText().toString())) {
                                    warehouseInput.setText(displayText);
                                    stockInput.setText(""); // Depo değiştiğinde stok seçimini temizle
                                    stockInput.setEnabled(true); // Depo seçildiğinde stok seçimini aktif et
                                    clearLotList(); // Depo değiştiğinde listeyi temizle
                                    totalStockQuantity = 0; // Depo değiştiğinde stok miktarını sıfırla
                                }
                            } else {
                                if (!displayText.equals(stockInput.getText().toString())) {
                                    stockInput.setText(displayText);
                                    clearLotList(); // Stok değiştiğinde listeyi temizle
                                    
                                    // Miktar inputundan değeri al
                                    try {
                                        String quantityStr = quantityInput.getText().toString().trim();
                                        if (!quantityStr.isEmpty()) {
                                            totalStockQuantity = Double.parseDouble(quantityStr);
                                            Toast.makeText(this, String.format("Stok Miktarı: %.2f", totalStockQuantity), Toast.LENGTH_SHORT).show();
                                        } else {
                                            totalStockQuantity = 0;
                                            Toast.makeText(this, "Lütfen miktar giriniz", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (NumberFormatException e) {
                                        totalStockQuantity = 0;
                                        Toast.makeText(this, "Geçerli bir miktar giriniz", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    private void clearLotList() {
        lotAdapter.clear();
        updateConfirmButtonVisibility();
        updateInputStates();
    }

    private void updateInputStates() {
        boolean hasItems = lotAdapter.getItemCount() > 0;
        
        // Input alanlarını devre dışı bırak veya etkinleştir
        warehouseInput.setEnabled(!hasItems);
        stockInput.setEnabled(!hasItems);
        quantityInput.setEnabled(!hasItems);
        
        // Input alanlarının alpha değerlerini ayarla
        float alpha = hasItems ? 0.5f : 1f;
        warehouseInput.setAlpha(alpha);
        stockInput.setAlpha(alpha);
        quantityInput.setAlpha(alpha);
    }

    private void showLotEntryDialog() {
        // Stok miktarı kontrolü
        if (totalStockQuantity <= 0) {
            Toast.makeText(this, "Lütfen önce stok miktarı giriniz", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_lot_entry, null);
        TextInputEditText lotNumberInput = dialogView.findViewById(R.id.lotNumberInput);
        TextInputEditText quantityDialogInput = dialogView.findViewById(R.id.quantityDialogInput);
        TextView tvRemainingQuantity = dialogView.findViewById(R.id.tvRemainingQuantity);
        TextInputLayout lotNumberLayout = (TextInputLayout) lotNumberInput.getParent().getParent();
        TextInputLayout quantityLayout = (TextInputLayout) quantityDialogInput.getParent().getParent();

        // Kalan miktarı hesapla ve göster
        double currentTotal = getCurrentTotalQuantity();
        double remainingQuantity = totalStockQuantity - currentTotal;
        updateRemainingQuantityText(tvRemainingQuantity, remainingQuantity);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Lot Bilgileri")
                .setPositiveButton("Ekle", null)
                .setNegativeButton("İptal", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                boolean isValid = true;
                String lotNumber = lotNumberInput.getText().toString().trim();
                String quantityStr = quantityDialogInput.getText().toString().trim();

                // Lot numarası validasyonu
                if (lotNumber.isEmpty()) {
                    lotNumberLayout.setError("Lot numarası boş olamaz");
                    isValid = false;
                } else {
                    lotNumberLayout.setError(null);
                }

                // Miktar validasyonu
                if (quantityStr.isEmpty()) {
                    quantityLayout.setError("Miktar boş olamaz");
                    isValid = false;
                } else {
                    try {
                        double quantity = Double.parseDouble(quantityStr);
                        
                        if (quantity <= 0) {
                            quantityLayout.setError("Miktar 0'dan büyük olmalıdır");
                            isValid = false;
                        } else if ((currentTotal + quantity) > totalStockQuantity) {
                            quantityLayout.setError("Toplam miktar stok miktarını aşamaz");
                            isValid = false;
                        } else {
                            quantityLayout.setError(null);
                        }
                    } catch (NumberFormatException e) {
                        quantityLayout.setError("Geçerli bir miktar giriniz");
                        isValid = false;
                    }
                }

                if (isValid) {
                    double quantity = Double.parseDouble(quantityStr);
                    lotAdapter.addItem(lotNumber, quantity);
                    updateConfirmButtonVisibility();
                    updateInputStates();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void updateRemainingQuantityText(TextView textView, double remainingQuantity) {
        String text = String.format("Kalan Miktar: %.2f Kg", remainingQuantity);
        textView.setText(text);
        // Kalan miktar negatifse kırmızı, değilse normal renkte göster
        textView.setTextColor(remainingQuantity < 0 ? 
            getResources().getColor(android.R.color.holo_red_dark) : 
            getResources().getColor(android.R.color.primary_text_light));
    }

    private double getCurrentTotalQuantity() {
        double total = 0;
        for (ConsumableLotAdapter.LotItem item : lotAdapter.getItems()) {
            total += item.getQuantity();
        }
        return total;
    }

    private void updateConfirmButtonVisibility() {
        double currentTotal = getCurrentTotalQuantity();
        boolean hasItems = lotAdapter.getItemCount() > 0;
        boolean isComplete = hasItems && Math.abs(currentTotal - totalStockQuantity) < 0.01;
        
        // Onayla butonunun görünürlüğünü ayarla
        btnConfirm.setVisibility(isComplete ? View.VISIBLE : View.GONE);
        
        // Satır Ekle butonunun görünürlüğünü ayarla
        btnAdd.setVisibility(isComplete ? View.GONE : View.VISIBLE);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && imm != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        quantityInput.clearFocus();
    }

    private void checkAddButtonState() {
        boolean isWarehouseSelected = !warehouseInput.getText().toString().isEmpty();
        boolean isStockSelected = !stockInput.getText().toString().isEmpty();
        boolean isQuantityValid = false;

        try {
            double quantity = Double.parseDouble(quantityInput.getText().toString().isEmpty() ? "0" : quantityInput.getText().toString());
            isQuantityValid = quantity > 0;
        } catch (NumberFormatException e) {
            isQuantityValid = false;
        }

        // Eğer onayla butonu görünürse, satır ekle butonu zaten gizlenmiş olacak
        if (btnConfirm.getVisibility() == View.VISIBLE) {
            btnAdd.setEnabled(false);
        } else {
            btnAdd.setEnabled(isWarehouseSelected && isStockSelected && isQuantityValid);
        }
    }

    private void updateTotalStockQuantity() {
        if (!stockInput.getText().toString().isEmpty()) {
            try {
                String quantityStr = quantityInput.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    totalStockQuantity = Double.parseDouble(quantityStr);
                } else {
                    totalStockQuantity = 0;
                }
            } catch (NumberFormatException e) {
                totalStockQuantity = 0;
            }
        }
    }

    @Override
    public void onItemSelected(SelectionAdapter.Item item) {
        // Seçilen item'ı işle
        // Şimdilik boş bırakıyoruz, daha sonra burada seçilen item ile ilgili işlemleri yapabiliriz
    }
} 
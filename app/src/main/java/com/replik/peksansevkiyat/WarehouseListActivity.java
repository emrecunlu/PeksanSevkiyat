package com.replik.peksansevkiyat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.replik.peksansevkiyat.Adapter.SelectionAdapter;
import com.replik.peksansevkiyat.Interface.CountingAPIClient;
import com.replik.peksansevkiyat.Interface.CountingAPIInterface;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.BaseResponse;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.Warehouse;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WarehouseListActivity extends AppCompatActivity implements SelectionAdapter.OnItemSelectedListener {
    private ImageButton imgLogo;
    private TextView txtUserName;
    private EditText etSearch;
    private ProgressBar progressBar;
    private RecyclerView rvItems;
    private SelectionAdapter adapter;
    private CountingAPIInterface apiInterface;
    private String selectedWarehouseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list);

        selectedWarehouseCode = getIntent().getStringExtra("selectedCode");
        apiInterface = CountingAPIClient.getRetrofit().create(CountingAPIInterface.class);

        initializeViews();
        setupViews();
        loadWarehouseList();

        // Klavyeyi gizle ve fokus kaldır
        getWindow().getDecorView().clearFocus();
        
        // Root view'a fokus ver
        View rootView = findViewById(android.R.id.content);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
    }

    private void initializeViews() {
        imgLogo = findViewById(R.id.imgLogo);
        txtUserName = findViewById(R.id.txtUserName);
        TextView txtMenuName = findViewById(R.id.txtMenuName);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);
        rvItems = findViewById(R.id.rvItems);

        txtUserName.setText(GlobalVariable.getUserName());
        txtMenuName.setText("Depo Seçimi");
        
        // EditText'in otomatik fokus almasını engelle
        etSearch.clearFocus();
    }

    private void setupViews() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectionAdapter(this);
        rvItems.setAdapter(adapter);

        imgLogo.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().getDecorView().clearFocus();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvItems != null) {
            rvItems.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void loadWarehouseList() {
        showLoading(true);
        apiInterface.getWarehouseList().enqueue(new Callback<BaseResponse<List<Warehouse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Warehouse>>> call, Response<BaseResponse<List<Warehouse>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Warehouse> warehouses = response.body().getData();
                    List<SelectionAdapter.Item> items = new ArrayList<>();
                    for (Warehouse warehouse : warehouses) {
                        items.add(new SelectionAdapter.Item(
                            String.valueOf(warehouse.getWarehouseCode()),
                            warehouse.getWarehouseName()
                        ));
                    }
                    adapter.setItems(items);
                    
                    // Seçili kodu ayarla
                    if (selectedWarehouseCode != null) {
                        adapter.setSelectedCode(selectedWarehouseCode);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Warehouse>>> call, Throwable t) {
                showLoading(false);
                // TODO: Hata durumunda kullanıcıya bilgi ver
            }
        });
    }

    @Override
    public void onItemSelected(SelectionAdapter.Item item) {
        // Eğer seçili item'a tıklandıysa bir şey yapma
        if (item.code.equals(selectedWarehouseCode)) {
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("code", item.code);
        resultIntent.putExtra("name", item.name);
        resultIntent.putExtra("type", "warehouse");
        setResult(RESULT_OK, resultIntent);
        finish();
    }
} 
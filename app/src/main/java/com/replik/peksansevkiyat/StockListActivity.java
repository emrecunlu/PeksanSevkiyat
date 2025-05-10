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
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.StockItem;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockListActivity extends AppCompatActivity implements SelectionAdapter.OnItemSelectedListener {
    private ImageButton imgLogo;
    private TextView txtUserName;
    private EditText etSearch;
    private ProgressBar progressBar;
    private RecyclerView rvItems;
    private SelectionAdapter adapter;
    private CountingAPIInterface apiInterface;
    private String selectedStockCode;
    private String warehouseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list);

        selectedStockCode = getIntent().getStringExtra("selectedCode");
        warehouseCode = getIntent().getStringExtra("warehouseCode");

        if (warehouseCode == null) {
            finish();
            return;
        }

        apiInterface = CountingAPIClient.getRetrofit().create(CountingAPIInterface.class);

        initializeViews();
        setupViews();
        loadStockList();

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
        txtMenuName.setText("Stok Seçimi");
        
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

    private void loadStockList() {
        showLoading(true);
        apiInterface.getStockList(Integer.parseInt(warehouseCode)).enqueue(new Callback<BaseResponse<List<StockItem>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<StockItem>>> call, Response<BaseResponse<List<StockItem>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StockItem> stocks = response.body().getData();
                    List<SelectionAdapter.Item> items = new ArrayList<>();
                    for (StockItem stock : stocks) {
                        items.add(new SelectionAdapter.Item(
                            stock.getStockCode(),
                            stock.getStockName()
                        ));
                    }
                    adapter.setItems(items);
                    
                    // Seçili kodu ayarla
                    if (selectedStockCode != null) {
                        adapter.setSelectedCode(selectedStockCode);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<StockItem>>> call, Throwable t) {
                showLoading(false);
                // TODO: Hata durumunda kullanıcıya bilgi ver
            }
        });
    }

    @Override
    public void onItemSelected(SelectionAdapter.Item item) {
        // Eğer seçili item'a tıklandıysa bir şey yapma
        if (item.code.equals(selectedStockCode)) {
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("code", item.code);
        resultIntent.putExtra("name", item.name);
        resultIntent.putExtra("type", "stock");
        setResult(RESULT_OK, resultIntent);
        finish();
    }
} 
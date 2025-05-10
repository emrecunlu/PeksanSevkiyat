package com.replik.peksansevkiyat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.replik.peksansevkiyat.Fragment.AllStocksFragment;
import com.replik.peksansevkiyat.Fragment.RequestedStocksFragment;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialStockListActivity extends AppCompatActivity {
    private ImageButton imgLogo;
    private TextView txtUserName, txtMenuName;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private List<Intent> collectedResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_material_stock_list);

        initializeViews();
        setupViews();
        setupViewPager();
    }

    private void initializeViews() {
        imgLogo = findViewById(R.id.iw_logo);
        txtUserName = findViewById(R.id.tw_username);
        txtMenuName = findViewById(R.id.tw_menu);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupViews() {
        txtUserName.setText(GlobalVariable.getUserName());
        imgLogo.setOnClickListener(v -> onBackPressed());
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Tüm Stok Kodları");
                    break;
                case 1:
                    tab.setText("Talep Edilen Stok Kodları");
                    break;
            }
        }).attach();
    }

    public void addResult(Intent result) {
        collectedResults.add(result);
    }

    @Override
    public void onBackPressed() {
        if (!collectedResults.isEmpty()) {
            // Tüm sonuçları birleştir
            ArrayList<String> allStockNames = new ArrayList<>();
            ArrayList<String> allStockCodes = new ArrayList<>();
            ArrayList<Double> allAmounts = new ArrayList<>();

            for (Intent result : collectedResults) {
                allStockNames.add(result.getStringExtra("stockName"));
                allStockCodes.add(result.getStringExtra("stockCode"));
                allAmounts.add(result.getDoubleExtra("amount", 0.0));
            }

            // Birleştirilmiş sonuçları gönder
            Intent combinedResult = new Intent();
            combinedResult.putStringArrayListExtra("stockNames", allStockNames);
            combinedResult.putStringArrayListExtra("stockCodes", allStockCodes);
            combinedResult.putExtra("amounts", allAmounts.stream().mapToDouble(Double::doubleValue).toArray());
            setResult(RESULT_OK, combinedResult);
        }
        super.onBackPressed();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new AllStocksFragment();
                case 1:
                    return new RequestedStocksFragment();
                default:
                    throw new IllegalStateException("Unexpected position " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
} 
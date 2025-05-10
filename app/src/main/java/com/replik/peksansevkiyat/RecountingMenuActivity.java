package com.replik.peksansevkiyat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.replik.peksansevkiyat.Transection.GlobalVariable;


public class RecountingMenuActivity extends AppCompatActivity {

    ImageButton imgLogoBtn;
    TextView txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recounting_menu);

        imgLogoBtn = (ImageButton) findViewById(R.id.imgLogo);
        txtUserName = (TextView) findViewById(R.id.txtUserName);

        txtUserName.setText(GlobalVariable.getUserName());

        imgLogoBtn.setOnClickListener(v -> {
            finish();
        });

        Button btnConsumableCounting = findViewById(R.id.btnConsumableCounting);
        btnConsumableCounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecountingMenuActivity.this, ConsumableCountingActivity.class);
                startActivity(intent);
            }
        });
    }
}
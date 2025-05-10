package com.replik.peksansevkiyat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.replik.peksansevkiyat.Interface.Interfaces;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements Interfaces.BluetoothDeviceInterface {

    ImageButton imgLogo;
    EditText txtApiUrl, txtPrinterName;
    Button btnSearch, btnSave;
    TextView txtUserName;
    AlertDialog alert;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtApiUrl = findViewById(R.id.txtApiUrl);
        txtApiUrl.setText(GlobalVariable.getApiUrl() == null ? "http://192.168.2.251:3131/" : GlobalVariable.getApiUrl());
        txtPrinterName = findViewById(R.id.txtPrinterName);
        txtPrinterName.setText(GlobalVariable.printerName);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserName.setText(GlobalVariable.getUserName());

        imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSearch = findViewById(R.id.btnSearchPrinter);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothDeviceFragment of = new BluetoothDeviceFragment();
                of.show(getFragmentManager(), "modalY");
            }
        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariable.setApiUrl(txtApiUrl.getText().toString());
                GlobalVariable.setPrinter(txtPrinterName.getText().toString());

                // fnWriteText("URL|" + txtApiUrl.getText().toString(), "PRN|" + txtPrinterName.getText().toString());
                writeDatabase(txtApiUrl.getText().toString(), txtPrinterName.getText().toString());

                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) +
                ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED;
    }

    private void fnWriteText(String url, String printer) {

        if (!checkPermission())
            requestPer();

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);
        //File filepath = storageVolume.getDirectory();
        //File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream fo = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + GlobalVariable.FileName));
            //FileOutputStream fo = openFileOutput(GlobalVariable.FileName, MODE_APPEND);
            fo.write((url + "\n").getBytes());
            fo.write(printer.getBytes());
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeDatabase(String apiUrl, String printerName) {
        database = this.openOrCreateDatabase("PeksanSevkiyat", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM Parameter", null);

        boolean hasValue = false;

        while (cursor.moveToNext()) {
            hasValue = true;
        }

        if (hasValue)
            database.execSQL("UPDATE Parameter SET apiUrl = '" + apiUrl + "', printerName = '" + printerName + "' WHERE id = 1");
        else
            database.execSQL("INSERT INTO Parameter (id, apiUrl, printerName) VALUES(1, '" + apiUrl + "', '" + printerName + "')");
    }

    void requestPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
            }, 200);
/*
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 200);
        }*/
    }

    @Override
    public void selectedDevice(Boolean statu, String value) {
        if (statu)
            txtPrinterName.setText(value.toString());
        else
            txtPrinterName.setText("");
    }
}
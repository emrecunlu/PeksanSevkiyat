package com.replik.peksansevkiyat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dcastalia.localappupdate.DownloadApk;
import com.replik.peksansevkiyat.DataClass.ModelDto.ApkVersion;
import com.replik.peksansevkiyat.DataClass.ModelDto.Personel.Personel;
import com.replik.peksansevkiyat.DataClass.ModelDto.Personel.PersonelList;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;
import com.replik.peksansevkiyat.Transection.Alert;
import com.replik.peksansevkiyat.Transection.Dialog;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    APIInterface apiInterface;
    AlertDialog alert;

    Context context = MainActivity.this;

    TextView lblVersion;
    ImageButton imgSettings, imgRefresh;
    Button btnLogin;
    Spinner ddlUser;
    ArrayList personelList;
    PersonelList personels;
    Integer SelectedPersonelId = -1;
    Integer SelectedPersonCode = -1;

    private SQLiteDatabase database;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager();

        progressBar = findViewById(R.id.progressBar);

        if (!checkPermission()) requestPer();

        Intent intent = getIntent();

        Log.i("From Notification", intent.getBooleanExtra("fromNotification", false) ? "VAR" : "YOK");

        lblVersion = findViewById(R.id.lblVersion);
        lblVersion.setText(GlobalVariable.apiVersion);

        ddlUser = findViewById(R.id.ddlUser);

        imgSettings = findViewById(R.id.imgSettings);
        imgRefresh = findViewById(R.id.imgRefresh);
        imgRefresh.setVisibility(View.GONE);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!GlobalVariable.apiUrl.isEmpty()) {
                    apiInterface = APIClient.getRetrofit().create(APIInterface.class);
                    getPersonelList();
                    imgRefresh.setVisibility(View.GONE);
                } else {
                    alert = Alert.getAlert(context, getString(R.string.error), getString(R.string.url_error));
                    alert.show();
                }
            }
        });

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SettingsActivity.class);
                startActivity(i);

                imgRefresh.setVisibility(View.VISIBLE);
            }
        });

        ///Check Global Variable
        boolean a = checkDatabase();// fileExist();
        if (!a) {
            Intent i = new Intent(context, SettingsActivity.class);
            startActivity(i);

            imgRefresh.setVisibility(View.VISIBLE);
        } else {
            readDatabase();
            //fnReadText();

            apiInterface = APIClient.getRetrofit().create(APIInterface.class);
            //Version Control
            fnVersionControl();

            getPersonelList();
        }

        ddlUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    SelectedPersonelId = personels.getPersonels().get(i - 1).getId();
                    SelectedPersonCode = Integer.valueOf(personels.getPersonels().get(i - 1).getStaffCode());
                } else {
                    SelectedPersonelId = -1;
                    SelectedPersonCode = -1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SelectedPersonelId != -1) {
                    String userName = ddlUser.getSelectedItem().toString();
                    GlobalVariable.setUserName(userName);
                    GlobalVariable.setUserId(SelectedPersonelId);
                    GlobalVariable.setUserCode(SelectedPersonCode);

                    if (getIntent().getBooleanExtra("fromNotification", false)) {
                        // Önce MenuActivity'yi başlat
                        Intent menuIntent = new Intent(context, MenuActivity.class);
                        menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(menuIntent);

                        // Sonra RawMaterialsActivity'yi başlat
                        Intent rawMaterialIntent = new Intent(context, RawMaterialsActivity.class);
                        rawMaterialIntent.putExtra("notificationRawMaterials", getIntent().getSerializableExtra("notificationRawMaterials"));
                        rawMaterialIntent.putExtra("fromNotification", true);
                        startActivity(rawMaterialIntent);

                        // MainActivity'yi kapat
                        finish();
                    } else {
                        Intent i = new Intent(context, MenuActivity.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(context, getString(R.string.please_select_user), Toast.LENGTH_LONG).show();
                }
            }
        });
        //txtUserName.setInputType(InputType.TYPE_NULL); /// HT: keyboard hide
    }

    private void fnVersionControl() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        apiInterface.getApkVersion().enqueue(new Callback<ApkVersion>() {
            @Override
            public void onResponse(Call<ApkVersion> call, Response<ApkVersion> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful()) {
                    if (!response.body().getVersion().equals(GlobalVariable.apiVersion)) {
                        fnNewVersionDownload(response.body().getUrl(), response.body().getDetail());
                    }
                } else {
                    alert = Alert.getAlert(context, getString(R.string.error), response.message());
                    alert.show();
                }
            }
            
            @Override
            public void onFailure(Call<ApkVersion> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    void fnNewVersionDownload(String url, String detail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.question_new_version));//getString(R.string.sure));
        builder.setMessage(detail);//getString(R.string.question_new_version));
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nDialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nDialog.hide();
                DownloadApk downloadApk = new DownloadApk(context);
                downloadApk.startDownloadingApk(url, "update");
            }
        });
        builder.show();
    }

    boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) + ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) + ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) + ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) + ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 200);
/*
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 200);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {

        } else {
            alert = Alert.getAlert(context, getString(R.string.error), "Yetkiler Tanımlı Değil !");
            alert.show();
        }
    }

    public void getPersonelList() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        apiInterface.getUserList().enqueue(new Callback<PersonelList>() {
            @Override
            public void onResponse(Call<PersonelList> call, Response<PersonelList> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response != null) {
                    if (response.body().getSuccess()) {
                        personels = response.body();
                        personelList = new ArrayList<>();
                        personelList.add(getString(R.string.please_select));
                        for (Personel p : response.body().getPersonels()) {
                            personelList.add(p.getFirstName() + " " + p.getLastName());
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, personelList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ddlUser.setAdapter(adapter);
                    } else {
                        alert = Alert.getAlert(context, getString(R.string.error), response.body().getMessage());
                        alert.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PersonelList> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                alert = Alert.getAlert(context, getString(R.string.error), t.getMessage());
                alert.show();
            }
        });
    }

    private void fnReadText() {
        Context context = this;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory().getPath() + GlobalVariable.FileName)));
            while ((line = in.readLine()) != null)
                //stringBuffer.append(lines);
                switch (line.substring(0, 3)) {
                    case "URL":
                        GlobalVariable.setApiUrl(line.replace("URL|", ""));
                        break;
                    case "PRN":
                        GlobalVariable.setPrinter(line.replace("PRN|", ""));
                        break;
                }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        /*
        if(fileExist(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.replik.peksansevkiyat/files/Replik.txt"))
            try {
                FileInputStream fileInputStream = openFileInput("Replik.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                String lines;
                while ((lines = bufferedReader.readLine()) != null) {
                    //stringBuffer.append(lines);
                    switch (lines.substring(0, 3)) {
                        case "URL":
                            GlobalVariable.setApiUrl(lines.replace("URL|",""));
                            break;
                        case "PRN":
                            GlobalVariable.setPrinter(lines.replace("PRN|",""));
                            break;
                    }
                    //stringBuffer = null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        */
    }

    public boolean fileExist() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + GlobalVariable.FileName.replace("/Replik.txt", ""));
        if (!dir.exists()) {
        }
        File filepath = Environment.getExternalStorageDirectory();
        //File path = new File(dir, GlobalVariable.FileName);
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // internal Storage
        //File fileInputImage = ;
        File path = new File(Environment.getExternalStorageDirectory().getPath() + GlobalVariable.FileName);
        boolean a = path.exists();
        return a;
    }

    boolean checkDatabase() {
        database = this.openOrCreateDatabase("PeksanSevkiyat", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Parameter (id INT, apiUrl VARCHAR, printerName VARCHAR)");
        Cursor cursor = database.rawQuery("SELECT * FROM Parameter", null);

        boolean hasValue = false;

        hasValue = cursor.getCount() > 0 ? true : false;

        if (hasValue) {
            int apiUrlIndex = cursor.getColumnIndex("apiUrl");
            int printerNameNoIndex = cursor.getColumnIndex("printerName");

            while (cursor.moveToNext())
                if (cursor.getString(apiUrlIndex).toString().equals("")) return false;
            return true;
        } else {
            database.execSQL("INSERT INTO Parameter (id, apiUrl, printerName) VALUES(1, '', '')");
            return false;
        }
    }

    void readDatabase() {
        database = this.openOrCreateDatabase("PeksanSevkiyat", MODE_PRIVATE, null);

        Cursor cursor = database.rawQuery("SELECT * FROM Parameter", null);

        int apiUrlIndex = cursor.getColumnIndex("apiUrl");
        int printerNameNoIndex = cursor.getColumnIndex("printerName");

        while (cursor.moveToNext()) {
            GlobalVariable.setApiUrl(cursor.getString(apiUrlIndex));
            GlobalVariable.setPrinter(cursor.getString(printerNameNoIndex));
        }
    }
}
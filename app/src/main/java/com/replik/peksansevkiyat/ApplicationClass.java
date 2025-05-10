package com.replik.peksansevkiyat;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.notifications.INotificationClickListener;
import com.onesignal.notifications.INotificationClickEvent;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.RawMaterialItem;
import com.replik.peksansevkiyat.Transection.GlobalVariable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApplicationClass extends Application {
    // NOTE: Replace the below with your own ONESIGNAL_APP_ID
    private static final String ONESIGNAL_APP_ID = "bb81bc47-839a-49ef-91a8-162dd9cc69f8";
    private static final String TAG = "ApplicationClass";

    @Override
    public void onCreate() {
        super.onCreate();

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // Debug loglarını etkinleştir
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // Bildirim izinlerini iste
        OneSignal.getNotifications().requestPermission(false, Continue.none());

        // Bildirim tıklama olayını dinle
        OneSignal.getNotifications().addClickListener(event -> {
            try {
                JSONObject data = event.getNotification().getAdditionalData();
                Log.d(TAG, "Bildirim tıklandı: " + (data != null ? data.toString() : "data null"));

                if (data != null && data.has("raw_materials")) {
                    ArrayList<RawMaterialItem> rawMaterials = new ArrayList<>();
                    JSONArray materials = data.getJSONArray("raw_materials");

                    for (int i = 0; i < materials.length(); i++) {
                        JSONObject material = materials.getJSONObject(i);
                        RawMaterialItem item = new RawMaterialItem(
                                material.getString("stockCode"),
                                material.getString("stockName"),
                                material.getDouble("amount")
                        );
                        rawMaterials.add(item);
                    }

                    Intent intent;
                    if (GlobalVariable.getUserId() == null) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("fromNotification", true);
                    } else {
                        intent = new Intent(getApplicationContext(), RawMaterialsActivity.class);
                    }

                    intent.putExtra("notificationRawMaterials", rawMaterials);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Bildirim işleme hatası: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

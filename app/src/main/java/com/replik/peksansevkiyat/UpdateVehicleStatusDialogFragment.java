package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.replik.peksansevkiyat.DataClass.ListAdapter.ListenerInterface;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class UpdateVehicleStatusDialogFragment extends DialogFragment {

    CheckBox hygenie, insect, smell, leakage;
    ListenerInterface.UpdateVehicleStatusDialogListener listener;
    Map<String, Boolean> status;

    public UpdateVehicleStatusDialogFragment(Map<String, Boolean> vehicleStatus, ListenerInterface.UpdateVehicleStatusDialogListener listener) {
        this.status = vehicleStatus;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.update_vehicle_status_dialog, null);

        hygenie = (CheckBox) view.findViewById(R.id.radio_hygenie);
        insect = (CheckBox) view.findViewById(R.id.radio_insect);
        smell = (CheckBox) view.findViewById(R.id.radio_smell);
        leakage = (CheckBox) view.findViewById(R.id.radio_leakage);

        hygenie.setChecked(Boolean.TRUE.equals(this.status.get("hygenie")));
        insect.setChecked(Boolean.TRUE.equals(this.status.get("insect")));
        smell.setChecked(Boolean.TRUE.equals(this.status.get("smell")));
        leakage.setChecked(Boolean.TRUE.equals(this.status.get("leakage")));

        return builder
                .setCancelable(false)
                .setView(view)
                .setTitle("Araç Kontrol")
                .setNegativeButton(getString(R.string.close), (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    Map<String, Boolean> map = new HashMap<>();

                    map.put("hygenie", hygenie.isChecked());
                    map.put("insect", insect.isChecked());
                    map.put("smell", smell.isChecked());
                    map.put("leakage", leakage.isChecked());

                    listener.onSubmit(map);
                    dialog.dismiss();
                })
                .create();
    }
}

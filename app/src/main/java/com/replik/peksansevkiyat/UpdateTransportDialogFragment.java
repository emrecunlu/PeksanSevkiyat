package com.replik.peksansevkiyat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.replik.peksansevkiyat.DataClass.ListAdapter.ListenerInterface;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingTransport;
import com.replik.peksansevkiyat.Interface.APIClient;
import com.replik.peksansevkiyat.Interface.APIInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTransportDialogFragment extends DialogFragment {
    MaterialAutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    List<OrderShippingTransport> transportList = new ArrayList<>();
    APIInterface apiInterface;
    OrderShippingTransport selectedTransport;
    AlertDialog loader;
    ListenerInterface.UpdateTransportDialogListener listener;
    int transportId;

    UpdateTransportDialogFragment(int transportId, ListenerInterface.UpdateTransportDialogListener listener) {
        this.listener = listener;
        this.transportId = transportId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.update_transport_dialog, null);

        apiInterface = APIClient.getRetrofit().create(APIInterface.class);

        autoCompleteTextView = (MaterialAutoCompleteTextView) view.findViewById(R.id.autoComplete_transport);

        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedTransport = transportList.get(position);
        });

        builder.
                setView(view)
                .setTitle("Nakliye Tipi")
                .setNeutralButton(getString(R.string.vehicle_control), (dialog, which) -> {
                    listener.onVehicleStatusOpen();
                })
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    listener.onTransportSelected(selectedTransport);
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                });

        fetchTransportList();

        return builder.show();
    }

    void fetchTransportList() {
        apiInterface.getTransportList().enqueue(
                new Callback<List<OrderShippingTransport>>() {
                    @Override
                    public void onResponse(Call<List<OrderShippingTransport>> call, Response<List<OrderShippingTransport>> response) {
                        if (response.body() != null) {
                            transportList = response.body();

                            for (int i = 0; i < transportList.size(); i++) {
                                if (transportList.get(i).getId() == transportId) {
                                    selectedTransport = transportList.get(i);
                                    autoCompleteTextView.setText(transportList.get(i).getDesc());
                                    break;
                                }
                            }

                            arrayAdapter = new ArrayAdapter<>(requireActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, response.body().stream().map(OrderShippingTransport::getDesc).collect(Collectors.toList()));
                            autoCompleteTextView.setAdapter(arrayAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderShippingTransport>> call, Throwable t) {
                    }
                }
        );
    }
}

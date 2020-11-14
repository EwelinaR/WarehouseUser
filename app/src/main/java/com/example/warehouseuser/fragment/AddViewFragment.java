package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.InternalStorage;
import com.example.warehouseuser.R;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.fragment.update.FragmentUpdate;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class AddViewFragment extends DetailedFragment implements FragmentUpdate, OnAuthenticationUpdate {

    private RestApi api;
    private Instrument newInstrument;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initEditTexts();
        initPriceField();
        initQuantityFields();
        initButtons();
        api = new RestApi(this.getContext());
    }

    private void initButtons() {
        Button cancel = getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            FragmentManager fm = getFragmentManager();
            Log.i("Screen", "Back to list view from add view");
            fm.popBackStack();
        });

        Button save = getActivity().findViewById(R.id.save);
        save.setOnClickListener(this::save);
        save.setText("Dodaj");

        Button delete = getActivity().findViewById(R.id.delete);
        delete.setVisibility(View.INVISIBLE);
    }

    private void initQuantityFields() {
        Button increase = getActivity().findViewById(R.id.increase);
        Button decrease = getActivity().findViewById(R.id.decrease);

        increase.setVisibility(View.INVISIBLE);
        decrease.setVisibility(View.INVISIBLE);
        quantityDifference.setVisibility(View.INVISIBLE);
    }

    private void save(View view) {
        if (!isValidData()) {
            Snackbar.make(view, getString(R.string.wrong_login_or_pass), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Log.i("Screen", "Go to list view from add view");
        newInstrument = new Instrument(
                0,
                manufacturer.getText().toString(),
                model.getText().toString(),
                Float.parseFloat(price.getText().toString()),
                0);

        InternalStorage storage = new InternalStorage(requireContext());
        try {
            storage.addInstrument(newInstrument);
        } catch (IOException e) {
            e.printStackTrace();
        }

        api.addInstrument(newInstrument, this);
    }

    @Override
    public void updateView(RequestResponseStatus status) {
        if (status == RequestResponseStatus.TIMEOUT) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.manufacturer_edit),
                    getString(R.string.connection_timeout), Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction(getString(R.string.retry_connection), view12 -> api.addInstrument(newInstrument, this));
            mySnackbar.show();
            return;
        } else if (status == RequestResponseStatus.UNAUTHORIZED) {
            api.refreshToken(this);
            return;
        }
        Log.i("Screen", "Go to list view from add view");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, new ListFragment(false));
        ft.commit();
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        api.addInstrument(newInstrument, this);
    }
}

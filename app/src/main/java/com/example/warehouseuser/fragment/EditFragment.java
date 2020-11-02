package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.R;
import com.google.android.material.snackbar.Snackbar;

public class EditFragment extends DetailedFragment implements FragmentUpdate, OnAuthenticationUpdate {

    private RestApi api;
    private Instrument instrument;
    private int numberOfRequests = 0;
    private boolean isDataChanged;
    private boolean isDeleteAction;

    public EditFragment(Instrument instrument) {
        this.instrument = instrument;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initEditTexts();
        initPriceField();
        initQuantityFields();
        initButtons();
        setInstrument();
        api = new RestApi(this.getContext());
    }

    private void initButtons() {
        Button cancel = getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            FragmentManager fm = getFragmentManager();
            Log.i("Screen", "Back to list view from edit view");
            fm.popBackStack();
        });

        Button save = getActivity().findViewById(R.id.save);
        save.setOnClickListener(this::save);
        save.setText("Zapisz");

        Button delete = getActivity().findViewById(R.id.delete);
        delete.setOnClickListener(view -> delete());
    }

    private void initQuantityFields() {
        Button increase = getActivity().findViewById(R.id.increase);
        Button decrease = getActivity().findViewById(R.id.decrease);

        increase.setOnClickListener(view -> setNewQuantity(1));
        decrease.setOnClickListener(view -> setNewQuantity(-1));
    }

    private void setNewQuantity(int quantityChange) {
        final int MAX_QUANTITY = 100;
        int currentQuantity = instrument.getQuantity();
        int currentDifference = Integer.parseInt(quantityDifference.getText().toString());
        int newQuantity = currentQuantity + currentDifference + quantityChange;
        if (newQuantity >= 0 && newQuantity <= MAX_QUANTITY) {
            quantityDifference.setText(String.valueOf(currentDifference + quantityChange));
        }
    }

    private void setInstrument() {
        manufacturer.setText(instrument.getManufacturer());
        manufacturer.setSelection(manufacturer.getText().length());
        model.setText(instrument.getModel());
        model.setSelection(model.getText().length());
        price.setText(String.format("%.2f", instrument.getPrice()));
        price.setSelection(price.getText().length());
        quantity.setText(String.valueOf(instrument.getQuantity()));
    }

    private void save(View view) {
        if (!isValidData()) {
            Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        isDeleteAction = false;
        checkDataChanges();
        sendRequests();
    }

    private void sendRequests() {
        int amount = Integer.parseInt(quantityDifference.getText().toString());
        if (amount > 0) {
            api.increaseQuantity(instrument.getId(), amount, this);
            numberOfRequests++;
        }
        else if (amount < 0) {
            api.decreaseQuantity(instrument.getId(), -amount, this);
            numberOfRequests++;
        }

        if (isDataChanged) {
            api.editInstrument(instrument, this);
            numberOfRequests++;
            System.out.println("DATA CHANGED");
        }

        if (numberOfRequests == 0) {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        }
    }

    private void checkDataChanges() {
        isDataChanged = false;
        if (!instrument.getManufacturer().equals(manufacturer.getText().toString())) {
            instrument.setManufacturer(manufacturer.getText().toString());
            isDataChanged = true;
        }
        if (!instrument.getModel().equals(model.getText().toString())) {
            instrument.setModel(model.getText().toString());
            isDataChanged = true;
        }
        if (instrument.getPrice() != Float.parseFloat(price.getText().toString())) {
            if (price.getText().toString().endsWith(".")) {
                String correctedPrice = price.getText().subSequence(0, price.getText().length()-1).toString();
                instrument.setPrice(Float.parseFloat(correctedPrice));
            } else {
                instrument.setPrice(Float.parseFloat(price.getText().toString()));
            }
            isDataChanged = true;
        }
    }

    private void delete() {
        isDeleteAction = true;
        numberOfRequests++;
        api.deleteInstrument(instrument.getId(), this);
    }

    @Override
    public void updateView(RequestResponseStatus status) {
        numberOfRequests--;
        if (numberOfRequests > 0) {
            return;
        }
        if (status == RequestResponseStatus.TIMEOUT) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.manufacturer_edit),
                    getString(R.string.connection_timeout), Snackbar.LENGTH_INDEFINITE);
            if (isDeleteAction) {
                mySnackbar.setAction(getString(R.string.retry_connection), view12 -> delete());
            }
            else
                mySnackbar.setAction(getString(R.string.retry_connection), view12 -> sendRequests());
            mySnackbar.show();
            return;
        } else if (status == RequestResponseStatus.FORBIDDEN) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.manufacturer_edit),
                    getString(R.string.connection_forbidden), Snackbar.LENGTH_LONG);
            mySnackbar.show();
            return;
        }else if (status == RequestResponseStatus.UNAUTHORIZED) {
            api.refreshToken(this);
            return;
        }

        Log.i("Screen", "Go to list view from edit view");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        if (isDeleteAction)
            delete();
        else
            sendRequests();
    }
}

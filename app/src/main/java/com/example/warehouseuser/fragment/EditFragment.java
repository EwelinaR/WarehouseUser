package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.R;

public class EditFragment extends DetailedFragment implements FragmentUpdate {

    private Instrument instrument;
    private int numberOfRequests = 0;

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
    }

    private void initButtons() {
        Button cancel = (Button) getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            FragmentManager fm = getFragmentManager();
            Log.i("Screen", "Back to list view from edit view");
            fm.popBackStack();
        });

        Button save = (Button) getActivity().findViewById(R.id.save);
        save.setOnClickListener(view -> save());
        save.setText("Zapisz");

        Button delete = (Button) getActivity().findViewById(R.id.delete);
        delete.setOnClickListener(view -> delete());
    }

    private void initQuantityFields() {
        Button increase = (Button) getActivity().findViewById(R.id.increase);
        Button decrease = (Button) getActivity().findViewById(R.id.decrease);

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

    private void save() {
        Log.i("Screen", "Go to list view from edit view");
        RestApi c = new RestApi();

        int amount = Integer.parseInt(quantityDifference.getText().toString());
        if (amount > 0) {
            c.increaseQuantity(instrument.getId(), amount, this);
            numberOfRequests++;
        }
        else if (amount < 0) {
            c.decreaseQuantity(instrument.getId(), -amount, this);
            numberOfRequests++;
        }

        if (isDataChanged()) {
            c.editInstrument(instrument, this);
            numberOfRequests++;
        }

        if (numberOfRequests == 0) {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        }
    }

    private boolean isDataChanged() {
        boolean isChanged = false;
        if (!instrument.getManufacturer().equals(manufacturer.getText().toString())) {
            instrument.setManufacturer(manufacturer.getText().toString());
            isChanged = true;
        }
        if (!instrument.getModel().equals(model.getText().toString())) {
            instrument.setModel(model.getText().toString());
            isChanged = true;
        }
        if (instrument.getPrice() != Float.parseFloat(price.getText().toString())) {
            if (price.getText().toString().endsWith(".")) {
                String correctedPrice = price.getText().subSequence(0, price.getText().length()-1).toString();
                instrument.setPrice(Float.parseFloat(correctedPrice));
            } else {
                instrument.setPrice(Float.parseFloat(price.getText().toString()));
            }
            isChanged = true;
        }
        return isChanged;
    }

    private void delete() {
        RestApi c = new RestApi();
        c.deleteInstrument(instrument.getId(), this);
    }

    @Override
    public void updateView(Instrument instruments) {
        if (numberOfRequests > 1) {
            numberOfRequests--;
            return;
        }
        Log.i("Screen", "Go to list view from edit view");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }
}

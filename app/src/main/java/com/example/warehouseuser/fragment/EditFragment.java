package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.example.warehouseuser.InternalStorage;
import com.example.warehouseuser.MainActivity;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.R;
import com.example.warehouseuser.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class EditFragment extends DetailedFragment {

    private final Instrument instrument;
    private boolean isDataChanged;

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
        Button cancel = getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            FragmentManager fm = getFragmentManager();
            Log.i("Screen", "Back to list view from edit view");
            fm.popBackStack();
        });

        Button save = getActivity().findViewById(R.id.save);
        save.setOnClickListener(this::save);
        save.setText("Zapisz");

        SessionManager manager = new SessionManager(requireContext());
        Button delete = getActivity().findViewById(R.id.delete);

        if (manager.isManager()) {
            delete.setOnClickListener(view -> delete());
        } else {
            delete.setVisibility(View.INVISIBLE);
        }
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
        if (isValidData()) {
            saveUpdateToInternalStorage();
            goToListView();
        } else {
            Snackbar.make(view, getString(R.string.wrong_login_or_pass), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void saveUpdateToInternalStorage() {
        Instrument instrument = getDataChanges();
        InternalStorage storage = new InternalStorage((MainActivity)getContext());
        if (isDataChanged) {
            try {
                storage.updateInstrument(instrument);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int amount = Integer.parseInt(quantityDifference.getText().toString());
        if (amount != 0) {
            try {
                storage.changeAmountOfInstrument(instrument, amount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Instrument getDataChanges() {
        String changedManufacturer = null;
        String changedModel = null;
        float changedPrice = -1;
        isDataChanged = false;
        if (!instrument.getManufacturer().equals(manufacturer.getText().toString())) {
            changedManufacturer = manufacturer.getText().toString();
            instrument.setManufacturer(changedManufacturer);
            isDataChanged = true;
        }
        if (!instrument.getModel().equals(model.getText().toString())) {
            changedModel = model.getText().toString();
            instrument.setModel(changedModel);
            isDataChanged = true;
        }
        if (instrument.getPrice() != Float.parseFloat(price.getText().toString())) {
            if (price.getText().toString().endsWith(".")) {
                changedPrice = Float.parseFloat(price.getText().subSequence(0, price.getText().length()-1).toString());
            } else {
                changedPrice = Float.parseFloat(price.getText().toString());
            }
            instrument.setPrice(changedPrice);
            isDataChanged = true;
        }
        return new Instrument(instrument.getId(), changedManufacturer, changedModel, changedPrice, -1);
    }

    private void delete() {
        InternalStorage storage = new InternalStorage((MainActivity)getContext());
        try {
            storage.deleteInstrument(instrument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        goToListView();
    }

    private void goToListView() {
        Log.i("Screen", "Go to list view from edit view");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }
}

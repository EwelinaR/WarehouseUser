package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.R;

public class EditFragment extends Fragment {

    private Instrument instrument;
    private EditText manufacturer;
    private EditText model;
    private EditText price;
    private TextView quantity;
    private TextView quantityDifference;

    public EditFragment() { }

    public EditFragment(Instrument instrument) {
        this.instrument = instrument;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        boolean isEditView = instrument != null;
        initEditTexts();
        initPriceField();
        initQuantityFields(isEditView);
        initButtons(isEditView);
        if(isEditView)
            setInstrument();
    }

    private void initEditTexts() {
        manufacturer = (EditText) getActivity().findViewById(R.id.manufacturer_edit);
        model = (EditText) getActivity().findViewById(R.id.model_edit);
        price = (EditText) getActivity().findViewById(R.id.price_edit);
        quantity = (TextView) getActivity().findViewById(R.id.quantity_amount);
        quantityDifference = (TextView) getActivity().findViewById(R.id.quantity_edit);
    }

    private void initPriceField() {
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence ch, int start, int before, int count) {
                String s = ch.toString();
                if(s.length() == 0) {
                    price.setText("0");
                    price.setSelection(price.getText().length());
                }
                if(!s.matches("^(\\d{1,6})((\\.)|(\\.\\d{0,2}))?$")) {
                    price.setText(s.substring(0, s.length()-1));
                    price.setSelection(price.getText().length());
                }
            }
        });
    }

    private void initButtons(boolean isEditView) {
        Button cancel = (Button) getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Log.i("Screen", "Back to list view from edit view");
                fm.popBackStack();
            }});

        Button save = (Button) getActivity().findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                save();
            }});

        Button delete = (Button) getActivity().findViewById(R.id.delete);

        if(isEditView) {
            save.setText("Dodaj");
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    delete();
                }});
        } else {
            save.setText("Zapisz");
            delete.setVisibility(View.INVISIBLE);
        }
    }

    private void initQuantityFields(boolean isEditView) {
        Button increase = (Button) getActivity().findViewById(R.id.increase);
        Button decrease = (Button) getActivity().findViewById(R.id.decrease);

        if(isEditView) {
            increase.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setNewQuantity(1);
                }});
            decrease.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setNewQuantity(-1);
                }});
        } else {
            increase.setVisibility(View.INVISIBLE);
            decrease.setVisibility(View.INVISIBLE);
            quantityDifference.setVisibility(View.INVISIBLE);
        }
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
        //TODO validate data
        //TODO update in server
        Log.i("Screen", "Go to list view from edit view");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, new ListFragment());
        ft.commit();
    }

    private void delete() {
        //TODO update in server
        Log.i("Screen", "Go to list view from edit view");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, new ListFragment());
        ft.commit();
    }
}

package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.warehouseuser.R;

public class DetailedFragment extends Fragment {
    protected EditText manufacturer;
    protected EditText model;
    protected EditText price;
    protected TextView quantity;
    protected TextView quantityDifference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_view, parent, false);
    }

    protected void initEditTexts() {
        manufacturer = (EditText) getActivity().findViewById(R.id.manufacturer_edit);
        model = (EditText) getActivity().findViewById(R.id.model_edit);
        price = (EditText) getActivity().findViewById(R.id.price_edit);
        quantity = (TextView) getActivity().findViewById(R.id.quantity_amount);
        quantityDifference = (TextView) getActivity().findViewById(R.id.quantity_edit);
    }

    protected void initPriceField() {
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
}

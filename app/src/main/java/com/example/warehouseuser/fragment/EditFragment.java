package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    private EditText quantity;

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
        initEditTexts();

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

        if (instrument != null) {
            setInstrument();
            save.setText("Zapisz");
        }
        else {
            save.setText("Dodaj");
        }
    }

    private void initEditTexts() {
        manufacturer = (EditText) getActivity().findViewById(R.id.manufacturer_edit);
        model = (EditText) getActivity().findViewById(R.id.model_edit);
        price = (EditText) getActivity().findViewById(R.id.price_edit);
        quantity = (EditText) getActivity().findViewById(R.id.quantity_edit);
    }

    private void setInstrument() {
        manufacturer.setText(instrument.getManufacturer());
        model.setText(instrument.getModel());
        price.setText(String.valueOf(instrument.getPrice()));
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
}

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
import com.example.warehouseuser.R;
import com.example.warehouseuser.api.RestApi;
import com.google.android.material.snackbar.Snackbar;

public class AddViewFragment extends DetailedFragment implements FragmentUpdate {

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
    }

    private void initButtons() {
        Button cancel = (Button) getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            FragmentManager fm = getFragmentManager();
            Log.i("Screen", "Back to list view from add view");
            fm.popBackStack();
        });

        Button save = (Button) getActivity().findViewById(R.id.save);
        save.setOnClickListener(this::save);
        save.setText("Dodaj");

        Button delete = (Button) getActivity().findViewById(R.id.delete);
        delete.setVisibility(View.INVISIBLE);
    }

    private void initQuantityFields() {
        Button increase = (Button) getActivity().findViewById(R.id.increase);
        Button decrease = (Button) getActivity().findViewById(R.id.decrease);

        increase.setVisibility(View.INVISIBLE);
        decrease.setVisibility(View.INVISIBLE);
        quantityDifference.setVisibility(View.INVISIBLE);
    }

    private void save(View view) {
        if (isValidData()) {
            Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Log.i("Screen", "Go to list view from add view");
        RestApi c = new RestApi();
        Instrument instrument = new Instrument(
                0,
                manufacturer.getText().toString(),
                model.getText().toString(),
                Float.parseFloat(price.getText().toString()),
                0);

        c.addInstrument(instrument, this);
    }

    @Override
    public void updateView(Instrument instruments) {
        Log.i("Screen", "Go to list view from add view");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, new ListFragment());
        ft.commit();
    }
}

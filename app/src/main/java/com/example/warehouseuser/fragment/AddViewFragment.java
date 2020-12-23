package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.example.warehouseuser.InternalStorage;
import com.example.warehouseuser.R;
import com.example.warehouseuser.data.InstrumentWrapper;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class AddViewFragment extends DetailedFragment {

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
        InstrumentWrapper newInstrument = new InstrumentWrapper(
                0,
                manufacturer.getText().toString(),
                model.getText().toString(),
                Float.parseFloat(price.getText().toString()),
                0,
                category.getSelectedItemPosition());

        InternalStorage storage = new InternalStorage(requireContext());
        try {
            storage.addInstrument(newInstrument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        goToListView();
    }

    private void goToListView() {
        Log.i("Screen", "Go to list view from add view");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }
}

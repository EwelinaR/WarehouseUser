package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.InstrumentAdapter;
import com.example.warehouseuser.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ListFragment extends Fragment implements FragmentUpdateList, FragmentUpdate {

    private RestApi controller;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        controller = new RestApi(this.getContext());
        controller.getToken(this, "Ala", "123");


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.add);
        fab.setOnClickListener(view1 -> {
            Log.i("Screen", "Go to add view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new AddViewFragment()).addToBackStack(null);
            ft.commit();
        });
    }

    @Override
    public void updateView(List<Instrument> instruments, int responseStatus) {
        if(responseStatus == -1) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.list_view),
                    "Brak połączenia z serwerem. ", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("połącz ponownie", view12 -> controller.getInstruments(this));
            mySnackbar.show();
            return;
        }
        InstrumentAdapter adapter = new InstrumentAdapter(getActivity(), instruments);
        ListView listView = (ListView) getActivity().findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Screen", "Go to edit view");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_placeholder, new EditFragment(instruments.get(position))).addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public void updateView(Instrument instruments) {
        controller.getInstruments(this);
    }
}

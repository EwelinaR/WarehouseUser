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

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.InstrumentAdapter;
import com.example.warehouseuser.R;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ArrayList<Instrument> instruments = new ArrayList<>();
        //TODO use data from server
        instruments.add(new Instrument(1, "MANUFACTURR", "MODELUU", 1212, 12));
        instruments.add(new Instrument(2, "XYZ", "MODELUU", 12, 1));

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
}

package com.example.warehouseuser.fragment;

import android.app.Activity;
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

import com.example.warehouseuser.InternalStorage;
import com.example.warehouseuser.MainActivity;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.data.Instrument;
import com.example.warehouseuser.InstrumentAdapter;
import com.example.warehouseuser.R;
import com.example.warehouseuser.fragment.update.FragmentUpdateList;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

public class ListFragment extends Fragment implements FragmentUpdateList, OnAuthenticationUpdate {

    private RestApi api;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = this.getActivity();
        ((MainActivity) activity).displayButtonsAfterSignIn();
        api = new RestApi(activity);
        getInstruments();

        FloatingActionButton fab = activity.findViewById(R.id.add);
        fab.setOnClickListener(view1 -> {
            Log.i("Screen", "Go to add view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new AddViewFragment()).addToBackStack(null);
            ft.commit();
        });
    }

    private void getInstruments() {
        InternalStorage storage = new InternalStorage(activity);
        List<Instrument> instruments = storage.readInstrumentsForDisplay();
        if (instruments == null) {
            api.getInstruments(this);
        } else {
            displayInstruments(instruments);
            Log.i("Storage", "Uses data from local storage");
        }
    }

    @Override
    public void updateView(RequestResponseStatus status, List<Instrument> instruments) {
        if (status == RequestResponseStatus.TIMEOUT) {
            Snackbar mySnackbar = Snackbar.make(activity.findViewById(R.id.list_view),
                    getString(R.string.connection_timeout), Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction(getString(R.string.retry_connection), view12 -> api.getInstruments(this));
            mySnackbar.show();
            return;
        }
        else if (status == RequestResponseStatus.UNAUTHORIZED) {
            api.refreshToken(this);
            return;
        }

        saveInstruments(instruments);
        displayInstruments(instruments);
    }

    private void displayInstruments(List<Instrument> instruments) {
        InstrumentAdapter adapter = new InstrumentAdapter(activity, instruments);
        ListView listView = activity.findViewById(R.id.list_view);
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

    private void saveInstruments(List<Instrument> instruments) {
        InternalStorage storage = new InternalStorage(activity);
        try {
            storage.saveInstrumentsFromServer(instruments);
            Log.i("Storage", "Saved data to local storage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        if (status == RequestResponseStatus.OK) {
            api.getInstruments(this);
        }
    }
}

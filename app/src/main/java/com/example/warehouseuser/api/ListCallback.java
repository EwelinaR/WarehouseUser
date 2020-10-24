package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.fragment.FragmentUpdateList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCallback implements Callback<List<Instrument>> {

    private FragmentUpdateList fragmentView;

    public ListCallback(FragmentUpdateList fragmentView) {
        this.fragmentView = fragmentView;
    }

    @Override
    public void onResponse(Call<List<Instrument>> call, Response<List<Instrument>> response) {
        if(response.isSuccessful()) {
            List<Instrument> instruments = response.body();
            instruments.forEach(instrument -> Log.i("API", "GET: " + instrument.toString()));
            fragmentView.updateView(instruments);
        } else {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                Log.e("API", jObjError.getJSONObject("error").getString("message"));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<List<Instrument>> call, Throwable t) {
        t.printStackTrace();
    }
}

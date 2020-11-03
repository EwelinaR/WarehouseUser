package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.fragment.update.FragmentUpdateList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
        Log.i("API", "CODE: "+response.code());

        if(response.isSuccessful()) {
            List<Instrument> instruments = response.body();
            instruments.forEach(instrument -> Log.i("API", "GET: " + instrument.toString()));
            fragmentView.updateView(RequestResponseStatus.OK, instruments);
        } else if (response.code() == 401) {
            fragmentView.updateView(RequestResponseStatus.UNAUTHORIZED, null);
        } else if (response.code() == 403) {
            fragmentView.updateView(RequestResponseStatus.FORBIDDEN, null);
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
        if (t instanceof SocketTimeoutException) {
            Log.e("Connection", "SocketTimeoutException");
            fragmentView.updateView(RequestResponseStatus.TIMEOUT, null);
        } else {
            t.printStackTrace();
        }
    }
}

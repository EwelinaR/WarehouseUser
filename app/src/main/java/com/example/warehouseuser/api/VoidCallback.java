package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.fragment.FragmentUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoidCallback implements Callback<Void> {

    private FragmentUpdate fragment;

    public VoidCallback(FragmentUpdate fragmentView) {
        this.fragment = fragmentView;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(response.isSuccessful()) {
            Log.i("API", "Request successful");
            fragment.updateView(null);
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
    public void onFailure(Call<Void> call, Throwable t) {
        t.printStackTrace();
    }
}
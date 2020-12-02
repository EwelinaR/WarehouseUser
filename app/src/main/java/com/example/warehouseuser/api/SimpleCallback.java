package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.fragment.update.FragmentUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleCallback implements Callback<ResponseBody> {

    private final FragmentUpdate fragment;

    public SimpleCallback(FragmentUpdate fragmentView) {
        this.fragment = fragmentView;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Log.i("API", "CODE: "+response.code());

        String message = "";
        try {
            if (response.body() != null)
            message = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response.isSuccessful()) {
            Log.i("API", "Request successful");
            fragment.updateView(RequestResponseStatus.OK, message);
        } else if (response.code() == 401) {
            fragment.updateView(RequestResponseStatus.UNAUTHORIZED, message);
        } else if (response.code() == 403) {
            fragment.updateView(RequestResponseStatus.FORBIDDEN, message);
        } else if (response.code() == 404) {
            fragment.updateView(RequestResponseStatus.NOT_FOUND, message);
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
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            Log.e("Connection", "SocketTimeoutException");
            fragment.updateView(RequestResponseStatus.TIMEOUT, "");
        } else {
            t.printStackTrace();
        }
    }
}
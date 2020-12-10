package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.Updater;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCallback implements Callback<ResponseBody> {

    private final Updater updater;
    private final boolean isQuantityChange;

    public UpdateCallback(Updater updater, boolean isQuantityChange) {
        this.updater = updater;
        this.isQuantityChange = isQuantityChange;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Log.i("API", "CODE: "+response.code());

        String message = "";
        try {
            if (response.body() != null) {
                message = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.code() == 208) {
            updater.update(RequestResponseStatus.CONFLICT, message, isQuantityChange);
        } else if(response.isSuccessful()) {
            Log.i("API", "Request successful");
            updater.update(RequestResponseStatus.OK, message, isQuantityChange);
        } else if (response.code() == 401) {
            updater.update(RequestResponseStatus.UNAUTHORIZED, message, isQuantityChange);
        } else if (response.code() == 404) {
            updater.update(RequestResponseStatus.NOT_FOUND, message, isQuantityChange);
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            Log.e("Connection", "SocketTimeoutException");
            updater.update(RequestResponseStatus.TIMEOUT, "", isQuantityChange);
        } else {
            t.printStackTrace();
        }
    }
}

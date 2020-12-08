package com.example.warehouseuser.api;

import android.util.Log;

import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.data.UserInfo;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoCallback implements Callback<UserInfo> {

    private final SessionManager manager;

    public UserInfoCallback(SessionManager manager) {
        this.manager = manager;
    }

    @Override
    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
        Log.i("API", "CODE: "+response.code());

        if (response.isSuccessful()) {
            UserInfo userInfo = response.body();
            manager.setManager(userInfo.isManager());
        }
    }

    @Override
    public void onFailure(Call<UserInfo> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            Log.e("Connection", "SocketTimeoutException");
        } else {
            t.printStackTrace();
        }
    }
}

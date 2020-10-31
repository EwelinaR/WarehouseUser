package com.example.warehouseuser.api;

import android.content.Context;
import android.util.Log;

import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.fragment.OnAuthenticationUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInCallback implements Callback<TokenResponse> {

    private OnAuthenticationUpdate update;
    private Context context;

    public SignInCallback(Context context, OnAuthenticationUpdate update) {
        this.update = update;
        this.context = context;
    }

    @Override
    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
        Log.w("API", "CODE: "+response.code());
        if(response.isSuccessful()) {
            SessionManager sessionManager = new SessionManager(context);
            TokenResponse token = response.body();
            sessionManager.setAccessToken(token.getAccessToken());
            sessionManager.setRefreshToken(token.getRefreshToken());
            update.onAuthentication(RequestResponseStatus.OK);
        } else if (response.code() == 400) {
            update.onAuthentication(RequestResponseStatus.UNAUTHORIZED);
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
    public void onFailure(Call<TokenResponse> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            Log.e("Connection", "SocketTimeoutException");
            update.onAuthentication(RequestResponseStatus.TIMEOUT);
        } else {
            t.printStackTrace();
        }
    }
}

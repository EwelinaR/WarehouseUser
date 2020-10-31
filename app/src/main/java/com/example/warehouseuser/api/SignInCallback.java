package com.example.warehouseuser.api;

import android.content.Context;
import android.util.Log;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.fragment.FragmentUpdate;
import com.example.warehouseuser.fragment.FragmentUpdateList;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import java.util.List;

import kotlin.Pair;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class SignInCallback implements Callback<TokenResponse> {

    private FragmentUpdate fragmentView;
    private Context context;

    public SignInCallback(Context context, FragmentUpdate fragmentView) {
        this.fragmentView = fragmentView;
        this.context = context;
    }


    @Override
    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
        Log.w("TOKEN", "CODE: "+response.code());
        if(response.isSuccessful()) {
            SessionManager sessionManager = new SessionManager(context);
            TokenResponse token = response.body();
            sessionManager.setAccessToken(token.getAccessToken());
            sessionManager.setRefreshToken(token.getRefreshToken());

            Log.w("TOKEN", "ACCESS: "+token.getAccessToken());
            Log.w("TOKEN", "REFRESH: "+token.getRefreshToken());
            fragmentView.updateView(null);
        } else if (response.code() == 401) {
            SessionManager sessionManager = new SessionManager(context);
            // wrong user login or password!

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
            fragmentView.updateView(null);
        } else {
            t.printStackTrace();
        }
    }
}

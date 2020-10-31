package com.example.warehouseuser;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private final String ACCESS_TOKEN = "access_token";
    private final String REFRESH_TOKEN = "refresh_token";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN, null);
    }

    public void setAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, null);
    }

    public void setRefreshToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REFRESH_TOKEN, token);
        editor.apply();
    }
}

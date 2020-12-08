package com.example.warehouseuser.api;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import com.example.warehouseuser.AuthenticationInterceptor;
import com.example.warehouseuser.Updater;
import com.example.warehouseuser.data.DetailedInstrument;
import com.example.warehouseuser.data.Instrument;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.data.UserInfo;
import com.example.warehouseuser.fragment.update.FragmentUpdate;
import com.example.warehouseuser.fragment.update.FragmentUpdateList;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    static final String BASE_URL = "http://192.168.1.132:8080/";
    private final SessionManager manager;
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final String BEARER_PREFIX = "Bearer ";

    public RestApi(Context context) {
        this.manager = new SessionManager(context);
    }

    private RetrofitApi createRetrofitApi(String token) {
        Log.i("TOKEN", token);
        AuthenticationInterceptor interceptor =
                new AuthenticationInterceptor(token);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RetrofitApi.class);
    }

    public void getToken(OnAuthenticationUpdate update, String username, String password) {
        String authToken = Credentials.basic(CLIENT_ID, CLIENT_SECRET);

        RetrofitApi retrofitApi = createRetrofitApi(authToken);

        Call<TokenResponse> call = retrofitApi.getAccessToken(username, password, "password");
        call.enqueue(new SignInCallback(manager, update));
    }

    public void refreshToken(OnAuthenticationUpdate update) {
        String authToken = Credentials.basic(CLIENT_ID, CLIENT_SECRET);
        RetrofitApi retrofitApi = createRetrofitApi(authToken);

        Call<TokenResponse> call = retrofitApi.refreshToken("refresh_token", manager.getRefreshToken());
        call.enqueue(new SignInCallback(manager, update));
    }

    public void saveUserRole() {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<UserInfo> call = retrofitApi.getUserInfo(manager.getAccessToken());
        call.enqueue(new UserInfoCallback(manager));
    }

    public void getInstruments(FragmentUpdateList fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new ListCallback(fragmentView));
    }

    public void sendNewInstrument(Instrument instrument, Updater updater) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<ResponseBody> call = retrofitApi.addInstrument(instrument);
        call.enqueue(new UpdateCallback(updater, false));
    }

    public void updateInstrument(DetailedInstrument instrument, Updater updater) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<ResponseBody> call = retrofitApi.updateInstrument(instrument.getId(), instrument);
        call.enqueue(new UpdateCallback(updater, false));
    }

    public void updateInstrumentQuantity(int id, int quantityChange, Updater updater) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<ResponseBody> call;
        if (quantityChange > 0)
            call = retrofitApi.increaseQuantity(id, quantityChange);
        else
            call = retrofitApi.decreaseQuantity(id, -quantityChange);
        call.enqueue(new UpdateCallback(updater, true));
    }

    public void deleteInstrument(int id, Updater updater) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<ResponseBody> call = retrofitApi.deleteInstrument(id);
        call.enqueue(new UpdateCallback(updater, false));
    }
}
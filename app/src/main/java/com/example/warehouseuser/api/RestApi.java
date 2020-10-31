package com.example.warehouseuser.api;

import android.content.Context;
import android.util.Log;

import com.example.warehouseuser.AuthenticationInterceptor;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.fragment.FragmentUpdate;
import com.example.warehouseuser.fragment.FragmentUpdateList;
import com.example.warehouseuser.fragment.OnAuthenticationUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    static final String BASE_URL = "http://192.168.1.132:8080/";

    private Context context;

    public RestApi(Context context) {
        this.context = context;
    }


    private RetrofitApi createRetrofitApi(String token) {
        Log.i("TOKEN", token);
        AuthenticationInterceptor interceptor =
                new AuthenticationInterceptor(token);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
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
        String authToken = Credentials.basic("client", "secret");

        RetrofitApi retrofitApi = createRetrofitApi(authToken);

        Call<TokenResponse> call = retrofitApi.getAccessToken(username, password, "password");
        call.enqueue(new SignInCallback(context, update));
    }

    public void getInstruments(FragmentUpdateList fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new ListCallback(fragmentView));
    }

    public void editInstrument(Instrument instrument, FragmentUpdate fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<Void> call = retrofitApi.updateInstrument(instrument);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void addInstrument(Instrument instrument, FragmentUpdate fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<Void> call = retrofitApi.addInstrument(instrument);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void increaseQuantity(int id, int amount, FragmentUpdate fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<Void> call = retrofitApi.increaseQuantity(id, amount);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void decreaseQuantity(int id, int amount, FragmentUpdate fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<Void> call = retrofitApi.decreaseQuantity(id, amount);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void deleteInstrument(int index, FragmentUpdate fragmentView) {
        SessionManager manager = new SessionManager(context);
        RetrofitApi retrofitApi = createRetrofitApi("Bearer "+manager.getAccessToken());

        Call<Void> call = retrofitApi.deleteInstrument(index);
        call.enqueue(new VoidCallback(fragmentView));
    }
}
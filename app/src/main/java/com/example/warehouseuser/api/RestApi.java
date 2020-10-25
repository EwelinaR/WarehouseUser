package com.example.warehouseuser.api;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.fragment.FragmentUpdate;
import com.example.warehouseuser.fragment.FragmentUpdateList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    static final String BASE_URL = "http://192.168.1.132:8080/";

    private RetrofitApi createRetrofitApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RetrofitApi.class);
    }

    public void getInstruments(FragmentUpdateList fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new ListCallback(fragmentView));
    }

    public void editInstrument(Instrument instrument, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.updateInstrument(instrument);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void addInstrument(Instrument instrument, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.addInstrument(instrument);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void increaseQuantity(int id, int amount, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.increaseQuantity(id, amount);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void decreaseQuantity(int id, int amount, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.decreaseQuantity(id, amount);
        call.enqueue(new VoidCallback(fragmentView));
    }

    public void deleteInstrument(int index, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.deleteInstrument(index);
        call.enqueue(new VoidCallback(fragmentView));
    }
}
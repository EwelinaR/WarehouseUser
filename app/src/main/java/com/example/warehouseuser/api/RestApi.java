package com.example.warehouseuser.api;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.fragment.FragmentUpdate;
import com.example.warehouseuser.fragment.FragmentUpdateList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    static final String BASE_URL = "http://192.168.1.132:8080/";

    private RetrofitApi createRetrofitApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RetrofitApi.class);
    }

    public void getInstruments(FragmentUpdateList fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new ListCallback(fragmentView));
    }

    public void deleteInstrument(int index, FragmentUpdate fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi();

        Call<Void> call = retrofitApi.deleteInstrument(index);
        call.enqueue(new VoidCallback(fragmentView));
    }
}
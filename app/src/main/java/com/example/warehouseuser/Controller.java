package com.example.warehouseuser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller implements Callback<List<Instrument>> {

    static final String BASE_URL = "http://192.168.1.132:8080/";

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

        Instrument instrument = new Instrument(3, "M", "Mod", 123, 3);
        Call<Void> call = retrofitApi.addInstrument(instrument);
        //Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.i("API", "Success");
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
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }});
    }

    @Override
    public void onResponse(Call<List<Instrument>> call, Response<List<Instrument>> response) {
        if(response.isSuccessful()) {
            List<Instrument> instruments = response.body();
            instruments.forEach(instrument -> Log.i("API", "GET: " + instrument.toString()));

            Log.i("API", instruments.get(0).getManufacturer());
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
    public void onFailure(Call<List<Instrument>> call, Throwable t) {
        t.printStackTrace();
    }
}
package com.example.warehouseuser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitApi {
    @GET("instruments")
    Call<List<Instrument>> getInstruments();

//    @DELETE("instrumetns/{id}")
//    void  loadChanges(@Path("id") int status);
}

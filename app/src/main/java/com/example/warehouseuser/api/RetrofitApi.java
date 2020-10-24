package com.example.warehouseuser.api;

import com.example.warehouseuser.Instrument;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitApi {
    @GET("instruments")
    Call<List<Instrument>> getInstruments();

    @POST("instruments")
    Call<Void> addInstrument(@Body Instrument instrument);

    @PUT("instruments/{id}")
    Call<Void> updateInstrument(@Path("id") int id, @Body Instrument instrument);

    @PUT("instruments/increase/{id}/{amount}")
    Call<Void> increaseQuantity(@Path("id") int id, @Path("amount") int amount);

    @PUT("instruments/decrease/{id}/{amount}")
    Call<Void> decreaseQuantity(@Path("id") int id, @Path("amount") int amount);

    @DELETE("instruments/{id}")
    Call<Void>  deleteInstrument(@Path("id") int status);
}
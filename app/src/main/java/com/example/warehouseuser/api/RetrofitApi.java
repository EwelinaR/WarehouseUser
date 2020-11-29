package com.example.warehouseuser.api;

import com.example.warehouseuser.Instrument;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApi {
    @GET("instruments")
    Call<List<Instrument>> getInstruments();

    @POST("instruments")
    Call<ResponseBody> addInstrument(@Body Instrument instrument);

    @PUT("instruments")
    Call<Void> updateInstrument(@Body Instrument instrument);

    @PUT("instruments/v2/")
    Call<ResponseBody> updateInstrument(@Body Instrument instrument, @Query("timestamp") Long date);

    @PUT("instruments/increase/{id}/{amount}")
    Call<ResponseBody> increaseQuantity(@Path("id") int id, @Path("amount") int amount);

    @PUT("instruments/decrease/{id}/{amount}")
    Call<ResponseBody> decreaseQuantity(@Path("id") int id, @Path("amount") int amount);

    @DELETE("instruments/{id}")
    Call<ResponseBody>  deleteInstrument(@Path("id") int status);

    @POST("oauth/token")
    @FormUrlEncoded
    Call<TokenResponse> getAccessToken(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType);

    @POST("oauth/token")
    @FormUrlEncoded
    Call<TokenResponse> refreshToken(@Field("grant_type") String grantType, @Field("refresh_token") String token);
}

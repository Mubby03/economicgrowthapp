package com.example.economicgrowthapp.Retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("v6/2cdaf5aa2eb2fd6aa6e48cdb/latest/{currency}")
    Call<JsonObject> getExchangeCurrency(@Path("currency") String currency);

}

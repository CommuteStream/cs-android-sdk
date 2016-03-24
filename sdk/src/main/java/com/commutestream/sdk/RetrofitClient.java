package com.commutestream.sdk;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * RetrofitClient implements Client using Retrofit
 */
public interface RetrofitClient {
    @GET("/banner")
    Call<AdResponse> getBanner(@QueryMap Map<String, String> params);
}

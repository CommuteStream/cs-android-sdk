package com.commutestream.sdk;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * RetrofitClient implements Client using Retrofit
 */
public interface RetrofitClient {
    @GET("/banner")
    Call<AdResponse> getBanner(@QueryMap Map<String, String> params);
}

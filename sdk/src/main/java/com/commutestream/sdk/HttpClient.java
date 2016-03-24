package com.commutestream.sdk;

import android.util.Log;


import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Http Client for CommuteStream Ads
 */
class HttpClient implements Client {
    private String baseURL = "https://api.commutestream.com:3000/";
    private OkHttpClient okClient;
    private Retrofit retrofit;
    private RetrofitClient client;

    HttpClient() {
        init();
    }

    HttpClient(String baseURL) {
        this.baseURL = baseURL;
        init();
    }

    private void init() {

        okClient = new OkHttpClient.Builder().addInterceptor(new HttpLogger()).build();
        retrofit = new Retrofit.Builder()
                .client(okClient)
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client = retrofit.create(RetrofitClient.class);
    }

    @Override
    public void getAd(AdRequest request, final AdResponseHandler adHandler) {
        final long start_time = System.nanoTime();

        Callback<AdResponse> callback = new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                long end_time = System.nanoTime();
                double difference = (end_time - start_time)/1e6;
                if(response.body() != null) {
                    if(CommuteStream.getTestingFlag() && response.body().getBannerRequestUuid() == null) {
                        Log.e("CS_SDK", "Response deserialization appears to have failed, possibly a Proguard Configuration problem!\n" +
                                "See documentation at https://commutestream.com/sdkinstructions regarding Proguard rules");
                    } else {
                        Log.v("CS_SDK", "Ad request successful: " + response.body().toString());
                    }
                    adHandler.onSuccess(response.body(), difference);
                } else {
                    Log.v("CS_SDK", "Ad request null response, HTTP Code: " + response.code() + " , Raw Body: " + response.raw());
                    adHandler.onError(new Exception("Empty or Incorrect HTTP Response"));
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                Log.v("CS_SDK", "Ad request error " + t.toString());
                adHandler.onError(t);
            }
        };
        Call<AdResponse> call = client.getBanner(AdRequestQueryMap.Map(request));
        call.enqueue(callback);
    }

    /**
     * Getter for the clients BaseURL
     *
     * @return
     */
    public String getBaseURL() { return baseURL; }
}

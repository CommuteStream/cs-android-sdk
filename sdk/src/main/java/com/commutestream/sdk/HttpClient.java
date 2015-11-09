package com.commutestream.sdk;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
        okClient = new OkHttpClient();
        okClient.interceptors().add(new HttpLogger());
        retrofit = new Retrofit.Builder()
                .client(okClient)
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client = retrofit.create(RetrofitClient.class);
    }

    @Override
    public void getAd(AdRequest request, final AdResponseHandler adHandler) {
        Callback<AdResponse> callback = new Callback<AdResponse>() {
            @Override
            public void onResponse(Response<AdResponse> response) {
                if(response.body() != null) {
                    Log.v("CS_SDK", "Ad request successful: " + response.body().toString());
                    adHandler.onSuccess(response.body());
                } else {
                    Log.v("CS_SDK", "Ad request null response, HTTP Code: " + response.code() + " , Raw Body: " + response.raw());
                    adHandler.onError(new Exception("Empty or Incorrect HTTP Response"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
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

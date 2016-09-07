package com.commutestream.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl;
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
    final Logger logger = LoggerFactory.getLogger(HttpLogger.class);

    private HttpUrl baseURL = new HttpUrl.Builder().scheme("http").host("10.11.12.159").port(3000).build();
    private OkHttpClient okClient;
    private Retrofit retrofit;
    private RetrofitClient client;

    HttpClient() {
        init();
    }

    HttpClient(HttpUrl baseURL) {
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
                if(response.isSuccessful() && response.body() != null) {
                    if(CommuteStream.getTestingFlag() && response.body().getBannerRequestUuid() == null) {
                        logger.error("CS_SDK", "Response deserialization appears to have failed, possibly a Proguard Configuration problem!\n" +
                                "See documentation at https://commutestream.com/sdkinstructions regarding Proguard rules");
                    } else {
                        logger.trace("CS_SDK", "Ad request succeeded in " + difference + "ms, response: " + response.raw());
                    }
                    adHandler.onSuccess(response.body(), difference);
                } else {
                    logger.trace("CS_SDK", "Ad request failed in " + difference + "ms, response, " + response.raw());
                    adHandler.onError(new Exception("Empty or Incorrect HTTP Response"));
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                logger.trace("CS_SDK", "Ad request error " + t.toString());
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
    public HttpUrl getBaseURL() { return baseURL; }
}

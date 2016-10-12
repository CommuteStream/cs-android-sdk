package com.commutestream.sdk;


import android.icu.util.TimeZone;
import android.support.annotation.NonNull;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Http Client for CommuteStream Ads
 */
class HttpClient implements Client {
    public final static Exception HttpFailure = new Exception("HTTP response was not OK");

    private static String BANNER_REQUEST_UUID_HEADER = "X-CS-BANNER-REQUEST-UUID";
    private static String REQUEST_ID_HEADER = "X-CS-REQUEST-ID";
    private static String IMPRESSION_URL_HEADER = "X-CS-IMPRESSION-URL";
    private static String CLICK_URL_HEADER = "X-CS-CLICK-URL";

    final Logger logger = LoggerFactory.getLogger(HttpLogger.class);

    private HttpUrl mBaseURL = new HttpUrl.Builder().scheme("https").host("api.commutestream.com").build();
    private OkHttpClient mClient;

    HttpClient() {
        init();
    }

    HttpClient(HttpUrl baseURL) {
        mBaseURL = baseURL;
        init();
    }

    private void init() {
        mClient = new OkHttpClient.Builder().addInterceptor(new HttpLogger()).build();
    }

    @Override
    public void postUpdates(UpdateRequest updates, final UpdateResponseHandler updateHandler) {
        final long startTime = System.nanoTime();

    }

    @Override
    public void getAd(AdRequest adRequest, final AdResponseHandler adHandler) {
        final long startTime = System.nanoTime();
        //Moshi moshi = new Moshi.Builder().build();
        //JsonAdapter<AdRequest> jsonAdapter = moshi.adapter(AdRequest.class);
        //String json = jsonAdapter.toJson(adRequest);
        TimeZone tz = TimeZone.getDefault();
        logger.debug("Current timezone is " + tz.getDisplayName());
        String json = "{}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        HttpUrl url = mBaseURL.newBuilder("/v2/banner").build();
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("Accepts", AdContentTypes.acceptsHeader())
                .post(body)
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                adHandler.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    adHandler.onError(HttpFailure);
                    return;
                }
                try {
                    final long endTime = System.nanoTime();
                    ResponseBody body = response.body();
                    AdMetadata metadata = new AdMetadata();
                    metadata.requestID = Integer.getInteger(response.header(HttpClient.REQUEST_ID_HEADER));
                    metadata.contentType = body.contentType().type() + "/" + body.contentType().subtype();
                    metadata.impressionUrl = response.header(HttpClient.IMPRESSION_URL_HEADER);
                    metadata.clickUrl = response.header(HttpClient.CLICK_URL_HEADER);
                    metadata.requestTime = endTime - startTime;
                    metadata.validate();
                    adHandler.onSuccess(metadata, response.body().bytes());
                } catch (Throwable t) {
                    adHandler.onError(t);
                }
            }
        };
        mClient.newCall(request).enqueue(callback);
    }

    /**
     * Getter for the clients BaseURL
     *
     * @return
     */
    public HttpUrl getBaseURL() { return mBaseURL; }
}

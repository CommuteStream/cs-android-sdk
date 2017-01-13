package com.commutestream.sdk;


import java.util.TimeZone;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Http Client for CommuteStream Ads
 */
class HttpClient implements Client {
    public final static Exception HttpFailure = new Exception("HTTP response was not OK");

    private static String REQUEST_ID_HEADER = "X-CS-REQUEST-ID";
    private static String IMPRESSION_URL_HEADER = "X-CS-IMPRESSION-URL";
    private static String CLICK_URL_HEADER = "X-CS-CLICK-URL";
    private static String AD_KIND_HEADER = "X-CS-AD-KIND";
    private static String AD_WIDTH_HEADER = "X-CS-AD-WIDTH";
    private static String AD_HEIGHT_HEADER = "X-CS-AD-HEIGHT";

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
    public void getAd(final AdRequest adRequest, final AdResponseHandler adHandler) {
        final long startTime = System.nanoTime();
        TimeZone tz = TimeZone.getDefault();
        String tzName = tz.getDisplayName(false, TimeZone.LONG);

        Location loc = adRequest.getLocation();
        Set<AgencyInterest> agency_interests = adRequest.getAgencyInterests();

         HttpUrl.Builder urlBuilder = mBaseURL.newBuilder("/v2/banner")
                .addQueryParameter("aaid", adRequest.getAAID())
                .addQueryParameter("ad_unit_uuid", adRequest.getAdUnitUuid())
                .addQueryParameter("timezone", tzName);
        if(loc != null) {
            urlBuilder.addQueryParameter("lat", Double.toString(loc.getLatitude()));
            urlBuilder.addQueryParameter("lon", Double.toString(loc.getLongitude()));
            urlBuilder.addQueryParameter("fix_time", Double.toString(loc.getTime()));
        }
        if(!agency_interests.isEmpty()) {
            urlBuilder.addQueryParameter("agency_interests", AgencyInterestCSVEncoder.Encode(adRequest.getAgencyInterests()));
        }
        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("Accepts", AdKinds.acceptsHeader())
                .get()
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                adHandler.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 404 || response.code() == 503) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adHandler.onNotFound();
                        }
                    });
                } else if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adHandler.onError(HttpFailure);
                        }
                    });
                } else {
                    try {
                        final long endTime = System.nanoTime();
                        ResponseBody body = response.body();
                        AdMetadata metadata = new AdMetadata();
                        metadata.requestID = Long.parseLong(response.header(HttpClient.REQUEST_ID_HEADER));
                        metadata.kind = response.header(HttpClient.AD_KIND_HEADER);
                        metadata.impressionUrl = response.header(HttpClient.IMPRESSION_URL_HEADER);
                        metadata.clickUrl = response.header(HttpClient.CLICK_URL_HEADER);
                        String widthStr = response.header(HttpClient.AD_WIDTH_HEADER);
                        String heightStr = response.header(HttpClient.AD_HEIGHT_HEADER);
                        if (widthStr != null && heightStr != null) {
                            metadata.adWidth = Integer.parseInt(widthStr);
                            metadata.adHeight = Integer.parseInt(heightStr);
                        } else {
                            logger.debug("CS_SDK", "width and height are " + widthStr + "," + heightStr);
                        }
                        metadata.viewHeight = adRequest.getViewHeight();
                        metadata.viewWidth = adRequest.getViewWidth();
                        metadata.requestTime = endTime - startTime;

                        metadata.validate();
                        final AdMetadata metadata0 = metadata;
                        final byte[] bodyBytes = body.bytes();
                        body.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                adHandler.onFound(metadata0, bodyBytes);
                            }
                        });
                    } catch (final Throwable t) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                adHandler.onError(t);
                            }
                        });
                    }
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

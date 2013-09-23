package com.commutestream.ads;


import android.util.Log;

import com.commutestream.ads.http.AsyncHttpClient;
import com.commutestream.ads.http.AsyncHttpResponseHandler;
import com.commutestream.ads.http.RequestParams;

public class RestClient {
    private static final String BASE_URL = "https://api.commutestreamdev.com:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	Log.v("CS_SDK", "GET: " + getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}

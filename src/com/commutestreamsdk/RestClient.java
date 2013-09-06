package com.commutestreamsdk;

import android.util.Log;

import com.commutestreamsdk.http.AsyncHttpClient;
import com.commutestreamsdk.http.AsyncHttpResponseHandler;
import com.commutestreamsdk.http.RequestParams;

public class RestClient {
    private static final String BASE_URL = "https://api.commutestreamdev.com:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	Log.v("CS_SDK", "GET: " + getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}

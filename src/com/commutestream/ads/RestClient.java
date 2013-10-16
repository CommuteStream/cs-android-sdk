package com.commutestream.ads;


import com.commutestream.ads.http.AsyncHttpClient;
import com.commutestream.ads.http.AsyncHttpResponseHandler;
import com.commutestream.ads.http.RequestParams;

public class RestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return CommuteStream.getApi_url() + relativeUrl;
    }

}

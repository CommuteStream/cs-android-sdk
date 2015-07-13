package com.commutestream.ads;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Http Client for CommuteStream Ads
 */
class HttpClient implements Client {

    private AsyncHttpClient client = new AsyncHttpClient();
    private String baseURL = "https://api.commutestream.com:3000/";

    @Override
    public synchronized void getAd(AdRequest request, final AdResponseHandler adHandler) {
        // Encode the request parameters
        RequestParams params = AdRequestURLEncoder.Encode(request);
        client.get(getAbsoluteUrl("banner"), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("CS_SDK", "Ad request successful");
                try {
                    if (response.has("error")) {
                        Log.v("CS_SDK", "error is " + response.getString("error"));
                    }
                    AdResponse adResponse = new AdResponse(response.getString("html"), response.getString("url"));
                    adHandler.onSuccess(adResponse);
                } catch (JSONException e) {
                    Log.v("CS_SDK", "JSON Exception " + e.toString());
                    adHandler.onError(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.v("CS_SDK", "Ad request failure");
                adHandler.onError(e);
            }
        });
    }

    /**
     * Get the current base API URL for the HttpClient
     *
     * @return baseURL
     */
    public synchronized String getBaseURL() {
        return baseURL;
    }

    /**
     * Sets the current API URL for HttpClient, should always end in a /
     * Example: https://api,commutestream.com/
     *
     * @param baseURL
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }


    private String getAbsoluteUrl(String relativeURL) {
        return baseURL + relativeURL;
    }

}

package com.commutestream.sdk;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClient extends WebViewClient {

    UrlHandler mUrlHandler;

    WebClient(UrlHandler urlHandler) {
        super();
        mUrlHandler = urlHandler;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return mUrlHandler.handleUrl(this, view, url);
    }

}

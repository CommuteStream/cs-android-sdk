package com.commutestream.sdk;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Url Handler may be overridden and given to the WebClient to handle url's differently
 * than the WebViewClient normally handles them.
 */
public abstract class UrlHandler {
    public boolean handleUrl(WebViewClient client, WebView view, String url) {
        return false;
    }
}

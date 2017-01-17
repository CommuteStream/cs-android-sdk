package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

public class AdWebView extends WebView {
    String mHtml;

    public AdWebView(Context context, String html) {
        super(context);
        mHtml = html;
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d("CS_SDK", "Attached webview, visisble " + getVisibility() + " shown " + isShown());
    }
}

package com.commutestream.sdk.mraid;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.commutestream.sdk.AdEventListener;

public class MRaidViewFactory {
    public static View create(Context context, final AdEventListener listener, double requestTime, int width, int height) {
        WebView webView = new WebView(context);

        // forward console.log in javascript to android Log
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.v("CS_SDK_WebView",
                        cm.message() + " -- From line " + cm.lineNumber());
                return true;
            }
        });
        MRaidBridgeListener mRaidBridgeListener = new MRaidBridgeListener();
        MRaidBridge mRaidBridge = new MRaidBridge(mRaidBridgeListener);
        mRaidBridge.attachView(webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        return webView;
    }
}


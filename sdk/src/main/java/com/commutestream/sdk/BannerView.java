package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

/**
 * BannerView displays a html banner as an android WebView
 */
public class BannerView extends WebView {
    public BannerView(Context context, double requestTime, int width, int height, final AdEventListener listener, String html) {
        super(context);
        // Set the JS var for the request time, since its done in Java
        final String html2 = html.replace("var loadTimeBanner = null;", "var loadTimeBanner = " + Math.round(requestTime) + ";");

        class JsObject {
            @JavascriptInterface
            public void onClicked() {
                listener.onClicked();
            }
        }

        // forward console.log in javascript to android Log
        this.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.v("CS_SDK_WebView",
                        cm.message() + " -- From line " + cm.lineNumber());
                return true;
            }
        });
        this.addJavascriptInterface(new JsObject(), "listener");
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        this.loadData(html2, "text/html", null);
        this.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
    }
}

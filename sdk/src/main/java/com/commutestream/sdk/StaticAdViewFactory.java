package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class StaticAdViewFactory {
    public static View create(final Context context, final AdEventListener listener,
                              final AdMetadata metadata, final byte[] content) {

        String html = content.toString();

        // Set the JS var for the request time, since its done in Java
        final String html2 = html.replace("var loadTimeBanner = null;", "var loadTimeBanner = " + Math.round(metadata.requestTime) + ";");

        // create a new webview and put the ad in it
        WebView webView = new WebView(context);

        // This block allows JS console messages to be transported to LogCat
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.v("CS_SDK_WebView",
                        cm.message() + " -- From line " + cm.lineNumber());
                return true;
            }
        });

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(html2, "text/html", null);

        webView.setLayoutParams(new RelativeLayout.LayoutParams(metadata.width,
                metadata.height));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        AdView adView = new AdView(context, listener);
        adView.setContentView(webView);
        return adView;
    }
}

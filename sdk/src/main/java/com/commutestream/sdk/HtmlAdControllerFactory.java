package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class HtmlAdControllerFactory {
    public static AdController create(final Context context, final AdEventListener listener,
                              final AdMetadata metadata, final byte[] content) {
        final String html = new String(content);

        // create a new webview and put the ad in it
        final WebView webView = new AdWebView(context, html);

        // This block allows JS console messages to be transported to LogCat
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.v("CS_SDK_WebView",
                        cm.message() + " -- From line " + cm.lineNumber());
                return true;
            }
        });
        double xScale = (double)metadata.viewWidth/(double)metadata.adWidth;
        double yScale = (double)metadata.viewHeight/(double)metadata.adHeight;
        double scale = Math.min(yScale, xScale);
        int width = (int)Math.floor(scale*(double)metadata.adWidth + 0.5);
        int height = (int)Math.floor(scale*(double)metadata.adHeight + 0.5);

        webView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);

        final UrlHandler urlHandler = new UrlHandler(context);
        final AdController adController = new AdController(context, metadata, webView, urlHandler);

        webView.setWebViewClient(new WebClient(urlHandler));
        webView.loadData(html, "text/html", null);
        return adController;
    }
}

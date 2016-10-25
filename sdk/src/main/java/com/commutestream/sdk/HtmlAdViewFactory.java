package com.commutestream.sdk;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.nio.charset.StandardCharsets;

public class HtmlAdViewFactory {
    public static View create(final Context context, final AdEventListener listener,
                              final AdMetadata metadata, final byte[] content) {

        String html = new String(content);

        // create a new webview and put the ad in it
        final WebView webView = new WebView(context);

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
        webView.loadData(html, "text/html", null);

        webView.setLayoutParams(new RelativeLayout.LayoutParams(metadata.width,
                metadata.height));
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);


        final AdView adView = new AdView(context, listener);
        adView.setContentView(webView);
        webView.setWebViewClient(new WebClient(new UrlHandler() {
            @Override
            public boolean handleUrl(WebViewClient client, WebView view, String url) {
                if(adView.wasClicked()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else {
                    Log.e("CS_SDK", "not loading a url since the ad has not seen any interaction or visibility yet");
                }
                return true;
            }
        }));
        return adView;
    }
}

package com.commutestream.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;


/**
 * AdMobAdapter provides an implementation of an AdMob CustomEventBanner
 * allowing CommuteStream to deliver banner advertisements directly using
 * AdMob Mediation.
 */
public class AdMobAdapter implements CustomEventBanner {

    private WebView adView;

    @Override
    public void requestBannerAd(final Context context, final CustomEventBannerListener listener,
                                final String serverParameter, final AdSize size,
                                final MediationAdRequest mediationAdRequest,
                                final Bundle customEventExtras) {
        if(!CommuteStream.isInitialized()) {
            CommuteStream.init(context, serverParameter);
        }
        CommuteStream.setBannerHeight(size.getHeightInPixels(context));
        CommuteStream.setBannerWidth(size.getWidthInPixels(context));
        CommuteStream.requestAd(new AdResponseHandler() {
            @Override
            public void onSuccess(AdResponse response, double requestTime) {
                // if there is something that the server wants us to
                // display we generate a webview for it and pass it
                // on to admob
                if (response.getHtml() != null) {
                    Log.v("CS_SDK", "BANNER REQUEST SUCCEESS, TOOK: " + requestTime + "ms");
                    adView = generateWebView(listener, context,
                            serverParameter, size,
                            mediationAdRequest,
                            response.getHtml(),
                            response.getUrl(),
                            requestTime);
                    listener.onAdLoaded(adView);
                } else if (response.getError() != null) {
                    Log.v("CS_SDK", "Response had an error " + response.getError());
                    listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                } else {
                    Log.v("CS_SDK", "Response had no error or html");
                    listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.v("CS_SDK", "FAILED_FETCH");
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            }
        });
    }

    // does the actual update of the activity
    @SuppressLint("SetJavaScriptEnabled")
    private WebView generateWebView(final CustomEventBannerListener listener,
                                    final Context context,
                                    final String serverParameter,
                                    final AdSize size,
                                    final MediationAdRequest request,
                                    final String html,
                                    final String url,
                                    final double requestTime) {

        Log.v("CS_SDK", "Generating Ad WebView");

        // Set the JS var for the request time, since its done in Java
        final String html2 = html.replace("var loadTimeBanner = null;", "var loadTimeBanner = " + Math.round(requestTime) + ";");

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

        class JsObject {
            @JavascriptInterface
            public void onAdClicked() {
                listener.onAdClicked();
            }
        }

        webView.addJavascriptInterface(new JsObject(), "listener");
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(html2, "text/html", null);

        webView.setLayoutParams(new RelativeLayout.LayoutParams(size.getWidthInPixels(context),
                size.getHeightInPixels(context)));


        // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        return webView;
    }

    @Override
    public void onDestroy() {
        // Do Nothing
    }

    @Override
    public void onResume() {
        // Do nothing
    }

    @Override
    public void onPause() {
        // Do nothing
    }
}

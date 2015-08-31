package com.commutestream.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;


/**
 * CustomEventBanner and AdListener implementations providing the
 * basis for CommuteStream CustomEvent support in AdMob
 * It's this class that gets instantiated by AdMob when it wants
 * a new ad
 */
public class Banner implements CustomEventBanner {

    private WebView adView;

    @Override
    public void requestBannerAd(final CustomEventBannerListener listener,
                                final Activity activity, final String label,
                                final String adUnit, final AdSize adSize,
                                final MediationAdRequest request, final Object customEventExtra) {
        if(!CommuteStream.isInitialized()) {
            CommuteStream.init(activity, adUnit);
        }
        CommuteStream.setBannerHeight(adSize.getHeightInPixels(activity));
        CommuteStream.setBannerWidth(adSize.getWidthInPixels(activity));
        CommuteStream.requestAd(new AdResponseHandler() {
            @Override
            public void onSuccess(AdResponse response) {
                // if there is something that the server wants us to
                // display we generate a webview for it and pass it
                // on to admob
                if (response.getHtml() != null) {
                    adView = generateWebView(listener, activity,
                            label, adUnit, adSize,
                            request, customEventExtra, response.getHtml(), response.getUrl());
                    listener.onReceivedAd(adView);
                } else if (response.getError() != null) {
                    Log.v("CS_SDK", "Response had an error " + response.getError());
                    listener.onFailedToReceiveAd();
                } else {
                    Log.v("CS_SDK", "Response had no error or html");
                    listener.onFailedToReceiveAd();
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.v("CS_SDK", "FAILED_FETCH");
                listener.onFailedToReceiveAd();
            }
        });
    }



    // does the actual update of the activity
    @SuppressLint("SetJavaScriptEnabled")
    private WebView generateWebView(final CustomEventBannerListener listener,
                                    final Activity activity, String label, String serverParameter,
                                    AdSize adSize, MediationAdRequest request,
                                    final Object customEventExtra, String html, final String url) {

        Log.v("CS_SDK", "Generating Ad WebView");
        // create a new webview and put the ad in it
        WebView webView = new WebView(activity);

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

        webView.setLayoutParams(new RelativeLayout.LayoutParams(adSize
                .getWidthInPixels(activity), adSize.getHeightInPixels(activity)));

        // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setOnTouchListener(new OnTouchListener() {

            // handle clicks on our new ad
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //If we are not testing then register all the clicks
                        if (!CommuteStream.getTestingFlag()) {
                            listener.onClick();
                            listener.onPresentScreen();
                            listener.onLeaveApplication();
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                .parse(url));
                        activity.startActivity(intent);
                    }
                } catch (Throwable t) {
                    // Something went wrong, oh well.
                }
                return false;
            }
        });
        return webView;

    }

    @Override
    public void destroy() {
        // Clean up custom event variables.
    }


}

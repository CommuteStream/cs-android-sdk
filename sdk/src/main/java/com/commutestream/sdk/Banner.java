package com.commutestream.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;


/**
 * CustomEventBanner and AdListener implementations providing the
 * basis for CommuteStream CustomEvent support in AdMob
 * It's this class that gets instantiated by AdMob when it wants
 * a new ad
 */
public class Banner implements CustomEventBanner {

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
            public void onSuccess(AdResponse response) {
                // if there is something that the server wants us to
                // display we generate a webview for it and pass it
                // on to admob
                if (response.getHtml() != null) {
                    adView = generateWebView(listener, context,
                            serverParameter, size,
                            mediationAdRequest,
                            response.getHtml(), response.getUrl());
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
                                    final String url) {

        Log.v("CS_SDK", "Generating Ad WebView");
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
        webView.loadData(html, "text/html", null);

        webView.setLayoutParams(new RelativeLayout.LayoutParams(size.getWidthInPixels(context),
                size.getHeightInPixels(context)));

        // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setOnTouchListener(new OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;
                            try {
                                if (!CommuteStream.getTestingFlag()) {
                                    listener.onAdClicked();
                                    listener.onAdLeftApplication();
                                } else {
                                    Log.v("CS_SDK", "TEST AD CLICKED!");
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                        .parse(url));
                                context.startActivity(intent);
                            } catch (Throwable t) {
                                // Something went wrong, oh well.
                            }
                        } else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING)
                            fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;
                }

                return false;
            }

        });
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

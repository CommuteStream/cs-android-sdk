package com.commutestream.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
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

    private DisplayMetrics displayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
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

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(html2, "text/html", null);

        webView.setLayoutParams(new RelativeLayout.LayoutParams(size.getWidthInPixels(context),
                size.getHeightInPixels(context)));


        // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        final DisplayMetrics dispMetrics =displayMetrics(context);

        webView.setOnTouchListener(new OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;
            public final static double MAX_DISTANCE = 0.1;

            private int fingerState = FINGER_RELEASED;
            private double distanceMoved = 0.0;
            private double lastX = 0.0;
            private double lastY = 0.0;

            private double getPhysicalX(MotionEvent motionEvent) {
                return motionEvent.getX()/dispMetrics.xdpi;
            }

            private double getPhysicalY(MotionEvent motionEvent) {
                return motionEvent.getY()/dispMetrics.ydpi;
            }

            private void deltaDistance(MotionEvent motionEvent) {
                double nextX = getPhysicalX(motionEvent);
                double nextY = getPhysicalY(motionEvent);
                double deltaX = nextX - lastX;
                double deltaY = nextY - lastY;
                double distance = Math.sqrt((deltaX * deltaX) + (deltaY*deltaY));
                distanceMoved += distance;
                lastX = nextX;
                lastY = nextY;
            }

            private void maybeClicked() {
                if (distanceMoved < MAX_DISTANCE) {

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
                }
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("CS_SDK", motionEvent.toString());
                Log.d("CS_SDK", "Last Position (" + lastX + ", " + lastY + ")");
                Log.d("CS_SDK", "Last Distance " + distanceMoved);

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED || fingerState == FINGER_UNDEFINED ) {
                            fingerState = FINGER_TOUCHED;
                            distanceMoved = 0.0;
                            lastX = getPhysicalX(motionEvent);
                            lastY = getPhysicalY(motionEvent);
                        } else {
                            fingerState = FINGER_UNDEFINED;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                            deltaDistance(motionEvent);
                            fingerState = FINGER_RELEASED;
                            maybeClicked();
                        } else {
                            fingerState = FINGER_UNDEFINED;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                            fingerState = FINGER_DRAGGING;
                            deltaDistance(motionEvent);
                        }
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;
                }

                Log.d("CS_SDK", "Next Position (" + lastX + ", " + lastY + ")");
                Log.d("CS_SDK", "Next distance " + distanceMoved);

                return true;
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

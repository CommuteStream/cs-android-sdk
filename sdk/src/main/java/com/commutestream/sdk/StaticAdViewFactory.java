package com.commutestream.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class StaticAdViewFactory {
    public static View create(final Context context, final AdEventListener listener,
                              final String html, final String redirectUrl,
                              final double requestTime, final int width, final int height) {

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

        webView.setLayoutParams(new RelativeLayout.LayoutParams(width,
                height));

        // webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        final DisplayMetrics dispMetrics = displayMetrics(context);

        webView.setOnTouchListener(new View.OnTouchListener() {
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
                return motionEvent.getX() / dispMetrics.xdpi;
            }

            private double getPhysicalY(MotionEvent motionEvent) {
                return motionEvent.getY() / dispMetrics.ydpi;
            }

            private void deltaDistance(MotionEvent motionEvent) {
                double nextX = getPhysicalX(motionEvent);
                double nextY = getPhysicalY(motionEvent);
                double deltaX = nextX - lastX;
                double deltaY = nextY - lastY;
                double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                distanceMoved += distance;
                lastX = nextX;
                lastY = nextY;
            }

            private void maybeClicked() {
                if (distanceMoved < MAX_DISTANCE) {

                    try {
                        if (!CommuteStream.getTestingFlag()) {
                            listener.onClicked();
                        } else {
                            Log.v("CS_SDK", "TEST AD CLICKED!");
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                .parse(redirectUrl));
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
                        if (fingerState == FINGER_RELEASED || fingerState == FINGER_UNDEFINED) {
                            fingerState = FINGER_TOUCHED;
                            distanceMoved = 0.0;
                            lastX = getPhysicalX(motionEvent);
                            lastY = getPhysicalY(motionEvent);
                        } else {
                            fingerState = FINGER_UNDEFINED;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
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
                        } else fingerState = FINGER_UNDEFINED;
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

    private static DisplayMetrics displayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}

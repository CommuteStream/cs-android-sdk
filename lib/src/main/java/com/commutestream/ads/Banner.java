package com.commutestream.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Base64;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

//CustomEventBanner and AdListener implementations providing the 
//basis for CommuteStream CustomEvent support in AdMob 
//It's this class that get's instantiated by AdMob when it wants
//a new ad
public class Banner implements CustomEventBanner {

    private WebView adView;
    private String appVersion;
    //private CustomEventBannerListener bannerListener;

    // Called when AdMob requests a CommuteStream Ad
    @Override
    public void requestBannerAd(final CustomEventBannerListener listener,
                                final Activity activity, final String label,
                                final String serverParameter, final AdSize adSize,
                                final MediationAdRequest request, final Object customEventExtra) {

        // Keep the custom event listener for use later.
        //this.bannerListener = listener;

        // There are some things we need to get from the activity and AdMob on
        // the first Banner Request
        if (!CommuteStream.isInitialized()) {

            // Get the versionName of the app using this library
            try {
                appVersion = activity.getPackageManager().getPackageInfo(
                        activity.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            // set the the parameter specified in AdMob
            CommuteStream.setAdUnitUuid(serverParameter);

            CommuteStream.setAppVersion(appVersion);
            CommuteStream.setAppName(activity.getPackageName());

            CommuteStream.setAidSha(getAndroidIDHash(activity, "SHA1"));
            CommuteStream.setAidMd5(getAndroidIDHash(activity, "MD5"));

            // set init so we don't do this stuff again
            CommuteStream.setInitialized(true);
        }

        // set the banner height
        CommuteStream.setBannerHeight(adSize.getHeightInPixels(activity));
        CommuteStream.setBannerWidth(adSize.getWidthInPixels(activity));

        CommuteStream.getClient().getAd(CommuteStream.nextRequest(), new AdResponseHandler() {
            @Override
            public void onSuccess(AdResponse response) {
                // if there is something that the server wants us to
                // display we generate a webview for it and pass it
                // on to admob
                if (response.getHtml() != null) {
                    adView = generateWebView(listener, activity,
                            label, serverParameter, adSize,
                            request, customEventExtra, response.getHtml(), response.getUrl());
                    listener.onReceivedAd(adView);
                } else {
                    listener.onFailedToReceiveAd();
                }
            }

            @Override
            public void onError(Throwable error){
                Log.v("CS_SDK", "FAILED_FETCH");
                listener.onFailedToReceiveAd();
            }
        });
    }

    private String getAndroidIDHash(Activity activity, String hashing) {
        String mmdid = Secure.getString(activity.getContentResolver(),
                Secure.ANDROID_ID);
        String hex;

        if (mmdid == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance(hashing);
            byte[] hashBytes = md.digest(mmdid.getBytes());
            hex = Base64.encodeToString(hashBytes, 0, hashBytes.length, 0);
        } catch (Exception e) {
            return null;
        }
        return hex;
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

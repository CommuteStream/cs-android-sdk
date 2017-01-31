package com.commutestream.sdk.mraid;

import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Provides the internal mraid v2 interface functionality and javascript bridge by defining the
 * WebView client's url handling for commutestream:// urls
 */
public class MRaidBridge {
    private MRaidBridgeListener mMRaidBridgeListener;
    private final WebViewClient mMraidWebViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("CS_SDK", "MRaid WebView Error " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return onRequest(view, url);
        }
    };
    private int mResizeWidth;
    private int mResizeHeight;

    public MRaidBridge(MRaidBridgeListener mRaidBridgeListener) {
        mMRaidBridgeListener = mRaidBridgeListener;
    }

    /**
     * Handle URL requests in thew MRaid web view that begin with the commutestream://
     * and mraid:// schemes, decoding them into Java calls that perform the necessary MRaid actions
     * which in turn may call javascript functions in the WebView with parameters to set response
     * values.
     * @param view
     * @param url
     * @return true if handled
     */
    public boolean onRequest(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if("commutestream".equals(scheme)) {
            if("test".equals(host)) {
                mMRaidBridgeListener.onTest();
                return true;
            }
        }
        return false;
    }

    /**
     * Attach to WebView
     */
    public void attachView(WebView view) {
        view.setWebViewClient(mMraidWebViewClient);
    }
}

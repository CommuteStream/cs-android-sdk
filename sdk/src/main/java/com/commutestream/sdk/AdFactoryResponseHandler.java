package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;

import com.commutestream.sdk.admob.AdMobEventForwarder;
import com.google.android.gms.ads.*;

/**
 * Takes an AdResponse and attempts to create an appropriate AdView from it
 * if possible. The AdHandler class is given either a null view or a generic View
 * depending on Ad availability.
 */
public class AdFactoryResponseHandler implements AdResponseHandler {
    private Context mContext;
    private AdHandler mHandler;
    private AdEventListener mListener;

    AdFactoryResponseHandler(Context context, AdHandler handler, AdEventListener listener) {
        mContext = context;
        mHandler = handler;
        mListener = listener;
    }

    @Override
    public void onSuccess(AdResponse response, double requestTime) {
        // if there is something that the server wants us to
        // display we generate a webview for it and pass it
        // on to admob
        if (response.getHtml() != null) {
            Log.v("CS_SDK", "BANNER REQUEST SUCCEESS, TOOK: " + requestTime + "ms");
            adView = StaticAdViewFactory.create(mContext, mListener, response.getHtml(),
                    response.getUrl(), requestTime, size.getWidthInPixels(mContext),
                    size.getHeightInPixels(context));
            listener.onAdLoaded(adView);
        } else if (response.getError() != null) {
            Log.v("CS_SDK", "Response had an error " + response.getError());
            listener.onAdFailedToLoad(com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST);
        } else {
            Log.v("CS_SDK", "Response had no error or html");
            listener.onAdFailedToLoad(com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL);
        }
        Stat

    }

    @Override
    public void onError(Throwable error) {

    }
}

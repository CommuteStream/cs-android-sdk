package com.commutestream.sdk.mopub;

import android.util.Log;

import com.commutestream.sdk.AdEventListener;
import com.mopub.mobileads.CustomEventBanner;

/**
 * Created by Sam on 1/20/2017.
 */

public class MoPubEventForwarder implements AdEventListener{
    private CustomEventBanner.CustomEventBannerListener mBannerListener;

    public MoPubEventForwarder(CustomEventBanner.CustomEventBannerListener listener) {
        mBannerListener = listener;
    }

    public void onImpressed() { Log.v("CS_SDK", "MoPub onImpressed"); }

    public void onClicked() {
        mBannerListener.onBannerClicked();
    }
}

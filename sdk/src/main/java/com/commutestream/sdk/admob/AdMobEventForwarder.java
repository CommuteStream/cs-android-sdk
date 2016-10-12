package com.commutestream.sdk.admob;

import com.commutestream.sdk.AdEventListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventListener;

/**
 * Forward Ad events to an AdMob CustomeEventListener
 * @see com.commutestream.sdk.AdEventListener
 * @see com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
 */
public class AdMobEventForwarder implements AdEventListener {
    private CustomEventListener listener;

    public AdMobEventForwarder(CustomEventListener listener) {
        this.listener = listener;
    }

    public void onImpressed() { this.listener.onAdOpened(); }

    public void onClicked() {
        this.listener.onAdClicked();
    }
}

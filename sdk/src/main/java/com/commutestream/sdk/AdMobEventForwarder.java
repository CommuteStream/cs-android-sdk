package com.commutestream.sdk;

import com.google.android.gms.ads.mediation.customevent.CustomEventListener;

/**
 * Forward Ad events to an AdMob CustomeEventListener
 * @see com.commutestream.sdk.AdEventListener
 * @see com.google.android.gms.ads.mediation.customevent.CustomEventListener
 */
public class AdMobEventForwarder implements AdEventListener {
    private CustomEventListener listener;

    public AdMobEventForwarder(CustomEventListener listener) {
        this.listener = listener;
    }

    public void onClicked() {
        this.listener.onAdClicked();
    }
}

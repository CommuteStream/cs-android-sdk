package com.commutestream.sdk.admob;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.commutestream.sdk.AdHandler;
import com.commutestream.sdk.AdMetadata;
import com.commutestream.sdk.CommuteStream;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;


/**
 * AdMobBannerAdapter provides an implementation of an AdMob CustomEventBanner
 * allowing CommuteStream to deliver banner advertisements directly using
 * AdMob Mediation.
 * @see com.google.android.gms.ads.mediation.customevent.CustomEventBanner
 */
public class AdMobBannerAdapter implements CustomEventBanner {

    private View adView;

    @Override
    public void requestBannerAd(final Context context, final CustomEventBannerListener listener,
                                final String serverParameter, final AdSize size,
                                final MediationAdRequest mediationAdRequest,
                                final Bundle customEventExtras) {
        if(!CommuteStream.isInitialized()) {
            CommuteStream.init(context, serverParameter);
        }
        AdMobEventForwarder adMobEventForwarder = new AdMobEventForwarder(listener);
        CommuteStream.setBannerHeight(size.getHeightInPixels(context));
        CommuteStream.setBannerWidth(size.getWidthInPixels(context));
        CommuteStream.getAd(context, new AdHandler() {
            @Override
            public void onFound(AdMetadata metadata, final View view) {
                listener.onAdLoaded(view);
            }

            @Override
            public void onNotFound() {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
            }

            @Override
            public void onError(Throwable error) {
                Log.v("CS_SDK", "Ad Error - " + error.getMessage());
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            }
        }, adMobEventForwarder);
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

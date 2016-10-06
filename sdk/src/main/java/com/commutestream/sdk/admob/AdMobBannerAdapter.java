package com.commutestream.sdk.admob;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.commutestream.sdk.AdHandler;
import com.commutestream.sdk.AdResponse;
import com.commutestream.sdk.AdResponseHandler;
import com.commutestream.sdk.CommuteStream;
import com.commutestream.sdk.StaticAdViewFactory;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
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
        CommuteStream.setBannerHeight(size.getHeightInPixels(context));
        CommuteStream.setBannerWidth(size.getWidthInPixels(context));
        CommuteStream.getAd(new AdHandler(View view) {

            @Override
            public void onSuccess(AdResponse response, double requestTime) {

            }

            @Override
            public void onError(Throwable error) {
                Log.v("CS_SDK", "FAILED_FETCH");
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            }
        });
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

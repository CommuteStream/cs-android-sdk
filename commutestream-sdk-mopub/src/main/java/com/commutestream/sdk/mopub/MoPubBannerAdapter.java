package com.commutestream.sdk.mopub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.commutestream.sdk.AdController;
import com.commutestream.sdk.AdHandler;
import com.commutestream.sdk.CommuteStream;
import com.mopub.common.DataKeys;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class MoPubBannerAdapter extends CustomEventBanner {
    @Override
    protected void loadBanner(Context context, final CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {

        Log.v("CS_SDK", "Entered loadBanner");
        Log.v("CS_SDK", serverExtras.toString());
        Log.v("CS_SDK", serverExtras.get("cs_ad_unit_uuid"));

        if(!CommuteStream.isInitialized()) {
            Log.v("CS_SDK", "Not initialized");
            CommuteStream.init(context, serverExtras.get("cs_ad_unit_uuid"));
        }else{
            Log.v("CS_SDK", "Already initialized");
        }

        MoPubEventForwarder moPubEventForwarder = new MoPubEventForwarder(customEventBannerListener);

        int width;
        int height;
        if (localExtrasAreValid(localExtras)) {
            width = (Integer) localExtras.get(DataKeys.AD_WIDTH);
            height = (Integer) localExtras.get(DataKeys.AD_HEIGHT);
        } else {
            customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        CommuteStream.setBannerHeight(height);
        CommuteStream.setBannerWidth(width);
        final MoPubBannerAdapter moPubAdapter = this;
        CommuteStream.getAd(context, new AdHandler() {
            @Override
            public void onFound(final AdController controller) {
                Log.v("CS_SDK", "onFound");
                //moPubAdapter.setAdController(controller);
                customEventBannerListener.onBannerLoaded(controller.getAdView());
            }

            @Override
            public void onNotFound() {
                Log.v("CS_SDK", "onNotFound");
                customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
            }

            @Override
            public void onError(Throwable error) {
                Log.v("CS_SDK", "onError");
                Log.v("CS_SDK", "Ad Error - " + error.getMessage());

                customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
            }
        }, moPubEventForwarder);
    }

    private boolean localExtrasAreValid(@NonNull final Map<String, Object> localExtras) {
        return localExtras.get(DataKeys.AD_WIDTH) instanceof Integer
                && localExtras.get(DataKeys.AD_HEIGHT) instanceof Integer;
    }

    @Override
    protected void onInvalidate() {

    }
}

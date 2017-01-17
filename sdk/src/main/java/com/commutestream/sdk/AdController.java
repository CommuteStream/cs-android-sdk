package com.commutestream.sdk;

import android.util.Log;
import android.view.View;

/**
 * Takes an AdView and deals with the lifecycle of the Ad (impression, click, so forth)
 * sending back information about lifecycle changes to our server.
 */
public class AdController {
    AdMetadata mAdMetadata;
    AdView mAdView;
    VisibilityMonitor mVisibilityMonitor;
    InteractionMonitor mInteractionMonitor;
    UrlHandler mUrlHandler;

    AdController(AdMetadata adMetadata, AdView adView, UrlHandler urlHandler) {
        mAdMetadata = adMetadata;
        mAdView = adView;
        mUrlHandler = urlHandler;

        mVisibilityMonitor = new VisibilityMonitor(mAdView, new VisibilityListener() {
            @Override
            public void onVisible(View view) {
                Log.d("CS_SDK", "Adapter saw visible, sending impression");
                CommuteStream.getClient().sendImpression(mAdMetadata);
            }

            @Override
            public void onHidden(View view) {
                Log.d("CS_SDK", "Adapter saw hidden");
            }
        });

        mInteractionMonitor = new InteractionMonitor(mAdView, new InteractionListener() {
            @Override
            public void onTap(View view) {
                if(mUrlHandler.enable()) {
                    Log.d("CS_SDK", "Adapter saw interaction, sending click");
                    CommuteStream.getClient().sendClick(mAdMetadata);
                }
            }
        });
    }

    public AdView getAdView() {
        return mAdView;
    }

    public void onDestroy() {
        mVisibilityMonitor.stopMonitoring();
    }

    public void onResume() {
        mVisibilityMonitor.startMonitoring();
    }

    public void onPause() {
        mVisibilityMonitor.stopMonitoring();
    }
}

package com.commutestream.sdk;

import android.content.Context;
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
    UrlHandler mUrlHandler;

    AdController(Context context, AdMetadata adMetadata, View view, UrlHandler urlHandler) {
        mAdMetadata = adMetadata;
        mUrlHandler = urlHandler;

        mAdView = new AdView(context, new InteractionListener() {
            @Override
            public void onTap(View view) {
                Log.v("CS_SDK", "onTap AdController");
                if(mUrlHandler.enable()) {
                    Log.d("CS_SDK", "Adapter saw interaction, sending click");
                    CommuteStream.getClient().sendClick(mAdMetadata);
                }
            }
        });

        mAdView.setContentView(view);

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

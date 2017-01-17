package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * AdView holds any other View which may be an advertisement taking note of visibility
 * changes and interactions that may be considered a "click"
 */
public class AdView extends FrameLayout {
    View mContentView;
    VisibilityMonitor mVisibilityMonitor;
    InteractionMonitor mInteractionMonitor;
    AdEventListener mAdEventListener;
    boolean mImpressed = false;
    boolean mClicked = false;

    public AdView(Context context, AdMetadata metadata, AdEventListener adEventListener) {
        super(context);
        init(adEventListener);
        // scale and fit accordingly
        setLayoutParams(new LayoutParams(metadata.viewWidth, metadata.viewHeight));
    }

    public void setContentView(View view) {
        if(mContentView != null) {
            removeView(mContentView);
        }
        mContentView = view;
        addView(mContentView);
    }

    private void init(AdEventListener adEventListener) {
        mAdEventListener = adEventListener;
        final AdView mAdView = this;
        mVisibilityMonitor = new VisibilityMonitor(this, new VisibilityListener() {
            @Override
            public void onVisible(View view) {
                mAdView.impressed();
            }

            @Override
            public void onHidden(View view) {

            }
        });
        mInteractionMonitor = new InteractionMonitor(this, new InteractionListener() {
            @Override
            public void onTap(View view) {
                mAdView.clicked();
            }
        });
    }

    public void onPause() {
    }

    public void onResume() {

    }

    public boolean wasImpressed() {
        return mImpressed;
    }

    public boolean wasClicked() {
        return mClicked;
    }

    private void impressed() {
        if(mImpressed) {
            return;
        }
        Log.v("CS_SDK", "Ad Impressed");
        mImpressed = true;
        mAdEventListener.onImpressed();
    }

    private void clicked() {
        if(mClicked || !mImpressed) {
            return;
        }
        Log.v("CS_SDK", "Ad Clicked");
        mClicked = true;
        mAdEventListener.onClicked();
    }
}

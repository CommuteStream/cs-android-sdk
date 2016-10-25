package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * AdView holds any other View which may be an advertisement taking note of visibility
 * changes and interactions that may be considered a "click"
 */
public class AdView extends FrameLayout implements VisibilityListener, InteractionListener {
    View mContentView;
    VisibilityMonitor mVisibilityMonitor;
    InteractionMonitor mInteractionMonitor;
    AdEventListener mAdEventListener;
    boolean mImpressed = false;
    boolean mClicked = false;

    public AdView(Context context, AdEventListener adEventListener) {
        super(context);
        init(adEventListener);
    }

    public void setContentView(View view) {
        mVisibilityMonitor = new VisibilityMonitor(this, view);
        mInteractionMonitor = new InteractionMonitor(this, view);
        if(mContentView != null) {
            removeView(mContentView);
        }
        mContentView = view;
        addView(mContentView);
    }

    private void init(AdEventListener adEventListener) {
        mAdEventListener = adEventListener;
        mVisibilityMonitor = new VisibilityMonitor(this, this);
        mInteractionMonitor = new InteractionMonitor(this, this);
    }

    public boolean wasImpressed() {
        return mImpressed;
    }

    public boolean wasClicked() {
        return mClicked;
    }

    @Override
    public void onVisible(View view) {
        impressed();
    }

    @Override
    public void onHidden(View view) {
    }

    @Override
    public void onTap(View view) {
        clicked();
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

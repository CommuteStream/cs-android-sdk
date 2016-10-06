package com.commutestream.sdk;

import android.content.Context;
import android.util.AttributeSet;
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

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdView(Context context) {
        super(context);
        init();
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

    private void init() {
        mVisibilityMonitor = new VisibilityMonitor(this, this);
        mInteractionMonitor = new InteractionMonitor(this, this);
    }

    @Override
    public void onVisible(View view) {
        Log.v("CS_SDK", "AdView Impression");

    }

    @Override
    public void onHidden(View view) {

    }

    @Override
    public void onTap(View view) {
        Log.v("CS_SDK", "AdView Click");

    }
}

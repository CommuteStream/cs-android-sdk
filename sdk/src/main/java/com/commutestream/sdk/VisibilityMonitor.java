package com.commutestream.sdk;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Monitor a view for visibility on screen, notifying a VisibilityListener
 * when a View becomes Visible on the device screen.
 */
public class VisibilityMonitor implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener, View.OnLayoutChangeListener {

    private VisibilityListener mListener;
    private View mView;
    private boolean mVisible = false;

    public VisibilityMonitor(VisibilityListener listener, View view) {
        mListener = listener;
        mView = view;
        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mView.getViewTreeObserver().addOnScrollChangedListener(this);
        mView.addOnLayoutChangeListener(this);
        checkVisible();
    }

    private boolean isVisible() {
        Rect globalVisibleRect = new Rect();
        Rect viewVisibleRect = new Rect();
        Point viewOffsetPoint = new Point();
        mView.getRootView().getGlobalVisibleRect(globalVisibleRect);
        mView.getGlobalVisibleRect(viewVisibleRect, viewOffsetPoint);
        Log.d("CS_SDK", "Global Hit Rect: " + globalVisibleRect.toString() + " View Visible Rect: " + viewVisibleRect.toString() + " View Offset Point: " + viewOffsetPoint + " Visibility: " + mView.getVisibility());
        return mView.getVisibility() == View.VISIBLE && Rect.intersects(globalVisibleRect, viewVisibleRect);
    }

    private void checkVisible() {
        boolean visible = isVisible();
        if(mVisible != visible) {
            mVisible = visible;
            if(mVisible) {
                mListener.onVisible(mView);
            } else {
                mListener.onHidden(mView);
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        checkVisible();

    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        checkVisible();
    }

    @Override
    public void onScrollChanged() {
        checkVisible();
    }
}

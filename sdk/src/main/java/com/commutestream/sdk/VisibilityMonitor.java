package com.commutestream.sdk;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Monitor a view for visibility on screen, notifying a VisibilityListener
 * when a View becomes Visible on the device screen.
 */
public class VisibilityMonitor implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener, View.OnLayoutChangeListener {

    private VisibilityListener mListener;
    private View mView;
    private boolean mVisible = false;

    public VisibilityMonitor(View view, VisibilityListener listener) {
        mView = view;
        mListener = listener;
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

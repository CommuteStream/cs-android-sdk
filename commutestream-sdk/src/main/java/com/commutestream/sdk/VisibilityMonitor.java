package com.commutestream.sdk;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Monitor a view for visibility on screen, notifying a VisibilityListener
 * when a View becomes Visible on the device screen.
 */
public class VisibilityMonitor {

    final private VisibilityListener mListener;
    final private View mView;
    private boolean mVisible = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Rect mGlobalVisibleRect = new Rect();
    private Rect mViewVisibleRect = new Rect();
    private Point mViewOffsetPoint = new Point();

    public VisibilityMonitor(final View view, final VisibilityListener listener) {
        mView = view;
        mListener = listener;
        mTimer = new Timer("CS SDK Ad Visibility Monitor", true);
        startMonitoring();
    }

    public void stopMonitoring() {
        mTimerTask.cancel();
        mTimer.purge();
    }

    public void startMonitoring() {
        if(mTimerTask != null) {
            stopMonitoring();
        }
        final VisibilityMonitor monitor = this;
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        monitor.checkVisible();
                    }
                });
            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 1000, 1000);
        checkVisible();
    }

    private boolean isVisible() {
        mView.getRootView().getGlobalVisibleRect(mGlobalVisibleRect);
        mView.getGlobalVisibleRect(mViewVisibleRect, mViewOffsetPoint);
        boolean visible = mView.isShown() && Rect.intersects(mGlobalVisibleRect, mViewVisibleRect);
        return visible;
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
}

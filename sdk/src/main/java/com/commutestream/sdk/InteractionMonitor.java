package com.commutestream.sdk;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Monitor a View for Touch interactions
 */
public class InteractionMonitor implements View.OnTouchListener, GestureDetector.OnGestureListener {

    InteractionListener mListener;
    View mView;
    GestureDetector mGestureDetector;

    public InteractionMonitor(View view, InteractionListener listener) {
        mView = view;
        mListener = listener;
        mGestureDetector = new GestureDetector(view.getContext(), this);
        mView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.v("CS_SDK", "Tap");
        mListener.onTap(mView);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}

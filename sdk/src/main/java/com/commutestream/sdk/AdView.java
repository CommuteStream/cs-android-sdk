package com.commutestream.sdk;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * AdView holds any other View which may be an advertisement taking note of visibility
 * changes and interactions that may be considered a "click"
 */
public class AdView extends RelativeLayout implements GestureDetector.OnGestureListener{
    private final InteractionListener mInteractionListener;
    View mContentView;

    GestureDetector mGestureDetector;

    public AdView(Context context, InteractionListener interactionListener) {
        super(context);

        mGestureDetector = new GestureDetector(context, this);
        mInteractionListener = interactionListener;

        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public void setContentView(View view) {
        if(mContentView != null) {
            removeView(mContentView);
        }
        mContentView = view;
        addView(mContentView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
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
        mInteractionListener.onTap(this);
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

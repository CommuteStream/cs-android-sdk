package com.commutestream.sdk;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class AdGestureDetector extends GestureDetector {
    public final static int FINGER_RELEASED = 0;
    public final static int FINGER_TOUCHED = 1;
    public final static int FINGER_DRAGGING = 2;
    public final static int FINGER_UNDEFINED = 3;
    public final static double MAX_TAP_DISTANCE = 0.1;

    private int fingerState = FINGER_RELEASED;
    private double mDistanceMoved = 0.0;
    private double mLastX = 0.0;
    private double mLastY = 0.0;
    private DisplayMetrics mDisplayMetrics;

    private View mView;

    public AdGestureDetector(Context context, View view) {
        super(context, new SimpleOnGestureListener());
    }

    private double getPhysicalX(MotionEvent motionEvent) {
        return motionEvent.getX() / mDisplayMetrics.xdpi;
    }

    private double getPhysicalY(MotionEvent motionEvent) {
        return motionEvent.getY() / mDisplayMetrics.ydpi;
    }

    private void deltaDistance(MotionEvent motionEvent) {
        double nextX = getPhysicalX(motionEvent);
        double nextY = getPhysicalY(motionEvent);
        double deltaX = nextX - mLastX;
        double deltaY = nextY - mLastY;
        double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        mDistanceMoved += distance;
        mLastX = nextX;
        mLastY = nextY;
    }

    private void maybeClicked() {
        if (mDistanceMoved < MAX_TAP_DISTANCE) {
            Log.d("CS_SDK", "Clicked");
        }
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("CS_SDK", motionEvent.toString());
        Log.d("CS_SDK", "Last Position (" + mLastX + ", " + mLastY + ")");
        Log.d("CS_SDK", "Last Distance " + mDistanceMoved);

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (fingerState == FINGER_RELEASED || fingerState == FINGER_UNDEFINED) {
                    fingerState = FINGER_TOUCHED;
                    mDistanceMoved = 0.0;
                    mLastX = getPhysicalX(motionEvent);
                    mLastY = getPhysicalY(motionEvent);
                } else {
                    fingerState = FINGER_UNDEFINED;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                    deltaDistance(motionEvent);
                    fingerState = FINGER_RELEASED;
                    maybeClicked();
                } else {
                    fingerState = FINGER_UNDEFINED;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                    fingerState = FINGER_DRAGGING;
                    deltaDistance(motionEvent);
                } else fingerState = FINGER_UNDEFINED;
                break;

            default:
                fingerState = FINGER_UNDEFINED;
        }
        return true;
    }
}

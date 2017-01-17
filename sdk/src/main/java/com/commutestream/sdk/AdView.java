package com.commutestream.sdk;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * AdView holds any other View which may be an advertisement taking note of visibility
 * changes and interactions that may be considered a "click"
 */
public class AdView extends RelativeLayout {
    View mContentView;

    public AdView(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public void setContentView(View view) {
        if(mContentView != null) {
            removeView(mContentView);
        }
        mContentView = view;
        addView(mContentView);
    }
}

package com.commutestream.sdk.mraid;

import android.util.Log;

import com.commutestream.sdk.mraid.properties.MRaidExpandProperties;
import com.commutestream.sdk.mraid.properties.MRaidOrientationProperties;
import com.commutestream.sdk.mraid.properties.MRaidResizeProperties;

/**
 * Listener for MRaid calls driven by the MRaidBridge
 */
public class MRaidBridgeListener {
    final static String TAG = "CS_MRAID";
    public void onTest() {
        Log.v(TAG, "MRaid Test Call");
    }

    public void setExpandProperties(MRaidExpandProperties properties) {
        Log.v(TAG, "Set Expand Properties");
    }

    public void setOrientationProperties(MRaidOrientationProperties properties) {
        Log.v(TAG, "Set Orientation Properties");
    }

    public void setResizeProperties(MRaidResizeProperties properties) {
        Log.v(TAG, "Set Resize Properties");
    }

    public void resize() {
        Log.v(TAG, "Resize");
    }

    public void expand() {
        Log.v(TAG, "Expand");
    }

    public void close() {
        Log.v(TAG, "Close");
    }

    public void open(String url) {
        Log.v(TAG, "Open " + url);
    }
}
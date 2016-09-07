package com.commutestream.sdk.mraid;

import android.util.Log;

/**
 * Listener for MRaid calls driven by the MRaidBridge
 */
public class MRaidBridgeListener {
    public void onTest() {
        Log.v("CS_SDK", "MRaid Bridge Test Works");
    }
}
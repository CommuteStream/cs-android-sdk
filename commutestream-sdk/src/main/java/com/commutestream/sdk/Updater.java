package com.commutestream.sdk;

import android.util.Log;

import java.util.TimerTask;

/**
 * Send updates to the server when our AdRequest has changed after a short period of time
 */
public class Updater extends TimerTask {
    @Override
    public void run() {
        Log.v("CS_SDK", "Sending client updates");
        CommuteStream.requestUpdate(new UpdateResponseHandler() {
            @Override
            public void onError(Throwable t) {
                Log.v("CS_SDK", "Update failed, reason: " + t.toString());
            }
        });
    }
}

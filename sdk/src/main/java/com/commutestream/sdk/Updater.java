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
        CommuteStream.requestUpdate(new AdResponseHandler() {
            @Override
            public void onSuccess(AdResponse response, double requestTime) {
                if(response.getError() != null) {
                    Log.v("CS_SDK", "Update response error message: " + response.getError());
                } else {
                    Log.v("CS_SDK", "Update succeeded in " + requestTime + "ms");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.v("CS_SDK", "Update failed, reason: " + t.toString());
            }
        });
    }
}

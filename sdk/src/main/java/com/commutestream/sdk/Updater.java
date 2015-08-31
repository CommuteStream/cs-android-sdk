package com.commutestream.sdk;

import android.util.Log;

import java.util.TimerTask;

/**
 * Send updates to the server when our AdRequest has changed after a short period of time
 */
public class Updater extends TimerTask {
    @Override
    public void run() {
        Log.v("CS_SDK", "TIMER FIRED");
        CommuteStream.requestUpdate(new AdResponseHandler() {
            @Override
            public void onSuccess(AdResponse response) {
                if(response.getError() != null) {
                    Log.v("CS_SDK", "UPDATE ERROR MESSAGE: " + response.getError());
                } else {
                    Log.v("CS_SDK", "UPDATE SUCCEEDED");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.v("CS_SDK", "UPDATE ERROR THROWN: " + t.toString());
            }
        });
    }
}

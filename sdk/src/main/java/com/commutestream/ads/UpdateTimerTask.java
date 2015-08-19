package com.commutestream.ads;

import android.util.Log;

import java.util.TimerTask;

/**
 * Send updates to the server periodically when our AdRequest has changed
 */
public class UpdateTimerTask extends TimerTask {
    @Override
    public void run() {
        Log.v("CS_SDK", "TIMER FIRED");
        AdRequest request = CommuteStream.nextRequest();
        if (request != null) {
            request.skipFetch = true;
            CommuteStream.getClient().getAd(request, new AdResponseHandler() {
                @Override
                public void onSuccess(AdResponse response) {
                    Log.v("CS_SDK", "UPDATE SUCCEEDED");
                }

                @Override
                public void onError(Throwable error) {
                    Log.v("CS_SDK", "UPDATE FAILED");
                }
            });
        }
    }
}

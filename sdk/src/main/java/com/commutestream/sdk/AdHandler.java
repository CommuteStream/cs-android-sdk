package com.commutestream.sdk;

import android.view.View;

public interface AdHandler {
    void onAd(AdMetadata metadata, View view);
    void onError(Throwable error);
}

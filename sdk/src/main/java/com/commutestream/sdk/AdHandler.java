package com.commutestream.sdk;

import android.view.View;

public interface AdHandler {
    void onFound(AdMetadata metadata, View view);
    void onNotFound();
    void onError(Throwable error);
}

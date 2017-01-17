package com.commutestream.sdk;

public interface AdHandler {
    void onFound(AdController controller);
    void onNotFound();
    void onError(Throwable error);
}

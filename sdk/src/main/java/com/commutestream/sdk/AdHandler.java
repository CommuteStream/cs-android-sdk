package com.commutestream.sdk;

public interface AdHandler {
    void onFound(AdMetadata metadata, AdView view);
    void onNotFound();
    void onError(Throwable error);
}

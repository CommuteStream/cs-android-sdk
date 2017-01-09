package com.commutestream.sdk;

/**
 * AdResponseHandler is the required interface for classes wishing to handle
 * responses to Ad Requests.
 */
interface AdResponseHandler {
    void onFound(AdMetadata metadata, byte[] content);
    void onNotFound();
    void onError(Throwable error);
}

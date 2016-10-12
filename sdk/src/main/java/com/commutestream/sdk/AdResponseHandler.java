package com.commutestream.sdk;

/**
 * AdResponseHandler is the required interface for classes wishing to handle
 * responses to Ad Requests.
 */
interface AdResponseHandler {
    void onSuccess(AdMetadata metadata, byte[] content);
    void onError(Throwable error);
}

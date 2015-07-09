package com.commutestream.ads;

/**
 * AdResponseHandler is used by the Client to handle AdRequest Responses.
 */
interface AdResponseHandler {
    void onSuccess(AdResponse response);
    void onError(Throwable error);
}

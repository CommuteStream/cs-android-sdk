package com.commutestream.sdk;

/**
 * Ad Client Interface
 */
interface Client {
    //void postUpdates(UpdateRequest request, UpdateResponseHandler handler);
    void getAd(AdRequest request, AdResponseHandler handler);
    void sendImpression(AdMetadata metadata);
    void sendClick(AdMetadata metadata);
}

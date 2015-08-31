package com.commutestream.sdk;

/**
 * Ad Client Interface
 */
interface Client {
    void getAd(AdRequest request, AdResponseHandler handler);
}

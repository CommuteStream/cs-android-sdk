package com.commutestream.ads;

/**
 * Ad Client Interface
 */
interface Client {
    void getAd(AdRequest request, AdResponseHandler handler);
}

package com.commutestream.sdk;

/**
 * Listen to events provided through out the lifecycle of an Ad
 */
public interface AdEventListener {
    void onImpressed();
    void onClicked();
}

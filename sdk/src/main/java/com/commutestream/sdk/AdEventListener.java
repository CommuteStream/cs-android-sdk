package com.commutestream.sdk;

/**
 * Listen to events provided by an Ad
 */
public interface AdEventListener {
    void onImpressed();
    void onClicked();
}

package com.commutestream.sdk;

import android.view.View;

/**
 * Listen to events provided through out the lifecycle of an Ad
 */
public interface AdEventListener {
    void onImpressed();
    void onClicked();
}

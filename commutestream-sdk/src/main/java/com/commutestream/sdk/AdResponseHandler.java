package com.commutestream.sdk;

import android.os.Handler;
import android.os.Looper;

/**
 * AdResponseHandler is the required interface for classes wishing to handle
 * responses to Ad Requests.
 */
abstract class AdResponseHandler {
    void found(final AdMetadata metadata, final byte[] content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onFound(metadata, content);
            }
        });
    }

    void notFound() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onNotFound();
            }
        });
    }

    void error(final Throwable error) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onNotFound();
            }
        });
    }

    abstract void onFound(AdMetadata metadata, byte[] content);
    abstract void onNotFound();
    abstract void onError(Throwable error);
}

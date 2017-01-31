package com.commutestream.sdk;

import android.content.Context;


class QueuedAdRequest {
    Context context;
    AdRequest request;
    AdHandler handler;
    AdEventListener listener;
}
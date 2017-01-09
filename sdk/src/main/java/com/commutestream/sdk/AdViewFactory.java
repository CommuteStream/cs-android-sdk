package com.commutestream.sdk;

import android.content.Context;
import android.view.View;

/**
 * AdViewFactory builds an ad view based on the ad type and content provided
 */
public class AdViewFactory {

    static public View build(Context context, AdEventListener listener, AdMetadata metadata, byte[] content) throws AdViewFactoryException {
        switch (metadata.kind) {
            case AdKinds.HTML:
                return HtmlAdViewFactory.create(context, listener, metadata, content);
            default:
                throw AdViewFactoryException.UnknownAdTypeException;
        }
    }
}

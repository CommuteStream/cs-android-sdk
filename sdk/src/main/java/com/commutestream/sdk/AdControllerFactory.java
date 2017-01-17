package com.commutestream.sdk;

import android.content.Context;

/**
 * AdControllerFactory builds an ad controller based on the ad type and content provided
 */
public class AdControllerFactory {

    static public AdController build(Context context, AdEventListener listener, AdMetadata metadata, byte[] content) throws AdControllerFactoryException {
        switch (metadata.kind) {
            case AdKinds.HTML:
                return HtmlAdControllerFactory.create(context, listener, metadata, content);
            default:
                throw AdControllerFactoryException.UnknownAdTypeException;
        }
    }
}

package com.commutestream.sdk;

import android.util.Log;

/**
 * AdMetadata  contains the decoded header, or metadata, from all http ad request responses along
 * with a validate method which ensures the contained metadata conforms to expectations.
 */
public class AdMetadata {
    public long requestID = 0;
    public String kind = null;
    public double requestTime = 0.0;
    public int width = 0;
    public int height = 0;
    public String impressionUrl = null;
    public String clickUrl = null;

    public void validate() throws AdMetadataException {
        if(requestID == 0) {
            throw AdMetadataException.InvalidRequestIDException;
        }
        Log.d("CS_SDK", "Kind " + kind);
        if(!(kind.equalsIgnoreCase(AdKinds.HTML) || kind.equalsIgnoreCase(AdKinds.MRAID))) {
            throw AdMetadataException.InvalidKindException;
        }
        if(requestTime <= 0.0) {
            throw AdMetadataException.InvalidRequestTimeException;
        }
        if(width <= 0) {
            throw AdMetadataException.InvalidWidthException;
        }
        if(height <= 0) {
            throw AdMetadataException.InvalidHeightException;
        }
        if(impressionUrl == null) {
            throw AdMetadataException.InvalidImpressionUrlException;
        }
        if(clickUrl == null) {
            throw AdMetadataException.InvalidClickUrlException;
        }
    }
}
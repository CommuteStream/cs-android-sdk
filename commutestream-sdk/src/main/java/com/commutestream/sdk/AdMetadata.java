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
    public int viewWidth = 0;
    public int viewHeight = 0;
    public int adWidth = 0;
    public int adHeight = 0;
    public String impressionUrl = null;
    public String clickUrl = null;

    public void validate() throws AdMetadataException {
        if(requestID == 0) {
            throw AdMetadataException.InvalidRequestIDException;
        }
        Log.d("CS_SDK", "Kind " + kind);

        if(requestTime <= 0.0) {
            throw AdMetadataException.InvalidRequestTimeException;
        }
        if(!(kind.equalsIgnoreCase(AdKinds.HTML) || kind.equalsIgnoreCase(AdKinds.MRAID))) {
            throw AdMetadataException.InvalidAdKindException;
        }
        if(adWidth <= 0) {
            throw AdMetadataException.InvalidAdWidthException;
        }
        if(adHeight <= 0) {
            throw AdMetadataException.InvalidAdHeightException;
        }
        if(viewWidth <= 0) {
            throw AdMetadataException.InvalidViewWidthException;
        }
        if(viewHeight <= 0) {
            throw AdMetadataException.InvalidViewHeightException;
        }
        if(impressionUrl == null) {
            throw AdMetadataException.InvalidImpressionUrlException;
        }
        if(clickUrl == null) {
            throw AdMetadataException.InvalidClickUrlException;
        }
    }
}
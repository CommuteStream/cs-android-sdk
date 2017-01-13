package com.commutestream.sdk;


public class AdMetadataException extends Exception {
    public final static AdMetadataException InvalidRequestIDException = new AdMetadataException("Invalid request id");
    public final static AdMetadataException InvalidRequestTimeException = new AdMetadataException("Invalid request time");
    public final static AdMetadataException InvalidAdKindException = new AdMetadataException("Invalid ad kind");
    public final static AdMetadataException InvalidAdWidthException = new AdMetadataException("Invalid ad width");
    public final static AdMetadataException InvalidAdHeightException = new AdMetadataException("Invalid ad height");
    public final static AdMetadataException InvalidViewWidthException = new AdMetadataException("Invalid view width");
    public final static AdMetadataException InvalidViewHeightException = new AdMetadataException("Invalid view height");
    public final static AdMetadataException InvalidImpressionUrlException = new AdMetadataException("Invalid impression url");
    public final static AdMetadataException InvalidClickUrlException = new AdMetadataException("Invalid click url");
    AdMetadataException(String reason) {
        super(reason);
    }
}

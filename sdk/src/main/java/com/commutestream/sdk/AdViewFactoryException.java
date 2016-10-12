package com.commutestream.sdk;

public class AdViewFactoryException extends Exception {
    public final static AdViewFactoryException UnknownAdTypeException = new AdViewFactoryException("Unknown ad type");

    AdViewFactoryException(String reason) {
        super(reason);
    }
}

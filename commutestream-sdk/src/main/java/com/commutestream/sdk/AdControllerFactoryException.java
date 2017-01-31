package com.commutestream.sdk;

public class AdControllerFactoryException extends Exception {
    public final static AdControllerFactoryException UnknownAdTypeException = new AdControllerFactoryException("Unknown ad type");

    AdControllerFactoryException(String reason) {
        super(reason);
    }
}

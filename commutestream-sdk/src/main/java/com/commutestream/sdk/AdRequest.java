package com.commutestream.sdk;

import android.location.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * AdRequest holds all the parameters for requesting an advertisement
 * <p/>
 * Notably the agency interests is a Set to avoid sending duplicate information within the 15s update
 * time window.
 */
class AdRequest {
    private static boolean testing = false;
    private static String appName;
    private static String appVersion;
    private static String sdkName;
    private static String sdkVersion;
    private static String sessionID;
    private static String aaid;
    private static boolean limitTracking;
    private static String theme;
    private static String adUnitUuid;
    private static int viewHeight = 50;
    private static int viewWidth = 320;

    private String timezone;
    private boolean skipFetch = false;
    private Location location;
    private Set<AgencyInterest> agencyInterests;

    public AdRequest() {
        this.setAgencyInterests(new HashSet<AgencyInterest>());
        TimeZone tz = TimeZone.getDefault();
        this.timezone = tz.getID();
    }

    static void setSessionID(String sessionID) {
        AdRequest.sessionID = sessionID;
    }

    static String getSessionID() {
        return sessionID;
    }

    String getTimezone() { return timezone; }

    static int getViewWidth() {
        return viewWidth;
    }

    static void setViewWidth(int viewWidth) {
        AdRequest.viewWidth = viewWidth;
    }

    static int getViewHeight() {
        return viewHeight;
    }

    static void setViewHeight(int viewHeight) {
        AdRequest.viewHeight = viewHeight;
    }

    static String getAdUnitUuid() {
        return adUnitUuid;
    }

    static void setAdUnitUuid(String adUnitUuid) {
        AdRequest.adUnitUuid = adUnitUuid;
    }

    static String getTheme() {
        return theme;
    }

    static void setTheme(String theme) {
        AdRequest.theme = theme;
    }

    static String getAAID() {
        return aaid;
    }

    static void setAAID(String aaid) {
        AdRequest.aaid = aaid;
    }


    static boolean getLimitTracking() {
        return limitTracking;
    }

    static void setLimitTracking(boolean limitTracking) {
        AdRequest.limitTracking = limitTracking;
    }

    static String getSdkVersion() {
        return sdkVersion;
    }

    static void setSdkVersion(String sdkVersion) {
        AdRequest.sdkVersion = sdkVersion;
    }

    static String getSdkName() {
        return sdkName;
    }

    static void setSdkName(String sdkName) {
        AdRequest.sdkName = sdkName;
    }

    static String getAppVersion() {
        return appVersion;
    }

    static void setAppVersion(String appVersion) {
        AdRequest.appVersion = appVersion;
    }

    static String getAppName() {
        return appName;
    }

    static void setAppName(String appName) {
        AdRequest.appName = appName;
    }

    static boolean isTesting() {
        return testing;
    }

    static void setTesting(boolean testing) {
        AdRequest.testing = testing;
    }

    Set<AgencyInterest> getAgencyInterests() {
        return agencyInterests;
    }

    void setAgencyInterests(Set<AgencyInterest> agencyInterests) {
        this.agencyInterests = agencyInterests;
    }

    Location getLocation() {
        return location;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    boolean isSkipFetch() {
        return skipFetch;
    }

    void setSkipFetch(boolean skipFetch) {
        this.skipFetch = skipFetch;
    }
}

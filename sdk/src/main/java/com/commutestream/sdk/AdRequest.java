package com.commutestream.sdk;

import android.location.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * AdRequest holds all the parameters for requesting an advertisement
 * <p/>
 * Notably the agency interests is a Set to avoid sending duplicate information within the 15s update
 * time window.
 */
public class AdRequest {
    private static boolean testing = false;
    private static String appName;
    private static String appVersion;
    private static String sdkName;
    private static String sdkVersion;
    private static String aidSha;
    private static String aidMd5;
    private static String aaid;
    private static String theme;
    private static String adUnitUuid;
    private static int bannerHeight;
    private static int bannerWidth;

    private boolean skipFetch = false;
    private Location location;

    private Set<AgencyInterest> agencyInterests;

    public AdRequest() {
        this.setAgencyInterests(new HashSet<AgencyInterest>());
    }

    public static int getBannerWidth() {
        return bannerWidth;
    }

    public static void setBannerWidth(int bannerWidth) {
        AdRequest.bannerWidth = bannerWidth;
    }

    public static int getBannerHeight() {
        return bannerHeight;
    }

    public static void setBannerHeight(int bannerHeight) {
        AdRequest.bannerHeight = bannerHeight;
    }

    public static String getAdUnitUuid() {
        return adUnitUuid;
    }

    public static void setAdUnitUuid(String adUnitUuid) {
        AdRequest.adUnitUuid = adUnitUuid;
    }

    public static String getTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        AdRequest.theme = theme;
    }

    public static String getAidMd5() {
        return aidMd5;
    }

    public static void setAidMd5(String aidMd5) {
        AdRequest.aidMd5 = aidMd5;
    }

    public static String getAidSha() {
        return aidSha;
    }

    public static void setAidSha(String aidSha) {
        AdRequest.aidSha = aidSha;
    }

    public static String getAAID() {
        return aaid;
    }

    public static void setAAID(String aaid) {
        AdRequest.aaid = aaid;
    }

    public static String getSdkVersion() {
        return sdkVersion;
    }

    public static void setSdkVersion(String sdkVersion) {
        AdRequest.sdkVersion = sdkVersion;
    }

    public static String getSdkName() {
        return sdkName;
    }

    public static void setSdkName(String sdkName) {
        AdRequest.sdkName = sdkName;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static void setAppVersion(String appVersion) {
        AdRequest.appVersion = appVersion;
    }

    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        AdRequest.appName = appName;
    }

    public static boolean isTesting() {
        return testing;
    }

    public static void setTesting(boolean testing) {
        AdRequest.testing = testing;
    }

    public Set<AgencyInterest> getAgencyInterests() {
        return agencyInterests;
    }

    public void setAgencyInterests(Set<AgencyInterest> agencyInterests) {
        this.agencyInterests = agencyInterests;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isSkipFetch() {
        return skipFetch;
    }

    public void setSkipFetch(boolean skipFetch) {
        this.skipFetch = skipFetch;
    }
}

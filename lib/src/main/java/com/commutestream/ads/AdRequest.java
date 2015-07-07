package com.commutestream.ads;

import android.location.Location;

import java.util.ArrayList;

/**
 * AdRequest holds all the parameters for requesting an advertisement
 */
class AdRequest {
    static boolean testing = false;
    static String appName;
    static String appVersion;
    static String sdkName;
    static String sdkVersion;
    static String aidSha;
    static String aidMd5;
    static String theme;
    static String adUnitUuid;

    boolean skipFetch = false;
    int bannerHeight;
    int bannerWidth;
    Location location;
    ArrayList<AgencyInterest> agencyInterests;
}

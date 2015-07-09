package com.commutestream.ads;

import android.location.Location;

import java.util.LinkedList;
import java.util.Queue;

/**
 * AdRequest holds all the parameters for requesting an advertisement
 */
class AdRequest {
    AdRequest() {
        this.agencyInterests = new LinkedList<AgencyInterest>();
    }

    static boolean testing = false;
    static String appName;
    static String appVersion;
    static String sdkName;
    static String sdkVersion;
    static String aidSha;
    static String aidMd5;
    static String theme;
    static String adUnitUuid;
    static int bannerHeight;
    static int bannerWidth;

    boolean skipFetch = false;
    Location location;
    Queue<AgencyInterest> agencyInterests;
}

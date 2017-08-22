package com.commutestream.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;


/**
 * Utility functions to use against an android context
 */
public class ContextUtils {
    static String getAppName(Context context) {
        return context.getPackageName();
    }

    static String getAppVersion(Context context) {
        String appVersion = "";
        try {
            appVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.v("CS_SDK", "App Version Not Found");
        }
        return appVersion;
    }

    /**
     * Attempt to get the Android Advertising ID, returns null and logs on failure
     * @param context
     * @return aaid
     */
    static String getAAID(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (Exception e) {
            Log.e("CS_SDK", "Failed to get Android Advertising ID", e);
            return null;
        }
    }

    /**
     * Attempt to get the whether the user has limit ad tracking enabled or not,
     * returns null and logs on failure
     * @param context
     * @return limitTracking
     */
    static boolean getLimitTracking(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).
                    isLimitAdTrackingEnabled();
        } catch (Exception e) {
            Log.e("CS_SDK", "Failed to get isLimitAdTrackingEnabled", e);
            return false;
        }
    }
}

package com.commutestream.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

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

    static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
}

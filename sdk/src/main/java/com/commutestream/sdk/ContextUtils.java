package com.commutestream.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

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
}

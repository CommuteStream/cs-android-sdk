package com.commutestreamsdk;

import android.app.Application;
import android.util.Log;

public class MyLibrary extends Application {
	private static String lib_name;
	private static String lib_versionName;
	private static String app_name;
	private static String app_versionName;
	private static boolean initialized;

    public void onCreate(){
    }

	public static String getAppName() {
		return app_name;
	}

	public static void setAppName(String app_name) {
		MyLibrary.app_name = app_name;
	}

	public static String getAppVersionName() {
		return app_versionName;
	}

	public static void setAppVersionName(String app_versionName) {
		MyLibrary.app_versionName = app_versionName;
	}

	public static String getLibVersionName() {
		return lib_versionName;
	}

	public static void setLibVersionName(String lib_versionName) {
		MyLibrary.lib_versionName = lib_versionName;
	}

	public static String getLibName() {
		return lib_name;
	}

	public static void setLibName(String lib_name) {
		MyLibrary.lib_name = lib_name;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void setInitialized(boolean initialized) {
		MyLibrary.initialized = initialized;
	}


}

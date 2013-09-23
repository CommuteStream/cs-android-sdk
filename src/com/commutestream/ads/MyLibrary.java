package com.commutestream.ads;

import java.util.Date;

import com.commutestream.ads.http.RequestParams;

import android.app.Application;
import android.location.Location;

//This application extension is where we store things that 
//should persist for the life of the application. i.e. The 
//CommuteStream class may get destroyed and re-instantiated 
//if it's within a view that gets destroyed and re-created

public class MyLibrary extends Application {
	private static boolean initialized = false;

	static String ad_unit_uuid;
	static String banner_height;
	static String banner_width;
	static String sdk_name;
	static String app_name;
	static String sdk_ver;
	static String app_ver;
	static String agency_id;
	static String stop_id;
	static String route_id;
	static String lat;
	static String lon;
	static String acc;
	static String fix_time;
	static String aid_sha;
	static String aid_md5;
	static String testing;

	static Location location;

	static RequestParams http_params = new RequestParams();

	static Date lastServerRequestTime = new Date();

	public void onCreate() {
	}

	public static String getApp_name() {
		return app_name;
	}

	public static void setApp_name(String app_name) {
		MyLibrary.app_name = app_name;
		http_params.put("app_name", app_name);
	}

	public static String getApp_ver() {
		return MyLibrary.app_ver;
	}

	public static void setApp_ver(String app_ver) {
		MyLibrary.app_ver = app_ver;
		http_params.put("app_ver", app_ver);
	}

	public static String getSdk_name() {
		return sdk_name;
	}

	public static void setSdk_name(String sdk_name) {
		MyLibrary.sdk_name = sdk_name;
	}

	public static String getSdk_ver() {
		return MyLibrary.sdk_ver;
	}

	public static void setSdk_ver(String sdk_ver) {
		MyLibrary.sdk_ver = sdk_ver;
		MyLibrary.http_params.put("sdk_ver", sdk_ver);
	}

	public static String getAid_sha() {
		return MyLibrary.aid_sha;
	}

	public static void setAid_sha(String aid_sha) {
		MyLibrary.aid_sha = aid_sha;
		MyLibrary.http_params.put("aid_sha", aid_sha);
	}

	public static String getAid_md5() {
		return MyLibrary.aid_md5;
	}

	public static void setAid_md5(String aid_md5) {
		MyLibrary.aid_md5 = aid_md5;
		MyLibrary.http_params.put("aid_md5", aid_md5);
	}

	public static String getBanner_height() {
		return MyLibrary.banner_height;
	}

	public static void setBanner_height(String banner_height) {
		MyLibrary.banner_height = banner_height;
		MyLibrary.http_params.put("banner_height", banner_height);
	}

	public static String getBanner_width() {
		return MyLibrary.banner_width;
	}

	public static void setBanner_width(String banner_width) {
		MyLibrary.banner_width = banner_width;
		MyLibrary.http_params.put("banner_width", banner_width);
	}

	public static boolean isInitialized() {
		return MyLibrary.initialized;
	}

	public static void setInitialized(boolean initialized) {
		MyLibrary.initialized = initialized;
	}

	public static String getAd_unit_uuid() {
		return MyLibrary.ad_unit_uuid;
	}

	public static void setAd_unit_uuid(String ad_unit_uuid) {
		MyLibrary.ad_unit_uuid = ad_unit_uuid;
		http_params.put("ad_unit_uuid", ad_unit_uuid);
	}

	public static void setLastServerRequestTime(Date date) {
		MyLibrary.lastServerRequestTime = date;
	}

}

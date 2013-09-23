package com.commutestream.ads;

import java.util.Date;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import com.commutestream.ads.http.JsonHttpResponseHandler;
import com.commutestream.ads.http.RequestParams;

import android.app.Application;
import android.location.Location;
import android.util.Log;

//This application extension is where we store things that 
//should persist for the life of the application. i.e. The 
//CommuteStream class may get destroyed and re-instantiated 
//if it's within a view that gets destroyed and re-created

public class CommuteStream extends Application {
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
	private static Date lastParameterChange = new Date();
	
	private static Timer parameterCheckTimer = new Timer();

	public static void init() {
		Log.v("CS_SDK", "Initializing CommuteStream");

		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send
		// the new parameters to ensure the server has the latest user info
		CommuteStream.parameterCheckTimer.scheduleAtFixedRate(
				new ParameterUpdateCheckTimer(CommuteStream.lastServerRequestTime) {
					@Override
					public void run() {
						Log.v("CS_SDK", "TIMER FIRED");
						if (CommuteStream.isInitialized()
								&& (CommuteStream.lastParameterChange.getTime() > CommuteStream.lastServerRequestTime
										.getTime())) {
							Log.v("CS_SDK", "Updating the server.");

							CommuteStream.http_params.put("skip_fetch", "true");

							RestClient.get("banner", CommuteStream.http_params,
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(
												JSONObject response) {
											CommuteStream.lastServerRequestTime = CommuteStream.lastParameterChange;
											try {
												if (response.has("error")) {
													String error = response
															.getString("error");
													Log.e("CS_SDK",
															"Error from banner server: "
																	+ error);
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}

										@Override
										public void onFailure(Throwable e,
												JSONObject errorResponse) {
											Log.v("CS_SDK", "UPDATE FAILED");
										}
									});
						}
					}
				}, 10000, 10000);
	}

	public static String getApp_name() {
		return CommuteStream.app_name;
	}

	public static void setApp_name(String app_name) {
		CommuteStream.app_name = app_name;
		CommuteStream.http_params.put("app_name", app_name);
	}

	public static String getApp_ver() {
		return CommuteStream.app_ver;
	}

	public static void setApp_ver(String app_ver) {
		CommuteStream.app_ver = app_ver;
		http_params.put("app_ver", app_ver);
	}

	public static String getSdk_name() {
		return CommuteStream.sdk_name;
	}

	public static void setSdk_name(String sdk_name) {
		CommuteStream.sdk_name = sdk_name;
	}

	public static String getSdk_ver() {
		return CommuteStream.sdk_ver;
	}

	public static void setSdk_ver(String sdk_ver) {
		CommuteStream.sdk_ver = sdk_ver;
		CommuteStream.http_params.put("sdk_ver", sdk_ver);
	}

	public static String getAid_sha() {
		return CommuteStream.aid_sha;
	}

	public static void setAid_sha(String aid_sha) {
		CommuteStream.aid_sha = aid_sha;
		CommuteStream.http_params.put("aid_sha", aid_sha);
	}

	public static String getAid_md5() {
		return CommuteStream.aid_md5;
	}

	public static void setAid_md5(String aid_md5) {
		CommuteStream.aid_md5 = aid_md5;
		CommuteStream.http_params.put("aid_md5", aid_md5);
	}

	public static String getBanner_height() {
		return CommuteStream.banner_height;
	}

	public static void setBanner_height(String banner_height) {
		CommuteStream.banner_height = banner_height;
		CommuteStream.http_params.put("banner_height", banner_height);
	}

	public static String getBanner_width() {
		return CommuteStream.banner_width;
	}

	public static void setBanner_width(String banner_width) {
		CommuteStream.banner_width = banner_width;
		CommuteStream.http_params.put("banner_width", banner_width);
	}

	public static boolean isInitialized() {
		return CommuteStream.initialized;
	}

	public static void setInitialized(boolean initialized) {
		CommuteStream.initialized = initialized;
	}

	public static String getAd_unit_uuid() {
		return CommuteStream.ad_unit_uuid;
	}

	public static void setAd_unit_uuid(String ad_unit_uuid) {
		CommuteStream.ad_unit_uuid = ad_unit_uuid;
		CommuteStream.http_params.put("ad_unit_uuid", ad_unit_uuid);
	}
	
	//App Interface
	

	public static void setAgency_id(String agency_id) {
		Log.v("CS_SDK", "Agency changed to: " + agency_id);
		CommuteStream.agency_id = agency_id;
		CommuteStream.http_params.put("agency_id", agency_id);
		CommuteStream.parameterChange();
	}

	public static String getAgency_id() {
		return CommuteStream.agency_id;
	}
	

	public static void setStop_id(String stop_id) {
		CommuteStream.stop_id = stop_id;
		CommuteStream.http_params.put("stop_id", stop_id);
		CommuteStream.parameterChange();
	}

	public static String getStop_id() {
		return CommuteStream.stop_id;
	}

	public static void setRoute_id(String route_id) {
		CommuteStream.route_id = route_id;
		CommuteStream.http_params.put("route_id", route_id);
		CommuteStream.parameterChange();
	}

	public static String getRoute_id() {
		return CommuteStream.route_id;
	}
	

	public static String getLocation() {
		return CommuteStream.lat;
	}

	public static void setLocation(Location location) {
		CommuteStream.location = location;
		CommuteStream.lat = Double.toString(location.getLatitude());
		CommuteStream.lon = Double.toString(location.getLongitude());
		CommuteStream.acc = Double.toString(location.getAccuracy());
		CommuteStream.fix_time = Long.toString(location.getTime());
		CommuteStream.http_params.put("lat", CommuteStream.lat);
		CommuteStream.http_params.put("lon", CommuteStream.lon);
		CommuteStream.http_params.put("acc", CommuteStream.acc);
		CommuteStream.http_params.put("fix_time", CommuteStream.fix_time);
		CommuteStream.parameterChange();
	}
	
	public static String getTesting() {
		return CommuteStream.testing;
	}

	public static void setTesting(String testing) {
		CommuteStream.testing = testing;
		CommuteStream.http_params.put("testing", testing);
	}
	
	

	public static void setLastServerRequestTime(Date date) {
		CommuteStream.lastServerRequestTime = date;
	}

	public static void parameterChange() {
		CommuteStream.lastParameterChange = new Date();
	}
}

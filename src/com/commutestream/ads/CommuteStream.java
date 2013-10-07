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

	static String agency_interest = "";

	static String aid_sha;
	static String aid_md5;
	static String testing;

	static Location location;

	static RequestParams http_params = new RequestParams();

	static Date lastServerRequestTime = new Date();
	private static Date lastParameterChange = new Date();

	private static Timer parameterCheckTimer = new Timer();

	public static void init() {
		Log.v("CS_SDK", "init()");

		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send
		// the new parameters to ensure the server has the latest user info
		CommuteStream.parameterCheckTimer.scheduleAtFixedRate(
				new ParameterUpdateCheckTimer(
						CommuteStream.lastServerRequestTime) {
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
											CommuteStream.reportSuccessfulGet();
											//CommuteStream.lastServerRequestTime = CommuteStream.lastParameterChange;
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
				}, 20000, 20000);
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

	// App Interface

	// This should be called by the app whenever tracking times for a given
	// route are displayed to a user
	public static void trackingDisplayed(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("TRACKING_DISPLAYED", agency_id, route_id, stop_id);
	}

	public static void alertDisplayed(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("ALERT_DISPLAYED", agency_id, route_id, stop_id);
	}

	public static void mapDisplayed(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("MAP_DISPLAYED", agency_id, route_id, stop_id);
	}

	public static void favoriteAdded(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("FAVORITE_ADDED", agency_id, route_id, stop_id);
	}

	public static void tripPlanningPointA(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("TRIP_PLANNING_POINT_A", agency_id, route_id,
				stop_id);
	}

	public static void tripPlanningPointB(String agency_id, String route_id,
			String stop_id) {
		setAgency_interest("TRIP_PLANNING_POINT_B", agency_id, route_id,
				stop_id);
	}

	private static void setAgency_interest(String type, String agency_id,
			String route_id, String stop_id) {
		// ad a comma if needed
		if (CommuteStream.agency_interest.length() > 0) {
			CommuteStream.agency_interest += ',';
		}

		CommuteStream.agency_interest += type + "," + agency_id + ","
				+ route_id + "," + stop_id;
		CommuteStream.http_params.put("agency_interest",
				CommuteStream.agency_interest);
		CommuteStream.parameterChange();
	}

	public static void setLocation(Location location) {
		// TODO - check accuracy of location and don't send locations that are
		// not better than the last
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

	public static void setTesting() {
		CommuteStream.testing = "true";
		CommuteStream.http_params.put("testing", testing);
	}

	public static void reportSuccessfulGet() {
		CommuteStream.lastServerRequestTime = new Date();

		// clear parameters that should only be sent once
		CommuteStream.http_params.remove("lat");
		CommuteStream.http_params.remove("lon");
		CommuteStream.http_params.remove("acc");
		CommuteStream.http_params.remove("fix_time");
		CommuteStream.http_params.remove("agency_interest");

		CommuteStream.agency_interest = "";

	}

	public static void parameterChange() {
		CommuteStream.lastParameterChange = new Date();
	}

	public static void parametersSent() {
		CommuteStream.lastParameterChange = new Date();
	}
}

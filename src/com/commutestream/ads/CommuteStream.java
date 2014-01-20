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

	private static String ad_unit_uuid;
	private static String banner_height;
	private static String banner_width;
	private static String sdk_name = "com.commutestreamsdk";
	private static String sdk_ver = "0.1.1";
	private static String app_name;
	private static String app_ver;
	private static String lat;
	private static String lon;
	private static String acc;
	private static String fix_time;

	private static Location currentBestLocation;

	private static String api_url = "https://api.commutestream.com:3000/";

	private static String agency_interest = "";

	private static String aid_sha;
	private static String aid_md5;
	private static Boolean testing = false;

	private static RequestParams http_params = new RequestParams();

	private static Date lastServerRequestTime = new Date();
	private static Date lastParameterChange = new Date();

	private static Timer parameterCheckTimer = new Timer();
	private static ParameterUpdateCheckTimer parameterCheckTimerTask = new ParameterUpdateCheckTimer(
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
							public void onSuccess(JSONObject response) {
								CommuteStream.reportSuccessfulGet();
								// CommuteStream.lastServerRequestTime
								// =
								// CommuteStream.lastParameterChange;
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
	};

	public static void init() {
		Log.v("CS_SDK", "init()");

		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send
		// the new parameters to ensure the server has the latest user info
		try {
			CommuteStream.parameterCheckTimer.scheduleAtFixedRate(CommuteStream.parameterCheckTimerTask, 20000, 20000);
			Log.v("CS_SDK", "Timer (Re)started");
		} catch(Exception e) {
			Log.v("CS_SDK", "Already Initialized");
		}

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

	public static String getApi_url() {
		return CommuteStream.api_url;
	}

	public static void setApi_url(String api_url) {
		CommuteStream.api_url = api_url;
	}


	public static void setTheme(String theme) {
		http_params.put("theme", theme);
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

	public static void setSkip_fetch(String skip_fetch) {
		CommuteStream.http_params.put("skip_fetch", skip_fetch);
	}

	public static RequestParams getHttp_params() {
		return CommuteStream.http_params;
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

	public static void setLocation(Location location) {
		// We check that the new location is a better one before sending it
		if (isBetterLocation(location, CommuteStream.currentBestLocation)) {
			CommuteStream.currentBestLocation = location;
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
	}

	public static Boolean getTesting() {
		return CommuteStream.testing;
	}

	public static void setTesting() {
		CommuteStream.testing = true;
		CommuteStream.http_params.put("testing", "true");
		Log.v("CS_SDK", "Testing Mode Set");
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

	// Location helper stuff
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}

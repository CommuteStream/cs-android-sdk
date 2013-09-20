package com.commutestreamsdk;

import java.util.Date;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import com.commutestreamsdk.http.JsonHttpResponseHandler;
import com.commutestreamsdk.http.RequestParams;

import android.location.Location;
import android.util.Log;

public class CommuteStream {
	
	private Date lastParameterChange;
	private Timer parameterCheckTimer = new Timer();

	public CommuteStream() {
		Log.v("CS_SDK", "IN THE CustomAdParmeters Constructor");


		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send
		// the new parameters to ensure the server has the latest user info
		this.parameterCheckTimer.scheduleAtFixedRate(
				new ParameterUpdateCheckTimer(MyLibrary.lastServerRequestTime) {
					@Override
					public void run() {
						//Log.v("CS_SDK", "parameterCheckTimer FIRED");

						if (lastParameterChange.getTime() > MyLibrary.lastServerRequestTime
								.getTime()) {
							Log.v("CS_SDK", "Updating the server.");

							MyLibrary.http_params.put("skip_fetch", "true");

							RestClient.get("banner", MyLibrary.http_params,
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(
												JSONObject response) {
											MyLibrary.lastServerRequestTime = lastParameterChange;
											try {
												if(response.has("error")){
													String error = response.getString("error");
													Log.e("CS_SDK", "Error from banner server: " + error);
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch block
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

	public void setAgency_id(String agency_id) {
		Log.v("CS_SDK", "Agency changed to: " + agency_id);
		MyLibrary.agency_id = agency_id;
		MyLibrary.http_params.put("agency_id", agency_id);
		this.parameterChange();
	}

	public String getAgency_id() {
		return MyLibrary.agency_id;
	}

	public void setStop_id(String stop_id) {
		MyLibrary.stop_id = stop_id;
		MyLibrary.http_params.put("stop_id", stop_id);
		this.parameterChange();
	}

	public String getStop_id() {
		return MyLibrary.stop_id;
	}

	public void setRoute_id(String route_id) {
		MyLibrary.route_id = route_id;
		MyLibrary.http_params.put("route_id", route_id);
		this.parameterChange();
	}

	public String getRoute_id() {
		return MyLibrary.route_id;
	}

	public Date getLastParameterChangeTime() {
		return this.lastParameterChange;
	}

	public void parameterChange() {
		this.lastParameterChange = new Date();
	}

	public RequestParams getHttpParams() {
		return MyLibrary.http_params;
	}


	public String getLocation() {
		return MyLibrary.lat;
	}

	public void setLocation(Location location) {
		MyLibrary.location = location;
		MyLibrary.lat = Double.toString(location.getLatitude());
		MyLibrary.lon = Double.toString(location.getLongitude());
		MyLibrary.acc = Double.toString(location.getAccuracy());
		MyLibrary.fix_time = Long.toString(location.getTime());
		MyLibrary.http_params.put("lat", MyLibrary.lat);
		MyLibrary.http_params.put("lon", MyLibrary.lon);
		MyLibrary.http_params.put("acc", MyLibrary.acc);
		MyLibrary.http_params.put("fix_time", MyLibrary.fix_time);
		this.parameterChange();
	}



	public String getTesting() {
		return MyLibrary.testing;
	}

	public void setTesting(String testing) {
		MyLibrary.testing = testing;
		MyLibrary.http_params.put("testing", testing);
	}

}

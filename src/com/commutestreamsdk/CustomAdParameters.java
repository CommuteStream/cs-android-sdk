package com.commutestreamsdk;

import java.util.Date;

import com.commutestreamsdk.http.RequestParams;

import android.util.Log;

public class CustomAdParameters {
	private String agency_id;
	private String stop_id;
	private String route_id;
	private Date lastParameterChange;
	
	RequestParams http_params = new RequestParams();
	
	public void setAgency(String agency_id) {
		Log.v("CS_SDK", "Agency changed to: " + agency_id);
		this.agency_id = agency_id;
		http_params.put("agency_id", agency_id);
		this.parameterChange();
	}

	public String getAgency() {
		return agency_id;
	}

	public void setStop(String stop_id) {
		this.stop_id = stop_id;
		http_params.put("stop_id", stop_id);
		this.parameterChange();
	}

	public String getStop() {
		return stop_id;
	}

	public void setRoute(String route_id) {
		this.route_id = route_id;
		http_params.put("route_id", route_id);
		this.parameterChange();
	}

	public String getRoute() {
		return route_id;
	}

	public Date getLastParameterChangeTime() {
		return this.lastParameterChange;
	}

	public void parameterChange() {
		this.lastParameterChange = new Date();
	}

	public RequestParams getHttpParams() {
		return http_params;
	}

}

package com.commutestreamsdk;

import java.util.Date;

import com.commutestreamsdk.http.RequestParams;

import android.util.Log;

public class CustomAdParameters {
	private String advertiser_id;
	private String banner_height;
	private String banner_width;
	private String sdk_ver;
	private String host_app_ver;
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

	public String getAdvertiser_id() {
		return advertiser_id;
	}

	public void setAdvertiser_id(String advertiser_id) {
		this.advertiser_id = advertiser_id;
		http_params.put("advertiser_id", advertiser_id);
		this.parameterChange();
	}

	public String getBanner_height() {
		return banner_height;
	}

	public void setBanner_height(String banner_height) {
		this.banner_height = banner_height;
		http_params.put("banner_height", banner_height);
		this.parameterChange();
	}

	public String getBanner_width() {
		return banner_width;
	}

	public void setBanner_width(String banner_width) {
		this.banner_width = banner_width;
		http_params.put("banner_width", banner_width);
		this.parameterChange();
	}

	public String getHost_app_ver() {
		return host_app_ver;
	}

	public void setHost_app_ver(String host_app_ver) {
		this.host_app_ver = host_app_ver;
		http_params.put("host_app_ver", host_app_ver);
		this.parameterChange();
	}

	public String getSdk_ver() {
		return sdk_ver;
	}

	public void setSdk_ver(String sdk_ver) {
		this.sdk_ver = sdk_ver;
		http_params.put("sdk_ver", sdk_ver);
		this.parameterChange();
	}

}

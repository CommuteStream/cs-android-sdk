package com.commutestreamsdk;

import java.util.Date;

import android.util.Log;

public class CustomAdParameters {
	private String agency;
	private String stop;
	private String route;
	private Date lastParameterChange;

	public void setAgency(String agency) {
		Log.v("CS_SDK", "Agency changed to: " + agency);
		this.agency = agency;
		this.parameterChange();
	}

	public String getAgency() {
		return agency;
	}

	public void setStop(String stop) {
		this.stop = stop;
		this.parameterChange();
	}

	public String getStop() {
		return stop;
	}

	public void setRoute(String route) {
		this.route = route;
		this.parameterChange();
	}

	public String getRoute() {
		return route;
	}

	public Date getLastParameterChangeTime() {
		return this.lastParameterChange;
	}

	public void parameterChange() {
		this.lastParameterChange = new Date();
	}

}

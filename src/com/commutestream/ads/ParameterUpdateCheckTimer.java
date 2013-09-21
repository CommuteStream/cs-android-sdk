package com.commutestream.ads;

import java.util.Date;
import java.util.TimerTask;

//extended to keep track of last intermittent server updates
public class ParameterUpdateCheckTimer extends TimerTask {

	private final Date lastServerUpdateTime;

	ParameterUpdateCheckTimer(Date date) {
		this.lastServerUpdateTime = date;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public Date getLastServerUpdateTime() {
		return lastServerUpdateTime;
	}

}

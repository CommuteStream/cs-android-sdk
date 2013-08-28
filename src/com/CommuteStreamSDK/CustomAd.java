package com.CommuteStreamSDK;

import java.util.Date;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

//CustomEventBanner and AdListener implementations providing the 
//basis for CommuteStream CustomEvent support in AdMob 
public class CustomAd implements CustomEventBanner, AdListener {

	private CustomEventBannerListener bannerListener;
	private AdView adView;
	private Timer parameterCheckTimer = new Timer();
	private Date lastServerRequestTime = new Date();

	// Called when AdMob requests a CommuteStream Ad
	@Override
	public void requestBannerAd(final CustomEventBannerListener listener,
			final Activity activity, String label, String serverParameter,
			AdSize adSize, MediationAdRequest request,
			final Object customEventExtra) {
			
		Log.v("CS_SDK", "Start of requestBannerAd");

		//Location needs to: 
		//(1) be added to custom parameters so updates are tracked
		//(2) untracked and simply pulled from request.getLocation
		//(3) somehow put a watch on changes to request.setLocation
		
		// if location is present in the ad request
		if (request.getLocation() != null) {
			Log.v("CS_SDK", "LAT: " + request.getLocation().getLatitude());
		}
		Log.v("CS_SDK", ((CustomAdParameters) customEventExtra).getAgency());
		
		//TODO - how to get and load data, use API class? error handling?
		//function that takes a activity and customAdParams and returns a webview?
		WebView webView = new WebView(activity);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.loadUrl("http://thedailydont.com");
		this.lastServerRequestTime = new Date();
		webView.setLayoutParams(new RelativeLayout.LayoutParams(adSize
				.getWidthInPixels(activity), adSize.getHeightInPixels(activity)));
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					listener.onClick();
					listener.onPresentScreen();
					listener.onLeaveApplication();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=com.labpixies.flood"));
					activity.startActivity(intent);
				} catch (Throwable t) {
					// Something went wrong, oh well.
				}
				return false;
			}
		});
		
		listener.onReceivedAd(webView);
		
		//TODO - Need to add onFailedToReceiveAd call on ad failure


		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send 
		// the new parameters to ensure the server has the latest user info
		this.parameterCheckTimer.scheduleAtFixedRate(
				new ParameterUpdateCheckTimer(this.lastServerRequestTime) {
					@Override
					public void run() {
						Log.v("CS_SDK", "parameterCheckTimer FIRED");

						Date lastParameterChangeTime = ((CustomAdParameters) customEventExtra)
								.getLastParameterChangeTime();

						if (lastParameterChangeTime.getTime() > lastServerRequestTime
								.getTime()) {
							Log.v("CS_SDK", "UPDATE THE SERVER!!!!");
							// fake server update
							lastServerRequestTime = lastParameterChangeTime;
						}
					}
				}, 2000, 2000);
		
		Log.v("CS_SDK", "End of requestBannerAd");
	}

	@Override
	public void destroy() {
		// Clean up custom event variables.
		this.parameterCheckTimer.cancel();
		Log.v("CS_SDK", "parameterCheckTimer CANCELED");
	}

	@Override
	public void onDismissScreen(Ad ad) {
		this.bannerListener.onDismissScreen();
	}

	@Override
	public void onFailedToReceiveAd(Ad ad, ErrorCode errorCode) {
		this.bannerListener.onFailedToReceiveAd();
	}

	@Override
	public void onLeaveApplication(Ad ad) {
		this.bannerListener.onLeaveApplication();
	}

	@Override
	public void onPresentScreen(Ad ad) {
		this.bannerListener.onPresentScreen();
	}

	@Override
	public void onReceiveAd(Ad ad) {
		this.bannerListener.onReceivedAd(this.adView);
	}
}

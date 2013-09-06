package com.commutestreamsdk;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.content.pm.PackageManager.NameNotFoundException;

import com.commutestreamsdk.http.JsonHttpResponseHandler;
import com.commutestreamsdk.http.RequestParams;
import com.commutestreamsdk.R;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

//CustomEventBanner and AdListener implementations providing the 
//basis for CommuteStream CustomEvent support in AdMob 
public class CustomAd implements CustomEventBanner, AdListener {

	private CustomEventBannerListener bannerListener;
	private WebView adView;
	private Timer parameterCheckTimer = new Timer();
	private Date lastServerRequestTime = new Date();
	private RequestParams params;
	private String app_version;
	private String sdk_version;

	// Called when AdMob requests a CommuteStream Ad
	@Override
	public void requestBannerAd(final CustomEventBannerListener listener,
			final Activity activity, final String label,
			final String serverParameter, final AdSize adSize,
			final MediationAdRequest request, final Object customEventExtra) {

		// typecast the customEventExtra object
		CustomAdParameters customAdParameters = ((CustomAdParameters) customEventExtra);

		// Keep the custom event listener for use later.
		this.bannerListener = listener;

		//Here we get and save some more persistent variables that we try to keep
		//around for the life of the app, since this class is reinstantiatied
		//with each ad retrieval by admob
		if (!MyLibrary.isInitialized()) {

			Log.v("CS_SDK", "Initializing Parameters");

			// Get the versionName of the app using this library
			try {
				app_version = activity.getPackageManager().getPackageInfo(
						activity.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			sdk_version = activity.getString(R.string.app_versionName);

			// set the the parameter specified in AdMob
			customAdParameters.setAdvertiser_id(serverParameter);
			customAdParameters.setBanner_height(Integer.toString(adSize
					.getHeightInPixels(activity)));
			customAdParameters.setBanner_width(Integer.toString(adSize
					.getWidthInPixels(activity)));

			customAdParameters.setHost_app_ver(app_version);
			customAdParameters.setSdk_ver(sdk_version);

			customAdParameters.setAid_sha(getAndroidIDHash(activity, "SHA1"));
			customAdParameters.setAid_md5(getAndroidIDHash(activity, "MD5"));

			// set App/Lib version info in MyLibrary
			MyLibrary.setLibName("com.commutestreamsdk");
			MyLibrary.setLibVersionName(sdk_version);
			MyLibrary.setAppName(activity.getPackageName());
			MyLibrary.setAppVersionName(app_version);
			
			//set init so we don't do this stuff again
			MyLibrary.setInitialized(true);
		}

		// get parameters
		params = customAdParameters.getHttpParams();
		params.put("requesting_item", "true");

		// attempt to "fetch" an item from the server
		RestClient.get("fetch", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject response) {
				try {
					// String fetch_id = response.getString("fetch_id");
					String html = response.getString("html");
					String url = response.getString("url");
					Boolean display_item = response.getBoolean("display_item");

					// if there is something that the server wants us to display
					// we generate a webview for it and pass it on to admob
					if (display_item) {
						adView = generateWebView(listener, activity, label,
								serverParameter, adSize, request,
								customEventExtra, html, url);
						listener.onReceivedAd(adView);
					} else {
						listener.onFailedToReceiveAd();
					}

				} catch (JSONException e) {
					e.printStackTrace();
					listener.onFailedToReceiveAd();
				}
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				Log.v("CS_SDK", "FETCH FAILED");
				listener.onFailedToReceiveAd();
			}
		});

		// Every few seconds we should check to see if the parameters have been
		// updated since the last request to the server. If so we should send
		// the new parameters to ensure the server has the latest user info
		this.parameterCheckTimer.scheduleAtFixedRate(
				new ParameterUpdateCheckTimer(this.lastServerRequestTime) {
					@Override
					public void run() {
						// Log.v("CS_SDK", "parameterCheckTimer FIRED");

						// check when the parameters were last updated
						final Date lastParameterChangeTime = ((CustomAdParameters) customEventExtra)
								.getLastParameterChangeTime();

						if (lastParameterChangeTime.getTime() > lastServerRequestTime
								.getTime()) {
							Log.v("CS_SDK", "Updating the server.");

							// get the parameters
							params = ((CustomAdParameters) customEventExtra)
									.getHttpParams();
							params.put("requesting_item", "false");

							RestClient.get("fetch", params,
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(
												JSONObject response) {
											lastServerRequestTime = lastParameterChangeTime;
										}

										@Override
										public void onFailure(Throwable e,
												JSONObject errorResponse) {
											Log.v("CS_SDK", "UPDATE FAILED");
										}
									});
						}
					}
				}, 2000, 2000);

		// Log.v("CS_SDK", "End of requestBannerAd");
	}

	private String getAndroidIDHash(Activity activity, String hashing) {
		String mmdid = Secure.getString(activity.getContentResolver(),
				Secure.ANDROID_ID);
		String hex;

		if (mmdid == null)
			return null;

		try {
			MessageDigest md = MessageDigest.getInstance(hashing);
			byte[] hashBytes = md.digest(mmdid.getBytes());
			hex = Base64.encodeToString(hashBytes, 0, hashBytes.length,
					0);

		} catch (Exception e) {
			return null;
		}
		return hex;
	}

	// does the actual update of the activity
	private WebView generateWebView(final CustomEventBannerListener listener,
			final Activity activity, String label, String serverParameter,
			AdSize adSize, MediationAdRequest request,
			final Object customEventExtra, String html, final String url) {

		// create a new webview and put the ad in it
		WebView webView = new WebView(activity);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.loadData(html, "text/html", null);
		this.lastServerRequestTime = new Date();
		webView.setLayoutParams(new RelativeLayout.LayoutParams(adSize
				.getWidthInPixels(activity), adSize.getHeightInPixels(activity)));
		webView.setOnTouchListener(new OnTouchListener() {

			// handle clicks on our new ad
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					// TODO reenable to record admob clicks
					// listener.onClick();
					// listener.onPresentScreen();
					// listener.onLeaveApplication();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					activity.startActivity(intent);
				} catch (Throwable t) {
					// Something went wrong, oh well.
				}
				return false;
			}
		});
		return webView;

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

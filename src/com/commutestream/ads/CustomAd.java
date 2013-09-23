package com.commutestream.ads;

import java.security.MessageDigest;
import java.util.Date;
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

import com.commutestream.ads.http.JsonHttpResponseHandler;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

//CustomEventBanner and AdListener implementations providing the 
//basis for CommuteStream CustomEvent support in AdMob 
//It's this class that get's instantiated by AdMob when it wants
//a new ad
public class CustomAd implements CustomEventBanner, AdListener {

	private CustomEventBannerListener bannerListener;
	private WebView adView;
	// private Timer parameterCheckTimer = new Timer();
	// private Date lastServerRequestTime = new Date();
	private String app_version;

	// We cannot package resources with the jar file and therefor
	// cannot read the version from the xml
	private static final String SDK_VERSION = "0.0.2";

	// Called when AdMob requests a CommuteStream Ad
	@Override
	public void requestBannerAd(final CustomEventBannerListener listener,
			final Activity activity, final String label,
			final String serverParameter, final AdSize adSize,
			final MediationAdRequest request, final Object customEventExtra) {

		// Keep the custom event listener for use later.
		this.bannerListener = listener;

		// There are some things we need to get from the activity and AdMob on
		// the first Banner Request
		if (!CommuteStream.isInitialized()) {

			 Log.v("CS_SDK", "Initializing CommuteStream");

			// Get the versionName of the app using this library
			try {
				app_version = activity.getPackageManager().getPackageInfo(
						activity.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			// set the the parameter specified in AdMob
			CommuteStream.setAd_unit_uuid(serverParameter);

			CommuteStream.setApp_ver(app_version);
			CommuteStream.setSdk_ver(SDK_VERSION);
			CommuteStream.setSdk_name("com.commutestreamsdk");
			CommuteStream.setApp_name(activity.getPackageName());

			CommuteStream.setAid_sha(getAndroidIDHash(activity, "SHA1"));
			CommuteStream.setAid_md5(getAndroidIDHash(activity, "MD5"));

			// set init so we don't do this stuff again
			CommuteStream.setInitialized(true);
		}

		// set the banner height
		CommuteStream.setBanner_height(Integer.toString(adSize
				.getHeightInPixels(activity)));
		CommuteStream.setBanner_width(Integer.toString(adSize
				.getWidthInPixels(activity)));

		CommuteStream.http_params.put("skip_fetch", "false");

		// attempt to get a "banner" from the server
		RestClient.get("banner", CommuteStream.http_params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONObject response) {
						try {
							// String banner_request_uuid =
							// response.getString("banner_request_uuid");
							String html = response.getString("html");
							String url = response.getString("url");
							Boolean item_returned = response
									.getBoolean("item_returned");

							if(response.has("error")){
								String error = response.getString("error");
								Log.e("CS_SDK", "Error from banner server: " + error);
							}
							
							// if there is something that the server wants us to
							// display we generate a webview for it and pass it
							// on to admob
							if (item_returned) {
								adView = generateWebView(listener, activity,
										label, serverParameter, adSize,
										request, customEventExtra, html, url);
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
			hex = Base64.encodeToString(hashBytes, 0, hashBytes.length, 0);

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

		// update the time of the banner request
		CommuteStream.setLastServerRequestTime(new Date());

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

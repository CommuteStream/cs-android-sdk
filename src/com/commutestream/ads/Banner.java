package com.commutestream.ads;

import java.security.MessageDigest;
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
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
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
public class Banner implements CustomEventBanner, AdListener {

	private CustomEventBannerListener bannerListener;
	private WebView adView;
	private String app_version;

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

		CommuteStream.setSkip_fetch("false");

		// attempt to get a "banner" from the server
		RestClient.get("banner", CommuteStream.getHttp_params(),
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONObject response) {
						try {

							// update the time of the banner request
							CommuteStream.reportSuccessfulGet();

							String html = response.getString("html");
							String url = response.getString("url");
							Boolean item_returned = response
									.getBoolean("item_returned");

							if (response.has("error")) {
								String error = response.getString("error");
								Log.e("CS_SDK", "Error from banner server: "
										+ error);
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

		Log.v("CS_SDK", "Generating Ad WebView");
		// create a new webview and put the ad in it
		WebView webView = new WebView(activity);

		// This block allows JS console messages to be transported to LogCat
		webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.v("CS_SDK_WebView",
						cm.message() + " -- From line " + cm.lineNumber());
				return true;
			}
		});

		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadData(html, "text/html", null);

		webView.setLayoutParams(new RelativeLayout.LayoutParams(adSize
				.getWidthInPixels(activity), adSize.getHeightInPixels(activity)));

		// webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setOnTouchListener(new OnTouchListener() {

			// handle clicks on our new ad
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						//If we are not testing then register all the clicks
						if(!CommuteStream.getTesting()){
							listener.onClick();
							listener.onPresentScreen();
							listener.onLeaveApplication();
						}
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse(url));
						activity.startActivity(intent);
					}
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

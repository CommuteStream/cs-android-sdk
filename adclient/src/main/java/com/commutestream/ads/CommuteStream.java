package com.commutestream.ads;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

//This application extension is where we store things that 
//should persist for the life of the application. i.e. The 
//CommuteStream class may get destroyed and re-instantiated 
//if it's within a view that gets destroyed and re-created

public class CommuteStream extends Application {
    private static boolean initialized = false;
    private static HttpClient httpClient;
    private static AdRequest request = new AdRequest();
    private static Date swapped = new Date();
    private static Date modified = new Date();

    private static Timer updateTimer = new Timer();
    private static UpdateTimerTask updateTimerTask = new UpdateTimerTask();

    public static void init() {
        Log.v("CS_SDK", "init()");

        // Every few seconds we should check to see if the parameters have been
        // updated since the last request to the server. If so we should send
        // the new parameters to ensure the server has the latest user info
        try {
            updateTimer.scheduleAtFixedRate(updateTimerTask, 20000, 20000);
            Log.v("CS_SDK", "Timer (Re)started");
        } catch (Exception e) {
            Log.v("CS_SDK", "Already Initialized");
        }

    }

    /**
     * Get the CommuteStream API Singleton Client
     * @return client
     */
    public static synchronized Client getClient() {
        //TODO use a config setting to determine which client to use
        return getHttpClient();
    }

    /**
     * Get the CommuteStream API Singleton HTTP Client
     * @return httpClient
     */
    static HttpClient getHttpClient() {
        if(httpClient == null) {
            httpClient = new HttpClient();
        }
        return httpClient;
    }

    /**
     * Set the CommuteStream API URL to use with the HTTP client
     * @param baseURL
     */
    public static void setBaseURL(String baseURL) {
        getHttpClient().setBaseURL(baseURL);
    }

    /**
     * Checks if the CommuteStream SDK has been initialized
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return CommuteStream.initialized;
    }

    /**
     * Get the ad unit UUID
     * @return adUnitUuid
     */
    public static synchronized String getAdUnitUuid() {
        return CommuteStream.request.adUnitUuid;
    }

    /**
     * Set the ad unit UUID
     * @param adUnitUuid
     */
    public static synchronized void setAdUnitUuid(String adUnitUuid) {
        CommuteStream.request.adUnitUuid = adUnitUuid;
    }

    /**
     * Get the applicatio name
     * @return appName
     */
    public static synchronized String getAppName() {
        return CommuteStream.request.appName;
    }

    /**
     * Set the application name
     * @param appName
     */
    public static synchronized void setAppName(String appName) {
        CommuteStream.request.appName = appName;
    }

    /**
     * Get the application version
     * @return appVersion
     */
    public static synchronized String getAppVersion() {
        return CommuteStream.request.appVersion;
    }

    /**
     * Set the application name
     * @param appVersion
     */
    public static synchronized void setAppVersion(String appVersion) {
        CommuteStream.request.appVersion = appVersion;
    }

    /**
     * Get the SDK Name
     * @return sdkName
     */
    public static synchronized String getSdkName() {
        return CommuteStream.request.sdkName;
    }

    /**
     * Get the SDK Version string
     * @return sdkVersion
     */
    public static synchronized String getSdkVersion() {
        return CommuteStream.request.sdkVersion;
    }

    /**
     * Set the theme to use (dark or light) for ad templates
     * @param theme
     */
    public static synchronized void setTheme(String theme) {
        CommuteStream.request.theme = theme;
    }

    /**
     * Get the android device ID SHA hash
     * @return aidSha
     */
    public static synchronized String getAidSha() {
        return CommuteStream.request.aidSha;
    }

    /**
     * Set the android device ID SHA hash
     * @param aidSha
     */
    public static synchronized void setAidSha(String aidSha) {
        CommuteStream.request.aidSha = aidSha;
    }

    /**
     * Get the android device ID MD5 hash sum value
     * @return aidMd5
     */
    public static synchronized String getAidMd5() {
        return CommuteStream.request.aidMd5;
    }

    /**
     * Set the android device ID MD5 hash sum value
     * @param aidMd5
     */
    public static synchronized void setAidMd5(String aidMd5) {
        CommuteStream.request.aidMd5 = aidMd5;
    }

    /**
     * Get the banner height
     * @return bannerHeight
     */
    public static synchronized int getBannerHeight() {
        return CommuteStream.request.bannerHeight;
    }

    /**
     * Set the banner height
     * @param bannerHeight
     */
    public static synchronized void setBannerHeight(int bannerHeight) {
        CommuteStream.request.bannerHeight = bannerHeight;
    }

    /**
     * Get the banner width
     * @return bannerWidth
     */
    public static synchronized int getBannerWidth() {
        return CommuteStream.request.bannerWidth;
    }

    /**
     * Set the banner width
     * @param bannerWidth
     */
    public static synchronized void setBannerWidth(int bannerWidth) {
        CommuteStream.request.bannerWidth = bannerWidth;
    }

    /**
     * Return the next AdRequest to perform,
     * The return may be null if no modifications have occurs since the last swap.
     * @return request
     */
    public static synchronized AdRequest nextRequest() {
        if(isInitialized() && CommuteStream.modified.compareTo(CommuteStream.swapped) > 0) {
            AdRequest nextRequest = CommuteStream.request;
            CommuteStream.request = new AdRequest();
            CommuteStream.swapped = new Date();
            return nextRequest;
        }
        return null;
    }

    /**
     * Tell CommuteStream that transit tracking information has been displayed
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void trackingDisplayed(String agencyID, String routeID,
                                         String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a transit alert has been displayed
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void alertDisplayed(String agencyID, String routeID,
                                      String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.ALERT_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a transit map has been displayed
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void mapDisplayed(String agencyID, String routeID,
                                    String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.MAP_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a favorite transit stop/route has been displayed
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void favoriteAdded(String agencyID, String routeID,
                                     String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a begin trip planning point has been used
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void tripPlanningPointA(String agencyID, String routeID,
                                          String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_A, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a end trip planning point has been used
     * @param agencyID
     * @param routeID
     * @param stopID
     */
    public static void tripPlanningPointB(String agencyID, String routeID,
                                          String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_B, agencyID, routeID, stopID));
    }

    /**
     * Sets the current device location
     * @param location
     */
    public static synchronized void setLocation(Location location) {
        if (isBetterLocation(location, CommuteStream.request.location)) {
            CommuteStream.request.location = location;
            CommuteStream.updateModified();
        }
    }

    /**
     * Get the testing flag
     * @return testing
     */
    public static synchronized Boolean getTestingFlag() {
        return CommuteStream.request.testing;
    }

    /**
     * Set the testing flag
     */
    public static synchronized void setTestingFlag(Boolean testing) {
        CommuteStream.request.testing = testing;
        Log.v("CS_SDK", "Testing Mode Set");
    }

    /**
     * Set the initialized flag
     * @param initialized
     */
    static void setInitialized(boolean initialized) {
        CommuteStream.initialized = initialized;
    }

    /**
     * Adds an agency interest to a queue of agency interests that gets sent and cleared on
     * each request made.
     * @param agencyInterest
     */
    private static synchronized void addAgencyInterest(AgencyInterest agencyInterest) {
        CommuteStream.request.agencyInterests.add(agencyInterest);
        CommuteStream.updateModified();
    }

    /**
     * Update request timestamp to now
     */
    private static void updateSwapped() {
        CommuteStream.swapped = new Date();
    }

    /**
     * Update parameter change timestamp to now
     */
    private static void updateModified() {
        CommuteStream.modified = new Date();
    }

    // Location helper stuff
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new
     *                            one
     */
    private static boolean isBetterLocation(Location location,
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

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}

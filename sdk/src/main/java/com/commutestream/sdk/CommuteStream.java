package com.commutestream.sdk;

import android.content.Context;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This application extension is where we store things that
 * should persist for the life of the application. i.e. The
 * CommuteStream class may get destroyed and re-instantiated
 * if it's within a view that gets destroyed and re-created
 */
public class CommuteStream {
    private static int requestsBeforeInit = 0;
    private static final String version = "0.6.0";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static boolean initialized = false;
    private static HttpClient httpClient;
    private static AdRequest request = new AdRequest();
    private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private static Runnable updater = new Updater();
    private static ScheduledFuture scheduledUpdate;

    /**
     * Initialize CommuteStream
     *
     * @deprecated Use init(Context context, String adunit) instead
     */
    public static void init() {
    }

    /**
     * Initialize CommuteStream using an Android Context and AdUnit
     *
     * @param context android context
     * @param adUnit ad unit uuid
     */
    public synchronized static void init(Context context, String adUnit) {
        CommuteStream.setSdkVersion(CommuteStream.version);
        CommuteStream.setAppName(ContextUtils.getAppName(context));
        CommuteStream.setAppVersion(ContextUtils.getAppVersion(context));
        CommuteStream.setAid(ContextUtils.getAndroidID(context));
        CommuteStream.setAdUnitUuid(adUnit);
        if (!isInitialized()) {
            CommuteStream.setInitialized(true);
        }
    }

    /**
     * Set the CommuteStream API URL to use with the HTTP client
     *
     * @param baseURL HTTP client base URL
     */
    public synchronized static void setBaseURL(String baseURL) {
        httpClient = new HttpClient(baseURL);
    }

    /**
     * Get the CommuteStream API URL currently being used
     *
     * @return base URL for HTTP client
     */
    public synchronized static String getBaseURL() {
        return getHttpClient().getBaseURL();
    }

    /**
     * Checks if the CommuteStream SDK has been initialized
     *
     * @return true if initialized, false otherwise
     */
    public synchronized static boolean isInitialized() {
        return CommuteStream.initialized;
    }

    /**
     * Set the initialized flag
     *
     * @param initialized
     */
    private static void setInitialized(boolean initialized) {
        CommuteStream.initialized = initialized;
        scheduleUpdate();
    }

    /**
     * Get the ad unit UUID
     *
     * @return adUnitUuid
     */
    public static synchronized String getAdUnitUuid() {
        return CommuteStream.request.getAdUnitUuid();
    }

    /**
     * Set the ad unit UUID
     *
     * @param adUnitUuid
     */
    public static synchronized void setAdUnitUuid(String adUnitUuid) {
        CommuteStream.request.setAdUnitUuid(adUnitUuid);
    }

    /**
     * Get the application name
     *
     * @return appName
     */
    public static synchronized String getAppName() {
        return CommuteStream.request.getAppName();
    }

    /**
     * Set the application name
     *
     * @param appName
     */
    public static synchronized void setAppName(String appName) {
        CommuteStream.request.setAppName(appName);
    }

    /**
     * Get the application version
     *
     * @return appVersion
     */
    public static synchronized String getAppVersion() {
        return CommuteStream.request.getAppVersion();
    }

    /**
     * Set the application name
     *
     * @param appVersion
     */
    public static synchronized void setAppVersion(String appVersion) {
        CommuteStream.request.setAppVersion(appVersion);
    }

    /**
     * Get the SDK Name
     *
     * @return sdkName
     */
    public static synchronized String getSdkName() {
        return CommuteStream.request.getSdkName();
    }

    /**
     * Get the SDK Version string
     *
     * @param version
     */
    private static synchronized void setSdkVersion(String version) {
        CommuteStream.request.setSdkVersion(version);
    }

    /**
     * Set the theme to use (dark or light) for ad templates
     *
     * @param theme
     */
    public static synchronized void setTheme(String theme) {
        CommuteStream.request.setTheme(theme);
    }

    /**
     * Set the android device ID
     *
     * @param aid Android ID
     */
    public static synchronized void setAid(String aid) {
        CommuteStream.request.setAidSha(CommuteStream.hashString("SHA", aid));
        CommuteStream.request.setAidMd5(CommuteStream.hashString("MD5", aid));
        Log.v("CS_SDK", "Set AndroidID SHA to " + getAidSha());
    }

    /**
     * Get the android device ID SHA hash
     *
     * @return aidSha Base64 encoded SHA of the Android ID
     */
    public static synchronized String getAidSha() {
        return CommuteStream.request.getAidSha();
    }

    /**
     * Get the android device ID MD5 hash sum value
     *
     * @return aidMd5 Base64 encoded MD5 of the Android ID
     */
    public static synchronized String getAidMd5() {
        return CommuteStream.request.getAidMd5();
    }

    /**
     * Get the banner height
     *
     * @return bannerHeight Banner height
     */
    public static synchronized int getBannerHeight() {
        return CommuteStream.request.getBannerHeight();
    }

    /**
     * Set the banner height
     *
     * @param bannerHeight Banner Height
     */
    public static synchronized void setBannerHeight(int bannerHeight) {
        CommuteStream.request.setBannerHeight(bannerHeight);
    }

    /**
     * Get the banner width
     *
     * @return bannerWidth Banner Width
     */
    public static synchronized int getBannerWidth() {
        return CommuteStream.request.getBannerWidth();
    }

    /**
     * Set the banner width
     *
     * @param bannerWidth Banner Width
     */
    public static synchronized void setBannerWidth(int bannerWidth) {
        CommuteStream.request.setBannerWidth(bannerWidth);
    }

    /**
     * Tell CommuteStream that transit tracking information has been displayed
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void trackingDisplayed(String agencyID, String routeID,
                                         String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a transit alert has been displayed
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void alertDisplayed(String agencyID, String routeID,
                                      String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.ALERT_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a transit map has been displayed
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void mapDisplayed(String agencyID, String routeID,
                                    String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.MAP_DISPLAYED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a favorite transit stop/route has been displayed
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void favoriteAdded(String agencyID, String routeID,
                                     String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a begin trip planning point has been used
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void tripPlanningPointA(String agencyID, String routeID,
                                          String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_A, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream that a end trip planning point has been used
     *
     * @param agencyID GTFS/CommuteStream Transit Agency ID
     * @param routeID GTFS/CommuteStream Transit Route ID
     * @param stopID GTFS/CommuteStream Transit Stop ID
     */
    public static void tripPlanningPointB(String agencyID, String routeID,
                                          String stopID) {
        CommuteStream.addAgencyInterest(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_B, agencyID, routeID, stopID));
    }

    /**
     * Tell CommuteStream about a device location
     *
     * @param location Location of the device
     */
    public static synchronized void setLocation(Location location) {
        if (isBetterLocation(location, CommuteStream.request.getLocation())) {
            CommuteStream.getRequest().setLocation(location);
            CommuteStream.scheduleUpdate();
        }
    }

    /**
     * Get the current best location set for the request
     *
     * @return current best location set
     */
    public static synchronized Location getLocation() {
        return CommuteStream.getRequest().getLocation();
    }

    /**
     * Get the testing flag
     *
     * @return testing Testing mode flag
     */
    public static synchronized Boolean getTestingFlag() {
        return CommuteStream.request.isTesting();
    }

    /**
     * Set the testing flag
     *
     * When set to true only test Ads are shown and no monetary exchange happens.
     *
     * @param testing Testing mode flag
     */
    public static synchronized void setTestingFlag(Boolean testing) {
        CommuteStream.request.setTesting(testing);
        Log.v("CS_SDK", "Testing Flag Set to " + Boolean.toString(testing));
    }

    /**
     * Adds an agency interest to a queue of agency interests that gets sent and cleared on
     * each request made.
     *
     * @param agencyInterest The agency interest to add to the next update request
     */
    public static synchronized void addAgencyInterest(AgencyInterest agencyInterest) {
        CommuteStream.request.getAgencyInterests().add(agencyInterest);
        CommuteStream.scheduleUpdate();
    }

    /**
     * Schedule an update request to be made in the near future.
     *
     * This lets us bundle updates rather than sending individual requests for each one
     */
    private synchronized static void scheduleUpdate() {
        Log.v("CS_SDK", "Schedule Update");

        if(!isInitialized()) {
            requestsBeforeInit += 1;
        }

        if(getTestingFlag() && !isInitialized() && requestsBeforeInit > 4) {
            Log.e("CS_SDK", "SDK has pending requests but is not yet initialized, possible AdMob Mediation or Proguard Rule Issue!\n" +
                    "See documentation at https://commutestream.com/sdkinstructions regarding Proguard rules and Admob Mediation Settings");
        }

        // if an update is already pending or the sdk is not initialized fully yet then don't bother
        // scheduling the timer.
        if (hasUpdatesPending() || !isInitialized()) {
            return;
        }

        // After 15 seconds from the first update made we send a request to the server
        // containing all the client updates
        scheduledUpdate = scheduler.schedule(updater, 15, TimeUnit.SECONDS);
        Log.v("CS_SDK", "Scheduled Update");
    }

    /**
     * Get the CommuteStream API Singleton Client
     *
     * @return client
     */
    static synchronized Client getClient() {
        return getHttpClient();
    }

    /**
     * Get the CommuteStream API Singleton HTTP Client
     *
     * @return httpClient
     */
    static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new HttpClient();
        }
        return httpClient;
    }

    /**
     * Cancel a scheduled update, done whenever a request is obtained
     */
    private static synchronized void cancelUpdate() {
        Log.v("CS_SDK", "Cancelling Update");

        if(!hasUpdatesPending()) {
            return;
        }

        // Every few seconds we should check to see if the parameters have been
        // updated since the last request to the server. If so we should send
        // the new parameters to ensure the server has the latest user info
        scheduledUpdate.cancel(false);
        Log.v("CS_SDK", "Update cancelled");
    }

    /**
     * Check if CommuteStream has pending updates scheduled
     *
     * @return true if there are scheduled pending updates
     */
    public static synchronized boolean hasUpdatesPending() {
        return (scheduledUpdate != null && !scheduledUpdate.isDone());
    }

    /**
     * Request an ad
     * @param handler response handler
     */
    static void requestAd(AdResponseHandler handler) {
        getClient().getAd(nextRequest(true), handler);
    }

    /**
     * Request update
     * @param handler response handler
     */
    static void requestUpdate(AdResponseHandler handler) {
        AdRequest request = nextRequest(false);
        request.setSkipFetch(true);
        getClient().getAd(request, handler);
    }

    /**
     * Next AdRequest to perform
     *
     * This clears the updates and update timer
     *
     * @return request Ad Request containing updates and information pertaining to this device
     */
    public static synchronized AdRequest nextRequest(boolean cancelUpdate) {
        cancelUpdate();
        AdRequest next = CommuteStream.request;
        CommuteStream.request = new AdRequest();
        return next;
    }

    /**
     * Get the AdRequest
     *
     * @return request containing updates and information that will be sent to CommuteStream upon
     * the next request done either by update or banner request
     */
    public static synchronized AdRequest getRequest() {
        return CommuteStream.request;
    }

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

    private static String hashString(String algorithm, String other) {
        String hashed;
        if (other == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = md.digest(other.getBytes());
            hashed = Base64.encodeToString(hashBytes, 0, hashBytes.length, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
        return hashed;
    }
}

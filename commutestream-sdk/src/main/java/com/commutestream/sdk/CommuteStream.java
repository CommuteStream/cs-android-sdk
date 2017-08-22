package com.commutestream.sdk;

import android.content.Context;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;

/**
 * This application extension is where we store things that
 * should persist for the life of the application. i.e. The
 * CommuteStream class may get destroyed and re-instantiated
 * if it's within a view that gets destroyed and re-created
 */
public class CommuteStream {

    private static boolean initialized = false;
    private static int requestsBeforeInit = 0;
    private static final String version = BuildConfig.VERSION_NAME;
    private static HttpClient httpClient;
    private static AdRequest request = new AdRequest();
    private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private static Runnable updater = new Updater();
    private static ScheduledFuture scheduledUpdate;
    private static QueuedAdRequest pendingRequest;

    /**
     * Initialize CommuteStream using an Android Context and AdUnit
     *
     * @param context android context
     * @param adUnit ad unit uuid
     */
    public synchronized static void init(Context context, String adUnit) {
        CommuteStream.setSessionID(generateSessionID());
        CommuteStream.setSdkVersion(CommuteStream.version);
        CommuteStream.setAppName(ContextUtils.getAppName(context));
        CommuteStream.setAppVersion(ContextUtils.getAppVersion(context));
        CommuteStream.setAdUnitUuid(adUnit);
        CommuteStream.lookupAAID(context);
    }

    /**
     * Set the CommuteStream API URL to use with the HTTP client
     *
     * @param baseURL HTTP client base URL
     */
    public synchronized static void setBaseURL(String baseURL) {
        HttpUrl url = HttpUrl.parse(baseURL);
        httpClient = new HttpClient(url);
    }

    /**
     * Get the CommuteStream API URL currently being used
     *
     * @return base URL for HTTP client
     */
    public synchronized static String getBaseURL() {
        return getHttpClient().getBaseURL().toString();
    }

    /**
     * Checks if the CommuteStream SDK has been initialized
     *
     * @return true if initialized, false otherwise
     */
    public synchronized static boolean isInitialized() {
        return initialized;
    }

    /**
     * Set the initialized flag to true
     */
    public synchronized static void setInitialized() {
        initialized = true;
        doPending();
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
     * Get the AAID from an Android Context and assign it to our singleton. This can only
     * happen on another thread so we simply spawn one up. This might fail for a wide variety
     * of reasons.
     */
    public static void lookupAAID(final Context context) {
        Thread aaidRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                CommuteStream.setAAID(ContextUtils.getAAID(context));
                CommuteStream.setLimitTracking(ContextUtils.getLimitTracking(context));
                CommuteStream.setInitialized();
            }
        });
        aaidRunner.start();
    }

    /**
     * Set the AAID
     *
     * @param aaid
     */
    public static synchronized void setAAID(String aaid) {
        CommuteStream.request.setAAID(aaid);
        if(isInitialized()) {
            doPending();
        }
    }

    /**
     * Get the assigned android advertising ID
     *
     * @return aaid Android Advertising ID
     */
    public static synchronized String getAAID() { return CommuteStream.request.getAAID(); }

    /**
     * Set the AAID
     *
     * @param limitTracking
     */
    public static synchronized void setLimitTracking(boolean limitTracking) {
        CommuteStream.request.setLimitTracking(limitTracking);
    }

    /**
     * Get if tracking is limited
     */
    public static synchronized boolean getLimitTracking() { return CommuteStream.request.getLimitTracking(); }

    /**
     * Get the banner height
     *
     * @return bannerHeight Banner height
     */
    public static synchronized int getBannerHeight() {
        return CommuteStream.request.getViewHeight();
    }

    /**
     * Set the banner height
     *
     * @param bannerHeight Banner Height
     */
    public static synchronized void setBannerHeight(int bannerHeight) {
        CommuteStream.request.setViewHeight(bannerHeight);
    }

    /**
     * Get the banner width
     *
     * @return bannerWidth Banner Width
     */
    public static synchronized int getBannerWidth() {
        return CommuteStream.request.getViewWidth();
    }

    /**
     * Set the banner width
     *
     * @param bannerWidth Banner Width
     */
    public static synchronized void setBannerWidth(int bannerWidth) {
        CommuteStream.request.setViewWidth(bannerWidth);
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
        CommuteStream.getRequest().setLocation(location);
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
        int seconds = 15;
        scheduledUpdate = scheduler.schedule(updater, seconds, TimeUnit.SECONDS);
        Log.v("CS_SDK", "Scheduled sending an update in " + seconds + " seconds");
    }

    private synchronized static void setSessionID(String sessionID) {
        CommuteStream.request.setSessionID(sessionID);
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
     * Get an Ad for display
     *
     * @param handler response handler
     */
    public static synchronized void getAd(final Context context, final AdHandler handler, final AdEventListener listener) {

        //try grabbing a new location to send - if we have one set it for delivery
        Location newLocation = DeviceLocation.getBestLocation(context);
        if(newLocation != null){
            setLocation(newLocation);
        }

        if(isInitialized()) {
            clearPending();
            doGetAd(context, nextRequest(true), handler, listener);
        } else {
            setPending(context, nextRequest(true), handler, listener);
        }
    }

    private static void setPending(final Context context, final AdRequest request, final AdHandler handler, final AdEventListener listener) {
        if(pendingRequest != null) {
            clearPending();
        }
        pendingRequest = new QueuedAdRequest();
        pendingRequest.context = context;
        pendingRequest.request = request;
        pendingRequest.handler = handler;
        pendingRequest.listener = listener;
    }

    private static void doPending() {
        if(pendingRequest != null) {
            doGetAd(pendingRequest.context, pendingRequest.request, pendingRequest.handler, pendingRequest.listener);
            pendingRequest = null;
        }
    }

    private static void clearPending() {
        if(pendingRequest != null) {
            pendingRequest.handler.onNotFound();
            pendingRequest = null;
        }
    }

    private static void doGetAd(final Context context, final AdRequest request, final AdHandler handler, final AdEventListener listener) {
        getClient().getAd(request, new AdResponseHandler() {
            @Override
            public void onFound(AdMetadata metadata, byte[] content) {
                try {
                    AdController controller = AdControllerFactory.build(context, listener, metadata, content);
                    handler.onFound(controller);
                } catch (Throwable error) {
                    handler.onError(error);
                }
            }

            @Override
            public void onNotFound() {
                handler.onNotFound();
            }

            @Override
            public void onError(Throwable error) {
                handler.onError(error);
            }
        });
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
     * @param handler update response handler
     */
    static void requestUpdate(final UpdateResponseHandler handler) {
        AdRequest request = nextRequest(false);
        request.setSkipFetch(true);
        getClient().getAd(request, new AdResponseHandler() {
            @Override
            void onFound(AdMetadata metadata, byte[] content) {
                // unexpected!
                Log.e("CS_SDK", "Unexpectedly saw an Ad Found response from an update!");
            }

            @Override
            void onNotFound() {
                // expected, do nothing
            }

            @Override
            void onError(Throwable error) {
                handler.onError(error);
            }
        });
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

    /**
     * Generate a base64 encoded 16 byte random session id unique to this instance of CommuteStream
     */
    private static String generateSessionID() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        byte[] encodedBytes = Base64.encode(randomBytes, Base64.URL_SAFE);
        return new String(encodedBytes);
    }
}

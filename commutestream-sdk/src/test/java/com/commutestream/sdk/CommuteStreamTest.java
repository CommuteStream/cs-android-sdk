package com.commutestream.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests our singleton API which is used by mediation adapters and publishers
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = "../commutestream-sdk/src/main/AndroidManifest.xml")
public class CommuteStreamTest  {

    private Activity mActivity;

    @Before
    public void init() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        CommuteStream.init(mActivity.getApplicationContext(), "testadunit");
        CommuteStream.setAAID("testaaid"); // needed since otherwise it will not be set
        CommuteStream.setLimitTracking(true); // needed since otherwise it will not be set
        CommuteStream.setInitialized();
        assertThat(CommuteStream.getAdUnitUuid(), is("testadunit"));
        assertThat(CommuteStream.getAAID(), is("testaaid"));
        assertThat(CommuteStream.getLimitTracking(), is(true));
        assertThat(CommuteStream.isInitialized(), is(true));
        CommuteStream.nextRequest(true); // clear current request if any
    }

    @Test
    public void testInit() {
        assertThat(CommuteStream.getAdUnitUuid(), is("testadunit"));
    }

    @Test
    public void setBaseURL() {
        String baseURL = "http://localhost:9000/";
        CommuteStream.setBaseURL(baseURL);
        assertThat(CommuteStream.getBaseURL(), is(baseURL));
    }

    @Test
    public void addAgencyInterest() {
        AdRequest nextRequest = CommuteStream.nextRequest(true);
        assertThat(CommuteStream.hasUpdatesPending(), is(false));
        AgencyInterest agencyInterest = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345");
        CommuteStream.addAgencyInterest(agencyInterest);
        assertThat(CommuteStream.hasUpdatesPending(), is(true));
        AdRequest nextRequest0 = CommuteStream.nextRequest(true);
        assertThat(nextRequest0, notNullValue());
        assertThat(nextRequest, not(nextRequest0));
        assertThat(nextRequest.getAgencyInterests().size(), is(0));
        assertThat(nextRequest0.getAgencyInterests(), contains(agencyInterest));
    }

    @Test
    public void trackingDisplayed() {
        CommuteStream.trackingDisplayed("cta", "Red", "12345");
        assertThat(CommuteStream.getRequest().getAgencyInterests(), contains(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345")));
    }

    @Test
    public void mapDisplayed() {
        CommuteStream.mapDisplayed("cta", "Red", "12345");
        assertThat(CommuteStream.getRequest().getAgencyInterests(), contains(new AgencyInterest(AgencyInterest.MAP_DISPLAYED, "cta", "Red", "12345")));
    }

    @Test
    public void favoriteAdded() {
        CommuteStream.favoriteAdded("cta", "Red", "12345");

        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasItem(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, "cta", "Red", "12345")));
    }

    @Test
    public void alertDisplayed() {
        CommuteStream.alertDisplayed("cta", "Red", "12345");
        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasItem(new AgencyInterest(AgencyInterest.ALERT_DISPLAYED, "cta", "Red", "12345")));
    }

    @Test
    public void tripPlanningPointA() {
        CommuteStream.tripPlanningPointA("cta", "Red", "12345");
        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasItem(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_A, "cta", "Red", "12345")));
    }

    @Test
    public void tripPlanningPointB() {
        CommuteStream.tripPlanningPointB("cta", "Red", "12345");
        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasItem(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_B, "cta", "Red", "12345")));
    }

    @Test
    public void setLocation() {
        if (mActivity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
            String NetworkLocationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(NetworkLocationProvider);
            CommuteStream.setLocation(lastKnownLocation);
            assertThat(CommuteStream.getLocation(), is(lastKnownLocation));
        }
    }

    @Test
    public void setTestingFlag() {
        assertThat(CommuteStream.getTestingFlag(), is(false));
        CommuteStream.setTestingFlag(true);
        assertThat(CommuteStream.getTestingFlag(), is(true));
        CommuteStream.setTestingFlag(false);
        assertThat(CommuteStream.getTestingFlag(), is(false));
    }

    @Factory
    public static <T, S extends Collection<T>> Matcher<? super S> hasNoDuplicates(Class<T> ofClass) {
        return new TypeSafeMatcher<S>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("without duplicates");
            }

            @Override
            public boolean matchesSafely(S item) {
                return new HashSet<T>(item).size() == item.size();
            }
        };
    }

    @Test
    public void deduplicateInterests() {
        assertThat(CommuteStream.getRequest().getAgencyInterests().size(), is(0));
        AgencyInterest interest = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "123456");
        AgencyInterest dupe = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "123456");
        CommuteStream.addAgencyInterest(interest);
        assertThat(CommuteStream.getRequest().getAgencyInterests(), contains(interest));
        CommuteStream.addAgencyInterest(interest);
        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasNoDuplicates(AgencyInterest.class));
        CommuteStream.addAgencyInterest(dupe);
        assertThat(CommuteStream.getRequest().getAgencyInterests(), hasNoDuplicates(AgencyInterest.class));
    }

    @Test
    public void disableAutomaticTracking() {
        CommuteStream.setAutomaticLocationTracking(false);
        assertThat(CommuteStream.getDeviceLocation(null), nullValue());
    }
}

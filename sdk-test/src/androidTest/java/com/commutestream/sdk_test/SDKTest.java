package com.commutestream.sdk_test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commutestream.sdk.AgencyInterest;
import com.commutestream.sdk.CommuteStream;
import com.commutestream.sdk.AdRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.*;


@RunWith(AndroidJUnit4.class)
public class SDKTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void launchActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        mActivityRule.launchActivity(intent);
        CommuteStream.nextRequest(true);
    }

    @Test
    public void isInitialized() {
        assertThat(CommuteStream.isInitialized()).isTrue();
        assertThat(CommuteStream.getAppName()).isNotEmpty();
        assertThat(CommuteStream.getAppVersion()).isNotEmpty();
        assertThat(CommuteStream.getAidSha()).isNotEmpty();
        assertThat(CommuteStream.getAidMd5()).isNotEmpty();
    }

    @Test
    public void setBaseURL() {
        String baseURL = "http://localhost:9000/";
        CommuteStream.setBaseURL(baseURL);
        assertThat(CommuteStream.getBaseURL()).isEqualTo(baseURL);
    }

    @Test
    public void addAgencyInterest() {
        AdRequest nextRequest = CommuteStream.nextRequest(true);
        assertThat(CommuteStream.hasUpdatesPending()).isFalse();
        AgencyInterest agencyInterest = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345");
        CommuteStream.addAgencyInterest(agencyInterest);
        assertThat(CommuteStream.hasUpdatesPending()).isTrue();
        AdRequest nextRequest0 = CommuteStream.nextRequest(true);
        assertThat(nextRequest).isNotEqualTo(nextRequest0);
        assertThat(nextRequest.getAgencyInterests()).isEmpty();
        assertThat(nextRequest0.getAgencyInterests()).contains(agencyInterest);
    }

    @Test
    public void trackingDisplayed() {
        CommuteStream.trackingDisplayed("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345"));
    }

    @Test
    public void mapDisplayed() {
        CommuteStream.mapDisplayed("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.MAP_DISPLAYED, "cta", "Red", "12345"));
    }

    @Test
    public void favoriteAdded() {
        CommuteStream.favoriteAdded("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, "cta", "Red", "12345"));
    }

    @Test
    public void alertDisplayed() {
        CommuteStream.alertDisplayed("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.ALERT_DISPLAYED, "cta", "Red", "12345"));
    }

    @Test
    public void tripPlanningPointA() {
        CommuteStream.tripPlanningPointA("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_A, "cta", "Red", "12345"));
    }

    @Test
    public void tripPlanningPointB() {
        CommuteStream.tripPlanningPointB("cta", "Red", "12345");
        CommuteStream.getRequest().getAgencyInterests().contains(new AgencyInterest(AgencyInterest.TRIP_PLANNING_POINT_B, "cta", "Red", "12345"));
    }

    @Test
    public void setLocation() {
        Activity activity = mActivityRule.getActivity();
        if (activity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            String NetworkLocationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(NetworkLocationProvider);
            CommuteStream.setLocation(lastKnownLocation);
            assertThat(CommuteStream.getLocation()).isEqualTo(lastKnownLocation);
        }
    }

    @Test
    public void setTestingFlag() {
        assertThat(CommuteStream.getTestingFlag()).isFalse();
        CommuteStream.setTestingFlag(true);
        assertThat(CommuteStream.getTestingFlag()).isTrue();
        CommuteStream.setTestingFlag(false);
        assertThat(CommuteStream.getTestingFlag()).isFalse();
    }

    @Test
    public void dedupelicateInterests() {
        assertThat(CommuteStream.getRequest().getAgencyInterests()).isEmpty();
        AgencyInterest interest = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "123456");
        AgencyInterest dupe = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "123456");
        CommuteStream.addAgencyInterest(interest);
        assertThat(CommuteStream.getRequest().getAgencyInterests()).contains(interest);
        CommuteStream.addAgencyInterest(interest);
        assertThat(CommuteStream.getRequest().getAgencyInterests()).doesNotHaveDuplicates();
        CommuteStream.addAgencyInterest(dupe);
        assertThat(CommuteStream.getRequest().getAgencyInterests()).doesNotHaveDuplicates();
    }
}
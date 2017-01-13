package com.commutestream.sdk_test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.commutestream.sdk.AgencyInterest;
import com.commutestream.sdk.CommuteStream;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    private Random rand = new Random();
    private ArrayList<AgencyInterest> mKnownInterests = new ArrayList<>();
    private ArrayList<AgencyInterest> mRecentInterests = new ArrayList<>();
    private InterestAdapter mInterestAdapter;

    private void fillKnownInterests() {
        // top 10 interests found in db
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "41420"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "41450"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", null, null));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "30256"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Brn", "30257"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "77", "7947"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "77", "17380"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "74", "1225"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "30289"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "80", "5676"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "30269"));
        mKnownInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "67", "14139"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillKnownInterests();
        CommuteStream.init(this, "c69d1610-92c1-4ddb-a72c-d44560496c10");

        mInterestAdapter = new InterestAdapter(this, mRecentInterests);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Location information if available
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            String NetworkLocationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(NetworkLocationProvider);
            adRequestBuilder = adRequestBuilder.setLocation(lastKnownLocation);
        }

        AdRequest adRequest = adRequestBuilder.build();
        mAdView.loadAd(adRequest);

        ListView lvRecent = (ListView) findViewById(R.id.recentInterests);
        lvRecent.setAdapter(mInterestAdapter);

        Button bRandomInterest = (Button) findViewById(R.id.randomAgencyInterest);
        bRandomInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomInterest();
            }
        });

        final ToggleButton bTestMode = (ToggleButton) findViewById(R.id.testMode);
        bTestMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommuteStream.setTestingFlag(bTestMode.isChecked());
            }
        });
        bTestMode.setChecked(false);
    }

    /**
     * Pick a random agency interest to add the the recent ones and tell CommuteStream about it
     */
    protected void randomInterest() {
        int i = rand.nextInt(mKnownInterests.size());
        AgencyInterest interest = mKnownInterests.get(i);
        mInterestAdapter.add(interest);
        CommuteStream.addAgencyInterest(interest);
        // Location information if available
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            String NetworkLocationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(NetworkLocationProvider);
            CommuteStream.setLocation(lastKnownLocation);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.commutestream.ads;

import android.app.Activity;
import android.location.Location;
import android.test.InstrumentationTestCase;

/**
 * AdRequestTest
 */
public class AdRequestTest extends InstrumentationTestCase {
    public void testPersistent() throws Exception {
        AdRequest req = new AdRequest();
        req.sdkName = "sdkName";
        req.sdkVersion = "sdkVersion";
        req.appName = "appName";
        req.appVersion = "appVersion";
        req.aidMd5 = "aidMd5";
        req.aidSha = "aidSha";
        req.adUnitUuid = "adUnit";
        req.testing = false;
        req.bannerHeight = 500;
        req.bannerWidth = 200;
        req.agencyInterests.add(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, "cta", "red", "445"));
        req.skipFetch = true;
        //req.location =
        AdRequest req0 = new AdRequest();
        assertEquals(req.sdkName, req0.sdkName);
        assertEquals(req.sdkVersion, req0.sdkVersion);
        assertEquals(req.appName, req0.appName);
        assertEquals(req.appVersion, req0.appVersion);
        assertEquals(req.aidMd5, req0.aidMd5);
        assertEquals(req.aidSha, req0.aidSha);
        assertEquals(req.adUnitUuid, req0.adUnitUuid);
        assertEquals(req.testing, req0.testing);
        assertEquals(req.bannerHeight, req0.bannerHeight);
        assertEquals(req.bannerWidth, req0.bannerWidth);
        assertTrue(req0.skipFetch == false);
        assertTrue(req0.agencyInterests.isEmpty());
    }
}

package com.commutestream.ads;

import com.loopj.android.http.RequestParams;

import junit.framework.TestCase;

/**
 * AdRequestURLEncoderTest
 */
public class AdRequestURLEncoderTest extends TestCase {
    public void testEncode() throws Exception {
        AdRequest req = AdRequestTest.mockedRequest();
        req.agencyInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", null, null));
        req.agencyInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "red", null));
        req.agencyInterests.add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "red", "123"));
        RequestParams params = AdRequestURLEncoder.Encode(req);
        assertTrue(params.has("sdk_name"));
        assertTrue(params.has("sdk_ver"));
        assertTrue(params.has("app_name"));
        assertTrue(params.has("app_ver"));
        assertTrue(params.has("banner_width"));
        assertTrue(params.has("banner_height"));
        assertTrue(params.has("ad_unit_uuid"));
        assertTrue(params.has("aid_sha"));
        assertTrue(params.has("aid_md5"));
        assertTrue(params.has("testing"));
        assertTrue(params.has("agency_interest"));
    }
}

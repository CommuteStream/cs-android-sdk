package com.commutestream.sdk;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Map;

/**
 * AdRequestQueryMapTest
 */
@RunWith(JUnit4.class)
public class AdRequestQueryMapTest  {

    @Test
    public void testEncode() throws Exception {
        AdRequest req = AdRequestTest.mockedRequest();
        req.setTesting(true);
        req.getAgencyInterests().add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", null, null));
        req.getAgencyInterests().add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "red", null));
        req.getAgencyInterests().add(new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "red", "123"));
        Map<String, String> params = AdRequestQueryMap.Map(req);
        assertThat(params).containsKey("sdk_name");
        assertThat(params).containsKey("sdk_ver");
        assertThat(params).containsKey("app_name");
        assertThat(params).containsKey("app_ver");
        assertThat(params).containsKey("banner_width");
        assertThat(params).containsKey("banner_height");
        assertThat(params).containsKey("ad_unit_uuid");
        assertThat(params).containsKey("aid_sha");
        assertThat(params).containsKey("aid_md5");
        assertThat(params).containsKey("testing");
        assertThat(params).containsKey("agency_interest");
    }
}

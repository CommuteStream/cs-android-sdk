package com.commutestream.sdk;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AdRequestTest {

    public static AdRequest mockedRequest() {
        AdRequest req = new AdRequest();
        req.setSdkName("sdkName");
        req.setSdkVersion("sdkVersion");
        req.setAppName("appName");
        req.setAppVersion("appVersion");
        req.setAidMd5("aidMd5");
        req.setAidSha("aidSha");
        req.setAdUnitUuid("adUnit");
        req.setTesting(false);
        req.setBannerHeight(500);
        req.setBannerWidth(200);
        return req;
    }

    @Test
    public void testPersistent() throws Exception {
        AdRequest req = mockedRequest();
        req.getAgencyInterests().add(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, "cta", "red", "445"));
        req.setSkipFetch(true);
        //req.location =
        AdRequest req0 = new AdRequest();
        assertThat(req.getSdkName()).isEqualTo(req0.getSdkName());
        assertThat(req.getSdkVersion()).isEqualTo(req0.getSdkVersion());
        assertThat(req.getAppName()).isEqualTo(req0.getAppName());
        assertThat(req.getAppVersion()).isEqualTo(req0.getAppVersion());
        assertThat(req.getAidMd5()).isEqualTo(req0.getAidMd5());
        assertThat(req.getAidSha()).isEqualTo(req0.getAidSha());
        assertThat(req.getAdUnitUuid()).isEqualTo(req0.getAdUnitUuid());
        assertThat(req.isTesting()).isEqualTo(req0.isTesting());
        assertThat(req.getBannerHeight()).isEqualTo(req0.getBannerHeight());
        assertThat(req.getBannerWidth()).isEqualTo(req0.getBannerWidth());
        assertThat(req0.isSkipFetch()).isFalse();
        assertThat(req0.getAgencyInterests()).isEmpty();
    }
}

package com.commutestream.sdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class AdRequestTest {

    public static AdRequest mockedRequest() {
        AdRequest req = new AdRequest();
        req.setSdkName("sdkName");
        req.setSdkVersion("sdkVersion");
        req.setAppName("appName");
        req.setAppVersion("appVersion");
        req.setAdUnitUuid("adUnit");
        req.setTesting(false);
        req.setViewHeight(500);
        req.setViewWidth(200);
        return req;
    }

    @Test
    public void testPersistent() throws Exception {
        AdRequest req = mockedRequest();
        req.getAgencyInterests().add(new AgencyInterest(AgencyInterest.FAVORITE_ADDED, "cta", "red", "445"));
        req.setSkipFetch(true);
        AdRequest req0 = new AdRequest();
        assertThat(req.getSdkName(), equalTo(req0.getSdkName()));
        assertThat(req.getSdkVersion(), equalTo(req0.getSdkVersion()));
        assertThat(req.getAppName(), equalTo(req0.getAppName()));
        assertThat(req.getAppVersion(), equalTo(req0.getAppVersion()));
        assertThat(req.getAdUnitUuid(), equalTo(req0.getAdUnitUuid()));
        assertThat(req.isTesting(), equalTo(req0.isTesting()));
        assertThat(req.getViewHeight(), equalTo(req0.getViewHeight()));
        assertThat(req.getViewWidth(), equalTo(req0.getViewWidth()));
        assertThat(req0.isSkipFetch(), is(false));
        assertThat(req0.getAgencyInterests(), empty());
    }
}

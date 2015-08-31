package com.commutestream.sdk;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AgencyInterestTest {

    @Test
    public void equals() {
        AgencyInterest ai = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345");
        AgencyInterest aiSame = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "12345");
        AgencyInterest aiNull = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, null, null, null);
        AgencyInterest aiInterestVaries = new AgencyInterest(AgencyInterest.ALERT_DISPLAYED, "cta", "Red", "12345");
        AgencyInterest aiAgencyVaries = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "paac", "Red", "12345");
        AgencyInterest aiRouteVaries = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Blue", "12345");
        AgencyInterest aiStopVaries = new AgencyInterest(AgencyInterest.TRACKING_DISPLAYED, "cta", "Red", "54321");
        assertThat(aiSame).isEqualTo(ai);
        assertThat(aiNull).isNotEqualTo(ai);
        assertThat(aiInterestVaries).isNotEqualTo(ai);
        assertThat(aiAgencyVaries).isNotEqualTo(ai);
        assertThat(aiRouteVaries).isNotEqualTo(ai);
        assertThat(aiStopVaries).isNotEqualTo(ai);
    }

}

package com.commutestream.sdk;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests our singleton API which is used by mediation adapters and publishers
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = "../sdk/src/main/AndroidManifest.xml")
public class CommuteStreamTest  {

    @Test
    public void testInit() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        CommuteStream.init(activity.getApplicationContext(), "testadunit");
        assertThat(CommuteStream.getAdUnitUuid(), is("testadunit"));
    }

}

package com.commutestream.sdk_test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commutestream.sdk.CommuteStream;

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
    }


}
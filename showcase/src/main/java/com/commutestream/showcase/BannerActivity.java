package com.commutestream.showcase;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.commutestream.sdk.AdEventListener;
import com.commutestream.sdk.StaticAdViewFactory;

public class BannerActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        AdEventListener listener = new AdEventListener() {
            @Override
            public void onClicked() {
                Log.v("CS_SHOWCASE", "Banner Clicked");
            }
        };
        String html = "<html><body><h1>Banner Ad</h1></body></html>";
        String redirectUrl = "https://commutestream.com";
        View bannerView = StaticAdViewFactory.create(getApplicationContext(), listener, html, redirectUrl, 0.0, 320, 50);
        FrameLayout bannerFrame = (FrameLayout) findViewById(R.id.bannerAdFrame);
        bannerFrame.addView(bannerView);
    }
}
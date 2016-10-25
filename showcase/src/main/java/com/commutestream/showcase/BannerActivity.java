package com.commutestream.showcase;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.commutestream.sdk.AdContentTypes;
import com.commutestream.sdk.AdEventListener;
import com.commutestream.sdk.AdMetadata;
import com.commutestream.sdk.HtmlAdViewFactory;
import com.commutestream.sdk.VisibilityListener;
import com.commutestream.sdk.VisibilityMonitor;

public class BannerActivity extends AppCompatActivity  {

    private VisibilityMonitor mVisibilityMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        AdEventListener listener = new AdEventListener() {
            @Override
            public void onImpressed() {
                Log.v("CS_SHOWCASE", "Banner Impressed");
            }

            @Override
            public void onClicked() {
                Log.v("CS_SHOWCASE", "Banner Clicked");
            }
        };
        try {
            byte[] html = new byte[8194];
            getAssets().open("seahorse/index.html", AssetManager.ACCESS_BUFFER).read(html);
            String redirectUrl = "https://commutestream.com";
            AdMetadata metadata = new AdMetadata();
            metadata.width = 320;
            metadata.height = 50;
            metadata.contentType = AdContentTypes.HTML;
            metadata.requestTime = 0.1;
            metadata.requestID = 1;
            metadata.clickUrl = "https://commutestream.com";
            metadata.impressionUrl = "https://commutestream.com";
            byte[] html2 = "<a href=\"https://commutestream.com\"><h1>CommuteStream</h1></a>".getBytes();
            View bannerView = HtmlAdViewFactory.create(getApplicationContext(), listener, metadata, html2);
            mVisibilityMonitor = new VisibilityMonitor(new VisibilityListener() {
                @Override
                public void onVisible(View view) {
                    Log.v("CS_SHOWCASE", "Banner Visible");
                }

                @Override
                public void onHidden(View view) {
                    Log.v("CS_SHOWCASE", "Banner Hidden");
                }
            }, bannerView);
            FrameLayout bannerFrame = (FrameLayout) findViewById(R.id.bannerAdFrame);
            bannerFrame.addView(bannerView);
        } catch (Exception e) {
            Log.d("SHOWCASE", "Failed to load banner " + e.getMessage());
        }
    }
}
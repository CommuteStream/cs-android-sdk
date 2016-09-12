package com.commutestream.showcase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bannerAdBtn = (Button) findViewById(R.id.bannerAdBtn);
        bannerAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BannerActivity.class);
                startActivity(i);
            }
        });

        Button mraidAdBtn = (Button) findViewById(R.id.mraidAdBtn);
        mraidAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MRaidActivity.class);
                startActivity(i);
            }
        });

        Button nativeStopAdBtn = (Button) findViewById(R.id.nativeStopAdBtn);
        nativeStopAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NativeStopActivity.class);
                startActivity(i);
            }
        });
    }
}

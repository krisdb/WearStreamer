package com.wear.streamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class WearMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        findViewById(R.id.main_podcast_link).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WearPodcastsActivity.class));
            }
        });

        findViewById(R.id.main_radio_link).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WearRadioActivity.class));
            }
        });
    }
}

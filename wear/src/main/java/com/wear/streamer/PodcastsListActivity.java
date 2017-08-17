package com.wear.streamer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PodcastsListActivity extends Activity{

    private WearableRecyclerView mMediaList = null;
    private List<PodcastItem> mPodcasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_list);

        mMediaList = (WearableRecyclerView) findViewById(R.id.podcast_list);

        mMediaList.setCircularScrollingGestureEnabled(true);
        mMediaList.setBezelWidth(0.5f);
        mMediaList.setScrollDegreesPerScreen(90);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.podcast_list_layout);

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsPodcastsActivity.class);
                startActivity(intent);

                return false;
            }

        });

        mPodcasts = DBUtilities.GetPodcasts(this);

        if (mPodcasts.size() > 0) {
            mMediaList.setAdapter(new PodcastsAdapter(this, mPodcasts));
            findViewById(R.id.empty_podcast_list).setVisibility(TextView.GONE);
        } else {
            findViewById(R.id.empty_podcast_list).setVisibility(TextView.VISIBLE);
        }

        StartAlarm();
    }

    private void StartAlarm()
    {
        final AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        final Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        final Long updateInterval = Long.valueOf(getResources().getInteger(R.integer.default_update_interval));

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + updateInterval,
                updateInterval,
                pendingIntent
        );
    }

}
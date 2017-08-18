package com.wear.streamer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PodcastEpisodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_episode);

        final int episodeId = getIntent().getExtras().getInt("eid");

        PodcastItem episode = DBUtilities.GetEpisode(this, episodeId);

        ((TextView) findViewById(R.id.podcast_episode_title)).setText(episode.getTitle());

        ((TextView) findViewById(R.id.podcast_episode_description)).setText(
                Html.fromHtml(episode.getDescription(),
                        Html.FROM_HTML_MODE_COMPACT
                ));

        final Button btnPlay = ((Button) findViewById(R.id.btn_podcast_play));

        final Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("id", episodeId);

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isMyServiceRunning(MediaPlayerService.class)) {
                    stopService(intent);
                    btnPlay.setText("Play");
                } else {
                    startService(intent);
                    btnPlay.setText("Pause");
                }
            }
        });

        if (episode.getMediaUrl() == null) {
            btnPlay.setEnabled(false);
            btnPlay.setText("Error");
        }

        ContentValues cv = new ContentValues();
        cv.put("read",1);

        new DBPodcastsEpisodes(this).update(cv, episodeId);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isMyServiceRunning(MediaPlayerService.class))
            ((Button)findViewById(R.id.btn_podcast_play)).setText("Pause");
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
package com.wear.streamer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PodcastEpisodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_episode);

        final int episodeId = getIntent().getExtras().getInt("eid");

        PodcastItem episode = DBUtilities.GetEpisode(this, episodeId);

        ((TextView)findViewById(R.id.podcast_episode_title)).setText(episode.getTitle());

        ((TextView)findViewById(R.id.podcast_episode_description)).setText(
                Html.fromHtml(episode.getDescription(),
                        Html.FROM_HTML_MODE_COMPACT
                ));

        final Button btnPlay = ((Button)findViewById(R.id.btn_podcast_play));

        final Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("id", episodeId);

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (btnPlay.getText() == "Play")
                {
                    startService(intent);
                    btnPlay.setText("Pause");
                }
                else
                {
                   stopService(intent);
                    btnPlay.setText("Play");
                }


            }
        });
    }

}
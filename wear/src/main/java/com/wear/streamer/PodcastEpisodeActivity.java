package com.wear.streamer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PodcastEpisodeActivity extends Activity implements MediaPlayer.OnBufferingUpdateListener{

    private MediaPlayer mMediaPlayer;
    private PodcastItem mEpisode;
    private Button mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_episode);

        final int episodeId = getIntent().getExtras().getInt("eid");

        mEpisode = DBUtilities.GetEpisode(this, episodeId);

        ((TextView)findViewById(R.id.podcast_episode_title)).setText(mEpisode.getTitle());
        ((TextView)findViewById(R.id.podcast_episode_description)).setText(Html.fromHtml(mEpisode.getDescription(), Html.FROM_HTML_MODE_COMPACT));

        mPlayButton = ((Button)findViewById(R.id.btn_podcast_play));

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mPlayButton.setText("Pause");
        }

        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    StopPodcast();
                    SavePosition();
                } else {
                    StartPodcast();
                }
            }
        });
    }

    private void StartPodcast() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setDataSource(mEpisode.getMediaUrl().toString());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer player) {
                    mMediaPlayer.seekTo(GetPosition());
                }
            });

            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mp) {
                    SystemClock.sleep(200);
                    mp.start();
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(getPackageName(), e.toString());
        }

        mPlayButton.setText("Pause");
    }

    private void StopPodcast() {

        mMediaPlayer.stop();
        mPlayButton.setText("Play");
    }

    private void SavePosition()
    {
        ContentValues cv = new ContentValues();
        cv.put("position", mMediaPlayer.getCurrentPosition());

        new DBPodcastsEpisodes(this).update(cv, mEpisode.getEpisodeId());
    }

    private int GetPosition() {

        final DBPodcastsEpisodes db = new DBPodcastsEpisodes(this);
        final SQLiteDatabase sdb = db.select();

        final Cursor cursor = sdb.rawQuery("SELECT [position] FROM [tbl_podcast_episodes] WHERE [id] = ?", new String[] { String.valueOf(mEpisode.getEpisodeId()) });

        if (cursor.moveToFirst())
            return cursor.getInt(0);
        else
            return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
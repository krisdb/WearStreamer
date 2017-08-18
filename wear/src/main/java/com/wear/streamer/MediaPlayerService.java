package com.wear.streamer;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener {
    private static String mPackage = null;
    private MediaPlayer mMediaPlayer;
    private PodcastItem mEpisode;

    public MediaPlayerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(mPackage, "Media service created");
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        super.onStartCommand(intent, flags, startId);

        Log.d(mPackage, "Media service started");

        mPackage = getPackageName();
        StartStream(intent.getExtras().getInt("id"));
        return START_NOT_STICKY;
    }


    private void StartStream(int episodeId)
    {
        mEpisode = DBUtilities.GetEpisode(this, episodeId);

        try {
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setDataSource(mEpisode.getMediaUrl().toString());
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer player) {
                    int position = GetPosition();
                    Log.d(mPackage, "Current position: " + position);
                    mMediaPlayer.seekTo(position);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(mPackage, "Media service destroyed");

        SavePosition();
        mMediaPlayer.stop();
        mMediaPlayer.release();
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

        int position = 0;

        if (cursor.moveToFirst())
            position = cursor.getInt(0);

        cursor.close();
        db.close();

        return position;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}

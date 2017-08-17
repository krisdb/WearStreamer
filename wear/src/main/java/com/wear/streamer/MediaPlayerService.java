package com.wear.streamer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MediaPlayerService extends IntentService implements MediaPlayer.OnBufferingUpdateListener {
    private static volatile PowerManager.WakeLock mWakeLock = null;
    private static String mPackage = null;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private PodcastItem mEpisode;


    public MediaPlayerService() {
        super("MediaPlayerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        super.onStartCommand(intent, flags, startId);

        mHandler = new Handler();

        mPackage = getPackageName();

        PowerManager.WakeLock lock = getLock(this.getApplicationContext());

        if (!lock.isHeld())
            lock.acquire();

        int episodeId = intent.getExtras().getInt("id");

        mEpisode = DBUtilities.GetEpisode(this, episodeId);

        try {
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
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final Context ctx = getApplicationContext();

        Log.d(mPackage, "Service Started");
        getLock(ctx).acquire();

        try {
            List<PodcastItem> podcasts = DBUtilities.GetPodcasts(ctx);

            for (PodcastItem podcast : podcasts)
                FeedParser.parse(ctx, podcast);
        } catch (Exception ex) {
            Log.e(ctx.getPackageName(), ex.getMessage().toString());
        } finally {
            PowerManager.WakeLock lock = getLock(this.getApplicationContext());

            if (lock.isHeld()) {
                lock.release();
            }
        }
    }

    synchronized private static PowerManager.WakeLock getLock(final Context context)
    {
        if (mWakeLock == null) {

            PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, mPackage);
            mWakeLock.setReferenceCounted(true);
        }

        return(mWakeLock);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SavePosition();
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

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
}

package com.wear.streamer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

public class BackgroundService extends IntentService {
    private static volatile PowerManager.WakeLock mWakeLock = null;
    private static String mPackage = null;
    private Handler mHandler;

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        mHandler = new Handler();

        mPackage = getPackageName();

        PowerManager.WakeLock lock = getLock(this.getApplicationContext());

        if (!lock.isHeld())
            lock.acquire();

        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final Context ctx = getApplicationContext();

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
}

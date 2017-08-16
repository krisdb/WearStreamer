package com.wear.streamer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            final Intent myIntent = new Intent(context, AlarmReceiver.class);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

            final Long updateInterval = Long.valueOf(30);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + updateInterval, updateInterval, pendingIntent);
        }

        context.startService(new Intent(context, BackgroundService.class));
    }
}
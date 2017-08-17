package com.wear.streamer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.TextView;

import static android.content.Context.ALARM_SERVICE;

public class SettingsPodcastsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.podcast);

        mActivity = getActivity();

        findPreference("pref_sync_podcasts").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                new SyncPodcasts(mActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return false;
            }
        });

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {

        final CheckBoxPreference cbUpdatesEnabled = (CheckBoxPreference)getPreferenceScreen().findPreference("updatesEnabled");
        final ListPreference lpUpdateInterval = (ListPreference)getPreferenceScreen().findPreference("updateInterval");

        final Boolean updatesEnabled = cbUpdatesEnabled.isChecked();
        lpUpdateInterval.setEnabled(updatesEnabled);

        if (updatesEnabled)
        {
            StartAlarm();
        }
        else
        {
            CancelAlarm();
        }
    }

    private void StartAlarm()
    {
        Alarm(false);
    }

    private void CancelAlarm()
    {
        Alarm(true);
    }

    private void Alarm(final Boolean cancel)
    {
        final AlarmManager alarmManager = (AlarmManager)mActivity.getSystemService(ALARM_SERVICE);

        final Intent myIntent = new Intent(mActivity, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, 0, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d(mActivity.getPackageName(), "Alarm Cancelled");

        if (!cancel)
        {
            final Long updateInterval = Long.valueOf(((ListPreference)getPreferenceScreen().findPreference("updateInterval")).getValue());

            Log.d(mActivity.getPackageName(), "Alarm Started");

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + updateInterval, updateInterval, pendingIntent);
        }
    }
}

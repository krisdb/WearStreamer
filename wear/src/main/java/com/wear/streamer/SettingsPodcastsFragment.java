package com.wear.streamer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class SettingsPodcastsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.podcast);

        mActivity = getActivity();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final Boolean updatesEnabled = prefs.getBoolean("updatesEnabled", true);

        final CheckBoxPreference cbUpdatesEnabled = (CheckBoxPreference)getPreferenceScreen().findPreference("updatesEnabled");
        cbUpdatesEnabled.setChecked(updatesEnabled);

        final ListPreference lpUpdateInterval = (ListPreference)getPreferenceScreen().findPreference("updateInterval");
        lpUpdateInterval.setValue(prefs.getString("updateInterval", String.valueOf(mActivity.getResources().getInteger(R.integer.default_update_interval))));
        lpUpdateInterval.setEnabled(updatesEnabled);

        findPreference("pref_sync_podcasts").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                new GetPodcasts(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private class GetPodcasts extends AsyncTask<Void, Void, Void>
    {
        private Context mContext;

        public GetPodcasts(final Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            new DBPodcastsEpisodes(mContext).deleteAll();
            Toast.makeText(mContext, "Syncing started", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            List<PodcastItem> podcasts = DBUtilities.GetPodcasts(mContext);

            for (PodcastItem podcast : podcasts)
                FeedParser.parse(mContext, podcast);

            return null;
        }

        protected void onPostExecute(Void param)
        {
            Toast.makeText(mContext, "Syncing finished", Toast.LENGTH_LONG).show();
        }
    }
}

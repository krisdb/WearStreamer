package com.wear.streamer;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class SettingsPodcastsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.podcast);

        findPreference("pref_sync_podcasts").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                new GetPodcasts(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                return false;
            }
        });
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

package com.wear.streamer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

public class SyncPodcasts extends AsyncTask<Void, Void, Void>
{
    private Context mContext;

    public SyncPodcasts(final Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        new DBPodcastsEpisodes(mContext).deleteAll();
        Toast.makeText(mContext, "Syncing started", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mContext, "Syncing completed", Toast.LENGTH_SHORT).show();
    }
}
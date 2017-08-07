package com.wear.streamer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WearPodcastsActivity extends Activity{

    private WearableRecyclerView mMediaList = null;
    private List<RssItem> mPodcasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_list);

        mMediaList = (WearableRecyclerView)findViewById(R.id.podcast_list);

        mMediaList.setCircularScrollingGestureEnabled(true);
        mMediaList.setBezelWidth(0.5f);
        mMediaList.setScrollDegreesPerScreen(90);

        new GetPodcasts(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetPodcasts extends AsyncTask<Void, Void, Void>
    {
        private Context mContext;

        public GetPodcasts(final Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RssItem item = new RssItem();
            item.setTitle("Into the Nexus");
            item.setLink("http://feeds.feedburner.com/itncast?format=xml");
            mPodcasts.add(item);

            item = new RssItem();
            item.setTitle("Norm Macdonald Live");
            item.setLink("http://norm.videopodcastnetwork.libsynpro.com/rss");
            mPodcasts.add(item);
            return null;
        }

        protected void onPostExecute(Void param)
        {
            PodcastsAdapter adapter = new PodcastsAdapter(mContext, mPodcasts);
            mMediaList.setAdapter(adapter);
        }
    }
}
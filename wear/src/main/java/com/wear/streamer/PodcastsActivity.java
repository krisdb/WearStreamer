package com.wear.streamer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PodcastsActivity extends Activity{

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

        new GetPodcasts(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

            mPodcasts = Utilities.GetPodcasts(mContext);

            return null;
        }

        protected void onPostExecute(Void param)
        {
            if (mPodcasts.size() > 0)
            {
                mMediaList.setAdapter(new PodcastsAdapter(mContext, mPodcasts));
                findViewById(R.id.empty_podcast_list).setVisibility(TextView.GONE);
            }
            else
            {
                findViewById(R.id.empty_podcast_list).setVisibility(TextView.VISIBLE);
            }
        }
    }
}
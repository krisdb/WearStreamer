package com.wear.streamer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import java.util.List;

public class WearPodcastEpisodesActivity extends Activity{

    private MediaPlayer mMediaPlayer = null;
    private WearableRecyclerView mMediaList = null;
    private List<RssItem> mPodcasts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_list);

        mMediaList = (WearableRecyclerView)findViewById(R.id.podcast_list);

        mMediaList.setCircularScrollingGestureEnabled(true);
        mMediaList.setBezelWidth(0.5f);
        mMediaList.setScrollDegreesPerScreen(90);

        String test = getIntent().getExtras().getString("url");

        new GetPodcasts(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getIntent().getExtras().getString("url"));
    }

    private class GetPodcasts extends AsyncTask<String, Void, Void>
    {
        private Context mContext;

        public GetPodcasts(final Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... url) {
            mPodcasts = FeedParser.parse(url[0]);
            return null;
        }

        protected void onPostExecute(Void param)
        {
            EpisodesAdapter adapter = new EpisodesAdapter(mContext, mPodcasts, mMediaPlayer);
            mMediaList.setAdapter(adapter);
        }
    }

    @Override
    public void onStop()
    {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        super.onStop();
    }
}
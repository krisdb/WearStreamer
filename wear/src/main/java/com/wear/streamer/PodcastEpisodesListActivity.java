package com.wear.streamer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.widget.TextView;

import java.util.List;

public class PodcastEpisodesListActivity extends Activity{

    private WearableRecyclerView mMediaList = null;
    private List<PodcastItem> mEpisodes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_episodes);

        mMediaList = (WearableRecyclerView)findViewById(R.id.episode_list);

        mMediaList.setCircularScrollingGestureEnabled(true);
        mMediaList.setBezelWidth(0.5f);
        mMediaList.setScrollDegreesPerScreen(90);

        mEpisodes = DBUtilities.GetEpisodes(this, this.getIntent().getExtras().getInt("pid"));

        if (mEpisodes.size() > 0)
        {
            mMediaList.setAdapter(new EpisodesAdapter(this, mEpisodes));
            findViewById(R.id.empty_podcast_episodes).setVisibility(TextView.GONE);
        }
        else
        {
            findViewById(R.id.empty_podcast_episodes).setVisibility(TextView.VISIBLE);
        }
    }
}
package com.wear.streamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PodcastEpisodesListActivity extends Activity {

    private EpisodesAdapter mAdapter = null;
    private List<PodcastItem> mEpisodes;
    private WearableRecyclerView mEpisodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_podcast_episodes);

        mEpisodeList = (WearableRecyclerView)findViewById(R.id.episode_list);

        mEpisodeList.setCircularScrollingGestureEnabled(true);
        mEpisodeList.setBezelWidth(0.5f);
        mEpisodeList.setScrollDegreesPerScreen(90);

        final int podcastId = getIntent().getExtras().getInt("pid");

        mEpisodes = DBUtilities.GetEpisodes(this, podcastId);

        PodcastItem podcast = DBUtilities.GetPodcast(this, podcastId);

        ((TextView) findViewById(R.id.podcast_listing_title)).setText(podcast.getTitle());

        if (mEpisodes.size() > 0)
        {
            mAdapter = new EpisodesAdapter(this, mEpisodes);
            mEpisodeList.setAdapter(mAdapter);
            findViewById(R.id.empty_podcast_episodes).setVisibility(TextView.GONE);
        }
        else
        {
            findViewById(R.id.empty_podcast_episodes).setVisibility(TextView.VISIBLE);
        }

        final Activity context = this;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.episodes_list_layout);

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Do you want to mark all items read?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues cv = new ContentValues();
                        cv.put("read", 1);
                        new DBPodcastsEpisodes(context).updateAll(cv, podcastId);

                        mEpisodes = DBUtilities.GetEpisodes(context, podcastId);
                        mAdapter = new EpisodesAdapter(context, mEpisodes);
                        mEpisodeList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                return false;
            }

        });
   }
}
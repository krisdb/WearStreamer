package com.wear.streamer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RadioActivity extends Activity{

    private MediaPlayer mMediaPlayer = null;
    private WearableRecyclerView mMediaList = null;
    private List<PodcastItem> mStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_radio_list);

        mMediaList = (WearableRecyclerView)findViewById(R.id.radio_list);

        mMediaList.setCircularScrollingGestureEnabled(true);
        mMediaList.setBezelWidth(0.5f);
        mMediaList.setScrollDegreesPerScreen(90);

        new GetRadioStation(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetRadioStation extends AsyncTask<Void, Void, Void>
    {
        private Context mContext;

        public GetRadioStation(final Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
/*            PodcastItem item = new PodcastItem();
            item.setTitle("BBC");
            item.setMediaUrl("http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p");
            item.setIsRadio(true);
            mStations.add(item);

            item = new RssItem();
            item.setTitle("ABC News");
            item.setMediaUrl("http://www.abc.net.au/res/streaming/audio/mp3/news_radio.pls");
            item.setIsRadio(true);
            mStations.add(item);

            item = new RssItem();
            item.setTitle("NPR");
            item.setMediaUrl("http://stream.radiosai.net:8002/");
            item.setIsRadio(true);
            mStations.add(item);*/
            return null;
        }

        protected void onPostExecute(Void param)
        {
            //EpisodesAdapter adapter = new EpisodesAdapter(mContext, mStations, mMediaPlayer);
            //mMediaList.setAdapter(adapter);
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
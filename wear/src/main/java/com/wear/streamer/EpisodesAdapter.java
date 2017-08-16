package com.wear.streamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class EpisodesAdapter extends WearableRecyclerView.Adapter<EpisodesAdapter.ViewHolder> implements MediaPlayer.OnBufferingUpdateListener  {

    private List<PodcastItem> mEpisodes;
    private MediaPlayer mMediaPlayer;
    private Context mContext;

    public static class ViewHolder extends WearableRecyclerView.ViewHolder {

        private final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.episodeView);
        }

        @Override
        public String toString() { return (String) mTextView.getText(); }
    }

    public EpisodesAdapter(Context context, List<PodcastItem> episodes, MediaPlayer mp) {
        mEpisodes = episodes;
        mMediaPlayer = mp;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episode_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final PodcastItem episode = mEpisodes.get(position);

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    StopStream(episode);
                    viewHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                } else {
                    StartSteam(viewHolder.mTextView, episode);
                    viewHolder.mTextView.setText("Loading...");
                }
            }
        });

        viewHolder.mTextView.setText(episode.getTitle());
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    private void StopStream(PodcastItem episode)
    {
        //if (episode.IsRadio() == false && mMediaPlayer != null && mMediaPlayer.isPlaying())
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            SavePosition(episode);
            mMediaPlayer.stop();
        }
    }

    private void StartSteam(final TextView tv, final PodcastItem episode) {

        if (episode.getMediaUrl() == null)
        {
            tv.setText(episode.getTitle());
            tv.setTypeface(null, Typeface.NORMAL);
            Toast.makeText(mContext, "Episode not available", Toast.LENGTH_LONG).show();
            return;
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(episode.getMediaUrl().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer player) {
                mMediaPlayer.seekTo(GetPosition(episode));
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(this);

        //if (episode.IsRadio() == false) {
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mp) {
                    SystemClock.sleep(200);
                    mMediaPlayer.start();
                    tv.setText(episode.getTitle());
                    tv.setTypeface(null, Typeface.BOLD);
                }
            });
        //}

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                tv.setText(episode.getTitle());
                tv.setTypeface(null, Typeface.NORMAL);
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (percent < 0 || percent > 100) {
        }
    }

    private void SavePosition(PodcastItem episode)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Utilities.GetSavedPositionKey(episode), mMediaPlayer.getCurrentPosition());
        editor.commit();
    }

    private int GetPosition(PodcastItem episode) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getInt(Utilities.GetSavedPositionKey(episode), 0);
    }

}
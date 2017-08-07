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


public class EpisodesAdapter extends WearableRecyclerView.Adapter<EpisodesAdapter.ViewHolder> implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener  {

    private List<RssItem> mItems;
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

    public EpisodesAdapter(Context context, List<RssItem> items, MediaPlayer mp) {
        mItems = items;
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

        final RssItem item = mItems.get(position);

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    StopStream(item.IsRadio() == false);
                    viewHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                } else {
                    StartSteam(viewHolder.mTextView, item);
                    viewHolder.mTextView.setText("Loading...");
                }
            }
        });

        viewHolder.mTextView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void StopStream(Boolean savePosition)
    {
        if (savePosition && mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            SavePosition();
            mMediaPlayer.stop();
        }
    }

    private void StartSteam(final TextView tv, final RssItem item) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(item.getMedia().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);

        if (item.IsRadio() == false) {
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mp) {
                    SystemClock.sleep(200);
                    mMediaPlayer.start();
                    tv.setText(item.getTitle());
                    tv.setTypeface(null, Typeface.BOLD);
                }
            });
        }

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                tv.setText(item.getTitle());
                tv.setTypeface(null, Typeface.NORMAL);
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mMediaPlayer.prepareAsync();
    }

    public void onPrepared(MediaPlayer player) {
        mMediaPlayer.seekTo(GetPosition());
        //mMediaPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (percent < 0 || percent > 100) {
        }
    }

    private void SavePosition()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(mContext.getString(R.string.pref_position), mMediaPlayer.getCurrentPosition());
        editor.commit();
    }

    private int GetPosition() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

        return pref.getInt(mContext.getString(R.string.pref_position), 0);
    }

}
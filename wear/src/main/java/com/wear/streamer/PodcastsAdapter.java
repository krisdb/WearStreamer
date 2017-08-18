package com.wear.streamer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class PodcastsAdapter extends WearableRecyclerView.Adapter<PodcastsAdapter.ViewHolder> {

    private List<PodcastItem> mItems;
    private Context mContext;

    public static class ViewHolder extends WearableRecyclerView.ViewHolder {

        private final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.podcastView);
        }

        @Override
        public String toString() { return (String) mTextView.getText(); }
    }

    public PodcastsAdapter(Context context, List<PodcastItem> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.podcast_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final PodcastItem podcast = mItems.get(position);

        final int podcastId = podcast.getPodcastId();

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PodcastEpisodesListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pid", podcastId);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

        viewHolder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setMessage("Are you sure to delete this podcast?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DBPodcasts(mContext).delete(podcastId);
                        mItems.clear();
                        mItems = DBUtilities.GetPodcasts(mContext);
                        notifyDataSetChanged();
                        dialog.dismiss();

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

        viewHolder.mTextView.setText(
                DBUtilities.HasNewEpisodes(mContext, podcastId) ?
                        podcast.getTitle() + " *" :
                        podcast.getTitle()
        );
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
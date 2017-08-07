package com.wear.streamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class PodcastsAdapter extends WearableRecyclerView.Adapter<PodcastsAdapter.ViewHolder> {

    private List<RssItem> mItems;
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

    public PodcastsAdapter(Context context, List<RssItem> items) {
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

        final RssItem item = mItems.get(position);

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WearPodcastEpisodesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", item.getLink().toString());
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

        viewHolder.mTextView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
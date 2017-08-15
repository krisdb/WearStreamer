package com.wear.streamer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;
import android.widget.Toast;

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

            final DBPodcasts db = new DBPodcasts(mContext);
            final SQLiteDatabase sdb = db.select();

            final Cursor cursor = sdb.rawQuery("SELECT title,link FROM [tbl_podcasts]", null);

            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast()) {
                    RssItem podcast = new RssItem();
                    podcast.setTitle(cursor.getString(0));
                    podcast.setLink(cursor.getString(1));
                    mPodcasts.add(podcast);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
            return null;
        }

        protected void onPostExecute(Void param)
        {
            PodcastsAdapter adapter = new PodcastsAdapter(mContext, mPodcasts);
            mMediaList.setAdapter(adapter);
        }
    }
}
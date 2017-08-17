package com.wear.streamer;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBUtilities {

    public static List<PodcastItem> GetPodcasts(Context ctx)
    {
        List<PodcastItem> podcasts = new ArrayList<>();

        final DBPodcasts db = new DBPodcasts(ctx);
        final SQLiteDatabase sdb = db.select();

        final Cursor cursor = sdb.rawQuery("SELECT [id],[title],[url] FROM [tbl_podcasts]", null);

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {
                PodcastItem podcast = new PodcastItem();
                podcast.setPodcastId(cursor.getInt(0));
                podcast.setTitle(cursor.getString(1));
                podcast.setUrl(cursor.getString(2));
                podcasts.add(podcast);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return podcasts;
    }

    public static PodcastItem GetEpisode(Context ctx, int episodeId)
    {
        PodcastItem episode = new PodcastItem();

        final DBPodcastsEpisodes db = new DBPodcastsEpisodes(ctx);
        final SQLiteDatabase sdb = db.select();

        final Cursor cursor = sdb.rawQuery("SELECT id,title,description,mediaurl FROM [tbl_podcast_episodes] WHERE id = ?", new String[] { String.valueOf(episodeId) });

        if (cursor.moveToFirst()) {
            episode.setEpisodeId(cursor.getInt(0));
            episode.setTitle(cursor.getString(1));
            episode.setDescription(cursor.getString(2));
            episode.setMediaUrl(cursor.getString(3));
        }
        cursor.close();
        db.close();

        return episode;
    }

    public static List<PodcastItem> GetEpisodes(Context ctx, int podcastId)
    {
        List<PodcastItem> podcasts = new ArrayList<>();

        final DBPodcastsEpisodes db = new DBPodcastsEpisodes(ctx);
        final SQLiteDatabase sdb = db.select();

        final Cursor cursor = sdb.rawQuery("SELECT id,title,url,mediaurl FROM [tbl_podcast_episodes] WHERE pid = ?", new String[] { String.valueOf(podcastId) });

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {
                PodcastItem podcast = new PodcastItem();
                podcast.setEpisodeId(cursor.getInt(0));
                podcast.setTitle(cursor.getString(1));
                podcast.setUrl(cursor.getString(2));
                if (cursor.getString(3) != null)
                    podcast.setMediaUrl(cursor.getString(3));
                podcasts.add(podcast);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return podcasts;
    }
}

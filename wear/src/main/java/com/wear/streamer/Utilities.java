package com.wear.streamer;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static List<RssItem> GetPodcasts(Context ctx)
    {
        List<RssItem> podcasts = new ArrayList<>();

        final DBPodcasts db = new DBPodcasts(ctx);
        final SQLiteDatabase sdb = db.select();

        final Cursor cursor = sdb.rawQuery("SELECT id,title,link FROM [tbl_podcasts]", null);

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {
                RssItem podcast = new RssItem();
                podcast.setId(cursor.getInt(0));
                podcast.setTitle(cursor.getString(1));
                podcast.setLink(cursor.getString(2));
                podcasts.add(podcast);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return podcasts;
    }


    public static String GetSavedPositionKey(RssItem item)
    {
        return "position_" + item.getTitle().substring(0,10).replaceAll(" ","");
    }
}

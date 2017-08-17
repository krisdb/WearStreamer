package com.wear.streamer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DBPodcastsEpisodes extends SQLiteOpenHelper
{
    public DBPodcastsEpisodes(final Context context) {
        super(context, "Episodes", null, 2);
    }

    public void onCreate(final SQLiteDatabase db)
    {
        final StringBuilder sbCreate = new StringBuilder();
        sbCreate.append("create table [tbl_podcast_episodes] (");
        sbCreate.append("[id] INTEGER primary key AUTOINCREMENT,");
        sbCreate.append("[pid] INTEGER not null,");
        sbCreate.append("[title] TEXT not null,");
        sbCreate.append("[url] TEXT not null,");
        sbCreate.append("[description] TEXT null,");
        sbCreate.append("[mediaurl] TEXT null,");
        sbCreate.append("[duration] INTEGER null,");
        sbCreate.append("[position] INTEGER not null DEFAULT 0,");
        sbCreate.append("[finished] INTEGER not null DEFAULT 0,");
        sbCreate.append("[read] INTEGER not null DEFAULT 0,");
        sbCreate.append("[episodeDate] DATETIME null,");
        sbCreate.append("[dateAdded] DATETIME not null DEFAULT (DATETIME(\'now\'))");
        sbCreate.append(");");

        db.execSQL(sbCreate.toString());
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE [tbl_podcast_episodes]");
        onCreate(db);
    }

    public SQLiteDatabase select()
    {
        return this.getReadableDatabase();
    }


    public void insert(final List<PodcastItem> episodes)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        //db.beginTransaction();

        for(PodcastItem episode : episodes) {
            ContentValues cv = new ContentValues();
            cv.put("pid", episode.getPodcastId());
            cv.put("title", episode.getTitle());
            cv.put("description", episode.getDescription());
            if (episode.getMediaUrl() != null)
                cv.put("mediaurl", episode.getMediaUrl().toString());
            cv.put("url", episode.getUrl().toString());
            cv.put("dateAdded", Utilities.GetDate());

            db.insert("tbl_podcast_episodes", null, cv);
        }
        //db.endTransaction();
        db.close();
    }

    public void insert(final ContentValues cv)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.insert("tbl_podcast_episodes", null, cv);
        db.close();
    }

    public void deleteAll()
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_podcast_episodes", null, null);
        db.close();
    }

    public void delete(final Integer id)
    {
        final SQLiteDatabase db = this.getWritableDatabase();

        db.delete("tbl_podcast_episodes","[id] = ?", new String[] { id.toString() });
        db.close();
    }

    public void update(final ContentValues cv, final Integer id)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.update("tbl_podcast_episodes", cv, "[id] = ?", new String[] { id.toString() });
        db.close();
    }

    public void updateAll(final ContentValues cv)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.update("tbl_podcast_episodes", cv, null, null);
        db.close();
    }
}

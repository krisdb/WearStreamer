package com.wear.streamer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DBPodcasts extends SQLiteOpenHelper
{
    public DBPodcasts(final Context context) {
        super(context, "Items", null, 1);
    }

    public void onCreate(final SQLiteDatabase db)
    {
        final StringBuilder sbCreate = new StringBuilder();
        sbCreate.append("create table [tbl_podcasts] (");
        sbCreate.append("[id] INTEGER primary key AUTOINCREMENT,");
        sbCreate.append("[title] TEXT not null,");
        sbCreate.append("[link] TEXT not null,");
        sbCreate.append("[description] TEXT null,");
        sbCreate.append("[dateAdded] DATETIME not null");
        sbCreate.append(");");

        db.execSQL(sbCreate.toString());
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE [tbl_podcasts]");
        onCreate(db);
    }

    public SQLiteDatabase select()
    {
        return this.getReadableDatabase();
    }

    public void insert(final ContentValues cv)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.insert("tbl_podcasts", null, cv);
        db.close();
    }

    public void deleteAll()
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_podcasts", null, null);
        db.close();
    }

    public void delete(final Integer id)
    {
        final SQLiteDatabase db = this.getWritableDatabase();

        db.delete("tbl_podcasts","[id] = ?", new String[] { id.toString() });
        db.close();
    }

    public void update(final ContentValues cv, final Integer itemId)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.update("tbl_podcasts", cv, "[id] = ?", new String[] { itemId.toString() });
        db.close();
    }

    public void updateAll(final ContentValues cv)
    {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.update("tbl_podcasts", cv, null, null);
        db.close();
    }
}

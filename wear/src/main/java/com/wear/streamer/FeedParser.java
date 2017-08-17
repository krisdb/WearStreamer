package com.wear.streamer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {

    public static void parse(Context ctx, final PodcastItem podcast) {
        final List<PodcastItem> episodes = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            InputStream stream = new URL(podcast.getUrl().toString()).openConnection().getInputStream();
            parser.setInput(stream, "UTF-8");
            boolean done = false;

            Log.d(ctx.getPackageName(), "Parsing " + podcast.getUrl());
            ContentValues cv = new ContentValues();
            PodcastItem episode = new PodcastItem();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item")) {
                            //cv = new ContentValues();
                            episode = new PodcastItem();
                        } else if (cv != null ) {
                            if (name.equalsIgnoreCase("link")) {
                                //cv.put("url", parser.nextText());
                                episode.setUrl(parser.nextText());
                            } else if (name.equalsIgnoreCase("description")) {
                                episode.setDescription(parser.nextText().trim());
                                //cv.put("description", parser.nextText());
                            } else if (name.equalsIgnoreCase("title")) {
                                //cv.put("title", parser.nextText());
                                episode.setTitle(parser.nextText().trim());
                            } else if (name.equalsIgnoreCase("enclosure")) {
                                //cv.put("mediaurl", parser.getAttributeValue(null, "url"));
                                episode.setMediaUrl(parser.getAttributeValue(null, "url"));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && cv != null) {
                            //cv.put("dateAdded", Utilities.GetDate());
                            //cv.put("pid", podcast.getPodcastId());
                            //new DBPodcastsEpisodes(ctx).insert(cv);
                            episode.setPodcastId(podcast.getPodcastId());
                            episodes.add(episode);
                        } else if (name.equalsIgnoreCase("channel")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(ctx.getPackageName(), e.toString());
        }

        new DBPodcastsEpisodes(ctx).insert(episodes);
        //return items;
    }
}
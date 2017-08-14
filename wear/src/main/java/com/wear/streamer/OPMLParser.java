package com.wear.streamer;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OPMLParser {

    public static List<PodcastItem> parse(final InputStream stream) {
        final List<PodcastItem> items = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            //InputStream stream = new URL(url).openConnection().getInputStream();
            parser.setInput(stream, "UTF-8");

            PodcastItem item;
            int eventType = parser.getEventType();
            boolean isInOpml = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equalsIgnoreCase("opml")) {
                            isInOpml = true;
                        }
                        if (isInOpml && parser.getName().equalsIgnoreCase("outline")) {
                            item = new PodcastItem();
                            final String title = parser.getAttributeValue(null, "title");
                            item.setTitle(title != null ? title : parser.getAttributeValue(null, "text"));
                            item.setLink(parser.getAttributeValue(null, "xmlUrl"));

                            items.add(item);
                            }
                        break;
                        }
                    eventType = parser.next();
                }


/*            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("outline")) {
                            item = new PodcastItem();

                            if (parser.getAttributeValue(null, "text").equalsIgnoreCase("feeds")) {
                                item.setTitle(parser.getAttributeValue(null, "text"));
                                item.setLink(parser.getAttributeValue(null, "xmlUrl"));

                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("outline") && item != null) {
                            items.add(item);
                        } else if (name.equalsIgnoreCase("body")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }*/
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return items;
    }
}
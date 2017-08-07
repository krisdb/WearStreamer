package com.wear.streamer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {

    public static List<RssItem> parse(final String url) {
        final List<RssItem> items = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            InputStream stream = new URL(url).openConnection().getInputStream();
            parser.setInput(stream, "UTF-8");
            boolean done = false;

            RssItem item = new RssItem();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item")) {
                            item = new RssItem();
                        } else if (item != null) {
                            if (name.equalsIgnoreCase("link")) {
                                item.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase("description")) {
                                item.setDescription(parser.nextText().trim());
                            } else if (name.equalsIgnoreCase("title")) {
                                item.setTitle(parser.nextText().trim());
                            } else if (name.equalsIgnoreCase("enclosure")) {
                                item.setMedia(parser.getAttributeValue(null, "url"));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && item != null) {
                            items.add(item);
                        } else if (name.equalsIgnoreCase("channel")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return items;
    }
}
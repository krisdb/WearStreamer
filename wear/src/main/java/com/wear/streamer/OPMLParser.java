package com.wear.streamer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OPMLParser {

    public static List<PodcastItem> parse(final InputStream stream) {
        final List<PodcastItem> items = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(stream, "UTF-8");

            PodcastItem item;
            int eventType = xpp.getEventType();
            boolean isInOpml = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:;
                        if (xpp.getName().equalsIgnoreCase("opml")) {
                            isInOpml = true;
                        } else if (isInOpml && xpp.getName().equalsIgnoreCase("outline")) {

                            int eventType2 = xpp.next();

                            while ( eventType2 != XmlPullParser.END_DOCUMENT)  {
                                if (eventType2 == XmlPullParser.START_TAG) {
                                    if (xpp.getAttributeValue(null, "text") != null && xpp.getAttributeValue(null, "xmlUrl") != null) {
                                        item = new PodcastItem();
                                        item.setTitle(xpp.getAttributeValue(null, "text"));
                                        item.setLink(xpp.getAttributeValue(null, "xmlUrl"));
                                        items.add(item);
                                    }
                                }
                                eventType2 = xpp.next();
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return items;
    }
}